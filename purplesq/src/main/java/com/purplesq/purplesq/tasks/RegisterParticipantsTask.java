package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.ParticipantVo;
import com.purplesq.purplesq.vos.TransactionVo;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nishant on 04/07/15.
 */
public class RegisterParticipantsTask extends AsyncTask<Void, Void, String> {

    private GenericAsyncTaskListener mListener;
    private ArrayList<ParticipantVo> mParticipants = new ArrayList<>();
    private String mEventId;
    private String mToken;
    private ErrorVo mErrorVo;

    public RegisterParticipantsTask(String event, String token, ArrayList<ParticipantVo> data, GenericAsyncTaskListener listener) {
        this.mEventId = event;
        this.mToken = token;
        this.mParticipants = data;
        this.mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {

        JSONObject jsonParticipants = new JSONObject();
        try {
            JSONArray jsonArrayStudents = new JSONArray();
            for (ParticipantVo participantVo : mParticipants) {
                JSONObject participant = new JSONObject();

                participant.put(PSQConsts.JSON_PARAM_FNAME, participantVo.getFirstname());
                participant.put(PSQConsts.JSON_PARAM_LNAME, participantVo.getLastname());
                participant.put(PSQConsts.JSON_PARAM_EMAIL, participantVo.getEmail());
                participant.put(PSQConsts.JSON_PARAM_PHONE, participantVo.getPhone());
                participant.put(PSQConsts.JSON_PARAM_INSTITUTE, participantVo.getInstitute());

                jsonArrayStudents.put(participant);
            }

            jsonParticipants.put(PSQConsts.JSON_PARAM_STUDENTS, jsonArrayStudents);
            jsonParticipants.put(PSQConsts.JSON_PARAM_CLIENT, ApiConst.HEADER_PARAM_ANDROID);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            RequestBody body = RequestBody.create(ApiConst.JSON, jsonParticipants.toString());

            Request request = new Request.Builder()
                    .url(ApiConst.URL_PAYMENT_INITIATE + mEventId + ApiConst.URL_PAYMENT_INITIATE_PART)
                    .header(ApiConst.HEADER_PURPLE_TOKEN, mToken)
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
                    .post(body)
                    .build();

            Response response = ApiConst.getHttpClient().newCall(request).execute();

            if (!response.isSuccessful()) {
                mErrorVo = new ErrorVo();
                mErrorVo.setCode(response.code());
                mErrorVo.setMessage(response.message());
                mErrorVo.setBody(response.body().string());
                return null;
            }

            return response.body().string();

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(final String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                TransactionVo transactionVo = ApiConst.getGson().fromJson(response, TransactionVo.class);
                mListener.genericAsyncTaskOnSuccess(transactionVo);
            } catch (Exception e) {
                e.printStackTrace();
                mListener.genericAsyncTaskOnError(mErrorVo);
            }
        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }
    }

    @Override
    protected void onCancelled() {
        mListener.genericAsyncTaskOnCancelled(null);
    }
}

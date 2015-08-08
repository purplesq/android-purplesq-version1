package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.ParticipantVo;
import com.purplesq.purplesq.vos.TransactionVo;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
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

    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Gson gson = new Gson();

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

        try {
            // Simulate network access.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            return null;
        }


        JSONObject jsonParticipants = new JSONObject();
        try {
            JSONArray jsonArrayStudents = new JSONArray();
            for (ParticipantVo participantVo : mParticipants) {
                JSONObject participant = new JSONObject();

                participant.put("fname", participantVo.getFirstname());
                participant.put("lname", participantVo.getLastname());
                participant.put("email", participantVo.getEmail());
                participant.put("phone", participantVo.getPhone());
                participant.put("institute", participantVo.getInstitute());

                jsonArrayStudents.put(participant);
            }

            jsonParticipants.put("students", jsonArrayStudents);
            jsonParticipants.put("client", "android");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            RequestBody body = RequestBody.create(JSON, jsonParticipants.toString());

            Request request = new Request.Builder()
                    .url("http://api.purplesq.com/payments/events/" + mEventId + "/initiate")
                    .header("Purple-Token", mToken)
                    .header("platform", "android")
                    .post(body)
                    .build();

            Response response = okHttpClient.newCall(request).execute();

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
                TransactionVo transactionVo = gson.fromJson(response, TransactionVo.class);
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

package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.UserVo;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by nishant on 06/08/15.
 */

public class ProfileUpdateTask extends AsyncTask<Void, Void, String> {

    private final UserVo mUserVo;
    private GenericAsyncTaskListener mListener;
    private ErrorVo mErrorVo;
    private String mToken;

    public ProfileUpdateTask(String token, UserVo userVo, GenericAsyncTaskListener listener) {
        mUserVo = userVo;
        mListener = listener;
        mToken = token;
    }

    @Override
    protected String doInBackground(Void... params) {
        JSONObject jsonUser = new JSONObject();
        try {
            jsonUser.put(PSQConsts.JSON_PARAM_FNAME, mUserVo.getFirstName());
            jsonUser.put(PSQConsts.JSON_PARAM_LNAME, mUserVo.getLastName());
            if (!TextUtils.isEmpty(mUserVo.getPhone())) {
                jsonUser.put(PSQConsts.JSON_PARAM_PHONE, mUserVo.getPhone());
            }
            if (!TextUtils.isEmpty(mUserVo.getInstitute())) {
                jsonUser.put(PSQConsts.JSON_PARAM_INSTITUTE, mUserVo.getInstitute());
            }
            jsonUser.put(PSQConsts.JSON_PARAM_DOB, mUserVo.getDob());
            if (!TextUtils.isEmpty(mUserVo.getGender())) {
                jsonUser.put(PSQConsts.JSON_PARAM_GENDER, mUserVo.getGender());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            RequestBody body = RequestBody.create(ApiConst.JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url(ApiConst.URL_PROFILE_UPDATE)
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
                    .header(ApiConst.HEADER_PURPLE_TOKEN, mToken)
                    .put(body)
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
    protected void onPostExecute(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                mListener.genericAsyncTaskOnSuccess(new JSONObject(response));
            } catch (JSONException e) {
                e.printStackTrace();
                mListener.genericAsyncTaskOnError(mErrorVo);
            }

        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }
    }

    @Override
    protected void onCancelled() {
        mListener.genericAsyncTaskOnCancelled(mErrorVo);
    }
}
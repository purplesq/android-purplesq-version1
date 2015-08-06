package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.UserVo;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
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

    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final UserVo mUserVo;
    private final OkHttpClient okHttpClient = new OkHttpClient();
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
            jsonUser.put("fname", mUserVo.getFirstName());
            jsonUser.put("lname", mUserVo.getLastName());
            if (!TextUtils.isEmpty(mUserVo.getPhone())) {
                jsonUser.put("phone", mUserVo.getPhone());
            }
            if (!TextUtils.isEmpty(mUserVo.getInstitute())) {
                jsonUser.put("institute", mUserVo.getInstitute());
            }
            jsonUser.put("dob", mUserVo.getDob());
            if (!TextUtils.isEmpty(mUserVo.getGender())) {
                jsonUser.put("gender", mUserVo.getGender());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Log.i("Nish", "JSON : " + jsonUser.toString());
            RequestBody body = RequestBody.create(JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url("http://dev.purplesq.com:4000/users/account")
                    .header("platform", "android")
                    .header("Purple-Token", mToken)
                    .put(body)
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
    protected void onPostExecute(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                Log.i("Nish", "Response : " + response);
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
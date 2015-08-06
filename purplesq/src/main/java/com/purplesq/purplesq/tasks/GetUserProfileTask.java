package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.vos.ErrorVo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by nishant on 06/08/15.
 */

public class GetUserProfileTask extends AsyncTask<Void, Void, String> {

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private GenericAsyncTaskListener mListener;
    private ErrorVo mErrorVo;
    private String mToken;

    public GetUserProfileTask(String token, GenericAsyncTaskListener listener) {
        mListener = listener;
        mToken = token;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Request request = new Request.Builder()
                    .url("http://dev.purplesq.com:4000/users/account")
                    .header("platform", "android")
                    .header("Purple-Token", mToken)
                    .get()
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
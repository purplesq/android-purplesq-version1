package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.vos.ErrorVo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by nishant on 28/07/15.
 */
public class GetAllCitiesTask extends AsyncTask<Void, Void, String> {

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    private GenericAsyncTaskListener mListener;
    private ErrorVo mErrorVo;

    public GetAllCitiesTask(GenericAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {

            Request request = new Request.Builder()
                    .url("http://dev.purplesq.com:4000/eduventures/events/cities")
                    .get()
                    .addHeader("platform", "android")
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
    protected void onPostExecute(String result) {

        if (!TextUtils.isEmpty(result)) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                if (jsonArray.length() > 0) {
                    mListener.genericAsyncTaskOnSuccess(jsonArray);
                } else {
                    mListener.genericAsyncTaskOnError(mErrorVo);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                mListener.genericAsyncTaskOnError(mErrorVo);
            }
        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }


    }
}
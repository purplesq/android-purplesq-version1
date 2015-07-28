package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
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

        try {
            JSONArray jsonArray = new JSONArray(result);
            if (jsonArray.length() > 0) {
                if (mListener != null) {
                    mListener.genericAsyncTaskOnSuccess(jsonArray);
                }
            } else {
                if (mListener != null) {
                    mListener.genericAsyncTaskOnSuccess(null);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.EventsVo;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by nishant on 02/06/15.
 */
public class GetAllEventsTask extends AsyncTask<Void, Void, String> {

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    private GenericAsyncTaskListener mListener;
    private String mCity;
    private ErrorVo mErrorVo;

    public GetAllEventsTask(GenericAsyncTaskListener listener, String city) {
        mListener = listener;
        mCity = city;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            String url = "http://dev.purplesq.com:4000/eduventures/events/odis";
            if (!TextUtils.isEmpty(mCity)) {
                if (!mCity.contains("All Events")) {
                    url = url + "?city=" + mCity;
                }
            }

            Request request = new Request.Builder()
                    .url(url)
                    .header("platform", "android")
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
            List<EventsVo> eventsVos = null;

            Type listType = new TypeToken<List<EventsVo>>() {
            }.getType();

            eventsVos = gson.fromJson(result, listType);
            mListener.genericAsyncTaskOnSuccess(eventsVos);

        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }
    }
}

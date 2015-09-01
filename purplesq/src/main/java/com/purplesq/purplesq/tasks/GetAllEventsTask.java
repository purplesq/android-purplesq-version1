package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.reflect.TypeToken;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
import com.purplesq.purplesq.utils.Config;
import com.purplesq.purplesq.utils.OkHttpLoggingInterceptor;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.EventsVo;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by nishant on 02/06/15.
 */
public class GetAllEventsTask extends AsyncTask<Void, Void, String> {

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

            ApiConst.getHttpClient().interceptors().clear();
            if (Config.DEBUG) {
                ApiConst.getHttpClient().interceptors().add(new OkHttpLoggingInterceptor());
            }

            String url = ApiConst.URL_GET_ALL_EVENTS;
            if (!TextUtils.isEmpty(mCity)) {
                if (!mCity.contains("All Events")) {
                    url = url + "?city=" + mCity;
                }
            }

            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
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
        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (mErrorVo == null) {
            if (Config.DEBUG) {
                Log.i("HTTP", "Response : " + response);
            }
            if (!TextUtils.isEmpty(response)) {
                List<EventsVo> eventsVos = null;

                Type listType = new TypeToken<List<EventsVo>>() {
                }.getType();

                eventsVos = ApiConst.getGson().fromJson(response, listType);
                mListener.genericAsyncTaskOnSuccess(eventsVos);

            } else {
                mListener.genericAsyncTaskOnError(mErrorVo);
            }
        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }
    }
}

package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
import com.purplesq.purplesq.vos.ErrorVo;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

/**
 * Created by nishant on 28/07/15.
 */
public class GetAllCitiesTask extends AsyncTask<Void, Void, String> {

    private GenericAsyncTaskListener mListener;
    private ErrorVo mErrorVo;

    public GetAllCitiesTask(GenericAsyncTaskListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {

            Request request = new Request.Builder()
                    .url(ApiConst.URL_GET_ALL_CITIES)
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
    protected void onPostExecute(String result) {
        if (mErrorVo == null) {
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
                    Crashlytics.logException(e);
                    mListener.genericAsyncTaskOnError(mErrorVo);
                }
            } else {
                mListener.genericAsyncTaskOnError(mErrorVo);
            }
        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }


    }
}
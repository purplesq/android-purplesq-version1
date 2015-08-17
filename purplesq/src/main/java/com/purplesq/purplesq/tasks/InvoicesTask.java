package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.reflect.TypeToken;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
import com.purplesq.purplesq.utils.Config;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.InvoicesVo;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by nishant on 17/08/15.
 */
public class InvoicesTask extends AsyncTask<Void, Void, String> {

    private GenericAsyncTaskListener mListener;
    private String mToken;
    private ErrorVo mErrorVo;

    public InvoicesTask(String token, GenericAsyncTaskListener listener) {
        mListener = listener;
        mToken = token;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {

            Request request = new Request.Builder()
                    .url(ApiConst.URL_INVOICES)
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
                    .header(ApiConst.HEADER_PURPLE_TOKEN, mToken)
                    .get()
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
        if (Config.DEBUG) {
            Log.i("Nish", "Invoices : \n" + result);
        }
        if (mErrorVo == null) {
            if (!TextUtils.isEmpty(result)) {
                List<InvoicesVo> invoicesVos = null;

                Type listType = new TypeToken<List<InvoicesVo>>() {
                }.getType();

                invoicesVos = ApiConst.getGson().fromJson(result, listType);
                mListener.genericAsyncTaskOnSuccess(invoicesVos);

            } else {
                mListener.genericAsyncTaskOnError(mErrorVo);
            }
        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }
    }
}

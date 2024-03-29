package com.purplesq.purplesq.tasks;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
import com.purplesq.purplesq.utils.Config;
import com.purplesq.purplesq.utils.OkHttpLoggingInterceptor;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.utils.Utils;
import com.purplesq.purplesq.vos.ErrorVo;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by nishant on 25/06/15.
 */

/**
 * Represents an asynchronous Registration task for Google Plus accounts.
 */
public class SocialRegistrationGoogleTask extends AsyncTask<Void, Void, String> {

    private final String mJsonGoogleUser;
    private GenericAsyncTaskListener mListener;
    private Context mContext;
    private ErrorVo mErrorVo;

    public SocialRegistrationGoogleTask(Context context, String googleJson, GenericAsyncTaskListener listener) {
        mContext = context;
        mJsonGoogleUser = googleJson;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {

        JSONObject jsonUser = new JSONObject();
        String accessToken = "";
        try {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;

            JSONObject jsonGoogle = new JSONObject(mJsonGoogleUser);
            accessToken = jsonGoogle.getString(PSQConsts.JSON_PARAM_ACCESS_TOKEN);
            jsonGoogle.remove(PSQConsts.JSON_PARAM_ACCESS_TOKEN);
            String email = jsonGoogle.getJSONArray(PSQConsts.JSON_PARAM_EMAILS).getJSONObject(0).getString("value");
            jsonUser.put(PSQConsts.JSON_PARAM_PROFILE, jsonGoogle);
            jsonUser.put(PSQConsts.JSON_PARAM_APP_NAME, versionName);
            jsonUser.put(PSQConsts.JSON_PARAM_APP_CODE, versionCode);
            jsonUser.put(PSQConsts.JSON_PARAM_DEVICE, Utils.getDeviceHash(mContext, email));
        } catch (JSONException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }

        try {
            ApiConst.getHttpClient().interceptors().clear();
            if (Config.DEBUG) {
                ApiConst.getHttpClient().interceptors().add(new OkHttpLoggingInterceptor());
            }

            RequestBody body = RequestBody.create(ApiConst.JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url(ApiConst.URL_LOGIN_GOOGLE)
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
                    .header(ApiConst.HEADER_ACCESS_TOKEN, accessToken)
                    .post(body)
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
            Crashlytics.logException(e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String response) {
        if (mErrorVo == null) {
            if (Config.DEBUG) {
                Log.i("HTTP", "Response : " + response);
            }
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    mListener.genericAsyncTaskOnSuccess(jsonResponse);
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

    @Override
    protected void onCancelled() {
        mListener.genericAsyncTaskOnCancelled(mErrorVo);
    }
}
package com.purplesq.purplesq.tasks;

/**
 * Created by nishant on 17/06/15.
 */

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
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, String> {

    private final String mEmail;
    private final String mPassword;
    private GenericAsyncTaskListener mListener;
    private Context mContext;
    private ErrorVo mErrorVo;

    public UserLoginTask(Context context, String email, String password, GenericAsyncTaskListener listener) {
        this.mContext = context;
        mEmail = email;
        mPassword = password;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {

        JSONObject jsonUser = new JSONObject();
        try {

            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;

//            {"email" : "testUser@purplesq.com", "password" : "psq1234"}
            jsonUser.put(PSQConsts.JSON_PARAM_EMAIL, mEmail);
            jsonUser.put(PSQConsts.JSON_PARAM_PASSWORD, mPassword);
            jsonUser.put(PSQConsts.JSON_PARAM_APP_NAME, versionName);
            jsonUser.put(PSQConsts.JSON_PARAM_APP_CODE, versionCode);
            jsonUser.put(PSQConsts.JSON_PARAM_DEVICE, Utils.getDeviceHash(mContext, mEmail));
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
                    .url(ApiConst.URL_LOGIN_EMAIL)
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
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
            e.printStackTrace();
            Crashlytics.logException(e);
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
                JSONObject jsonResponse;
                try {
                    jsonResponse = new JSONObject(response);
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
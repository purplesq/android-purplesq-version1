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
public class SocialRegistrationFacebookTask extends AsyncTask<Void, Void, String> {

    private final String mJsonFBUser;
    private final String mFBToken;
    private GenericAsyncTaskListener mListener;
    private Context mContext;
    private ErrorVo mErrorVo;

    public SocialRegistrationFacebookTask(Context context, String fbToken, String facebookJson, GenericAsyncTaskListener listener) {
        mContext = context;
        mJsonFBUser = facebookJson;
        mListener = listener;
        mFBToken = fbToken;
    }

    @Override
    protected String doInBackground(Void... params) {

        JSONObject jsonUser = new JSONObject();
        try {

            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;

            JSONObject jsonFacebook = new JSONObject(mJsonFBUser);

            JSONObject jsonProfile = new JSONObject();
            jsonProfile.put(PSQConsts.JSON_PARAM_ID, jsonFacebook.get(PSQConsts.JSON_PARAM_ID));
            jsonProfile.put(PSQConsts.JSON_PARAM_FIRST_NAME, jsonFacebook.get(PSQConsts.JSON_PARAM_FIRST_NAME));
            jsonProfile.put(PSQConsts.JSON_PARAM_LAST_NAME, jsonFacebook.get(PSQConsts.JSON_PARAM_LAST_NAME));
            jsonProfile.put(PSQConsts.JSON_PARAM_EMAIL, jsonFacebook.get(PSQConsts.JSON_PARAM_EMAIL));
            jsonUser.put(PSQConsts.JSON_PARAM_PROFILE, jsonProfile);
            jsonUser.put(PSQConsts.JSON_PARAM_APP_NAME, versionName);
            jsonUser.put(PSQConsts.JSON_PARAM_APP_CODE, versionCode);
            jsonUser.put(PSQConsts.JSON_PARAM_DEVICE, Utils.getDeviceHash(mContext, jsonFacebook.getString(PSQConsts.JSON_PARAM_EMAIL)));
        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        try {
            ApiConst.getHttpClient().interceptors().clear();
            if (Config.DEBUG) {
                ApiConst.getHttpClient().interceptors().add(new OkHttpLoggingInterceptor());
            }

            RequestBody body = RequestBody.create(ApiConst.JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url(ApiConst.URL_LOGIN_FACEBOOK)
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
                    .header(ApiConst.HEADER_ACCESS_TOKEN, mFBToken)
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
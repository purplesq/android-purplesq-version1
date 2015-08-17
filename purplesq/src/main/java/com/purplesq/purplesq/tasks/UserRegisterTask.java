package com.purplesq.purplesq.tasks;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
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
 * Created by nishant on 23/06/15.
 */

/**
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserRegisterTask extends AsyncTask<Void, Void, String> {

    private final String mFirstName;
    private final String mLastName;
    private final String mEmail;
    private final String mPassword;
    private final String mPhoneNo;
    private GenericAsyncTaskListener mListener;
    private Context mContext;
    private ErrorVo mErrorVo;

    public UserRegisterTask(Context context, String firstname, String lastname, String email, String password, String phoneno, GenericAsyncTaskListener listener) {
        mContext = context;
        mFirstName = firstname;
        mLastName = lastname;
        mEmail = email;
        mPassword = password;
        mPhoneNo = phoneno;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {

        JSONObject jsonUser = new JSONObject();
        try {

            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;

//            {"fname" : "testUser", "lname" : "testUser", "email" : "testUser@purplesq.com", "phone" : "7676767676", "password" : "psq1234"}
            jsonUser.put(PSQConsts.JSON_PARAM_FNAME, mFirstName);
            jsonUser.put(PSQConsts.JSON_PARAM_LNAME, mLastName);
            jsonUser.put(PSQConsts.JSON_PARAM_EMAIL, mEmail);
            jsonUser.put(PSQConsts.JSON_PARAM_PHONE, mPhoneNo);
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
            RequestBody body = RequestBody.create(ApiConst.JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url(ApiConst.URL_LOGIN_REGISTER)
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
        } catch (IOException ex) {
            ex.printStackTrace();
            Crashlytics.logException(ex);
            return null;
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (mErrorVo == null) {
            if (!TextUtils.isEmpty(response)) {
                try {
                    mListener.genericAsyncTaskOnSuccess(new JSONObject(response));
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
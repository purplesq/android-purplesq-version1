package com.purplesq.purplesq.tasks;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.purplesq.purplesq.Utils;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.vos.ErrorVo;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
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

    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String mFirstName;
    private final String mLastName;
    private final String mEmail;
    private final String mPassword;
    private final String mPhoneNo;
    private final OkHttpClient okHttpClient = new OkHttpClient();
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
            jsonUser.put("fname", mFirstName);
            jsonUser.put("lname", mLastName);
            jsonUser.put("email", mEmail);
            jsonUser.put("phone", mPhoneNo);
            jsonUser.put("password", mPassword);
            jsonUser.put("app_name", versionName);
            jsonUser.put("app_code", versionCode);
            jsonUser.put("device", Utils.getDeviceHash(mContext, mEmail));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            RequestBody body = RequestBody.create(JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url("http://api.purplesq.com/users/account")
                    .header("platform", "android")
                    .post(body)
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
    protected void onPostExecute(String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                mListener.genericAsyncTaskOnSuccess(new JSONObject(response));
            } catch (JSONException e) {
                e.printStackTrace();
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
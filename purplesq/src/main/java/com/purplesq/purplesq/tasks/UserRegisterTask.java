package com.purplesq.purplesq.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.purplesq.purplesq.Utils;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
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

    private final String mFirstName;
    private final String mLastName;
    private final String mEmail;
    private final String mPassword;
    private final String mPhoneNo;
    private GenericAsyncTaskListener mListener;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private Context mContext;

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
        // TODO: attempt authentication against a network service.

        try {
            // Simulate network access.
            Thread.sleep(500);
        } catch (InterruptedException e) {
            return null;
        }


        JSONObject jsonUser = new JSONObject();
        try {
//            {"fname" : "testUser", "lname" : "testUser", "email" : "testUser@purplesq.com", "phone" : "7676767676", "password" : "psq1234"}
            jsonUser.put("fname", mFirstName);
            jsonUser.put("lname", mLastName);
            jsonUser.put("email", mEmail);
            jsonUser.put("phone", mPhoneNo);
            jsonUser.put("password", mPassword);
            jsonUser.put("device", Utils.getDeviceHash(mContext, mEmail));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            Log.i("Nish", "JSON : " + jsonUser.toString());
            RequestBody body = RequestBody.create(JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url("http://dev.purplesq.com:4000/users/account")
                    .header("platform", "android")
                    .post(body)
                    .build();

            Response response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                Log.i("Nish", "Response failed Message : " + response.message());
                Log.i("Nish", "Response failed Body : " + response.body().string());
                return null;
            }

            return response.body().string();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String response) {
        if (TextUtils.isEmpty(response)) {
            mListener.genericAsyncTaskOnSuccess(false);
        } else {
            Log.i("Nish", "Response : " + response);
            mListener.genericAsyncTaskOnSuccess(true);
        }
    }

    @Override
    protected void onCancelled() {
        mListener.genericAsyncTaskOnCancelled(null);
    }
}
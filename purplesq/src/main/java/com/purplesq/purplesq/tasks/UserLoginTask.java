package com.purplesq.purplesq.tasks;

/**
 * Created by nishant on 17/06/15.
 */

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

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
 * Represents an asynchronous login/registration task used to authenticate
 * the user.
 */
public class UserLoginTask extends AsyncTask<Void, Void, String> {

    private final String mEmail;
    private final String mPassword;
    private GenericAsyncTaskListener mListener;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public UserLoginTask(String email, String password, GenericAsyncTaskListener listener) {
        mEmail = email;
        mPassword = password;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        // TODO: attempt authentication against a network service.

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return null;
        }


        JSONObject jsonUser = new JSONObject();
        try {
//            {"email" : "testUser@purplesq.com", "password" : "psq1234"}
            jsonUser.put("email", mEmail);
            jsonUser.put("password", mPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {

            RequestBody body = RequestBody.create(JSON, jsonUser.toString());

            Log.i("Nish", "Request Json : " + jsonUser.toString());

            Request request = new Request.Builder()
                    .url("http://dev.purplesq.com:4000/users/login")
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
package com.purplesq.purplesq.tasks;

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
 * Created by nishant on 25/06/15.
 */

/**
 * Represents an asynchronous Registration task for Google Plus accounts.
 */
public class SocialRegistrationFacebookTask extends AsyncTask<Void, Void, String> {

    private final String mJsonFBUser;
    private GenericAsyncTaskListener mListener;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public SocialRegistrationFacebookTask(String facebookJson, GenericAsyncTaskListener listener) {
        mJsonFBUser = facebookJson;
        mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {

        JSONObject jsonUser = new JSONObject();
        try {
            JSONObject jsonFacebook = new JSONObject(mJsonFBUser);

            JSONObject jsonProfile = new JSONObject();
            jsonProfile.put("id", jsonFacebook.get("id"));
            jsonProfile.put("first_name", jsonFacebook.get("first_name"));
            jsonProfile.put("last_name", jsonFacebook.get("last_name"));
            jsonProfile.put("email", jsonFacebook.get("email"));

            jsonUser.put("profile", jsonProfile);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            RequestBody body = RequestBody.create(JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url("http://dev.purplesq.com:4000/users/facebook")
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
            mListener.genericAsyncTaskOnSuccess(null);
        } else {
            Log.i("Nish", "FBReg Response : " + response);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                mListener.genericAsyncTaskOnSuccess(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCancelled() {
        mListener.genericAsyncTaskOnCancelled(null);
    }
}
package com.purplesq.purplesq.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

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
 * Created by nishant on 25/06/15.
 */

/**
 * Represents an asynchronous Registration task for Google Plus accounts.
 */
public class SocialRegistrationFacebookTask extends AsyncTask<Void, Void, String> {

    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String mJsonFBUser;
    private final String mFBToken;
    private final OkHttpClient okHttpClient = new OkHttpClient();
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
            JSONObject jsonFacebook = new JSONObject(mJsonFBUser);

            JSONObject jsonProfile = new JSONObject();
            jsonProfile.put("id", jsonFacebook.get("id"));
            jsonProfile.put("first_name", jsonFacebook.get("first_name"));
            jsonProfile.put("last_name", jsonFacebook.get("last_name"));
            jsonProfile.put("email", jsonFacebook.get("email"));
            jsonUser.put("profile", jsonProfile);
            jsonUser.put("device", Utils.getDeviceHash(mContext, jsonFacebook.getString("email")));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            RequestBody body = RequestBody.create(JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url("http://dev.purplesq.com:4000/users/facebook")
                    .header("platform", "android")
                    .header("access-token", mFBToken)
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
    protected void onPostExecute(final String response) {
        if (!TextUtils.isEmpty(response)) {
            Log.i("Nish", "FBReg Response : " + response);
            try {
                JSONObject jsonResponse = new JSONObject(response);
                mListener.genericAsyncTaskOnSuccess(jsonResponse);
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
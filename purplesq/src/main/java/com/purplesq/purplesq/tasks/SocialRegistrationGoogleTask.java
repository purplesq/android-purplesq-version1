package com.purplesq.purplesq.tasks;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.Utils;
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
public class SocialRegistrationGoogleTask extends AsyncTask<Void, Void, String> {

    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final String mJsonGoogleUser;
    private final OkHttpClient okHttpClient = new OkHttpClient();
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
            accessToken = jsonGoogle.getString("access-token");
            jsonGoogle.remove("access-token");
            String email = jsonGoogle.getJSONArray("emails").getJSONObject(0).getString("value");
            jsonUser.put("profile", jsonGoogle);
            jsonUser.put("app_name", versionName);
            jsonUser.put("app_code", versionCode);
            jsonUser.put("device", Utils.getDeviceHash(mContext, email));
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        try {
            RequestBody body = RequestBody.create(JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url("http://api.purplesq.com/users/google")
                    .header("platform", "android")
                    .header("access-token", accessToken)
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
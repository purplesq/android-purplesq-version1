package com.purplesq.purplesq.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.purplesq.purplesq.fragments.LoginFragment;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by nishant on 18/06/15.
 */
public class GooglePlusLoginInfoTask extends AsyncTask<Void, Void, String> {

    GenericAsyncTaskListener mListener;
    Activity mActivity;
    String mAccount = "";
    String mAccessToken;

    public GooglePlusLoginInfoTask(Activity activity, String account, GenericAsyncTaskListener listener) {
        mActivity = activity;
        mListener = listener;
        mAccount = account;
    }


    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL("https://www.googleapis.com/plus/v1/people/me");
            mAccessToken = GoogleAuthUtil.getToken(mActivity, mAccount, "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/plus.profile.emails.read");

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Bearer " + mAccessToken);

            String content;

            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                InputStreamReader inputStream = new InputStreamReader(urlConnection.getInputStream());
                int i = inputStream.read();
                while (i != -1) {
                    bo.write(i);
                    i = inputStream.read();
                }
                content = bo.toString();
            } catch (IOException e) {
                content = "";
            }

            return content;

        } catch (UserRecoverableAuthException userAuthEx) {
            // Start the user recoverable action using the intent returned by getIntent()
            mActivity.startActivityForResult(userAuthEx.getIntent(), LoginFragment.RC_SIGN_IN);
            return null;
        } catch (Exception e) {
            // Handle error
            e.printStackTrace(); // Uncomment if needed during debugging.
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String jsonObject) {
        Log.i("Nish", "Response : " + jsonObject);
        try {
            JSONObject jsonResponse = new JSONObject(jsonObject);
            jsonResponse.put("access-token", mAccessToken);
            mListener.genericAsyncTaskOnSuccess(jsonResponse.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

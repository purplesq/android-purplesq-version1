package com.purplesq.purplesq.tasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.purplesq.purplesq.fragments.LoginFragment;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.ErrorVo;

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

    private GenericAsyncTaskListener mListener;
    private Activity mActivity;
    private String mAccount = "";
    private String mAccessToken;
    private ErrorVo mErrorVo;

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
                Crashlytics.logException(e);
            }

            return content;

        } catch (UserRecoverableAuthException userAuthEx) {
            // Start the user recoverable action using the intent returned by getIntent()
            mActivity.startActivityForResult(userAuthEx.getIntent(), LoginFragment.RC_SIGN_IN);
            Crashlytics.logException(userAuthEx);
            mErrorVo = new ErrorVo();
            mErrorVo.setBody(userAuthEx.getMessage());
            return null;
        } catch (Exception e) {
            // Handle error
            e.printStackTrace(); // Uncomment if needed during debugging.
            Crashlytics.logException(e);
            mErrorVo = new ErrorVo();
            mErrorVo.setBody(e.getMessage());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String jsonObject) {
        if (!TextUtils.isEmpty(jsonObject)) {
            try {
                JSONObject jsonResponse = new JSONObject(jsonObject);
                jsonResponse.put(PSQConsts.JSON_PARAM_ACCESS_TOKEN, mAccessToken);
                mListener.genericAsyncTaskOnSuccess(jsonResponse.toString());

            } catch (JSONException e) {
                Crashlytics.logException(e);
                e.printStackTrace();
            }
        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }
    }
}

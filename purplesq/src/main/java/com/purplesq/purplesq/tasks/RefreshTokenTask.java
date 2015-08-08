package com.purplesq.purplesq.tasks;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.purplesq.purplesq.Utils;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.UserVo;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by nishant on 29/07/15.
 */
public class RefreshTokenTask extends AsyncTask<Void, Void, String> {

    public final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final UserVo mUser;
    private final OkHttpClient okHttpClient = new OkHttpClient();
    private GenericAsyncTaskListener mListener;
    private Context mContext;
    private ErrorVo mErrorVo;

    public RefreshTokenTask(Context context, UserVo user, GenericAsyncTaskListener listener) {
        this.mContext = context;
        this.mUser = user;
        this.mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            String versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
            int versionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;

            JSONObject jsonUser = new JSONObject();
            jsonUser.put("uid", mUser.getId());
            jsonUser.put("email", mUser.getEmail());
            jsonUser.put("device", Utils.getDeviceHash(mContext, mUser.getEmail()));
            jsonUser.put("app_name", versionName);
            jsonUser.put("app_code", versionCode);

            RequestBody body = RequestBody.create(JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url("http://api.purplesq.com/users/refresh-token")
                    .header("platform", "android")
                    .addHeader("content-type", "application/json")
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
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String response) {

        if (!TextUtils.isEmpty(response)) {
            Gson gson = new Gson();
            AuthVo authVo = gson.fromJson(response, AuthVo.class);
            AuthDataManager.insertOrUpdateAuthData(mContext, authVo);
            mListener.genericAsyncTaskOnSuccess(true);
        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }
    }

    @Override
    protected void onCancelled() {
        mListener.genericAsyncTaskOnCancelled(null);
    }
}

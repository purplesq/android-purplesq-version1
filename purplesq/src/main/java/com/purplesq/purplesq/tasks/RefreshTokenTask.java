package com.purplesq.purplesq.tasks;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.utils.Utils;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.UserVo;
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

    private final UserVo mUser;
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
            jsonUser.put(PSQConsts.JSON_PARAM_UID, mUser.getId());
            jsonUser.put(PSQConsts.JSON_PARAM_EMAIL, mUser.getEmail());
            jsonUser.put(PSQConsts.JSON_PARAM_DEVICE, Utils.getDeviceHash(mContext, mUser.getEmail()));
            jsonUser.put(PSQConsts.JSON_PARAM_APP_NAME, versionName);
            jsonUser.put(PSQConsts.JSON_PARAM_APP_CODE, versionCode);

            RequestBody body = RequestBody.create(ApiConst.JSON, jsonUser.toString());

            Request request = new Request.Builder()
                    .url(ApiConst.URL_REFRESH_TOKEN)
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
                    .addHeader(ApiConst.HEADER_CONTENT_TYPE, ApiConst.HEADER_PARAM_JSON)
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

        } catch (IOException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Crashlytics.logException(e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(final String response) {
        if (mErrorVo == null) {
            if (!TextUtils.isEmpty(response)) {
                Gson gson = new Gson();
                AuthVo authVo = gson.fromJson(response, AuthVo.class);
                AuthDataManager.insertOrUpdateAuthData(mContext, authVo);
                mListener.genericAsyncTaskOnSuccess(true);
            } else {
                mListener.genericAsyncTaskOnError(mErrorVo);
            }
        } else {
            mListener.genericAsyncTaskOnError(mErrorVo);
        }
    }

    @Override
    protected void onCancelled() {
        mListener.genericAsyncTaskOnCancelled(null);
    }
}

package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
import com.purplesq.purplesq.utils.Config;
import com.purplesq.purplesq.utils.OkHttpLoggingInterceptor;
import com.purplesq.purplesq.vos.CouponsVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by nishant on 19/09/15.
 */
public class CheckCouponTask extends AsyncTask<Void, Void, String> {

    private GenericAsyncTaskListener mListener;
    private ErrorVo mErrorVo;
    private String mToken, mCouponCode, mEventId;
    private int mAmount;

    public CheckCouponTask(String token, String couponCode, String eventId, int amount, GenericAsyncTaskListener listener) {
        mListener = listener;
        mToken = token;
        mCouponCode = couponCode;
        mEventId = eventId;
        mAmount = amount;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            ApiConst.getHttpClient().interceptors().clear();
            if (Config.DEBUG) {
                ApiConst.getHttpClient().interceptors().add(new OkHttpLoggingInterceptor());
            }

            JSONObject jsonPincode;
            jsonPincode = new JSONObject();
            jsonPincode.put("couponCode", mCouponCode);
            jsonPincode.put("event_id", mEventId);
            jsonPincode.put("amount", mAmount);

            RequestBody body = RequestBody.create(ApiConst.JSON, jsonPincode.toString());

            Request request = new Request.Builder()
                    .url(ApiConst.URL_CHECK_COUPON)
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
                    .header(ApiConst.HEADER_PURPLE_TOKEN, mToken)
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
        }
    }

    @Override
    protected void onPostExecute(String response) {
        if (mErrorVo == null) {
            if (Config.DEBUG) {
                Log.i("HTTP", "Response : " + response);
            }
            if (!TextUtils.isEmpty(response)) {
                try {
                    CouponsVo couponsVo = ApiConst.getGson().fromJson(response, CouponsVo.class);
                    mListener.genericAsyncTaskOnSuccess(couponsVo);
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                    mListener.genericAsyncTaskOnError(mErrorVo);
                }

            } else {
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
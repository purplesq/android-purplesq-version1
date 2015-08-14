package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.utils.ApiConst;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.PaymentPayUVo;
import com.purplesq.purplesq.vos.TransactionVo;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by nishant on 07/07/15.
 */
public class PaymentTask extends AsyncTask<Void, Void, String> {

    private GenericAsyncTaskListener mListener;
    private String mToken;
    private TransactionVo mTransactionVo;
    private ErrorVo mErrorVo;

    public PaymentTask(String token, TransactionVo transactionVo, GenericAsyncTaskListener listener) {
        this.mToken = token;
        this.mTransactionVo = transactionVo;
        this.mListener = listener;
    }

    @Override
    protected String doInBackground(Void... params) {
        try {

            RequestBody formBody = new FormEncodingBuilder()
                    .add(PSQConsts.JSON_PARAM_ID, mTransactionVo.getId())
                    .add(PSQConsts.JSON_PARAM_STATUS, mTransactionVo.getStatus())
                    .add(PSQConsts.JSON_PARAM_EMAIL, mTransactionVo.getEmail())
                    .add(PSQConsts.JSON_PARAM_PHONE, mTransactionVo.getPhone() + "")
                    .add(PSQConsts.JSON_PARAM_MODE, mTransactionVo.getMode())
                    .add(PSQConsts.JSON_PARAM_AMOUNT, mTransactionVo.getAmount() + "")
                    .build();


            Request request = new Request.Builder()
                    .url(ApiConst.URL_PAYMENT + mTransactionVo.getId())
                    .header(ApiConst.HEADER_PURPLE_TOKEN, mToken)
                    .header(ApiConst.HEADER_PLATFORM, ApiConst.HEADER_PARAM_ANDROID)
                    .post(formBody)
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
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

    }

    @Override
    protected void onPostExecute(final String response) {
        if (!TextUtils.isEmpty(response)) {
            try {
                PaymentPayUVo paymentPayUVo = ApiConst.getGson().fromJson(response, PaymentPayUVo.class);
                mListener.genericAsyncTaskOnSuccess(paymentPayUVo);
            } catch (Exception e) {
                e.printStackTrace();
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

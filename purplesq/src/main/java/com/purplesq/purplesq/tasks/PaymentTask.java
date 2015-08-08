package com.purplesq.purplesq.tasks;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.PaymentPayUVo;
import com.purplesq.purplesq.vos.TransactionVo;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by nishant on 07/07/15.
 */
public class PaymentTask extends AsyncTask<Void, Void, String> {

    private final OkHttpClient okHttpClient = new OkHttpClient();
    private final Gson gson = new Gson();

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
                    .add("id", mTransactionVo.getId())
                    .add("status", mTransactionVo.getStatus())
                    .add("email", mTransactionVo.getEmail())
                    .add("phone", mTransactionVo.getPhone() + "")
                    .add("mode", mTransactionVo.getMode())
                    .add("amount", mTransactionVo.getAmount() + "")
                    .build();


            Request request = new Request.Builder()
                    .url("http://api.purplesq.com/payments/process/" + mTransactionVo.getId())
                    .header("Purple-Token", mToken)
                    .header("platform", "android")
                    .post(formBody)
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
                PaymentPayUVo paymentPayUVo = gson.fromJson(response, PaymentPayUVo.class);
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

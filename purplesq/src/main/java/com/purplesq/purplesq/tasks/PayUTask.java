package com.purplesq.purplesq.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.crashlytics.android.Crashlytics;
import com.payu.sdk.PayU;
import com.purplesq.purplesq.vos.PaymentPayUVo.PaymentRequstVo;

import java.util.HashMap;

/**
 * Created by nishant on 08/07/15.
 */
public class PayUTask extends AsyncTask<Void, Void, Void> {

    Activity mActivity;
    PaymentRequstVo mPaymentRequstVo;

    public PayUTask(Activity activity, PaymentRequstVo paymentRequstVo) {
        this.mActivity = activity;
        this.mPaymentRequstVo = paymentRequstVo;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        try {

            HashMap<String, String> params = new HashMap<>();
            params.put(PayU.MERCHANT_KEY, mPaymentRequstVo.getKey());
            params.put(PayU.TXNID, mPaymentRequstVo.getTxnid());
            params.put(PayU.AMOUNT, ((int) mPaymentRequstVo.getAmount() + ""));
            params.put(PayU.PRODUCT_INFO, mPaymentRequstVo.getProductinfo());
            params.put(PayU.FIRSTNAME, mPaymentRequstVo.getFirstname());
            params.put(PayU.EMAIL, mPaymentRequstVo.getEmail());
            params.put(PayU.SURL, mPaymentRequstVo.getSurl());
            params.put(PayU.FURL, mPaymentRequstVo.getFurl());
            params.put("curl", mPaymentRequstVo.getCurl());
            params.put("phone", mPaymentRequstVo.getPhone() + "");
            params.put("udf1", mPaymentRequstVo.getUdf1());
            params.put("lastname", mPaymentRequstVo.getLastname());
            params.put("hash", mPaymentRequstVo.getHash());
            PayU.getInstance(mActivity).startPaymentProcess(mPaymentRequstVo.getAmount(), params,
                    new PayU.PaymentMode[]{PayU.PaymentMode.CC, PayU.PaymentMode.DC, PayU.PaymentMode.NB, PayU.PaymentMode.PAYU_MONEY});
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        return null;
    }

}

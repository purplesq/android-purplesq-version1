package com.purplesq.purplesq.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.payu.sdk.PayU;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.PayUTask;
import com.purplesq.purplesq.tasks.PaymentTask;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.EventsVo;
import com.purplesq.purplesq.vos.PaymentPayUVo;
import com.purplesq.purplesq.vos.TransactionVo;

import java.util.ArrayList;

public class PaymentActivity extends Activity implements GenericAsyncTaskListener {

    private Activity mActivity;
    private TransactionVo mTransactionVo;
    private float mAmount;
    private ArrayList<String> mPaticipantsNames;
    private int position = -1;
    private EventsVo mEventData;
    private PaymentTask mPaymentTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mActivity = this;
        setupActionBar();
        getIntentExtras();

        populateUI();


    }

    private void populateUI() {

        CardView cardView = (CardView) findViewById(R.id.activity_payment_cardview);
        LinearLayout participantslayout = (LinearLayout) findViewById(R.id.activity_payment_linlayout_participants);
        Button btnPay = (Button) findViewById(R.id.activity_payment_btn_pay);
        TextView tvAmount = (TextView) findViewById(R.id.activity_payment_tv_amount);

        ImageView image = (ImageView) cardView.findViewById(R.id.item_cardlayout_imageview);
        (cardView.findViewById(R.id.item_cardlayout_btn_book)).setVisibility(View.GONE);
        TextView tvHeading = (TextView) cardView.findViewById(R.id.item_cardlayout_textview_heading);
        TextView tvSubText = (TextView) cardView.findViewById(R.id.item_cardlayout_textview_subheading);


        tvHeading.setText(mEventData.getName());
        tvSubText.setText(mEventData.getSummary());
        ImageLoader.getInstance().displayImage(mEventData.getThumbnail(), image);

        for (int i = 0; i < mPaticipantsNames.size(); i++) {
            TextView tvName = new TextView(mActivity);
            tvName.setText((i + 1) + ". " + mPaticipantsNames.get(i));
            tvName.setPadding(4, 4, 4, 4);
            tvName.setTextAppearance(mActivity, android.R.style.TextAppearance_Medium);

            participantslayout.addView(tvName);
        }

        tvAmount.setText(mAmount + "");
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthVo authVo = AuthDataManager.getAuthData(PaymentActivity.this);
                if (authVo != null) {
                    mPaymentTask = new PaymentTask(authVo.getToken(), mTransactionVo, PaymentActivity.this);
                    mPaymentTask.execute((Void) null);
                }
            }
        });


    }

    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    public void getIntentExtras() {
        Intent i = getIntent();
        if (i.hasExtra("transaction")) {
            String transaction = i.getStringExtra("transaction");
            if (!TextUtils.isEmpty(transaction)) {
                Gson gson = new Gson();
                mTransactionVo = gson.fromJson(transaction, TransactionVo.class);

                if (mTransactionVo != null) {
                    mAmount = mTransactionVo.getAmount();
                }
            }
        }
        if (i.hasExtra("participants-name")) {
            mPaticipantsNames = i.getStringArrayListExtra("participants-name");
        }

        if (i.hasExtra("event-position")) {
            position = i.getIntExtra("event-position", -1);
        }

        if (position >= 0) {
            mEventData = ((PurpleSQ) mActivity.getApplication()).getEventsData().get(position);
        }

    }

    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        mPaymentTask = null;
        if (obj != null && obj instanceof PaymentPayUVo) {
            PaymentPayUVo paymentPayUVo = (PaymentPayUVo) obj;
            PaymentPayUVo.PaymentRequstVo mPaymentRequstVo = paymentPayUVo.getRequest();
            new PayUTask(mActivity, mPaymentRequstVo).execute();
        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayU.RESULT) {
            if (resultCode == RESULT_OK) {
                //success
                if (data != null) {
                    String jsonString = data.getStringExtra("result");
                    jsonString = jsonString.replaceAll("&quot;", "\"");
                    Toast.makeText(this, "Success" + jsonString, Toast.LENGTH_LONG).show();
                    Log.i("Nish", "Transaction Details : " + jsonString);
                    Intent intent = new Intent(mActivity, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    startActivity(intent);
                    finish();

                }
            }
            if (resultCode == RESULT_CANCELED) {
                //failed
                if (data != null)
                    Toast.makeText(this, "Failed" + data.getStringExtra("result"), Toast.LENGTH_LONG).show();
            }
        }
    }
}

package com.purplesq.purplesq.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.payu.sdk.PayU;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.PayUTask;
import com.purplesq.purplesq.tasks.PaymentTask;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.EventsVo;
import com.purplesq.purplesq.vos.PaymentPayUVo;
import com.purplesq.purplesq.vos.TransactionVo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class PaymentActivity extends AppCompatActivity implements GenericAsyncTaskListener {

    private final String UNICODE_RUPEE = "\uf156";
    private Activity mActivity;
    private TransactionVo mTransactionVo;
    private float mAmount;
    private ArrayList<String> mPaticipantsNames, mPaticipantsInstitute;
    private int position = -1;
    private EventsVo mEventData;
    private PaymentTask mPaymentTask;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        mActivity = this;
        setupToolBar();
        getIntentExtras();

        populateUI();


    }

    private void populateUI() {

        CardView cardView = (CardView) findViewById(R.id.activity_payment_cardview);
        LinearLayout participantslayout = (LinearLayout) findViewById(R.id.activity_payment_layout_participants);
        Button btnPay = (Button) findViewById(R.id.activity_participants_btn_pay);
        TextView tvAmount = (TextView) findViewById(R.id.activity_payment_tv_eventamount);

        TextView tvHeading = (TextView) findViewById(R.id.activity_payment_tv_eventname);
        TextView tvDate = (TextView) findViewById(R.id.activity_payment_tv_eventdate);


        String eventDay = "";
        try {
            Date date = new Date(mEventData.getSchedule().getStart_date());

            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            eventDay = sdf2.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }


        tvHeading.setText(mEventData.getName());
        tvDate.setText(eventDay + ", " + mEventData.getLocation().getCity());

        for (int i = 0; i < mPaticipantsNames.size(); i++) {

            View participantView = getLayoutInflater().inflate(R.layout.item_payment_participants, null);
            ((TextView) participantView.findViewById(R.id.item_payment_participants_tv_name)).setText(mPaticipantsNames.get(i));
            ((TextView) participantView.findViewById(R.id.item_payment_participants_tv_insitute)).setText(mPaticipantsInstitute.get(i));
            ((TextView) participantView.findViewById(R.id.item_payment_participants_tv_number)).setText((i + 1) + "");

            participantslayout.addView(participantView);
        }

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome.ttf");
        tvAmount.setTypeface(font);
        tvAmount.setText(UNICODE_RUPEE + " " + (int) mAmount);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthVo authVo = AuthDataManager.getAuthData(PaymentActivity.this);
                if (authVo != null) {

                    PurpleSQ.showLoadingDialog(PaymentActivity.this);

                    mPaymentTask = new PaymentTask(authVo.getToken(), mTransactionVo, PaymentActivity.this);
                    mPaymentTask.execute((Void) null);
                }
            }
        });


    }

    /**
     * Set up the {@link android.support.v7.widget.Toolbar}.
     */
    private void setupToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
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
            mPaticipantsInstitute = i.getStringArrayListExtra("participants-institute");
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
        PurpleSQ.dismissLoadingDialog();

        mPaymentTask = null;
        if (obj != null && obj instanceof PaymentPayUVo) {
            PaymentPayUVo paymentPayUVo = (PaymentPayUVo) obj;
            PaymentPayUVo.PaymentRequstVo mPaymentRequstVo = paymentPayUVo.getRequest();
            new PayUTask(mActivity, mPaymentRequstVo).execute((Void) null);
        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {
        PurpleSQ.dismissLoadingDialog();

        mPaymentTask = null;
        if (obj instanceof ErrorVo) {
            ErrorVo errorVo = (ErrorVo) obj;
            Log.i("Nish", "Response failed Code : " + errorVo.getCode());
            Log.i("Nish", "Response failed Message : " + errorVo.getMessage());
            Log.i("Nish", "Response failed Body : " + errorVo.getBody());
        }
    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {
        PurpleSQ.dismissLoadingDialog();
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

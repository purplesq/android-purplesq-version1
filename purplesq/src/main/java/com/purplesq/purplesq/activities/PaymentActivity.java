package com.purplesq.purplesq.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer.Result;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.widget.ShareDialog;
import com.payu.sdk.PayU;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.fragments.ErrorDialogFragment;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.CheckCouponTask;
import com.purplesq.purplesq.tasks.PayUTask;
import com.purplesq.purplesq.tasks.PaymentTask;
import com.purplesq.purplesq.tasks.RegisterParticipantsTask;
import com.purplesq.purplesq.utils.Config;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.CouponsVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.EventsVo;
import com.purplesq.purplesq.vos.ParticipantVo;
import com.purplesq.purplesq.vos.PaymentPayUVo;
import com.purplesq.purplesq.vos.TransactionVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PaymentActivity extends AppCompatActivity implements GenericAsyncTaskListener {

    private Activity mActivity;
    private TransactionVo mTransactionVo;
    private ArrayList<ParticipantVo> mParticipantList;
    private AuthVo authVo;
    private float mAmount;
    private int position = -1;
    private EventsVo mEventData;
    private PaymentTask mPaymentTask;
    private Toolbar mToolbar;
    private CallbackManager callbackManager;
    private String couponCode = "", mEventId;
    private boolean isErrorSet = false;
    private int discountedPrice = -1;
    private TextView tvDiscount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mActivity = this;

        FacebookSdk.sdkInitialize(mActivity.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        setupToolBar();
        getIntentExtras();

        populateUI();


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
        if (getIntent().hasExtra(PSQConsts.EXTRAS_EVENT_ID)) {
            mEventId = getIntent().getStringExtra(PSQConsts.EXTRAS_EVENT_ID);
        }

        if (i.hasExtra(PSQConsts.EXTRAS_EVENT_POSITION)) {
            position = i.getIntExtra(PSQConsts.EXTRAS_EVENT_POSITION, -1);
        }

        if (i.hasExtra(PSQConsts.EXTRAS_PARTICIPANTS)) {
            mParticipantList = i.getParcelableArrayListExtra(PSQConsts.EXTRAS_PARTICIPANTS);
        }

        if (position >= 0) {
            mEventData = ((PurpleSQ) mActivity.getApplication()).getEventsData().get(position);
        }

        if (mEventData != null && !mParticipantList.isEmpty()) {
            mAmount = mEventData.getCost().getTotal() * mParticipantList.size();
        }

        authVo = AuthDataManager.getAuthData(PaymentActivity.this);
    }


    private void populateUI() {

        LinearLayout participantslayout = (LinearLayout) findViewById(R.id.activity_payment_layout_participants);
        Button btnPay = (Button) findViewById(R.id.activity_payment_btn_pay);
        Button btnCod = (Button) findViewById(R.id.activity_payment_btn_cod);
        TextView tvAmount = (TextView) findViewById(R.id.activity_payment_tv_eventamount);
        TextView tvHeading = (TextView) findViewById(R.id.activity_payment_tv_eventname);
        TextView tvDate = (TextView) findViewById(R.id.activity_payment_tv_eventdate);
        tvDiscount = (TextView) findViewById(R.id.activity_payment_tv_coupon_applied);

        final EditText etCoupons = (EditText) findViewById(R.id.activity_payment_et_coupon);


        String eventDay = "";
        try {
            Date date = new Date(mEventData.getSchedule().getStart_date());
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            eventDay = sdf2.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
        tvHeading.setText(mEventData.getName());
        tvDate.setText(eventDay + ", " + mEventData.getLocation().getCity());

        for (int i = 0; i < mParticipantList.size(); i++) {
            View participantView = getLayoutInflater().inflate(R.layout.item_payment_participants, participantslayout, false);

            ((TextView) participantView.findViewById(R.id.item_payment_participants_tv_name)).setText(mParticipantList.get(i).getFirstname() + " " + mParticipantList.get(i).getLastname());
            ((TextView) participantView.findViewById(R.id.item_payment_participants_tv_insitute)).setText(mParticipantList.get(i).getInstitute());
            ((TextView) participantView.findViewById(R.id.item_payment_participants_tv_number)).setText((i + 1) + "");

            participantslayout.addView(participantView);
        }

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome.ttf");
        tvAmount.setTypeface(font);
        tvAmount.setText(PSQConsts.UNICODE_RUPEE + " " + (int) mAmount);

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (authVo != null) {
                    PurpleSQ.showLoadingDialog(PaymentActivity.this);
                    new RegisterParticipantsTask(mEventId, authVo.getToken(), couponCode, mParticipantList, PaymentActivity.this).execute((Void) null);
                }
            }
        });

        btnCod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CodActivity.class);
                intent.putExtra(PSQConsts.EXTRAS_EVENT_POSITION, position);
                intent.putExtra("discount", discountedPrice);
                intent.putExtra("coupon", couponCode);
                intent.putExtra("discount-string", tvDiscount.getText());
                intent.putParcelableArrayListExtra(PSQConsts.EXTRAS_PARTICIPANTS, mParticipantList);
                startActivity(intent);
            }
        });

        findViewById(R.id.activity_payment_tv_coupon_apply).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponCode = etCoupons.getText().toString();
                if (!TextUtils.isEmpty(couponCode)) {
                    new CheckCouponTask(authVo.getToken(), couponCode, mEventData.getId(), (int) mAmount, PaymentActivity.this).execute();
                }
            }
        });

        findViewById(R.id.activity_payment_fbshare_cardview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareOnFB();
            }
        });

        etCoupons.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                etCoupons.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        findViewById(R.id.activity_payment_coupon_cardview).setVisibility(View.VISIBLE);
    }

    private void updateUiForCoupon(CouponsVo couponsVo) {
        List<CouponsVo.OffersVo> offersVo = couponsVo.getOffers();
        float discountedAmount = mAmount;
        for (int i = 0; i < offersVo.size(); i++) {
            if (offersVo.get(i).getType().equalsIgnoreCase("Percentage")) {
                int percentage = offersVo.get(i).getDiscount();
                discountedAmount = discountedAmount - (discountedAmount * percentage / 100);
                discountedAmount = (float) Math.floor(discountedAmount);
            } else {
                int discount = offersVo.get(i).getDiscount();
                discountedAmount = discountedAmount - discount;
            }
        }

        if (discountedAmount < mAmount) {
            if (discountedAmount < 0) {
                discountedAmount = 0;
            }
            discountedPrice = (int) discountedAmount;

            TextView tvAmount = (TextView) findViewById(R.id.activity_payment_tv_eventamount);
            tvAmount.setTextColor(getResources().getColor(R.color.teal500));
            tvAmount.setPaintFlags(tvAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            TextView tvDiscountedAmount = (TextView) findViewById(R.id.activity_payment_tv_eventamount_discounted);
            Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome.ttf");
            tvDiscountedAmount.setTypeface(font);
            tvDiscountedAmount.setText(PSQConsts.UNICODE_RUPEE + " " + (int) discountedAmount);
            tvDiscountedAmount.setVisibility(View.VISIBLE);

            tvDiscount.setTypeface(font);
            tvDiscount.setText("Coupon Applied : " + couponCode);
            tvDiscount.setVisibility(View.VISIBLE);

            findViewById(R.id.activity_payment_coupon_cardview).setVisibility(View.GONE);
        }
    }

    private void shareOnFB() {
        // Create an object
        Set<String> permissions = AccessToken.getCurrentAccessToken().getPermissions();
        if (!permissions.contains("publish_actions")) {
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    if (AccessToken.getCurrentAccessToken().getPermissions().contains("publish_actions")) {
                        shareOnFB();
                    }
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException e) {

                }
            });
            LoginManager.getInstance().logInWithPublishPermissions(PaymentActivity.this, Arrays.asList("publish_actions"));
        } else {
            String dateString = "";
            String dateString2 = "";
            try {
                Date date = new Date(mEventData.getSchedule().getStart_date());
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH);
                dateString = sdf2.format(date);
                date.setTime(mEventData.getSchedule().getEnd_date());
                dateString2 = sdf2.format(date);

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }

            String url = "http://purplesq.com/event/" + mEventData.get_id();

            ShareOpenGraphObject eventObject = new ShareOpenGraphObject.Builder()
                    .putString("fb:app_id", "852292264845107")
                    .putString("og:type", "purplesquirrel:event")
                    .putString("og:title", mEventData.getName())
                    .putString("og:description", mEventData.getSummary())
                    .putString("og:url", url)
                    .putString("og:image", mEventData.getThumbnail())
                    .build();

            ShareOpenGraphAction action = new ShareOpenGraphAction.Builder()
                    .setActionType("purplesquirrel:attend")
                    .putString("start_time", dateString)
                    .putString("expires_time", dateString2)
                    .putObject("event", eventObject)
                    .build();

            ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                    .setPreviewPropertyName("event")
                    .setAction(action)
                    .build();

            if (ShareDialog.canShow(ShareOpenGraphContent.class)) {
                ShareDialog shareDialog = new ShareDialog(this);
                shareDialog.registerCallback(callbackManager, new FacebookCallback<Result>() {
                    @Override
                    public void onSuccess(Result result) {
                        if (!TextUtils.isEmpty(result.getPostId())) {
                            if (Config.DEBUG) {
                                Log.i("Nish", "FB Share Success : " + result.getPostId());
                            }
                            findViewById(R.id.activity_payment_coupon_cardview).setVisibility(View.GONE);
                        } else {
                            if (Config.DEBUG) {
                                Log.i("Nish", "FB Share Success without postId ");
                            }
                        }
                    }

                    @Override
                    public void onCancel() {

                    }

                    @Override
                    public void onError(FacebookException e) {

                    }
                });
                shareDialog.show(content);
            }
        }
    }

    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }

        mPaymentTask = null;
        if (obj != null && obj instanceof PaymentPayUVo) {
            PaymentPayUVo paymentPayUVo = (PaymentPayUVo) obj;
            PaymentPayUVo.PaymentRequstVo mPaymentRequstVo = paymentPayUVo.getRequest();
            new PayUTask(mActivity, mPaymentRequstVo).execute((Void) null);
        } else if (obj != null && obj instanceof CouponsVo) {
            CouponsVo couponsVo = (CouponsVo) obj;
            updateUiForCoupon(couponsVo);
        } else if (obj != null && obj instanceof TransactionVo) {
            TransactionVo transactionVo = (TransactionVo) obj;
            try {
                mTransactionVo = transactionVo;

                PurpleSQ.showLoadingDialog(PaymentActivity.this);
                mPaymentTask = new PaymentTask(authVo.getToken(), couponCode, mTransactionVo, PaymentActivity.this);
                mPaymentTask.execute((Void) null);
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }

        mPaymentTask = null;
        if (obj instanceof ErrorVo) {
            ErrorVo errorVo = (ErrorVo) obj;

            if (errorVo.getCode() == 400) {
                try {
                    JSONObject json = new JSONObject(errorVo.getBody());
                    if (!TextUtils.isEmpty(json.getString("message"))) {
                        ((EditText) findViewById(R.id.activity_payment_et_coupon)).setError(json.getString("message"));
                    } else {
                        ((EditText) findViewById(R.id.activity_payment_et_coupon)).setError(errorVo.getMessage());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ((EditText) findViewById(R.id.activity_payment_et_coupon)).setError(errorVo.getMessage());
                }
                isErrorSet = true;
                findViewById(R.id.activity_payment_et_coupon).requestFocus();
            } else {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag(PSQConsts.DIALOG_FRAGMENT_ERROR);
                if (prev != null) {
                    ft.remove(prev);
                }

                ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(errorVo);
                errorDialogFragment.show(ft, "error_dialog");
            }
        }
    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }

    @Override
    public void onBackPressed() {
        if (isErrorSet) {
            findViewById(R.id.activity_payment_et_coupon).clearFocus();
            isErrorSet = false;
        } else {
            super.onBackPressed();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PayU.RESULT) {
            if (resultCode == RESULT_OK) {
                //success
                if (data != null) {
                    String jsonString = data.getStringExtra(PSQConsts.EXTRAS_RESULT);
//                    jsonString = jsonString.replaceAll("&quot;", "\"");
//                    Log.i("Nish", "Result String : " + jsonString);
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
                    Toast.makeText(this, "Failed" + data.getStringExtra(PSQConsts.EXTRAS_RESULT), Toast.LENGTH_LONG).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

}

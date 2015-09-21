package com.purplesq.purplesq.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.CheckPincodeForCoDTask;
import com.purplesq.purplesq.tasks.CoDRegistrationTask;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.utils.Utils;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.EventsVo;
import com.purplesq.purplesq.vos.ParticipantVo;
import com.purplesq.purplesq.vos.ShipmentVo;
import com.purplesq.purplesq.vos.TransactionVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CodActivity extends AppCompatActivity implements GenericAsyncTaskListener {

    private AppCompatActivity mActivity;
    private ArrayList<ParticipantVo> mParticipantList;
    private float mAmount;
    private int position = -1;
    private EventsVo mEventData;
    private CardView cardAddress, cardViewPayment;
    private RelativeLayout layoutPincode;
    private LinearLayout layoutGetPincode;
    private EditText etPincode;
    private TextView tvPincode;
    private Button btnSubmit, btnDone;
    private int discountedAmount = -1;
    private String coupunString, mCoupon;
    private String codFirstName, codLastName, codAddress, codLandmark, codCity, codState, codPhone;
    private int codPincode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cod);
        mActivity = this;

        getIntentExtras();

        populateUI();
    }


    public void getIntentExtras() {
        Intent i = getIntent();
        if (i.hasExtra(PSQConsts.EXTRAS_EVENT_POSITION)) {
            position = i.getIntExtra(PSQConsts.EXTRAS_EVENT_POSITION, -1);
        }

        if (i.hasExtra("coupon")) {
            mCoupon = i.getStringExtra("coupon");
        }

        if (i.hasExtra("discount")) {
            discountedAmount = i.getIntExtra("discount", -1);
        }

        if (i.hasExtra("discount-string")) {
            coupunString = i.getStringExtra("discount-string");
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
    }

    private void populateUI() {

        cardAddress = (CardView) findViewById(R.id.activity_cod_cardview_address);
        layoutPincode = (RelativeLayout) findViewById(R.id.activity_cod_layout_pincode);
        layoutGetPincode = (LinearLayout) findViewById(R.id.activity_cod_layout_enter_pincode);
        cardViewPayment = (CardView) findViewById(R.id.activity_cod_cardview_payment);
        etPincode = (EditText) findViewById(R.id.activity_cod_et_pincode);
        tvPincode = (TextView) findViewById(R.id.activity_cod_tv_pincode);
        btnSubmit = (Button) findViewById(R.id.activity_cod_btn_cod_request);
        btnDone = (Button) findViewById(R.id.activity_cod_btn_cod_done);


        LinearLayout participantslayout = (LinearLayout) findViewById(R.id.activity_cod_layout_participants);
        TextView tvAmount = (TextView) findViewById(R.id.activity_cod_tv_eventamount);
        TextView tvDiscountedAmount = (TextView) findViewById(R.id.activity_cod_tv_eventamount_discounted);
        TextView tvDiscount = (TextView) findViewById(R.id.activity_cod_tv_coupon_applied);
        TextView tvCashCollection = (TextView) findViewById(R.id.activity_cod_tv_cash_collection);
        TextView tvFinalAmount = (TextView) findViewById(R.id.activity_cod_tv_final_amount);
        TextView tvHeading = (TextView) findViewById(R.id.activity_cod_tv_eventname);
        TextView tvDate = (TextView) findViewById(R.id.activity_cod_tv_eventdate);


        layoutGetPincode.setVisibility(View.VISIBLE);
        layoutPincode.setVisibility(View.GONE);
        cardAddress.setVisibility(View.GONE);
        cardViewPayment.setVisibility(View.GONE);
        btnSubmit.setVisibility(View.GONE);
        btnDone.setVisibility(View.GONE);


        String[] stateList = getResources().getStringArray(R.array.states);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity, android.R.layout.simple_dropdown_item_1line, stateList);
        AutoCompleteTextView atState = (AutoCompleteTextView) findViewById(R.id.activity_cod_autotv_state);
        atState.setAdapter(adapter);


        findViewById(R.id.activity_cod_tv_pincode_check).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = AuthDataManager.getAuthData(mActivity).getToken();
                if (!TextUtils.isEmpty(etPincode.getText()) && !TextUtils.isEmpty(token)) {
                    PurpleSQ.showLoadingDialog(mActivity);
                    new CheckPincodeForCoDTask(token, etPincode.getText().toString(), CodActivity.this).execute();
                }
            }
        });

        findViewById(R.id.activity_cod_tv_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutGetPincode.setVisibility(View.VISIBLE);
                layoutPincode.setVisibility(View.GONE);
                cardAddress.setVisibility(View.GONE);
                cardViewPayment.setVisibility(View.GONE);
                btnSubmit.setVisibility(View.GONE);
                btnDone.setVisibility(View.GONE);
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAddressValidations()) {

                    View view = mActivity.getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }

                    layoutGetPincode.setVisibility(View.GONE);
                    layoutPincode.setVisibility(View.GONE);
                    cardAddress.setVisibility(View.GONE);
                    btnDone.setVisibility(View.GONE);
                    btnSubmit.setVisibility(View.VISIBLE);
                    cardViewPayment.setVisibility(View.VISIBLE);
                } else {
                }
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = mActivity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                ShipmentVo shipmentVo = new ShipmentVo();
                ShipmentVo.ReceiverVo receiverVo = shipmentVo.new ReceiverVo();
                ShipmentVo.AddressVo addressVo = shipmentVo.new AddressVo();

                receiverVo.setName(codFirstName + " " + codLastName);
                receiverVo.setPhone(codPhone);
                addressVo.setAdd(codAddress);
                addressVo.setLandmark(codLandmark);
                addressVo.setCity(codCity);
                addressVo.setState(codState);
                addressVo.setPin(codPincode);
                addressVo.setCountry("India");

                shipmentVo.setReceiver(receiverVo);
                shipmentVo.setAddress(addressVo);

                AuthVo authVo = AuthDataManager.getAuthData(mActivity);

                new CoDRegistrationTask(mEventData.getId(), authVo.getToken(), mParticipantList, mCoupon, shipmentVo, CodActivity.this).execute((Void) null);
            }
        });


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

        tvAmount.setText(PSQConsts.UNICODE_RUPEE + " " + (int) mAmount);
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome.ttf");
        tvAmount.setTextColor(getResources().getColor(R.color.teal500));
        tvAmount.setTypeface(font);

        int finalAmount;
        if (discountedAmount >= 0) {
            tvDiscountedAmount.setTypeface(font);
            tvDiscountedAmount.setText(PSQConsts.UNICODE_RUPEE + " " + discountedAmount);
            tvDiscountedAmount.setVisibility(View.VISIBLE);
            finalAmount = discountedAmount;
            tvAmount.setPaintFlags(tvAmount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvDiscountedAmount.setVisibility(View.GONE);
            finalAmount = (int) mAmount;
        }

        if (!TextUtils.isEmpty(coupunString)) {
            tvDiscount.setTypeface(font);
            tvDiscount.setText(coupunString);
            tvDiscount.setVisibility(View.VISIBLE);
        } else {
            tvDiscount.setVisibility(View.GONE);
        }

        tvCashCollection.setTypeface(font);
        tvFinalAmount.setTypeface(font);
        tvCashCollection.setText("Cash Collection : + " + PSQConsts.UNICODE_RUPEE + " 30");
        tvFinalAmount.setText("Final : " + PSQConsts.UNICODE_RUPEE + " " + (finalAmount + 30));


        ((EditText) findViewById(R.id.activity_cod_et_fname)).setText(mParticipantList.get(0).getFirstname());
        ((EditText) findViewById(R.id.activity_cod_et_lname)).setText(mParticipantList.get(0).getLastname());
        ((EditText) findViewById(R.id.activity_cod_et_phone)).setText(mParticipantList.get(0).getPhone());

    }

    private boolean checkAddressValidations() {
        EditText etFirstName = (EditText) findViewById(R.id.activity_cod_et_fname);
        EditText etLastName = (EditText) findViewById(R.id.activity_cod_et_lname);
        EditText etAddress = (EditText) findViewById(R.id.activity_cod_et_address);
        EditText etLandmark = (EditText) findViewById(R.id.activity_cod_et_landmark);
        EditText etCity = (EditText) findViewById(R.id.activity_cod_et_city);
        EditText etPhone = (EditText) findViewById(R.id.activity_cod_et_phone);
        AutoCompleteTextView atState = (AutoCompleteTextView) findViewById(R.id.activity_cod_autotv_state);

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String landmark = etLandmark.getText().toString().trim();
        String city = etCity.getText().toString().trim();
        String state = atState.getText().toString().trim();
        String phoneno = etPhone.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(firstName)) {
            etFirstName.setError(getString(R.string.error_field_required));
            focusView = etFirstName;
            cancel = true;
        } else {
            codFirstName = firstName;
        }

        if (TextUtils.isEmpty(lastName)) {
            etLastName.setError(getString(R.string.error_field_required));
            focusView = etLastName;
            cancel = true;
        } else {
            codLastName = lastName;
        }

        if (TextUtils.isEmpty(address)) {
            etAddress.setError(getString(R.string.error_field_required));
            focusView = etAddress;
            cancel = true;
        } else {
            codAddress = address;
        }

        codLandmark = landmark;

        if (TextUtils.isEmpty(city)) {
            etCity.setError(getString(R.string.error_field_required));
            focusView = etCity;
            cancel = true;
        } else {
            codCity = city;
        }

        if (TextUtils.isEmpty(state)) {
            atState.setError(getString(R.string.error_field_required));
            focusView = atState;
            cancel = true;
        } else {
            codState = state;
        }

        if (!Utils.isNumeric(phoneno)) {
            etPhone.setError(getString(R.string.error_invalid_phoneno));
            focusView = etPhone;
            cancel = true;
        } else {
            codPhone = phoneno;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        if (obj != null && obj instanceof JSONObject) {
            layoutGetPincode.setVisibility(View.GONE);
            layoutPincode.setVisibility(View.VISIBLE);
            cardAddress.setVisibility(View.VISIBLE);
            cardViewPayment.setVisibility(View.GONE);
            btnDone.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
            tvPincode.setText("Pincode : " + etPincode.getText());
            codPincode = Integer.parseInt(etPincode.getText().toString());
        } else if (obj != null && obj instanceof TransactionVo) {
            TransactionVo transactionVo = (TransactionVo) obj;
            try {
                if (transactionVo.getStatus().equalsIgnoreCase("success")) {
                    Toast.makeText(mActivity, "Sucesss : Order placed successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mActivity, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else if (transactionVo.getStatus().equalsIgnoreCase("initiated")) {
                    Toast.makeText(mActivity, "Initiated : Order placed successfully", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mActivity, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }

        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {
        if (obj instanceof ErrorVo) {
            ErrorVo errorVo = (ErrorVo) obj;
            if (errorVo.getCode() == 400) {
                try {
                    JSONObject json = new JSONObject(errorVo.getBody());
                    if (!TextUtils.isEmpty(json.getString("message"))) {
                        etPincode.setError(json.getString("message"));
                    } else {
                        etPincode.setError(errorVo.getMessage());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    etPincode.setError(errorVo.getMessage());
                }
                findViewById(R.id.activity_payment_et_coupon).requestFocus();
            }
        }
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {

    }
}

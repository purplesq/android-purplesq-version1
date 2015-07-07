package com.purplesq.purplesq.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.vos.EventsVo;
import com.purplesq.purplesq.vos.MediaVo;

import java.util.ArrayList;

public class PaymentActivity extends Activity {

    private Activity mActivity;
    private String mTransactionId;
    private double mAmount;
    private ArrayList<String> mPaticipantsNames;
    private int position = -1;
    private EventsVo mEventData;

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
        tvSubText.setText(mEventData.getSummary().getContent());
        for (MediaVo mediaVo : mEventData.getMedia()) {
            if (mediaVo.getSubtype().contentEquals("Thumbnail")) {
                ImageLoader.getInstance().displayImage(mediaVo.getUrl(), image);
            }
        }

        for (int i = 0; i < mPaticipantsNames.size(); i++) {
            TextView tvName = new TextView(mActivity);
            tvName.setText((i+1)+ ". " +mPaticipantsNames.get(i));
            tvName.setPadding(4, 4, 4, 4);
            tvName.setTextAppearance(mActivity, android.R.style.TextAppearance_Medium);

            participantslayout.addView(tvName);
        }

        tvAmount.setText(mAmount+"");
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
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
        if (i.hasExtra("transaction-id")) {
            mTransactionId = i.getStringExtra("transaction-id");
        }
        if (i.hasExtra("amount")) {
            mAmount = i.getDoubleExtra("amount", 0.0);
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
}

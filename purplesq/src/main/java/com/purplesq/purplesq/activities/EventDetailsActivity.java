package com.purplesq.purplesq.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.vos.EventFacilitiesVo;
import com.purplesq.purplesq.vos.EventFaqsVo;
import com.purplesq.purplesq.vos.EventItinerariesVo;
import com.purplesq.purplesq.vos.EventsVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class EventDetailsActivity extends AppCompatActivity {

    private final String UNICODE_RUPEE = "\uf156";
    private final String UNICODE_CERTIFICATE = "\uf0a3";
    private final String UNICODE_FOOD = "\uf0f5";
    private final String UNICODE_GUIDE = "\uf21d";
    private final String UNICODE_KIT = "\uf1b3";
    private final String UNICODE_TRAVEL = "\uf207";
    private final String UNICODE_ACCOMODATION = "\uf1ad";
    private final String UNICODE_ASTERISK = "\uF069";

    private final String STATE_SELECTED_POSITION = "selected_event_position";

    int position = -1;
    private AppCompatActivity mActivity;
    private EventsVo mEventData;
    private ImageView imageView;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private int imageColor;
    private boolean isCollapsed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        mActivity = this;

        if (getIntent().hasExtra("event-position")) {
            position = getIntent().getIntExtra("event-position", -1);
        } else if (savedInstanceState != null) {
            position = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        mEventData = ((PurpleSQ) getApplication()).getEventsData().get(position);

        setupToolBar();

        populateUI();

        collapseToolbar();
        expandHalfToolbar();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, position);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        position = savedInstanceState.getInt(STATE_SELECTED_POSITION);
    }

    /**
     * Set up the {@link android.support.v7.widget.Toolbar}.
     */
    private void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_event_details_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        ViewCompat.setTransitionName(findViewById(R.id.activity_event_details_appbarlayout), "EXTRA_IMAGE");

        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.activity_event_details_collapsing_toolbar);
        mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);

        imageView = (ImageView) findViewById(R.id.activity_event_details_iv_header);

        mCollapsingToolbar.setTitle(mEventData.getName());
        Log.i("Nish", "Thumbnail : " + mEventData.getThumbnail());
        ImageLoader.getInstance().displayImage(mEventData.getThumbnail(), imageView);
    }

    public void expandToolbar() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_event_details_coordinatorlayout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_event_details_appbarlayout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, 1, new int[2]);
            isCollapsed = false;
        }
    }

    public void expandHalfToolbar() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_event_details_coordinatorlayout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_event_details_appbarlayout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if(behavior!=null) {
            behavior.setTopAndBottomOffset(-100);
            behavior.onNestedPreScroll(coordinatorLayout, appBarLayout, null, 0, 1, new int[2]);
            isCollapsed = false;
        }
    }

    public void collapseToolbar() {
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_event_details_coordinatorlayout);
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_event_details_appbarlayout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if(behavior!=null) {
            behavior.onNestedFling(coordinatorLayout, appBarLayout, null, 0, 10000, true);
            isCollapsed = true;
        }
    }

    private void populateUI() {

        populateTopCard();
        populateStatusCard();
        populateFeaturesCard();
        populateScheduleCard();
        populateWhatYouGetCard();
        populateFAQCard();
    }

    private void populateTopCard() {

        String eventDay = "";
        try {
            Date date = new Date(mEventData.getSchedule().getStart_date());

            SimpleDateFormat sdf2 = new SimpleDateFormat("dd, MMM", Locale.ENGLISH);
            eventDay = sdf2.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome.ttf");
        String date_cost = eventDay + " | " + UNICODE_RUPEE + " " + mEventData.getCost().getTotal();

        ((TextView) findViewById(R.id.activity_event_details_tv_headline)).setText(mEventData.getName());
        ((TextView) findViewById(R.id.activity_event_details_tv_event_date)).setTypeface(font);
        ((TextView) findViewById(R.id.activity_event_details_tv_event_date)).setText(date_cost);
    }

    private void populateStatusCard() {

        findViewById(R.id.activity_event_details_btn_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(mActivity, ParticipantsActivity.class);
//                i.putExtra("event-id", mEventData.get_id());
//                i.putExtra("event-position", position);
//                mActivity.startActivity(i);
                if (isCollapsed) {
                    expandToolbar();
                } else {
                    collapseToolbar();
                }
            }
        });


        String registrationTill = "";
        try {
            Date date = new Date(mEventData.getSchedule().getRegistration().getUntil());

            SimpleDateFormat sdf2 = new SimpleDateFormat("dd, MMM hh:mm a", Locale.ENGLISH);
            registrationTill = sdf2.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String eligibility = mEventData.getEligibility();

        String availability = "Registrations end " + registrationTill;

        if (!TextUtils.isEmpty(eligibility)) {
            ((TextView) findViewById(R.id.activity_event_details_statuscard_tv_eligibility)).setText(eligibility);
        } else {
            findViewById(R.id.activity_event_details_statuscard_tv_eligibility).setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(registrationTill)) {
            ((TextView) findViewById(R.id.activity_event_details_statuscard_tv_availability)).setText(availability);
        } else {
            findViewById(R.id.activity_event_details_statuscard_tv_availability).setVisibility(View.GONE);
        }

        ((TextView) findViewById(R.id.activity_event_details_statuscard_tv_description)).setText(mEventData.getSummary());

    }

    private void populateFeaturesCard() {

        LinearLayout featuresLayout = (LinearLayout) findViewById(R.id.activity_event_details_cardview_features);

        String[] features = mEventData.getLearnings().split("\n");
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome.ttf");

        for (String feature : features) {
            LinearLayout featureLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_fontawesome_row, null);
            TextView fontTv = (TextView) featureLayout.findViewById(R.id.item_fontawesome_row_font);
            fontTv.setTypeface(font);
            fontTv.setText(UNICODE_ASTERISK);
            ((TextView) featureLayout.findViewById(R.id.item_fontawesome_row_normaltext)).setText(feature);
            featuresLayout.addView(featureLayout);
        }
    }

    private void populateScheduleCard() {
        LinearLayout scheduleLayout = (LinearLayout) findViewById(R.id.activity_event_details_cardview_schedule);

        for (EventItinerariesVo itinerary : mEventData.getItineraries()) {
            RelativeLayout itineraryLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.item_schedule, null);
            ((TextView) itineraryLayout.findViewById(R.id.item_schedule_tv_time)).setText(itinerary.getItinerariesSchedule().getSince());
            ((TextView) itineraryLayout.findViewById(R.id.item_schedule_tv_title)).setText(itinerary.getTitle());
            ((TextView) itineraryLayout.findViewById(R.id.item_schedule_tv_detail)).setText(itinerary.getDescription());

            scheduleLayout.addView(itineraryLayout);
        }
    }

    private void populateWhatYouGetCard() {
        LinearLayout whatYouGetLayout = (LinearLayout) findViewById(R.id.activity_event_details_cardview_whatuget);
        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome.ttf");

        for (EventFacilitiesVo facility : mEventData.getFacilities()) {
            LinearLayout whatYouGetItemLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_fontawesome_row, null);
            TextView fontTv = (TextView) whatYouGetItemLayout.findViewById(R.id.item_fontawesome_row_font);
            fontTv.setTypeface(font);
            switch (facility.getType()) {
                case "Accomodation":
                    fontTv.setText(UNICODE_ACCOMODATION);
                    break;
                case "Travel":
                    fontTv.setText(UNICODE_TRAVEL);
                    break;
                case "Food":
                    fontTv.setText(UNICODE_FOOD);
                    break;
                case "Kit":
                    fontTv.setText(UNICODE_KIT);
                    break;
                case "Guide":
                    fontTv.setText(UNICODE_GUIDE);
                    break;
                case "Certificate":
                    fontTv.setText(UNICODE_CERTIFICATE);
                    break;
            }

            ((TextView) whatYouGetItemLayout.findViewById(R.id.item_fontawesome_row_normaltext)).setText(facility.getName());
            whatYouGetLayout.addView(whatYouGetItemLayout);
        }
    }

    private void populateFAQCard() {
        LinearLayout faqsLayout = (LinearLayout) findViewById(R.id.activity_event_details_cardview_faq);

        for (EventFaqsVo faq : mEventData.getFaqs()) {
            GridLayout faqItemLayout = (GridLayout) getLayoutInflater().inflate(R.layout.item_faq, null);
            ((TextView) faqItemLayout.findViewById(R.id.item_faq_question)).setText(faq.getFaqQue());
            ((TextView) faqItemLayout.findViewById(R.id.item_faq_answer)).setText(faq.getFaqAns());

            faqsLayout.addView(faqItemLayout);
        }
    }

}

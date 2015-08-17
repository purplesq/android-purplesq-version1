package com.purplesq.purplesq.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.EventFacilitiesVo;
import com.purplesq.purplesq.vos.EventFaqsVo;
import com.purplesq.purplesq.vos.EventItinerariesVo;
import com.purplesq.purplesq.vos.EventsVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class EventDetailsActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {

    private final String STATE_SELECTED_POSITION = "selected_event_position";

    int position = -1;
    int scrimTriggerOffset;
    int scrollRange;
    int toolbarHeight;
    int toolbarMinHeight;
    boolean isToolbarCalculationDone = false;
    private AppCompatActivity mActivity;
    private EventsVo mEventData;
    private CollapsingToolbarLayout mCollapsingToolbar;
    private LinearLayout mBottomBar;
    private Button mBtnBook;
    private NestedScrollView mScrollView;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        mActivity = this;

        if (getIntent().hasExtra(PSQConsts.EXTRAS_EVENT_POSITION)) {
            position = getIntent().getIntExtra(PSQConsts.EXTRAS_EVENT_POSITION, -1);
        } else if (savedInstanceState != null) {
            position = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }

        PurpleSQ app = (PurpleSQ) getApplication();

        if (app.getEventsData() != null) {
            mEventData = app.getEventsData().get(position);
        }

        setupToolBar();

        populateUI();
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
        final Toolbar toolbar = (Toolbar) findViewById(R.id.activity_event_details_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
        }

        ViewCompat.setTransitionName(findViewById(R.id.activity_event_details_appbarlayout), "EXTRA_IMAGE");

        ImageView imageView = (ImageView) findViewById(R.id.activity_event_details_iv_header);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_event_details_coordinatorlayout);
        mCollapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.activity_event_details_collapsing_toolbar);


        mCollapsingToolbar.setExpandedTitleColor(Color.TRANSPARENT);
        mCollapsingToolbar.setTitle(mEventData.getName());
        ImageLoader.getInstance().displayImage(mEventData.getThumbnail(), imageView);

        ((AppBarLayout) findViewById(R.id.activity_event_details_appbarlayout)).addOnOffsetChangedListener(this);

    }

//    public void expandToolbar() {
//        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_event_details_appbarlayout);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.setTopAndBottomOffset(0);
//            behavior.onNestedPreScroll(mCoordinatorLayout, appBarLayout, null, 0, 1, new int[2]);
//        }
//    }

//    public void collapseToolbar() {
//        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_event_details_appbarlayout);
//        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
//        if (behavior != null) {
//            behavior.onNestedFling(mCoordinatorLayout, appBarLayout, null, 0, 10000, true);
//        }
//    }

    public void expandHalfToolbar() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.activity_event_details_appbarlayout);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        AppBarLayout.Behavior behavior = (AppBarLayout.Behavior) params.getBehavior();
        if (behavior != null) {
            behavior.setTopAndBottomOffset(-100);
            behavior.onNestedPreScroll(mCoordinatorLayout, appBarLayout, null, 0, 1, new int[2]);
        }
    }

    private void setupBottomBar() {
        mBottomBar = (LinearLayout) findViewById(R.id.activity_event_details_bottombar);

        mScrollView = (NestedScrollView) findViewById(R.id.activity_event_details_scrollview);
        mScrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                Rect scrollBounds = new Rect();
                mScrollView.getHitRect(scrollBounds);

                if (mBtnBook.getLocalVisibleRect(scrollBounds)) {
                    mBottomBar.setVisibility(View.GONE);
                } else {
                    mBottomBar.setVisibility(View.VISIBLE);
                }
            }
        });

        int seatsRemaining = mEventData.getBatch_size() - mEventData.getConsumed();
        String bottomText;
        if (seatsRemaining > 1) {
            bottomText = PSQConsts.UNICODE_RUPEE + " " + mEventData.getCost().getTotal() + " | " + seatsRemaining + " seats left";
        } else if (seatsRemaining == 1) {
            bottomText = PSQConsts.UNICODE_RUPEE + " " + mEventData.getCost().getTotal() + " | " + seatsRemaining + " seat left";
        } else {
            bottomText = "Event is sold out.";
        }

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome.ttf");
        ((TextView) mBottomBar.findViewById(R.id.snackbar_text)).setTypeface(font);
        ((TextView) mBottomBar.findViewById(R.id.snackbar_text)).setText(bottomText);

        if (mEventData.isSoldout()) {
            ((TextView) mBottomBar.findViewById(R.id.snackbar_action)).setText("Sold Out");
            mBottomBar.findViewById(R.id.snackbar_action).setOnClickListener(null);

        } else {
            mBottomBar.findViewById(R.id.snackbar_action).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mActivity, ParticipantsActivity.class);
                    i.putExtra(PSQConsts.EXTRAS_EVENT_ID, mEventData.get_id());
                    i.putExtra(PSQConsts.EXTRAS_EVENT_POSITION, position);
                    mActivity.startActivity(i);
                }
            });
        }

    }

    private void populateUI() {
        if (mEventData == null) {
            PurpleSQ app = (PurpleSQ) getApplication();

            if (app.getEventsData() != null) {
                mEventData = app.getEventsData().get(position);
            }
        }

        if (mEventData != null) {

            populateTopCard();
            populateStatusCard();
            populateFeaturesCard();
            populateScheduleCard();
            populateWhatYouGetCard();
            populateFAQCard();
            setupBottomBar();
        }
    }

    private void populateTopCard() {

        String eventDay = "";
        try {
            Date date = new Date(mEventData.getSchedule().getStart_date());

            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM", Locale.ENGLISH);
            eventDay = sdf2.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
        }

        Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome.ttf");
        String date_cost = eventDay + " | " + PSQConsts.UNICODE_RUPEE + " " + mEventData.getCost().getTotal();

        ((TextView) findViewById(R.id.activity_event_details_tv_headline)).setText(mEventData.getName());
        ((TextView) findViewById(R.id.activity_event_details_tv_event_date)).setTypeface(font);
        ((TextView) findViewById(R.id.activity_event_details_tv_event_date)).setText(date_cost);
    }

    private void populateStatusCard() {

        mBtnBook = (Button) findViewById(R.id.activity_event_details_btn_book);

        if (mEventData.isSoldout()) {
            mBtnBook.setText("Sold Out");
            mBtnBook.setTextColor(getResources().getColor(R.color.white));
            mBtnBook.setEnabled(false);
            mBtnBook.setOnClickListener(null);
        } else {
            mBtnBook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mActivity, ParticipantsActivity.class);
                    i.putExtra(PSQConsts.EXTRAS_EVENT_ID, mEventData.get_id());
                    i.putExtra(PSQConsts.EXTRAS_EVENT_POSITION, position);
                    mActivity.startActivity(i);
                }
            });
        }


        String registrationTill = "";
        try {
            Date date = new Date(mEventData.getSchedule().getRegistration().getUntil());

            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, hh:mm a", Locale.ENGLISH);
            registrationTill = sdf2.format(date);

        } catch (Exception e) {
            e.printStackTrace();
            Crashlytics.logException(e);
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
            fontTv.setText(PSQConsts.UNICODE_ASTERISK);
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
                    fontTv.setText(PSQConsts.UNICODE_ACCOMODATION);
                    break;
                case "Travel":
                    fontTv.setText(PSQConsts.UNICODE_TRAVEL);
                    break;
                case "Food":
                    fontTv.setText(PSQConsts.UNICODE_FOOD);
                    break;
                case "Kit":
                    fontTv.setText(PSQConsts.UNICODE_KIT);
                    break;
                case "Guide":
                    fontTv.setText(PSQConsts.UNICODE_GUIDE);
                    break;
                case "Certificate":
                    fontTv.setText(PSQConsts.UNICODE_CERTIFICATE);
                    break;
            }

            ((TextView) whatYouGetItemLayout.findViewById(R.id.item_fontawesome_row_normaltext)).setText(facility.getName());
            whatYouGetLayout.addView(whatYouGetItemLayout);
        }
    }

    private void populateFAQCard() {
        LinearLayout faqsLayout = (LinearLayout) findViewById(R.id.activity_event_details_cardview_faq);

        for (EventFaqsVo faq : mEventData.getFaqs()) {
            LinearLayout faqItemLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.item_faq, null);
            ((TextView) faqItemLayout.findViewById(R.id.item_faq_tv_question)).setText(faq.getFaqQue());
            ((TextView) faqItemLayout.findViewById(R.id.item_faq_tv_answer)).setText(faq.getFaqAns());

            faqsLayout.addView(faqItemLayout);
        }

        findViewById(R.id.activity_event_details_tv_queries).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse("tel:+918080809339"));
                    startActivity(phoneIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Comming Soon!!!", Snackbar.LENGTH_LONG);
                    View snackbarView = snackbar.getView();
                    TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();

                    Crashlytics.logException(ex);
                }
            }
        });

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (!isToolbarCalculationDone) {
            if (toolbarHeight <= 0) {
                scrollRange = ((AppBarLayout) findViewById(R.id.activity_event_details_appbarlayout)).getTotalScrollRange();
                toolbarHeight = mCollapsingToolbar.getHeight();
                toolbarMinHeight = ViewCompat.getMinimumHeight(mCollapsingToolbar);
                scrimTriggerOffset = (int) (2 * ViewCompat.getMinimumHeight(mCollapsingToolbar));
                isToolbarCalculationDone = true;
            }
        }

        if (isToolbarCalculationDone) {
            if (mCollapsingToolbar.getContentScrim() != null || mCollapsingToolbar.getStatusBarScrim() != null) {

                if (verticalOffset == 0) {
                } else if (Math.abs(verticalOffset) == scrollRange) {
                    mCollapsingToolbar.setContentScrimColor(getResources().getColor(R.color.colorPrimary));
                } else if ((toolbarHeight + verticalOffset) < scrimTriggerOffset) {
                    int alphaRange = scrimTriggerOffset - toolbarMinHeight;
                    int currentAlphaAbs = scrimTriggerOffset - ((toolbarHeight + verticalOffset));
                    int currentAlphaRelative = (int) (((float) currentAlphaAbs / (float) alphaRange) * 255);
                    String hex = Integer.toHexString(currentAlphaRelative).toUpperCase();
                    if (hex.length() == 1) {
                        hex = "0" + hex;
                    }
                    String hexColor = String.format("%06X", (0xFFFFFF & getResources().getColor(R.color.colorPrimary)));
                    String color = "#" + hex + hexColor;
                    mCollapsingToolbar.setContentScrimColor(Color.parseColor(color));

                } else {
                    mCollapsingToolbar.setContentScrimColor(getResources().getColor(R.color.transparent));
                }
            }
        } else {
            mCollapsingToolbar.setContentScrimColor(getResources().getColor(R.color.transparent));
        }
    }

}

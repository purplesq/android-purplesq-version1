package com.purplesq.purplesq.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.activities.ParticipantsActivity;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.customviews.ObservableScrollView;
import com.purplesq.purplesq.vos.EventsVo;
import com.purplesq.purplesq.vos.FacilitiesVo;
import com.purplesq.purplesq.vos.FaqsVo;
import com.purplesq.purplesq.vos.ItinerariesVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by nishant on 15/06/15.
 */
public class EventDetailsFragment extends Fragment implements ObservableScrollView.Callbacks {

    private EventsVo eventData;
    private RelativeLayout mTopView;
    private View mPlaceHolder;
    private ImageView mImageView;
    private ObservableScrollView mObservableScrollView;
    private Activity mActivity;
    private int position = -1;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            correctUI();
        }
    };

    public EventDetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Activity activity) {
        mActivity = activity;
        super.onAttach(activity);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_details, container, false);
        Bundle args = getArguments();

        position = args.getInt("event-position", -1);

        if (position >= 0) {
            eventData = ((PurpleSQ) mActivity.getApplication()).getEventsData().get(position);
        }

        if (eventData != null) {
            populateUI(rootView, inflater);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        handler.sendEmptyMessageDelayed(0, 100);
    }


    private void populateUI(View rootView, LayoutInflater inflater) {

        mObservableScrollView = (ObservableScrollView) rootView.findViewById(R.id.fragment_event_details_scrollview);
        mObservableScrollView.setCallbacks(this);

        mTopView = (RelativeLayout) rootView.findViewById(R.id.fragment_event_details_layout_top_sticky);
        mPlaceHolder = rootView.findViewById(R.id.fragment_event_details_cardview_placeholder);
        mImageView = (ImageView) rootView.findViewById(R.id.fragment_event_details_topcard_iv_banner);

        mObservableScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                onScrollChanged(mObservableScrollView.getScrollY());
            }
        });

        populateTopCard(rootView);
        populateStatusCard(rootView);
        populateFeaturesCard(rootView);
        populateScheduleCard(rootView, inflater);
        populateWhatYouGetCard(rootView);
        populatePartnersCard(rootView, inflater);
        populateFAQCard(rootView, inflater);
        populateContactUsCard(rootView);
    }

    private void correctUI() {
        if (mTopView.getHeight() > 0) {
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(mTopView.getWidth(), mTopView.getHeight());
            mPlaceHolder.setLayoutParams(param);
            mPlaceHolder.invalidate();
        }
    }


    private void populateTopCard(View rootView) {
        String eventDay = "";
        try {
            Date date = new Date(eventData.getSchedule().getStart_date());

            SimpleDateFormat sdf2 = new SimpleDateFormat("dd, MMM", Locale.ENGLISH);
            eventDay = sdf2.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        ((TextView) rootView.findViewById(R.id.fragment_event_details_topcard_tv_heading)).setText(eventData.getName());
        ((TextView) rootView.findViewById(R.id.fragment_event_details_topcard_tv_cost)).setText(eventData.getCost().getTotal() + "/-");
        ((TextView) rootView.findViewById(R.id.fragment_event_details_topcard_tv_date)).setText(eventDay);
        ImageLoader.getInstance().displayImage(eventData.getThumbnail(), mImageView);
        rootView.findViewById(R.id.fragment_event_details_topcard_btn_book).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity, ParticipantsActivity.class);
                i.putExtra("event-id", eventData.get_id());
                i.putExtra("event-position", position);
                mActivity.startActivity(i);
            }
        });
    }

    private void populateStatusCard(View rootView) {
        String registrationTill = "";
        try {
            Date date = new Date(eventData.getSchedule().getRegistration().getUntil());

            SimpleDateFormat sdf2 = new SimpleDateFormat("dd, MMM hh:mm a", Locale.ENGLISH);
            registrationTill = sdf2.format(date);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String eligibility = eventData.getEligibility();

        String availability = "Registrations end " + registrationTill;
        String disclaimer = "Disclaimer";

        ((TextView) rootView.findViewById(R.id.fragment_event_details_statuscard_tv_eligibility)).setText(eligibility);
        ((TextView) rootView.findViewById(R.id.fragment_event_details_statuscard_tv_availability)).setText(availability);
        ((TextView) rootView.findViewById(R.id.fragment_event_details_statuscard_tv_disclaimer)).setText(disclaimer);
        ((TextView) rootView.findViewById(R.id.fragment_event_details_statuscard_tv_description)).setText(eventData.getSummary());

    }

    private void populateFeaturesCard(View rootView) {

        LinearLayout featuresLayout = (LinearLayout) rootView.findViewById(R.id.fragment_event_details_cardview_features);

        String[] features = eventData.getLearnings().split("\n");

        for (String feature : features) {
            TextView tv = new TextView(mActivity);
            tv.setText(feature);
            tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setPadding(4, 4, 4, 4);
            featuresLayout.addView(tv);
        }
    }

    private void populateScheduleCard(View rootView, LayoutInflater inflater) {
        LinearLayout scheduleLayout = (LinearLayout) rootView.findViewById(R.id.fragment_event_details_cardview_schedule);

        for (ItinerariesVo itinerary : eventData.getItineraries()) {
            RelativeLayout itineraryLayout = (RelativeLayout) inflater.inflate(R.layout.item_schedule, null);
            ((TextView) itineraryLayout.findViewById(R.id.item_schedule_tv_time)).setText(itinerary.getItinerariesSchedule().getSince());
            ((TextView) itineraryLayout.findViewById(R.id.item_schedule_tv_title)).setText(itinerary.getTitle());
            ((TextView) itineraryLayout.findViewById(R.id.item_schedule_tv_detail)).setText(itinerary.getDescription());

            scheduleLayout.addView(itineraryLayout);
        }
    }

    private void populateWhatYouGetCard(View rootView) {
        LinearLayout whatYouGetLayout = (LinearLayout) rootView.findViewById(R.id.fragment_event_details_cardview_whatuget);

        for (FacilitiesVo facility : eventData.getFacilities()) {
            TextView tv = new TextView(mActivity);
            tv.setText(facility.getName());
            tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setPadding(4, 4, 4, 4);
            whatYouGetLayout.addView(tv);
        }
    }

    private void populatePartnersCard(View rootView, LayoutInflater inflater) {
        GridLayout partnersLayout = (GridLayout) rootView.findViewById(R.id.fragment_event_details_partnerscard_gridlayout);

        int iconWidth = 0;
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        if (display != null) {
            Point size = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                display.getSize(size);
            } else {
                size.x = display.getWidth();
                size.y = display.getHeight();
            }
            iconWidth = (size.x / 3) - 20;

        }


        for (String mediaUrl : eventData.getMedia()) {
            ImageView partner = (ImageView) inflater.inflate(R.layout.item_gridlayout_imageview, null);
            if (iconWidth > 0) {
                partner.setLayoutParams(new GridLayout.LayoutParams(new ViewGroup.LayoutParams(iconWidth, iconWidth)));
            }

            partner.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ImageLoader.getInstance().displayImage(mediaUrl, partner);
            partnersLayout.addView(partner);
        }

    }

    private void populateFAQCard(View rootView, LayoutInflater inflater) {
        LinearLayout faqsLayout = (LinearLayout) rootView.findViewById(R.id.fragment_event_details_cardview_faq);

        for (FaqsVo faq : eventData.getFaqs()) {
            GridLayout faqItemLayout = (GridLayout) inflater.inflate(R.layout.item_faq, null);
            ((TextView) faqItemLayout.findViewById(R.id.item_faq_question)).setText(faq.getFaqQue());
            ((TextView) faqItemLayout.findViewById(R.id.item_faq_answer)).setText(faq.getFaqAns());

            faqsLayout.addView(faqItemLayout);
        }
    }

    private void populateContactUsCard(View rootView) {
    }

    @Override
    public void onScrollChanged(int scrollY) {
        mTopView.setTranslationY(Math.max(mPlaceHolder.getTop(), scrollY));
        ViewHelper.setTranslationY(mImageView, scrollY / 2);
    }

    @Override
    public void onDownMotionEvent() {

    }

    @Override
    public void onUpOrCancelMotionEvent() {

    }
}

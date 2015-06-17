package com.purplesq.purplesq.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.adapters.EventDetailsPagerAdapter;
import com.purplesq.purplesq.application.PurpleSQ;

public class EventDetailsActivity extends Activity {

    int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        int totalItems = ((PurpleSQ) getApplication()).getEventsData().size();

        getActionBar().setTitle("One Day Events");

        if (getIntent().hasExtra("event-position")) {
            position = getIntent().getIntExtra("event-position", -1);
        }

        EventDetailsPagerAdapter mEventDetailsPagerAdapter = new EventDetailsPagerAdapter(getFragmentManager(), totalItems);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.activity_event_details_pager);
        mViewPager.setAdapter(mEventDetailsPagerAdapter);
        mViewPager.setCurrentItem(position);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }


}

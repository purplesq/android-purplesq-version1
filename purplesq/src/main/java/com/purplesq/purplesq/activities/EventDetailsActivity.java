package com.purplesq.purplesq.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.adapters.EventDetailsPagerAdapter;
import com.purplesq.purplesq.application.PurpleSQ;

public class EventDetailsActivity extends AppCompatActivity {

    int position = -1;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        setupToolBar();

        int totalItems = ((PurpleSQ) getApplication()).getEventsData().size();

        if (getIntent().hasExtra("event-position")) {
            position = getIntent().getIntExtra("event-position", -1);
        }

        EventDetailsPagerAdapter mEventDetailsPagerAdapter = new EventDetailsPagerAdapter(getSupportFragmentManager(), totalItems);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.activity_event_details_pager);
        mViewPager.setAdapter(mEventDetailsPagerAdapter);
        mViewPager.setCurrentItem(position);

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

    @Override
    protected void onResume() {
        super.onResume();
    }


}

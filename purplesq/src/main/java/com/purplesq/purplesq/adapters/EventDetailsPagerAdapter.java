package com.purplesq.purplesq.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.purplesq.purplesq.fragments.EventDetailsFragment;

/**
 * Created by nishant on 15/06/15.
 */
public class EventDetailsPagerAdapter extends FragmentStatePagerAdapter {

    private int totalCount;

    public EventDetailsPagerAdapter(FragmentManager fm, int total) {
        super(fm);
        totalCount = total;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment detailFragment = new EventDetailsFragment();
        Bundle args = new Bundle();
        args.putInt("event-position", position);
        detailFragment.setArguments(args);
        return detailFragment;
    }

    @Override
    public int getCount() {
        return totalCount;
    }
}
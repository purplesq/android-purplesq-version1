package com.purplesq.purplesq.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.purplesq.purplesq.fragments.LoginFragment;
import com.purplesq.purplesq.fragments.SignUpFragment;

/**
 * Created by nishant on 31/07/15.
 */
public class LoginPagerAdapter extends FragmentStatePagerAdapter {

    public LoginPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return LoginFragment.newInstance();
            case 1:
                return SignUpFragment.newInstance();
            default:
                return LoginFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "LOG IN";
        } else {
            return "SIGN UP";
        }
    }
}


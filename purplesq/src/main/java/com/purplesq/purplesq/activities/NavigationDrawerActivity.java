package com.purplesq.purplesq.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.fragments.HomeFragment;

/**
 * Created by nishant on 11/05/15.
 */
public class NavigationDrawerActivity extends AppCompatActivity {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    DrawerLayout mDrawerLayout;
    FrameLayout mMainContainer;
    ListView mDrawerListView;
    ActionBarDrawerToggle mDrawerToggle;
    int mCurrentSelectedPosition = 0;
    String mTitle;
    private boolean mUserLearnedDrawer;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_navigation_drawer, null);
        mMainContainer = (FrameLayout) mDrawerLayout.findViewById(R.id.main_container);

        setContentView(mDrawerLayout);
        getLayoutInflater().inflate(layoutResID, mMainContainer, true);

        mTitle = getTitle().toString();
        actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
        }

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        mDrawerListView = (ListView) mDrawerLayout.findViewById(R.id.navigation_drawer_listview);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                this,
                R.layout.item_navigation_drawer,
                R.id.item_navigation_drawer_textview,
                new String[]{
                        getString(R.string.title_leftdrawer_home),
                        getString(R.string.title_leftdrawer_collection),
                        getString(R.string.title_leftdrawer_login),
                        getString(R.string.title_leftdrawer_section1),
                        getString(R.string.title_leftdrawer_settings)
                }));


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                actionBar.setTitle(mTitle);
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                actionBar.setTitle(mTitle);

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(NavigationDrawerActivity.this);
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Select either the default item (0) or the last selected item.
        selectItem(mCurrentSelectedPosition);

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(mDrawerListView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            if (mDrawerListView != null) {
                mDrawerLayout.closeDrawer(mDrawerListView);
            }
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position) {
            case 0:
                fragmentManager.beginTransaction().replace(R.id.main_container, HomeFragment.newInstance()).commit();
                break;
            case 1:
                fragmentManager.beginTransaction().replace(R.id.main_container, HomeFragment.newInstance()).commit();
                break;
            case 2:
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                break;
            case 3:
                break;
            case 4:
                break;
        }

        switch (position) {
            case 0:
                mTitle = getString(R.string.title_leftdrawer_home);
                break;
            case 1:
                mTitle = getString(R.string.title_leftdrawer_collection);
                break;
            case 2:
                mTitle = getString(R.string.title_leftdrawer_login);
                break;
            case 3:
                mTitle = getString(R.string.title_leftdrawer_section1);
                break;
            case 4:
                mTitle = getString(R.string.title_leftdrawer_settings);
                break;
        }

        actionBar.setTitle(mTitle);

    }

    public boolean isDrawerOpen() {
        if (mDrawerLayout != null) {
            if (mDrawerListView != null) {
                return mDrawerLayout.isDrawerOpen(mDrawerListView);
            }
        }
        return false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

}

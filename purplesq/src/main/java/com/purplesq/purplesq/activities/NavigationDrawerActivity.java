package com.purplesq.purplesq.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.datamanagers.UserProfileDataManager;
import com.purplesq.purplesq.fragments.HomeFragment;
import com.purplesq.purplesq.fragments.ProfileFragment;
import com.purplesq.purplesq.fragments.TermsAndConditionsFragment;
import com.purplesq.purplesq.vos.UserVo;

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

    private DrawerLayout mDrawerLayout;
    private FrameLayout mMainContainer;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mCurrentSelectedPosition = 0;
    private String mTitle;
    private boolean mUserLearnedDrawer;
    private Toolbar mToolbar;
    private ActionBar mActionBar;
    private boolean isUserProfileUpdated = false;

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

        mToolbar = (Toolbar) mDrawerLayout.findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mActionBar = getSupportActionBar();

        mTitle = getTitle().toString();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setHomeButtonEnabled(true);
            mActionBar.setDisplayShowTitleEnabled(true);
            mActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        }

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, Gravity.LEFT);
        mNavigationView = (NavigationView) mDrawerLayout.findViewById(R.id.navigation_drawer_navigationview);
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                mDrawerLayout.closeDrawers();
                selectItem(menuItem.getItemId());
                return true;
            }
        });

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                mToolbar,             /* Toolbar */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mActionBar.setTitle(mTitle);
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mActionBar.setTitle(mTitle);

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

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(mNavigationView);
        }

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        selectDefaultPage();
        loadUserDetailsInDrawer();

    }

    public void selectDefaultPage() {
        selectItem(R.id.menu_navigation_events);
    }

    private void loadUserDetailsInDrawer() {

        UserVo user = UserProfileDataManager.getUserBacisProfile(this);

        if (user != null) {
            isUserProfileUpdated = true;
            String userName = user.getFirstName() + " " + user.getLastName();
            String email = user.getEmail();
            String userUrl = user.getImageurl();

            if (!TextUtils.isEmpty(userName)) {
                ((TextView) mDrawerLayout.findViewById(R.id.drawer_user_profile_tv_name)).setText(userName);
            } else {
                ((TextView) mDrawerLayout.findViewById(R.id.drawer_user_profile_tv_name)).setText("Welcome");
            }

            if (!TextUtils.isEmpty(email)) {
                ((TextView) mDrawerLayout.findViewById(R.id.drawer_user_profile_tv_email)).setText(email);
            } else {
                ((TextView) mDrawerLayout.findViewById(R.id.drawer_user_profile_tv_email)).setText("Please update your profile.");
            }

            if (!TextUtils.isEmpty(userUrl)) {
                ImageLoader.getInstance().displayImage(userUrl, ((de.hdodenhof.circleimageview.CircleImageView) mDrawerLayout.findViewById(R.id.drawer_user_profile_circleView)));
            } else {
                getSupportLoaderManager().initLoader(0, null, new LoaderManager.LoaderCallbacks<Cursor>() {
                    @Override
                    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
                        return new android.support.v4.content.CursorLoader(
                                NavigationDrawerActivity.this,
                                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                                new String[]{ContactsContract.CommonDataKinds.Photo.PHOTO_URI},
                                ContactsContract.Contacts.Data.MIMETYPE + "=?",
                                new String[]{ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE},
                                null);
                    }

                    @Override
                    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                        Uri photo = Uri.EMPTY;
                        if (!cursor.isClosed()) {
                            while (cursor.moveToNext()) {
                                photo = Uri.parse(cursor.getString(0));
                            }
                            cursor.close();
                        }

                        if (!TextUtils.isEmpty(photo.toString())) {
                            ((de.hdodenhof.circleimageview.CircleImageView) mDrawerLayout.findViewById(R.id.drawer_user_profile_circleView)).setImageURI(photo);
                        }
                    }

                    @Override
                    public void onLoaderReset(Loader<Cursor> loader) {

                    }
                });
            }
        }

        if (user != null) {
            mDrawerLayout.findViewById(R.id.drawer_user_profile_circleView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.main_container, ProfileFragment.newInstance()).commit();
                    mTitle = getString(R.string.title_leftdrawer_profile);
                    mDrawerLayout.closeDrawers();
                }
            });
        } else {
            mDrawerLayout.findViewById(R.id.drawer_user_profile_circleView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(NavigationDrawerActivity.this, LoginActivity.class);
                    i.putExtra("finish-activity", HomeActivity.class.getName());
                    startActivityForResult(i, 777);
                    mTitle = getString(R.string.title_leftdrawer_profile);
                    mDrawerLayout.closeDrawers();
                }
            });
        }

    }

    private void selectItem(int itemId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        boolean isSecondGroupClicked = false;
        switch (itemId) {
            case R.id.menu_navigation_events:
                fragmentManager.beginTransaction().replace(R.id.main_container, HomeFragment.newInstance()).commit();
                mTitle = getString(R.string.title_activity_Home);
                isSecondGroupClicked = false;
                break;
            case R.id.menu_navigation_invoices:
                mTitle = getString(R.string.title_leftdrawer_invoices);
                isSecondGroupClicked = false;
                break;
            case R.id.menu_navigation_queries:
                mTitle = getString(R.string.title_leftdrawer_queries);
                try {
                    Intent phoneIntent = new Intent(Intent.ACTION_CALL);
                    phoneIntent.setData(Uri.parse("tel:+912261491313"));
                    startActivity(phoneIntent);
                    selectDefaultPage();
                    isSecondGroupClicked = false;
                } catch (android.content.ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            case R.id.menu_navigation_rateus:
                mTitle = getString(R.string.title_leftdrawer_rateus);
                isSecondGroupClicked = false;
                break;
            case R.id.menu_navigation_help:
                mTitle = getString(R.string.title_leftdrawer_help);
                isSecondGroupClicked = true;
                break;
            case R.id.menu_navigation_tnc:
                fragmentManager.beginTransaction().replace(R.id.main_container, TermsAndConditionsFragment.newInstance()).commit();
                mTitle = getString(R.string.title_leftdrawer_tnc);
                isSecondGroupClicked = true;

                Log.i("Nish", "Menu length : " + mNavigationView.getMenu().size());
                break;
            case R.id.menu_navigation_settings:
                mTitle = getString(R.string.title_leftdrawer_settings);
                isSecondGroupClicked = true;
                break;
        }

        if (isSecondGroupClicked) {
            for (int i = 0; i < 4; i++) {
                mNavigationView.getMenu().getItem(i).setChecked(false);
            }
        } else {
            for (int i = 4; i < 7; i++) {
                mNavigationView.getMenu().getItem(i).setChecked(false);
            }
        }

        mActionBar.setTitle(mTitle);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isUserProfileUpdated) {
            loadUserDetailsInDrawer();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 777) {
            if (!isUserProfileUpdated) {
                loadUserDetailsInDrawer();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main_container);
        if (fragment instanceof ProfileFragment) {
            selectDefaultPage();
            loadUserDetailsInDrawer();
            return;
        }

        if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mDrawerLayout.openDrawer(Gravity.LEFT);
            return;
        }

        super.onBackPressed();
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
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
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

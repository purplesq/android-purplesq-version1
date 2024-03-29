package com.purplesq.purplesq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.adapters.LoginPagerAdapter;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.fragments.ErrorDialogFragment;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.SocialRegistrationGoogleTask;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements GenericAsyncTaskListener {

    public boolean isInfoReceived = false;
    private String finishActivity;
    private String mEventId;
    private int position;
    private LoginPagerAdapter loginPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent i = getIntent();
        if (i.hasExtra(PSQConsts.EXTRAS_FINISH_ACTIVITY)) {
            finishActivity = i.getStringExtra(PSQConsts.EXTRAS_FINISH_ACTIVITY);
        }
        if (getIntent().hasExtra(PSQConsts.EXTRAS_EVENT_ID)) {
            mEventId = getIntent().getStringExtra(PSQConsts.EXTRAS_EVENT_ID);
        }

        if (getIntent().hasExtra(PSQConsts.EXTRAS_EVENT_POSITION)) {
            position = getIntent().getIntExtra(PSQConsts.EXTRAS_EVENT_POSITION, -1);
        }

        setupToolBar();

        loginPagerAdapter = new LoginPagerAdapter(getSupportFragmentManager());
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.activity_login_viewpager);
        mViewPager.setAdapter(loginPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_login_tablayout);
        tabLayout.setTabsFromPagerAdapter(loginPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(loginPagerAdapter.getPageTitle(tab.getPosition()));
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Set up the {@link android.support.v7.widget.Toolbar}.
     */
    private void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_login_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }

        if (obj instanceof String) {
            isInfoReceived = true;

            PurpleSQ.showLoadingDialog(LoginActivity.this);
            SocialRegistrationGoogleTask mSocialRegistrationGoogleTask = new SocialRegistrationGoogleTask(LoginActivity.this, (String) obj, this);
            mSocialRegistrationGoogleTask.execute((Void) null);
        }
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.has(PSQConsts.JSON_PARAM_TOKEN)) {
                try {
                    Gson gson = new Gson();
                    AuthVo authVo = gson.fromJson(jsonObject.toString(), AuthVo.class);

                    AuthDataManager.insertOrUpdateAuthData(LoginActivity.this, authVo);

                    if (!TextUtils.isEmpty(finishActivity)) {
                        Intent intent = new Intent();
                        intent.putExtra(PSQConsts.EXTRAS_FROM_ACTIVITY, LoginActivity.class.getName());
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Intent i = new Intent(LoginActivity.this, ParticipantsActivity.class);
                        if (!TextUtils.isEmpty(mEventId)) {
                            i.putExtra(PSQConsts.EXTRAS_EVENT_ID, mEventId);
                            i.putExtra(PSQConsts.EXTRAS_EVENT_POSITION, position);
                        }
                        LoginActivity.this.startActivity(i);
                        finish();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
            }
        }

    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }

        if (obj instanceof ErrorVo) {
            ErrorVo errorVo = (ErrorVo) obj;

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag(PSQConsts.DIALOG_FRAGMENT_ERROR);
            if (prev != null) {
                ft.remove(prev);
            }

            ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(errorVo);
            errorDialogFragment.show(ft, "error_dialog");
        }
    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }


}


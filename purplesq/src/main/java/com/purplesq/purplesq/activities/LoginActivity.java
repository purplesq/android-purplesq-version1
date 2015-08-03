package com.purplesq.purplesq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.gson.Gson;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.adapters.LoginPagerAdapter;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.SocialRegistrationGoogleTask;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements GenericAsyncTaskListener {

    public boolean isInfoReceived = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupToolBar();

        LoginPagerAdapter loginPagerAdapter = new LoginPagerAdapter(getSupportFragmentManager());
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.activity_login_viewpager);
        mViewPager.setAdapter(loginPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.activity_login_tablayout);
        tabLayout.setTabsFromPagerAdapter(loginPagerAdapter);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
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
        if (obj instanceof String) {
            isInfoReceived = true;
            SocialRegistrationGoogleTask mSocialRegistrationGoogleTask = new SocialRegistrationGoogleTask(LoginActivity.this, (String) obj, this);
            mSocialRegistrationGoogleTask.execute((Void) null);
        }
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.has("token")) {
                Log.i("Nish", "Login Successful Task : " + jsonObject.toString());

                try {
                    Gson gson = new Gson();
                    AuthVo authVo = gson.fromJson(jsonObject.toString(), AuthVo.class);

                    AuthDataManager.insertOrUpdateAuthData(LoginActivity.this, authVo);

                    Intent i = new Intent(LoginActivity.this, ParticipantsActivity.class);
                    LoginActivity.this.startActivity(i);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {
        if (obj instanceof ErrorVo) {
            ErrorVo errorVo = (ErrorVo) obj;
            Log.i("Nish", "Response failed Code : " + errorVo.getCode());
            Log.i("Nish", "Response failed Message : " + errorVo.getMessage());
            Log.i("Nish", "Response failed Body : " + errorVo.getBody());
        }
    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {
    }


}


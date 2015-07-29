package com.purplesq.purplesq.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.gson.Gson;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.SocialRegistrationGoogleTask;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * A login screen that offers login via email/password and via Google+ sign in.
 * <p/>
 * ************ IMPORTANT SETUP NOTES: ************
 * In order for Google+ sign in to work with your app, you must first go to:
 * https://developers.google.com/+/mobile/android/getting-started#step_1_enable_the_google_api
 * and follow the steps in "Step 1" to create an OAuth 2.0 client for your package.
 */
public class LoginActivity extends SocialLoginBaseActivity implements GenericAsyncTaskListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setupToolBar();

        // Find the Google+ sign in button.
        SignInButton mPlusSignInButton = (SignInButton) findViewById(R.id.activity_login_btn_plus_sign_in);
        mPlusSignInButton.setSize(SignInButton.SIZE_WIDE);
        if (supportsGooglePlayServices()) {
            // Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    goolePlusSignIn();
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play Services.
            mPlusSignInButton.setVisibility(View.GONE);
            return;
        }

        LinearLayout mFBSignInButton = (LinearLayout) findViewById(R.id.activity_login_layout_fb_sign_in);
        mFBSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
            }
        });


        Button mEmailSignInButton = (Button) findViewById(R.id.activity_login_btn_email_sign_in);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, EmailLoginActivity.class);
                startActivityForResult(i, 100);
            }
        });

        Button mEmailSignUpButton = (Button) findViewById(R.id.activity_login_btn_sign_up);
        mEmailSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });

    }

    /**
     * Set up the {@link android.support.v7.widget.Toolbar}.
     */
    private void setupToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 100) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                if (data != null && data.hasExtra("response")) {
                    try {
                        String response = data.getStringExtra("response");
                        JSONObject jsonResponse = new JSONObject(response);
                        genericAsyncTaskOnSuccess(jsonResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
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


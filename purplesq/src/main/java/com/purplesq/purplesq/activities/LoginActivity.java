package com.purplesq.purplesq.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.SocialRegistrationGoogleTask;

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
        setupActionBar();

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
                startActivity(i);
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
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    protected void updateDataOnConnected(String data) {

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
    public void genericAsyncTaskOnSuccess(Object obj) {
        if (obj instanceof String) {
            isInfoReceived = true;
            SocialRegistrationGoogleTask mSocialRegistrationGoogleTask = new SocialRegistrationGoogleTask((String) obj, this);
            mSocialRegistrationGoogleTask.execute((Void) null);
        }
        if (obj instanceof JSONObject) {
            JSONObject jsonObject = (JSONObject) obj;
            if (jsonObject.has("token")) {
                updateDataOnConnected("Registered : " + jsonObject.toString());
                Log.i("Nish", "Login Successful Task : " + jsonObject.toString());

                try {
                    JSONObject user = jsonObject.getJSONObject("user");


                    SharedPreferences sharedPreferences = getSharedPreferences("purple-squirrel-settings", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", jsonObject.getString("token").trim());
                    editor.putString("user-id", user.getString("_id").trim());
                    editor.putString("user-fname", user.getString("fname").trim());
                    editor.putString("user-lname", user.getString("lname").trim());
                    editor.putString("user-email", user.getString("email").trim());
                    editor.putString("user-phone", user.getString("phone").trim());
                    editor.apply();

                    Intent i = new Intent(LoginActivity.this, ParticipantsActivity.class);
                    LoginActivity.this.startActivity(i);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {
    }


}


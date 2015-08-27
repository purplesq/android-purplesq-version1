package com.purplesq.purplesq.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.activities.LoginActivity;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.tasks.GooglePlusLoginInfoTask;
import com.purplesq.purplesq.tasks.SocialRegistrationFacebookTask;
import com.purplesq.purplesq.tasks.UserLoginTask;
import com.purplesq.purplesq.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LoginFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        FacebookCallback<LoginResult>, LoaderManager.LoaderCallbacks<Cursor> {

    public static final int RC_SIGN_IN = 0;
    private static final String TAG = LoginFragment.class.getSimpleName();
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private static final String SAVED_PROGRESS = "sign_in_progress";


    protected CallbackManager callbackManager;
    private GoogleApiClient mGoogleApiClient;
    private int mSignInProgress;
    private PendingIntent mSignInIntent;
    private int mSignInError;
    private AppCompatActivity mActivity;

    private SignInButton mPlusSignInButton;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private boolean isGoogleClicked = false;
    private boolean isGoogleClickedWithoutConnection = false;
    private String googleEmail = "";

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mActivity == null) {
            mActivity = (AppCompatActivity) getActivity();
        }


        mGoogleApiClient = buildGoogleApiClient();

        FacebookSdk.sdkInitialize(mActivity.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, LoginFragment.this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // Find the Google+ sign in button.
        mPlusSignInButton = (SignInButton) rootView.findViewById(R.id.fragment_login_btn_plus_sign_in);
        mPlusSignInButton.setSize(SignInButton.SIZE_STANDARD);

        Button mFBSignInButton = (Button) rootView.findViewById(R.id.fragment_login_btn_fb_sign_in);
        mFBSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PurpleSQ.showLoadingDialog(mActivity);
                LoginManager.getInstance().logInWithReadPermissions(LoginFragment.this, Arrays.asList("public_profile", "email"));
            }
        });


        mEmailView = (AutoCompleteTextView) rootView.findViewById(R.id.fragment_login_autotv_email);
        populateAutoComplete();

        mPasswordView = (EditText) rootView.findViewById(R.id.fragment_login_et_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.integer.customImeActionId || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        rootView.findViewById(R.id.fragment_login_btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });


        return rootView;
    }

    private GoogleApiClient buildGoogleApiClient() {
        // When we build the GoogleApiClient we specify where connected and
        // connection failed callbacks should be returned, which Google APIs our
        // app uses and which OAuth 2.0 scopes our app requests.
        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(LoginFragment.this)
                .addOnConnectionFailedListener(LoginFragment.this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addScope(new Scope(Scopes.PROFILE));

        return builder.build();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AppCompatActivity) getActivity();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isGoogleClicked || isGoogleClickedWithoutConnection) {
            if (!mGoogleApiClient.isConnected()) {
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
            }
        }
        if (supportsGooglePlayServices()) {
            // Remove and Set a listener to connect the user when the G+ button is clicked.
            mPlusSignInButton.setOnClickListener(null);
            mPlusSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mSignInProgress = STATE_SIGN_IN;
                    if (mGoogleApiClient.isConnected()) {
                        isGoogleClicked = true;
                        PurpleSQ.showLoadingDialog(mActivity);
                        goolePlusSignIn();
                    } else {
                        isGoogleClickedWithoutConnection = true;
                        mGoogleApiClient.connect();
                    }
                }
            });
        } else {
            // Don't offer G+ sign in if the app's version is too low to support Google Play Services.
            mPlusSignInButton.setVisibility(View.GONE);
        }

        if (isGoogleClicked || isGoogleClickedWithoutConnection) {
            goolePlusSignIn();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }


    /**
     * Check if the device supports Google Play Services.  It's best
     * practice to check first rather than handling this as an error case.
     *
     * @return whether the device supports Google Play Services
     */
    private boolean supportsGooglePlayServices() {
        int serviceCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
        switch (serviceCode) {
            case 2:
                try {
                    GooglePlayServicesUtil.getErrorDialog(2, mActivity, 0).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                    return false;
                }
                break;
            case ConnectionResult.SUCCESS:
                return true;
        }
        return true;
//        return GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS;
    }

    public void goolePlusSignIn() {
        if (!TextUtils.isEmpty(googleEmail)) {
            onConnected(null);
        } else {
            if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void goolePlusSignOut() {
        mSignInProgress = STATE_SIGN_IN;
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    private void populateAutoComplete() {
        getLoaderManager().initLoader(0, null, this);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!Utils.isValidEmailAddress(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            PurpleSQ.showLoadingDialog(mActivity);
            new UserLoginTask(mActivity, email, password, (LoginActivity) mActivity).execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                mActivity,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                new String[]{ContactsContract.CommonDataKinds.Email.ADDRESS},
                ContactsContract.Contacts.Data.MIMETYPE + "=?",
                new String[]{ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        while (cursor.moveToNext()) {
            emails.add(cursor.getString(0));
        }

        cursor.close();
        addEmailsToAutoComplete(emails);

        if (TextUtils.isEmpty(mEmailView.getText())) {
            mEmailView.requestFocus();
        } else {
            mPasswordView.requestFocus();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        if (emailAddressCollection.size() == 1) {
            mEmailView.setText(emailAddressCollection.get(0));
        } else {
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            ArrayAdapter<String> adapter = new ArrayAdapter<>(mActivity,
                    android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

            mEmailView.setAdapter(adapter);
        }
    }


    /**
     * An earlier connection failed, and we're now receiving the result of the resolution attempt by PlusClient.
     *
     * @see #onConnectionFailed(ConnectionResult)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // If the error resolution was successful we should continue
                // processing errors.
                mSignInProgress = STATE_SIGN_IN;
            } else {
                // If the error resolution was not successful or the user canceled,
                // we should stop processing errors.
                mSignInProgress = STATE_DEFAULT;
            }

            if (!mGoogleApiClient.isConnecting()) {
                // If Google Play services resolved the issue with a dialog then
                // onStart is not called so we need to re-attempt connection here.
                mGoogleApiClient.connect();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        // Reaching onConnected means we consider the user signed in.

        if (!((LoginActivity) mActivity).isInfoReceived) {
            googleEmail = Plus.AccountApi.getAccountName(mGoogleApiClient);
        }

        if (isGoogleClicked || isGoogleClickedWithoutConnection) {
            PurpleSQ.showLoadingDialog(mActivity);
            new GooglePlusLoginInfoTask(mActivity, googleEmail, (LoginActivity) mActivity).execute((Void) null);
            // Indicate that the sign in process is complete.
            mSignInProgress = STATE_DEFAULT;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason.
        // We call connect() to attempt to re-establish the connection or get a
        // ConnectionResult that we can attempt to resolve.
        mGoogleApiClient.connect();
    }

    @Override
    public void onSuccess(final LoginResult loginResult) {
        PurpleSQ.showLoadingDialog(mActivity);

        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        JSONObject userJson = response.getJSONObject();
                        String fbToken = loginResult.getAccessToken().getToken();

                        PurpleSQ.showLoadingDialog(mActivity);
                        new SocialRegistrationFacebookTask(mActivity, fbToken, userJson.toString(), (LoginActivity) mActivity).execute((Void) null);
                    }
                });

        request.executeAsync();
    }

    @Override
    public void onCancel() {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }

    @Override
    public void onError(FacebookException e) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
        e.printStackTrace();
    }

    /**
     * Connection failed for some reason (called by GoogleApiClient)
     * Try and resolve the result.  Failure here is usually not an indication of a serious error,
     * just that the user's input is needed.
     *
     * @see #onActivityResult(int, int, Intent)
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }

        // Refer to the javadoc for ConnectionResult to see what error codes might
        // be returned in onConnectionFailed.
        Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + result.getErrorCode());

        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {
            // An API requested for GoogleApiClient is not available. The device's current
            // configuration might not be supported with the requested API or a required component
            // may not be installed, such as the Android Wear application. You may need to use a
            // second GoogleApiClient to manage the application's optional APIs.
            Log.w(TAG, "API Unavailable.");
        } else if (mSignInProgress != STATE_IN_PROGRESS) {
            // We do not have an intent in progress so we should store the latest
            // error resolution intent for use when the sign in button is clicked.
            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {
                // STATE_SIGN_IN indicates the user already clicked the sign in button
                // so we should continue processing errors until the user is signed in
                // or they click cancel.
                resolveSignInError();
            }
        }

    }

    /* Starts an appropriate intent or dialog for user interaction to resolve
     * the current error preventing the user from being signed in.  This could
     * be a dialog allowing the user to select an account, an activity allowing
     * the user to consent to the permissions being requested by your app, a
     * setting to enable device networking, etc.
     */
    private void resolveSignInError() {
        if (mSignInIntent != null) {
            // We have an intent which will allow our user to sign in or
            // resolve an error.  For example if the user needs to
            // select an account to sign in with, or if they need to consent
            // to the permissions your app is requesting.

            try {
                // Send the pending intent that we stored on the most recent
                // OnConnectionFailed callback.  This will allow the user to
                // resolve the error currently preventing our connection to
                // Google Play services.
                mSignInProgress = STATE_IN_PROGRESS;
                mActivity.startIntentSenderForResult(mSignInIntent.getIntentSender(), RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {

                // The intent was canceled before it was sent.  Attempt to connect to
                // get an updated ConnectionResult.
                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
                Crashlytics.logException(e);
            }
        } else {
            // Google Play services wasn't able to provide an intent for some
            // error types, so we show the default Google Play services error
            // dialog which may still start an intent on our behalf if the
            // user can resolve the issue.

            if (PurpleSQ.isLoadingDialogVisible()) {
                PurpleSQ.dismissLoadingDialog();
            }

            createErrorDialog().show();
        }
    }

    private Dialog createErrorDialog() {
        if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
            return GooglePlayServicesUtil.getErrorDialog(
                    mSignInError,
                    mActivity,
                    RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            Log.e(TAG, "Google Play services resolution cancelled");
                            mSignInProgress = STATE_DEFAULT;
                        }
                    });
        } else {
            return new AlertDialog.Builder(mActivity)
                    .setMessage("Google Play services error")
                    .setPositiveButton("Close",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.e(TAG, "Google Play services error could not be " + "resolved: " + mSignInError);
                                    mSignInProgress = STATE_DEFAULT;
                                }
                            })
                    .create();
        }
    }
}

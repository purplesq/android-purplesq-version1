package com.purplesq.purplesq.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.UserRegisterTask;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, GenericAsyncTaskListener {

    private AutoCompleteTextView mEmailView, mPhoneNoView;
    private EditText mFirstNameView, mLastNameView, mPasswordView, mConfirmPasswordView;
    private View mProgressView;
    private ImageView mUserImageView;

    private UserRegisterTask mRegisterTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        setupToolBar();

        mFirstNameView = (EditText) findViewById(R.id.activity_signup_et_first_name);
        mLastNameView = (EditText) findViewById(R.id.activity_signup_et_last_name);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.activity_signup_autotv_email);
        mPhoneNoView = (AutoCompleteTextView) findViewById(R.id.activity_signup_autotv_phoneno);

        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.activity_signup_et_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.activity_signup_et_confirm_password);
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                int imeId = getResources().getInteger(R.integer.customImeActionId);
                if (id == imeId || id == EditorInfo.IME_ACTION_DONE) {
                    Log.i("Nish", "Sign Up clicked : " + id);
                    registerUser();
                    return true;
                }
                return false;
            }
        });


        mProgressView = findViewById(R.id.activity_signup_progress);
        mUserImageView = (ImageView) findViewById(R.id.activity_signup_iv_image);
    }

    private void populateAutoComplete() {
        getSupportLoaderManager().initLoader(0, null, this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_email_login_btn_ok) {
            registerUser();
            Log.i("Nish", "Done clicked.");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the errors are presented
     * and no actual register attempt is made.
     */
    public void registerUser() {
        if (mRegisterTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstNameView.getText().toString();
        String lastName = mLastNameView.getText().toString();
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mConfirmPasswordView.getText().toString();
        String phoneno = mPhoneNoView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid confirmPassword, if the user entered one.
        if (!TextUtils.isEmpty(confirmPassword) && (confirmPassword.compareTo(password) != 0)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_does_not_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
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
            showProgress(true);
            mRegisterTask = new UserRegisterTask(this, firstName, lastName, email, password, phoneno, this);
            mRegisterTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                this,
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY),
                ProfileQuery.PROJECTION,
                ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
                        + ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
                        + ContactsContract.Contacts.Data.MIMETYPE + "=? OR "
                        + ContactsContract.Contacts.Data.MIMETYPE + "=?",
                new String[]{
                        ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE,
                        ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
                },
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        List<String> phones = new ArrayList<>();
        Uri photo = Uri.EMPTY;
        String firstname = "";
        String lastname = "";
        String mime_type;
        while (cursor.moveToNext()) {
            mime_type = cursor.getString(ProfileQuery.MIME_TYPE);
            if (mime_type.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                emails.add(cursor.getString(ProfileQuery.ADDRESS));
            } else if (mime_type.equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                String phoneno = cursor.getString(ProfileQuery.NUMBER);
                if (phoneno.contains(" ")) {
                    phoneno = phoneno.replaceAll(" ", "");
                }
                if (phoneno.contains("-")) {
                    phoneno = phoneno.replaceAll("-", "");
                }
                if (phoneno.contains("+")) {
                    phoneno = phoneno.replaceAll("\\+", "");
                }
                if (phoneno.startsWith("91")) {
                    phoneno = phoneno.replaceFirst("91", "");
                }
                phones.add(phoneno);
            } else if (mime_type.equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                photo = Uri.parse(cursor.getString(ProfileQuery.PHOTO_URI));
            } else if (mime_type.equals(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)) {
                firstname = cursor.getString(ProfileQuery.GIVEN_NAME);
                lastname = cursor.getString(ProfileQuery.FAMILY_NAME);
            }
        }

        cursor.close();

        applyPhoto(photo);
        applyName(firstname, lastname);
        addEmailsToAutoComplete(emails);
        addPhonesToAutoComplete(phones);

        if (TextUtils.isEmpty(mFirstNameView.getText())) {
            mFirstNameView.requestFocus();
        } else if (TextUtils.isEmpty(mLastNameView.getText())) {
            mLastNameView.requestFocus();
        }
        if (TextUtils.isEmpty(mEmailView.getText())) {
            mEmailView.requestFocus();
        } else if (TextUtils.isEmpty(mPhoneNoView.getText())) {
            mPhoneNoView.requestFocus();
        } else {
            mPasswordView.requestFocus();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Photo.PHOTO_URI,
                ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME,
                ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME,
                ContactsContract.Contacts.Data.MIMETYPE
        };

        int ADDRESS = 0;
        int NUMBER = 1;
        int PHOTO_URI = 2;
        int GIVEN_NAME = 3;
        int FAMILY_NAME = 4;
        int MIME_TYPE = 5;
    }

    private void applyPhoto(Uri photo) {
        if (!TextUtils.isEmpty(photo.toString())) {
            mUserImageView.setImageURI(photo);
            mUserImageView.setVisibility(View.VISIBLE);
        }
    }

    private void applyName(String firstname, String lastname) {
        if (!TextUtils.isEmpty(firstname)) {
            mFirstNameView.setText(firstname);
        }
        if (!TextUtils.isEmpty(lastname)) {
            mLastNameView.setText(lastname);
        }
    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        if (emailAddressCollection.size() == 1) {
            mEmailView.setText(emailAddressCollection.get(0));
        } else {
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            ArrayAdapter<String> adapter
                    = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

            mEmailView.setAdapter(adapter);
        }
    }

    private void addPhonesToAutoComplete(List<String> phoneNumberCollection) {
        if (phoneNumberCollection.size() == 1) {
            mPhoneNoView.setText(phoneNumberCollection.get(0));
        } else {
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            ArrayAdapter<String> adapter
                    = new ArrayAdapter<>(SignUpActivity.this, android.R.layout.simple_dropdown_item_1line, phoneNumberCollection);

            mPhoneNoView.setAdapter(adapter);
        }
    }


    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        if (obj instanceof Boolean) {
            boolean success = (boolean) obj;

            mRegisterTask = null;
            showProgress(false);

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
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
        mRegisterTask = null;
        showProgress(false);
    }
}

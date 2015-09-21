package com.purplesq.purplesq.fragments;


import android.app.Activity;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.activities.LoginActivity;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.tasks.UserRegisterTask;
import com.purplesq.purplesq.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class SignUpFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private AutoCompleteTextView mEmailView, mPhoneNoView;
    private EditText mFirstNameView, mLastNameView, mPasswordView, mConfirmPasswordView;
    private ImageView mUserImageView;
    private AppCompatActivity mActivity;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance() {
        SignUpFragment fragment = new SignUpFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mFirstNameView = (EditText) rootView.findViewById(R.id.fragment_signup_et_firstname);
        mLastNameView = (EditText) rootView.findViewById(R.id.fragment_signup_et_lastname);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) rootView.findViewById(R.id.fragment_signup_autotv_email);
        mPhoneNoView = (AutoCompleteTextView) rootView.findViewById(R.id.fragment_signup_autotv_phoneno);

        mPasswordView = (EditText) rootView.findViewById(R.id.fragment_signup_et_password);
        mConfirmPasswordView = (EditText) rootView.findViewById(R.id.fragment_signup_et_confirm_password);
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                int imeId = getResources().getInteger(R.integer.customImeActionId);
                if (id == imeId || id == EditorInfo.IME_ACTION_DONE) {
                    registerUser();
                    return true;
                }
                return false;
            }
        });

        mUserImageView = (ImageView) rootView.findViewById(R.id.fragment_signup_imageView);
        rootView.findViewById(R.id.fragment_signup_btn_signup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        populateAutoComplete();

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (AppCompatActivity) activity;
    }

    private void populateAutoComplete() {
        mActivity.getSupportLoaderManager().initLoader(0, null, this);
    }


    /**
     * Register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the errors are presented
     * and no actual register attempt is made.
     */
    public void registerUser() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstNameView.getText().toString().trim();
        String lastName = mLastNameView.getText().toString().trim();
        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();
        String confirmPassword = mConfirmPasswordView.getText().toString().trim();
        String phoneno = mPhoneNoView.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(firstName)) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            focusView = mFirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            mLastNameView.setError(getString(R.string.error_field_required));
            focusView = mLastNameView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid confirmPassword, if the user entered one.
        if (TextUtils.isEmpty(confirmPassword) || !isPasswordValid(confirmPassword)) {
            mConfirmPasswordView.setError(getString(R.string.error_password_does_not_match));
            focusView = mConfirmPasswordView;
            cancel = true;
        }

        // Check for a valid confirmPassword, if the user entered one.
        if (confirmPassword.compareTo(password) != 0) {
            mConfirmPasswordView.setError(getString(R.string.error_password_does_not_match));
            focusView = mConfirmPasswordView;
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

        if (!Utils.isNumeric(phoneno)) {
            mPhoneNoView.setError(getString(R.string.error_invalid_phoneno));
            focusView = mPhoneNoView;
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
            new UserRegisterTask(mActivity, firstName, lastName, email, password, phoneno, (LoginActivity) mActivity).execute((Void) null);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 3;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(
                mActivity,
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
                String uri = cursor.getString(ProfileQuery.PHOTO_URI);
                if (!TextUtils.isEmpty(uri)) {
                    photo = Uri.parse(uri);
                }
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
                    = new ArrayAdapter<>(mActivity, android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

            mEmailView.setAdapter(adapter);
        }
    }

    private void addPhonesToAutoComplete(List<String> phoneNumberCollection) {
        if (phoneNumberCollection.size() == 1) {
            mPhoneNoView.setText(phoneNumberCollection.get(0));
        } else {
            //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
            ArrayAdapter<String> adapter
                    = new ArrayAdapter<>(mActivity, android.R.layout.simple_dropdown_item_1line, phoneNumberCollection);

            mPhoneNoView.setAdapter(adapter);
        }
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


}

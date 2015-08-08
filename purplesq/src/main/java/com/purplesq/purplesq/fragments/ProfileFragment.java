package com.purplesq.purplesq.fragments;


import android.app.DatePickerDialog.OnDateSetListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.datamanagers.UserProfileDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.GetUserProfileTask;
import com.purplesq.purplesq.tasks.ProfileUpdateTask;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.UserVo;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment implements GenericAsyncTaskListener {

    private TextView tvEditSave, tvName, tvEmail, tvPhone, tvInsitute, tvDob, tvGender;
    private EditText etFName, etLName, etPhone, etInsitute, etDob;
    private Button btnDob;
    private LinearLayout layoutEdit, layoutShow;
    private RadioButton rbtnMale, rbtnFemale;
    private RadioGroup radioGroup;
    private UserVo userVo;
    private String mToken;

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AuthVo authVo = AuthDataManager.getAuthData(getActivity());
        userVo = authVo.getUser();
        mToken = authVo.getToken();

        if (!TextUtils.isEmpty(mToken)) {
            PurpleSQ.showLoadingDialog(getActivity());
            new GetUserProfileTask(mToken, this).execute((Void) null);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        if (userVo == null) {
            AuthVo authVo = AuthDataManager.getAuthData(getActivity());
            userVo = authVo.getUser();
            mToken = authVo.getToken();

            if (!TextUtils.isEmpty(mToken)) {
                PurpleSQ.showLoadingDialog(getActivity());
                new GetUserProfileTask(mToken, this).execute((Void) null);
            }
        }

        tvEditSave = (TextView) rootView.findViewById(R.id.fragment_profile_tv_editsave);

        tvName = (TextView) rootView.findViewById(R.id.fragment_profile_tv_name);
        tvEmail = (TextView) rootView.findViewById(R.id.fragment_profile_tv_email);
        tvPhone = (TextView) rootView.findViewById(R.id.fragment_profile_tv_phone);
        tvInsitute = (TextView) rootView.findViewById(R.id.fragment_profile_tv_institute);
        tvDob = (TextView) rootView.findViewById(R.id.fragment_profile_tv_dob);
        tvGender = (TextView) rootView.findViewById(R.id.fragment_profile_tv_gender);

        etFName = (EditText) rootView.findViewById(R.id.fragment_profile_et_fname);
        etLName = (EditText) rootView.findViewById(R.id.fragment_profile_et_lname);
        etPhone = (EditText) rootView.findViewById(R.id.fragment_profile_et_phone);
        etInsitute = (EditText) rootView.findViewById(R.id.fragment_profile_et_institute);
        etDob = (EditText) rootView.findViewById(R.id.fragment_profile_et_dob);

        btnDob = (Button) rootView.findViewById(R.id.fragment_profile_btn_dob);

        radioGroup = (RadioGroup) rootView.findViewById(R.id.fragment_profile_rgroup);
        rbtnMale = (RadioButton) rootView.findViewById(R.id.fragment_profile_rbtn_male);
        rbtnFemale = (RadioButton) rootView.findViewById(R.id.fragment_profile_rbtn_female);

        layoutShow = (LinearLayout) rootView.findViewById(R.id.fragment_profile_layout_show);
        layoutEdit = (LinearLayout) rootView.findViewById(R.id.fragment_profile_layout_edit);


        layoutShow.setVisibility(View.VISIBLE);
        layoutEdit.setVisibility(View.GONE);

        tvEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layoutShow.getVisibility() == View.VISIBLE) {
                    performEditClickAction();
                } else {
                    performSaveClickAction();
                }
            }
        });

        btnDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.fragment_profile_rbtn_male) {
                    userVo.setGender("Male");
                } else if (checkedId == R.id.fragment_profile_rbtn_female) {
                    userVo.setGender("Female");
                }
            }
        });


        updateShowCard();

        return rootView;
    }

    private void updateShowCard() {
        tvName.setText(userVo.getFirstName() + " " + userVo.getLastName());
        tvEmail.setText(userVo.getEmail());

        if (!TextUtils.isEmpty(userVo.getPhone())) {
            tvPhone.setText(userVo.getPhone());
            tvPhone.setVisibility(View.VISIBLE);
        } else {
            tvPhone.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(userVo.getInstitute())) {
            tvInsitute.setText(userVo.getInstitute());
            tvInsitute.setVisibility(View.VISIBLE);
        } else {
            tvInsitute.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(userVo.getGender())) {
            tvGender.setText(userVo.getGender());
            tvGender.setVisibility(View.VISIBLE);
        } else {
            tvGender.setVisibility(View.GONE);
        }

        if (userVo.getDob() > 0) {
            try {
                Date date = new Date(userVo.getDob());

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                tvDob.setText(sdf.format(date));
                tvDob.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            tvDob.setVisibility(View.GONE);
        }
    }

    private void showDatePicker() {

        DatePickerDialogFragment datePickerDialogFragment = new DatePickerDialogFragment();

        Calendar calender = Calendar.getInstance();
        if (userVo.getDob() > 0) {
            calender.setTimeInMillis(userVo.getDob());
        }

        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        datePickerDialogFragment.setArguments(args);

        datePickerDialogFragment.setCallBack(new OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                Calendar cal = Calendar.getInstance();
                cal.set(year, month, day);
                userVo.setDob(cal.getTimeInMillis());
                if (userVo.getDob() > 0) {
                    try {
                        Date date = new Date(userVo.getDob());

                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                        etDob.setText(sdf.format(date));
                        tvDob.setText(sdf.format(date));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    etDob.setText("");
                    tvDob.setVisibility(View.GONE);
                }
            }
        });

        datePickerDialogFragment.show(getActivity().getSupportFragmentManager(), "Date Picker");
    }

    private void performSaveClickAction() {
        tvEditSave.setText("EDIT");

        boolean isValidData = true;

        if (!TextUtils.isEmpty(etFName.getText())) {
            userVo.setFirstName(etFName.getText().toString().trim());
        } else {
            etFName.setError(getString(R.string.error_field_required));
            etFName.requestFocus();
            isValidData = false;
        }

        if (!TextUtils.isEmpty(etLName.getText())) {
            userVo.setLastName(etLName.getText().toString().trim());
        } else {
            etLName.setError(getString(R.string.error_field_required));
            etLName.requestFocus();
            isValidData = false;
        }

        if (!TextUtils.isEmpty(etPhone.getText())) {
            userVo.setPhone(etPhone.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(etInsitute.getText())) {
            userVo.setInstitute(etInsitute.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(etFName.getText())) {
            userVo.setFirstName(etFName.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(etFName.getText())) {
            userVo.setFirstName(etFName.getText().toString().trim());
        }

        if (radioGroup.getCheckedRadioButtonId() == R.id.fragment_profile_rbtn_male) {
            userVo.setGender("Male");
        } else if (radioGroup.getCheckedRadioButtonId() == R.id.fragment_profile_rbtn_female) {
            userVo.setGender("Female");
        }

        if (isValidData) {
            PurpleSQ.showLoadingDialog(getActivity());
            new ProfileUpdateTask(mToken, userVo, ProfileFragment.this).execute((Void) null);

            layoutShow.setVisibility(View.VISIBLE);
            layoutEdit.setVisibility(View.GONE);

        }

    }

    private void performEditClickAction() {

        tvEditSave.setText("SAVE");

        etFName.setText(userVo.getFirstName());
        etLName.setText(userVo.getLastName());

        if (!TextUtils.isEmpty(userVo.getPhone())) {
            etPhone.setText(userVo.getPhone());
        } else {
            etPhone.setText("");
        }

        if (!TextUtils.isEmpty(userVo.getInstitute())) {
            etInsitute.setText(userVo.getInstitute());
        } else {
            etInsitute.setText("");
        }

        if (!TextUtils.isEmpty(userVo.getGender())) {
            if (userVo.getGender().equalsIgnoreCase("Male")) {
                rbtnMale.setChecked(true);
            } else if (userVo.getGender().equalsIgnoreCase("Female")) {
                rbtnFemale.setChecked(true);
            } else {
                rbtnMale.setChecked(false);
                rbtnFemale.setChecked(false);
            }
        } else {
            rbtnMale.setChecked(false);
            rbtnFemale.setChecked(false);
        }

        if (userVo.getDob() > 0) {
            try {
                Date date = new Date(userVo.getDob());

                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
                etDob.setText(sdf.format(date));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            etDob.setText("");
        }

        layoutShow.setVisibility(View.GONE);
        layoutEdit.setVisibility(View.VISIBLE);

    }

    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        PurpleSQ.dismissLoadingDialog();

        if (obj != null) {
            if (obj instanceof JSONObject) {
                try {
                    JSONObject jsonResponse = (JSONObject) obj;

                    if (jsonResponse.has("_id")) {

                        if (userVo.getId().equalsIgnoreCase(jsonResponse.getString("_id"))) {
                            Gson gson = new Gson();
                            UserVo user = gson.fromJson(jsonResponse.toString(), UserVo.class);
                            if (user != null) {
                                userVo = user;
                                AuthVo auth = new AuthVo();
                                auth.setToken(mToken);
                                auth.setUser(userVo);
                                UserProfileDataManager.insertOrUpdateUserProfile(getActivity(), userVo);
                                AuthDataManager.insertOrUpdateAuthData(getActivity(), auth);
                                updateShowCard();
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {
        PurpleSQ.dismissLoadingDialog();

        if (obj instanceof ErrorVo) {
            ErrorVo errorVo = (ErrorVo) obj;
            Log.i("Nish", "Response failed Code : " + errorVo.getCode());
            Log.i("Nish", "Response failed Message : " + errorVo.getMessage());
            Log.i("Nish", "Response failed Body : " + errorVo.getBody());

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("error_dialog");
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
        PurpleSQ.dismissLoadingDialog();
    }
}

package com.purplesq.purplesq.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.datamanagers.UserProfileDataManager;
import com.purplesq.purplesq.vos.UserVo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView tvEditSave, tvName, tvEmail, tvPhone, tvInsitute, tvDob, tvGender;
    private EditText etFName, etLName, etPhone, etInsitute, etDob;
    private LinearLayout layoutEdit, layoutShow;
    private RadioButton rbtnMale, rbtnFemale;
    private UserVo userVo;

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

        userVo = UserProfileDataManager.getUserProfile(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        if (userVo == null) {
            userVo = UserProfileDataManager.getUserProfile(getActivity());
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


        return rootView;
    }

    private void performSaveClickAction() {
        tvEditSave.setText("EDIT");

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
                rbtnMale.setSelected(true);
            } else if (userVo.getGender().equalsIgnoreCase("Female")) {
                rbtnFemale.setSelected(true);
            } else {
                rbtnMale.setSelected(false);
                rbtnFemale.setSelected(false);
            }
        } else {
            rbtnMale.setSelected(false);
            rbtnFemale.setSelected(false);
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

    private void performEditClickAction() {
        tvEditSave.setText("SAVE");

        if (!TextUtils.isEmpty(etFName.getText())) {
            userVo.setFirstName(etFName.getText().toString().trim());
        }
        if (!TextUtils.isEmpty(etLName.getText())) {
            userVo.setLastName(etLName.getText().toString().trim());
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

    }


}

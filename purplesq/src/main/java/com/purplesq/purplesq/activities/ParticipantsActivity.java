package com.purplesq.purplesq.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.fragments.ErrorDialogFragment;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.RegisterParticipantsTask;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.utils.Utils;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.ParticipantVo;
import com.purplesq.purplesq.vos.TransactionVo;
import com.purplesq.purplesq.vos.UserVo;

import java.util.ArrayList;

public class ParticipantsActivity extends AppCompatActivity implements GenericAsyncTaskListener {

    private LinearLayout mParticipantsLayout;
    private AppCompatActivity mActivity;
    private ArrayList<ParticipantVo> mParticipantList;
    private AuthVo authVo;
    private boolean inEditMode = false;
    private String mEventId = "";
    private RegisterParticipantsTask mRegisterParticipantsTask;
    private int position = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);
        mActivity = this;
        setupToolBar();

        if (getIntent().hasExtra(PSQConsts.EXTRAS_EVENT_ID)) {
            mEventId = getIntent().getStringExtra(PSQConsts.EXTRAS_EVENT_ID);
        }

        if (getIntent().hasExtra(PSQConsts.EXTRAS_EVENT_POSITION)) {
            position = getIntent().getIntExtra(PSQConsts.EXTRAS_EVENT_POSITION, -1);
        }

        authVo = AuthDataManager.getAuthData(this);

        if (authVo == null || authVo.getUser() == null || TextUtils.isEmpty(authVo.getUser().getId())) {
            Intent i = new Intent(this, LoginActivity.class);
            if (!TextUtils.isEmpty(mEventId)) {
                i.putExtra(PSQConsts.EXTRAS_EVENT_ID, mEventId);
                i.putExtra(PSQConsts.EXTRAS_EVENT_POSITION, position);
            }
            startActivity(i);
            finish();
        } else {
            UserVo userVo = authVo.getUser();
            mParticipantList = new ArrayList<>();

            mParticipantsLayout = (LinearLayout) findViewById(R.id.activity_participants_layout_participants);

            ParticipantVo participantVo = new ParticipantVo(0);
            participantVo.setFirstname(userVo.getFirstName());
            participantVo.setLastname(userVo.getLastName());
            participantVo.setEmail(userVo.getEmail());
            participantVo.setPhone(userVo.getPhone());
            participantVo.setInstitute(userVo.getInstitute());
            mParticipantList.add(participantVo);

            populateParticipants();

            findViewById(R.id.activity_participants_tv_add_participants).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inEditMode = true;
                    ParticipantVo participantVo = new ParticipantVo(mParticipantList.size());
                    mParticipantList.add(participantVo);
                    populateParticipants();
                }
            });

            findViewById(R.id.activity_participants_btn_proceed).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    registerParticipants();
                }
            });
        }
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


    private void populateParticipants() {

        mParticipantsLayout.removeAllViews();

        for (int i = 0; i < mParticipantList.size(); i++) {

            final View participantView = mActivity.getLayoutInflater().inflate(R.layout.item_participants, null);

            final int position = i;

            final CardView cardView = (CardView) participantView.findViewById(R.id.item_participants_cardview);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(0.0f);
            }

            final RelativeLayout editLayout = (RelativeLayout) participantView.findViewById(R.id.item_participants_layout_edit);
            final RelativeLayout savedLayout = (RelativeLayout) participantView.findViewById(R.id.item_participants_layout_saved);

            final EditText etFirstName = (EditText) participantView.findViewById(R.id.item_participants_et_firstname);
            final EditText etLastName = (EditText) participantView.findViewById(R.id.item_participants_et_lastname);
            final EditText etEmail = (EditText) participantView.findViewById(R.id.item_participants_et_email);
            final EditText etPhone = (EditText) participantView.findViewById(R.id.item_participants_et_phone);
            final EditText etInstitute = (EditText) participantView.findViewById(R.id.item_participants_et_insitute);
            final TextView tvNo = (TextView) participantView.findViewById(R.id.item_participants_tv_no);
            final TextView tvSave = (TextView) participantView.findViewById(R.id.item_participants_tv_save);
            final TextView tvCancel = (TextView) participantView.findViewById(R.id.item_participants_tv_cancel);

            final TextView tvNumber = (TextView) participantView.findViewById(R.id.item_participants_tv_number);
            final TextView tvName = (TextView) participantView.findViewById(R.id.item_participants_tv_name);
            final TextView tvInstitute = (TextView) participantView.findViewById(R.id.item_participants_tv_insitute);
            final TextView tvEdit = (TextView) participantView.findViewById(R.id.item_participants_tv_edit);
            final TextView tvDelete = (TextView) participantView.findViewById(R.id.item_participants_tv_delete);


            if (position == 0 && isParticipantEmpty(position)) {
                editLayout.setVisibility(View.VISIBLE);
                savedLayout.setVisibility(View.GONE);
                inEditMode = true;
            } else {
                tvNo.setText((position + 1) + "");
                String fname = mParticipantList.get(position).getFirstname();
                String lname = mParticipantList.get(position).getLastname();
                String email = mParticipantList.get(position).getEmail();
                String phone = mParticipantList.get(position).getPhone();
                String institute = mParticipantList.get(position).getInstitute();

                etFirstName.setText(fname);
                etLastName.setText(lname);
                etEmail.setText(email);
                etPhone.setText(phone);
                etInstitute.setText(institute);

                if (!TextUtils.isEmpty(mParticipantList.get(position).getEmail())) {
                    if (mParticipantList.get(position).getEmail().contentEquals(authVo.getUser().getEmail())) {
                        tvName.setText(fname + " " + lname + " (You)");
                    } else {
                        tvName.setText(fname + " " + lname);
                    }
                } else {
                    tvName.setText("");
                    inEditMode = true;
                }

                tvInstitute.setText(institute);
                tvNumber.setText((position + 1) + "");

                if (position == 0) {
                    if (mParticipantList.get(position).getEmail().contentEquals(authVo.getUser().getEmail())) {
                        if (!TextUtils.isEmpty(mParticipantList.get(position).getInstitute()) && !TextUtils.isEmpty(mParticipantList.get(position).getPhone())) {
                            inEditMode = false;
                        } else {
                            inEditMode = true;
                        }
                    }
                }

                if (!inEditMode) {
                    editLayout.setVisibility(View.GONE);
                    savedLayout.setVisibility(View.VISIBLE);
                } else {
                    editLayout.setVisibility(View.VISIBLE);
                    savedLayout.setVisibility(View.GONE);
                    etFirstName.requestFocus();
                }
            }

            tvSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSavedClicked(participantView, position);
                }
            });

            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onDeleteClicked(position);
                }
            });

            tvEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onEditClicked(participantView);
                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCancelClicked(participantView, position);
                }
            });

            savedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSavedLayoutClicked(participantView, position);
                }
            });

            checkActionButtonsVisibility();
            mParticipantsLayout.addView(participantView);
        }

        View view = new View(mActivity);
        view.setMinimumHeight(10);
        view.setBackgroundResource(R.color.white);
        mParticipantsLayout.addView(view);
    }

    private void onSavedClicked(final View participantView, int position) {
        boolean isDataCorrect = true;

        RelativeLayout editLayout = (RelativeLayout) participantView.findViewById(R.id.item_participants_layout_edit);
        RelativeLayout savedLayout = (RelativeLayout) participantView.findViewById(R.id.item_participants_layout_saved);

        EditText etFirstName = (EditText) participantView.findViewById(R.id.item_participants_et_firstname);
        EditText etLastName = (EditText) participantView.findViewById(R.id.item_participants_et_lastname);
        EditText etEmail = (EditText) participantView.findViewById(R.id.item_participants_et_email);
        EditText etPhone = (EditText) participantView.findViewById(R.id.item_participants_et_phone);
        EditText etInstitute = (EditText) participantView.findViewById(R.id.item_participants_et_insitute);
        TextView tvName = (TextView) participantView.findViewById(R.id.item_participants_tv_name);
        TextView tvInstitute = (TextView) participantView.findViewById(R.id.item_participants_tv_insitute);

        String fname = etFirstName.getText().toString().trim();
        String lname = etLastName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String institute = etInstitute.getText().toString().trim();

        if (!TextUtils.isEmpty(fname)) {
            mParticipantList.get(position).setFirstname(fname);
        } else {
            isDataCorrect = false;
            etFirstName.setError(mActivity.getString(R.string.error_field_required));
            etFirstName.requestFocus();
        }

        if (!TextUtils.isEmpty(lname)) {
            mParticipantList.get(position).setLastname(lname);
        } else {
            isDataCorrect = false;
            etLastName.setError(mActivity.getString(R.string.error_field_required));
            etLastName.requestFocus();
        }

        if (!TextUtils.isEmpty(email) && Utils.isValidEmailAddress(email)) {
            mParticipantList.get(position).setEmail(email);
        } else {
            isDataCorrect = false;
            if (TextUtils.isEmpty(email))
                etEmail.setError(mActivity.getString(R.string.error_field_required));
            else
                etEmail.setError(mActivity.getString(R.string.error_invalid_email));
            etEmail.requestFocus();
        }

        if (!TextUtils.isEmpty(phone) && (phone.length() > 9) && (phone.length() < 14) && Utils.isNumeric(phone)) {
            mParticipantList.get(position).setPhone(phone);
        } else {
            isDataCorrect = false;
            if (TextUtils.isEmpty(phone))
                etPhone.setError(mActivity.getString(R.string.error_field_required));
            else
                etPhone.setError(mActivity.getString(R.string.error_invalid_phoneno));
            etPhone.requestFocus();
        }

        if (!TextUtils.isEmpty(institute)) {
            mParticipantList.get(position).setInstitute(institute);
        } else {
            isDataCorrect = false;
            etInstitute.setError(mActivity.getString(R.string.error_field_required));
            etInstitute.requestFocus();
        }

        if (isDataCorrect) {
            tvName.setText(fname + " " + lname);
            tvInstitute.setText(institute);

            editLayout.setVisibility(View.GONE);
            savedLayout.setVisibility(View.VISIBLE);

            inEditMode = false;
        }


        checkActionButtonsVisibility();
    }

    private void onCancelClicked(final View participantView, int position) {
        RelativeLayout editLayout = (RelativeLayout) participantView.findViewById(R.id.item_participants_layout_edit);
        RelativeLayout savedLayout = (RelativeLayout) participantView.findViewById(R.id.item_participants_layout_saved);

        if (isParticipantEmpty(position)) {
            mParticipantList.remove(position);

            if (mParticipantList.isEmpty()) {
                ParticipantVo participantVo = new ParticipantVo(mParticipantList.size());
                mParticipantList.add(participantVo);
                inEditMode = true;
            }

            populateParticipants();
        } else {
            editLayout.setVisibility(View.GONE);
            savedLayout.setVisibility(View.VISIBLE);
            inEditMode = false;
        }

        checkActionButtonsVisibility();
    }

    private void onEditClicked(View participantView) {
        RelativeLayout editLayout = (RelativeLayout) participantView.findViewById(R.id.item_participants_layout_edit);
        RelativeLayout savedLayout = (RelativeLayout) participantView.findViewById(R.id.item_participants_layout_saved);

        editLayout.setVisibility(View.VISIBLE);
        savedLayout.setVisibility(View.GONE);
        inEditMode = true;
        checkActionButtonsVisibility();
    }

    private void onDeleteClicked(int position) {
        mParticipantList.remove(position);
        if (mParticipantList.isEmpty()) {
            ParticipantVo participantVo = new ParticipantVo(mParticipantList.size());
            mParticipantList.add(participantVo);
            inEditMode = true;
        }
        populateParticipants();
        checkActionButtonsVisibility();
    }

    private void onSavedLayoutClicked(View participantView, int position) {
        LinearLayout editDeletelayout = (LinearLayout) participantView.findViewById(R.id.item_participants_layout_edit_delete);
        final CardView cardView = (CardView) participantView.findViewById(R.id.item_participants_cardview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cardView.setElevation(0.0f);
        }

        if (editDeletelayout.getVisibility() == View.VISIBLE) {
            editDeletelayout.setVisibility(View.GONE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(0.0f);
            }

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setElevation(5.0f);
            }

            toggleParticipantsActions(position);
            editDeletelayout.setVisibility(View.VISIBLE);
        }

        checkActionButtonsVisibility();
    }

    private boolean isParticipantEmpty(int position) {
        boolean isEmpty = true;
        if (!TextUtils.isEmpty(mParticipantList.get(position).getFirstname())) {
            isEmpty = false;
        }

        if (!TextUtils.isEmpty(mParticipantList.get(position).getLastname())) {
            isEmpty = false;
        }
        return isEmpty;
    }

    private void checkActionButtonsVisibility() {
        if (inEditMode) {
            findViewById(R.id.activity_participants_tv_add_participants).setVisibility(View.GONE);
            findViewById(R.id.activity_participants_btn_proceed).setVisibility(View.GONE);
        } else {
            findViewById(R.id.activity_participants_tv_add_participants).setVisibility(View.VISIBLE);
            findViewById(R.id.activity_participants_btn_proceed).setVisibility(View.VISIBLE);
        }
    }

    private void toggleParticipantsActions(int clickedPosition) {
        int participants = mParticipantsLayout.getChildCount() - 1;
        for (int i = 0; i < participants; i++) {
            if (i != clickedPosition) {
                final View participantView = mParticipantsLayout.getChildAt(i);
                LinearLayout editDeletelayout = (LinearLayout) participantView.findViewById(R.id.item_participants_layout_edit_delete);
                if (editDeletelayout.getVisibility() == View.VISIBLE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        participantView.findViewById(R.id.item_participants_cardview).setElevation(0.0f);
                    }
                    editDeletelayout.setVisibility(View.GONE);
                }
            }
        }
    }


    private void registerParticipants() {
        if (mRegisterParticipantsTask != null) {
            return;
        }

        if (!mParticipantList.isEmpty()) {

            PurpleSQ.showLoadingDialog(ParticipantsActivity.this);
            mRegisterParticipantsTask = new RegisterParticipantsTask(mEventId, authVo.getToken(), mParticipantList, this);
            mRegisterParticipantsTask.execute((Void) null);
        }
    }

    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        mRegisterParticipantsTask = null;
        if (obj != null && obj instanceof TransactionVo) {
            TransactionVo transactionVo = (TransactionVo) obj;
            try {
                ArrayList<String> participantList = new ArrayList<>();
                ArrayList<String> participantIntitute = new ArrayList<>();
                for (ParticipantVo participantVo : mParticipantList) {
                    participantList.add(participantVo.getFirstname() + " " + participantVo.getLastname());
                    participantIntitute.add(participantVo.getInstitute());
                }

                Intent intent = new Intent(mActivity, PaymentActivity.class);
                intent.putExtra(PSQConsts.EXTRAS_TRANSACTION, transactionVo.toString());
                intent.putExtra(PSQConsts.EXTRAS_EVENT_POSITION, position);
                intent.putExtra(PSQConsts.EXTRAS_EVENT_ID, mEventId);
                intent.putParcelableArrayListExtra(PSQConsts.EXTRAS_PARTICIPANTS, mParticipantList);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }
        }

        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }

        mRegisterParticipantsTask = null;
        if (obj instanceof ErrorVo) {
            ErrorVo errorVo = (ErrorVo) obj;

            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag(PSQConsts.DIALOG_FRAGMENT_ERROR);
            if (prev != null) {
                ft.remove(prev);
            }

            ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(errorVo);
            errorDialogFragment.show(ft, PSQConsts.DIALOG_FRAGMENT_ERROR);
        }
    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {
        mRegisterParticipantsTask = null;
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }

}

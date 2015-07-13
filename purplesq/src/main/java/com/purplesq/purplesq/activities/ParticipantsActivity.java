package com.purplesq.purplesq.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.tasks.RegisterParticipantsTask;
import com.purplesq.purplesq.vos.ParticipantVo;
import com.purplesq.purplesq.vos.TransactionVo;
import com.purplesq.purplesq.vos.UserVo;

import java.util.ArrayList;

public class ParticipantsActivity extends Activity implements GenericAsyncTaskListener {

    private LinearLayout mParticipantsLayout;
    private Activity mActivity;
    private ArrayList<ParticipantVo> mParticipantList;
    private UserVo userVo;
    private boolean isUserEdited = false;
    private boolean isUserDeleted = false;
    private String mEventId = "";
    private String mToken = "";
    private RegisterParticipantsTask mRegisterParticipantsTask;
    private int position = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);
        mActivity = this;
        setupActionBar();

        if (getIntent().hasExtra("event-id")) {
            mEventId = getIntent().getStringExtra("event-id");
        }

        if (getIntent().hasExtra("event-position")) {
            position = getIntent().getIntExtra("event-position", -1);
        }

        SharedPreferences sharedPreferences = getSharedPreferences("purple-squirrel-settings", MODE_PRIVATE);

        String userId = sharedPreferences.getString("user-id", "");
        if (TextUtils.isEmpty(userId)) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            userVo = new UserVo();
            userVo.setId(userId);
            userVo.setFirstName(sharedPreferences.getString("user-fname", ""));
            userVo.setLastName(sharedPreferences.getString("user-lname", ""));
            userVo.setEmail(sharedPreferences.getString("user-email", ""));
            userVo.setPhone(sharedPreferences.getString("user-phone", ""));
            mToken = sharedPreferences.getString("token", "");

            mParticipantList = new ArrayList<>();

            mParticipantsLayout = (LinearLayout) findViewById(R.id.activity_participants_layout_participants);
            TextView mTvAddMore = (TextView) findViewById(R.id.activity_participants_tv_add_participants);

            ParticipantVo participantVo = new ParticipantVo(mParticipantList.size());
            participantVo.setName(userVo.getFirstName() + " " + userVo.getLastName());
            participantVo.setEmail(userVo.getEmail());
            participantVo.setPhone(userVo.getPhone());
            participantVo.setInstitute(userVo.getInstitute());
            participantVo.setPosition(0);
            mParticipantList.add(participantVo);
            populateUser(0);

            mTvAddMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isUserEdited = true;
                    if (getParticipantData(true)) {
                        ParticipantVo participantVo = new ParticipantVo(mParticipantList.size());
                        mParticipantList.add(participantVo);
                        populateParticipants();
                    } else {
                        Toast.makeText(mActivity, "Please insert all data", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void populateUser(final int position) {

        View participantView = mActivity.getLayoutInflater().inflate(R.layout.item_participants, null);

        TextView tvNo = (TextView) participantView.findViewById(R.id.item_participants_tv_no);
        tvNo.setText(1 + "");

        final EditText etName = (EditText) participantView.findViewById(R.id.item_participants_et_name);
        final EditText etEmail = (EditText) participantView.findViewById(R.id.item_participants_et_email);
        final EditText etPhone = (EditText) participantView.findViewById(R.id.item_participants_et_phone);
        final EditText etInstitute = (EditText) participantView.findViewById(R.id.item_participants_et_insitute);
        final Button btnClear = (Button) participantView.findViewById(R.id.item_participants_btn_remove);

        etName.setText("You (" + mParticipantList.get(0).getName() + ")");
        etName.setEnabled(false);
        etEmail.setText(mParticipantList.get(0).getEmail());
        etEmail.setVisibility(View.GONE);
        if (TextUtils.isEmpty(mParticipantList.get(0).getPhone())) {
            etPhone.setVisibility(View.VISIBLE);
        } else {
            etPhone.setText(mParticipantList.get(0).getPhone());
            etPhone.setVisibility(View.GONE);
        }
        etInstitute.setText(mParticipantList.get(0).getInstitute());

        if (isUserEdited) {
            btnClear.setBackgroundResource(R.drawable.ic_content_clear);
            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteParticipant(position);
                }
            });

        } else {
            btnClear.setBackgroundResource(R.drawable.ic_content_create);
            btnClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etName.setEnabled(true);
                    etEmail.setVisibility(View.VISIBLE);
                    etPhone.setVisibility(View.VISIBLE);
                    etInstitute.setVisibility(View.VISIBLE);
                    btnClear.setBackgroundResource(R.drawable.ic_content_save);

                    btnClear.setOnClickListener(null);

                    btnClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isUserEdited = true;
                            mParticipantList.get(0).setPhone(etPhone.getText().toString().trim());
                            mParticipantList.get(0).setName(etName.getText().toString().trim());
                            mParticipantList.get(0).setEmail(etEmail.getText().toString().trim());
                            mParticipantList.get(0).setPhone(etPhone.getText().toString().trim());
                            mParticipantList.get(0).setInstitute(etInstitute.getText().toString().trim());
                            populateParticipants();
                        }
                    });
                }
            });
        }

        mParticipantsLayout.addView(participantView);

    }

    private void deleteParticipant(int position) {
        getParticipantData(false);
        if (position == 0) {
            if (!TextUtils.isEmpty(mParticipantList.get(0).getEmail())) {
                if (mParticipantList.get(0).getEmail().equalsIgnoreCase(userVo.getEmail())) {
                    isUserDeleted = true;
                }
            }
        }
        mParticipantList.remove(position);
        populateParticipants();

    }

    private void populateParticipants() {

        mParticipantsLayout.removeAllViews();

        for (int i = 0; i < mParticipantList.size(); i++) {
            if (i == 0 && !isUserDeleted) {
                populateUser(i);
            } else {
                final int position = i;

                View participantView = mActivity.getLayoutInflater().inflate(R.layout.item_participants, null);

                TextView tvNo = (TextView) participantView.findViewById(R.id.item_participants_tv_no);
                tvNo.setText(i + 1 + "");

                EditText etName = (EditText) participantView.findViewById(R.id.item_participants_et_name);
                EditText etEmail = (EditText) participantView.findViewById(R.id.item_participants_et_email);
                EditText etPhone = (EditText) participantView.findViewById(R.id.item_participants_et_phone);
                EditText etInstitute = (EditText) participantView.findViewById(R.id.item_participants_et_insitute);
                Button btnClear = (Button) participantView.findViewById(R.id.item_participants_btn_remove);

                etName.setText(mParticipantList.get(i).getName());
                etEmail.setText(mParticipantList.get(i).getEmail());
                etPhone.setText(mParticipantList.get(i).getPhone());
                etInstitute.setText(mParticipantList.get(0).getInstitute());

                btnClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteParticipant(position);
                    }
                });


                if (mParticipantList.get(i).isFixed()) {
                    etName.setEnabled(false);
                    etEmail.setEnabled(false);
                    etPhone.setEnabled(false);
                    etInstitute.setEnabled(false);
                } else {
                    etName.setEnabled(true);
                    etEmail.setEnabled(true);
                    etPhone.setEnabled(true);
                    etInstitute.setEnabled(true);
                }

                mParticipantsLayout.addView(participantView);
            }
        }
    }

    private boolean getParticipantData(boolean isAdded) {
        boolean isValidInput = true;
        if (!mParticipantList.isEmpty()) {
            int childs = mParticipantsLayout.getChildCount();
            View participantView = mParticipantsLayout.getChildAt(childs - 1);

            EditText etName = (EditText) participantView.findViewById(R.id.item_participants_et_name);
            EditText etEmail = (EditText) participantView.findViewById(R.id.item_participants_et_email);
            EditText etPhone = (EditText) participantView.findViewById(R.id.item_participants_et_phone);
            EditText etInstitute = (EditText) participantView.findViewById(R.id.item_participants_et_insitute);

            if (isAdded) {
                if (TextUtils.isEmpty(etName.getText()))
                    isValidInput = false;
                if (TextUtils.isEmpty(etEmail.getText()))
                    isValidInput = false;
                if (TextUtils.isEmpty(etPhone.getText()))
                    isValidInput = false;
                if (TextUtils.isEmpty(etInstitute.getText()))
                    isValidInput = false;
            }

            if (isValidInput) {
                if (mParticipantList.size() > 1) {
                    mParticipantList.get(mParticipantList.size() - 1).setName(etName.getText().toString());
                    mParticipantList.get(mParticipantList.size() - 1).setEmail(etEmail.getText().toString());
                }

                mParticipantList.get(mParticipantList.size() - 1).setPhone(etPhone.getText().toString());
                mParticipantList.get(mParticipantList.size() - 1).setInstitute(etInstitute.getText().toString());

                if (isAdded) {
                    mParticipantList.get(mParticipantList.size() - 1).setIsFixed(true);
                }
            }

        }

        return isValidInput;
    }


    /**
     * Set up the {@link android.app.ActionBar}.
     */
    private void setupActionBar() {
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_participants, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_participants_btn_ok) {
            registerParticipants();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registerParticipants() {
        if (mRegisterParticipantsTask != null) {
            return;
        }
        getParticipantData(false);
        if (!mParticipantList.isEmpty()) {
            mRegisterParticipantsTask = new RegisterParticipantsTask(mEventId, mToken, mParticipantList, this);
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
                for (ParticipantVo participantVo : mParticipantList) {
                    participantList.add(participantVo.getName());
                }

                Intent intent = new Intent(mActivity, PaymentActivity.class);
                intent.putExtra("transaction", transactionVo.toString());
                intent.putExtra("event-position", position);
                intent.putStringArrayListExtra("participants-name", participantList);
                startActivity(intent);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void genericAsyncTaskOnError(Object obj) {
        mRegisterParticipantsTask = null;
    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {
        mRegisterParticipantsTask = null;
    }
}

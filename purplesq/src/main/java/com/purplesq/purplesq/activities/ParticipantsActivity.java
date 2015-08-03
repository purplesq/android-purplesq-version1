package com.purplesq.purplesq.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.adapters.RecyclerViewParticipantsAdapter;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.interfces.RecyclerViewItemClickListener;
import com.purplesq.purplesq.tasks.RegisterParticipantsTask;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.ParticipantVo;
import com.purplesq.purplesq.vos.TransactionVo;
import com.purplesq.purplesq.vos.UserVo;

import java.util.ArrayList;

public class ParticipantsActivity extends AppCompatActivity implements RecyclerViewItemClickListener, GenericAsyncTaskListener {

    private LinearLayout mParticipantsLayout;
    private Activity mActivity;
    private ArrayList<ParticipantVo> mParticipantList;
    private AuthVo authVo;
    private boolean isUserSaved = false;
    private boolean isUserDeleted = false;
    private String mEventId = "";
    private RegisterParticipantsTask mRegisterParticipantsTask;
    private int position = -1;
    private Toolbar mToolbar;

    private RecyclerView mRecyclerView;
    private RecyclerViewParticipantsAdapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participants);
        mActivity = this;
        setupToolBar();

        if (getIntent().hasExtra("event-id")) {
            mEventId = getIntent().getStringExtra("event-id");
        }

        if (getIntent().hasExtra("event-position")) {
            position = getIntent().getIntExtra("event-position", -1);
        }

        mRecyclerView = (RecyclerView) findViewById(R.id.activity_participants_recycler_view_participants);
        mRecyclerViewLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        findViewById(R.id.activity_participants_btn_proceed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerParticipants();
            }
        });

        authVo = AuthDataManager.getAuthData(this);

        if (authVo == null || authVo.getUser() == null || TextUtils.isEmpty(authVo.getUser().getId())) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        } else {
            UserVo userVo = authVo.getUser();
            mParticipantList = new ArrayList<>();

            ParticipantVo participantVo = new ParticipantVo(1);
            participantVo.setFirstname(userVo.getFirstName());
            participantVo.setLastname(userVo.getLastName());
            participantVo.setEmail(userVo.getEmail());
            participantVo.setPhone(userVo.getPhone());
            participantVo.setInstitute(userVo.getInstitute());
            participantVo.setPosition(1);
            mParticipantList.add(participantVo);

            mRecyclerViewAdapter = new RecyclerViewParticipantsAdapter(mActivity, mParticipantList, userVo.getEmail());
            mRecyclerView.setAdapter(mRecyclerViewAdapter);
        }
    }

    /**
     * Set up the {@link android.support.v7.widget.Toolbar}.
     */
    private void setupToolBar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void registerParticipants() {
        if (mRegisterParticipantsTask != null) {
            return;
        }

        mParticipantList = mRecyclerViewAdapter.getDataset();
        if (!mParticipantList.isEmpty()) {
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
                for (ParticipantVo participantVo : mParticipantList) {
                    participantList.add(participantVo.getFirstname() + " " + participantVo.getLastname());
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
        mRegisterParticipantsTask = null;
    }

    @Override
    public void OnRecyclerViewItemClick(int position) {

    }
}

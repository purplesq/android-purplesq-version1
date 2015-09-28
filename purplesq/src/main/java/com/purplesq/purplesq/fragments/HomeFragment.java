package com.purplesq.purplesq.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.crashlytics.android.Crashlytics;
import com.purplesq.purplesq.R;
import com.purplesq.purplesq.activities.EventDetailsActivity;
import com.purplesq.purplesq.adapters.RecyclerViewAdapter;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.datamanagers.AuthDataManager;
import com.purplesq.purplesq.dialogs.CheckUpdateDialogFragment;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.interfces.RecyclerViewItemClickListener;
import com.purplesq.purplesq.tasks.CheckUpdateTask;
import com.purplesq.purplesq.tasks.GetAllCitiesTask;
import com.purplesq.purplesq.tasks.GetAllEventsTask;
import com.purplesq.purplesq.tasks.RefreshTokenTask;
import com.purplesq.purplesq.utils.Config;
import com.purplesq.purplesq.utils.PSQConsts;
import com.purplesq.purplesq.vos.AuthVo;
import com.purplesq.purplesq.vos.ErrorVo;
import com.purplesq.purplesq.vos.EventsVo;
import com.purplesq.purplesq.vos.VersionVo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements GenericAsyncTaskListener, RecyclerViewItemClickListener, OnItemSelectedListener {

    private OnItemSelectedListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private List<EventsVo> allEvents;
    private Spinner mSpinner;
    private GenericAsyncTaskListener mGenericAsyncTaskListener;


    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View remoteView = inflater.inflate(R.layout.fragment_home, container, false);
        mListener = this;
        mGenericAsyncTaskListener = this;

        mRecyclerView = (RecyclerView) remoteView.findViewById(R.id.fragment_home_recycler_view);
        mSpinner = (Spinner) remoteView.findViewById(R.id.fragment_home_spinner_cities);
        mSpinner.setOnItemSelectedListener(mListener);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        AuthVo authVo = AuthDataManager.getAuthData(getActivity());
        if (authVo != null) {
            long timeToFetchToken = authVo.getExpiryTime() - System.currentTimeMillis();

            if (timeToFetchToken < (1000 * 60 * 60 * 24)) { //86400000
                PurpleSQ.showLoadingDialog(getActivity());
                new RefreshTokenTask(getActivity(), authVo.getUser(), this).execute((Void) null);
            }
        }

        new CheckUpdateTask(this).execute((Void) null);

        PurpleSQ.showLoadingDialog(getActivity());
        new GetAllCitiesTask(this).execute((Void) null);

        return remoteView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mGenericAsyncTaskListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        if (obj != null) {
            if (obj instanceof VersionVo) {
                try {
                    VersionVo versionVo = (VersionVo) obj;
                    int currentVersionCode = 0;
                    int installedVersionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;

                    currentVersionCode = versionVo.getCurrentVersion();
                    int forceInstallCode = versionVo.getForcedVersions();

                    if (installedVersionCode < forceInstallCode) {
                        CheckUpdateDialogFragment updateDialogFragment = new CheckUpdateDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("versionVo", versionVo);
                        bundle.putBoolean("forced", true);
                        updateDialogFragment.setArguments(bundle);

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(PSQConsts.DIALOG_FRAGMENT_UPDATE);
                        if (prev != null) {
                            ft.remove(prev);
                        }

                        updateDialogFragment.show(ft, PSQConsts.DIALOG_FRAGMENT_UPDATE);
                    } else if (installedVersionCode != 0 && installedVersionCode < currentVersionCode) {
                        CheckUpdateDialogFragment updateDialogFragment = new CheckUpdateDialogFragment();
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("versionVo", versionVo);
                        bundle.putBoolean("forced", false);
                        updateDialogFragment.setArguments(bundle);

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(PSQConsts.DIALOG_FRAGMENT_UPDATE);
                        if (prev != null) {
                            ft.remove(prev);
                        }
                        updateDialogFragment.show(ft, PSQConsts.DIALOG_FRAGMENT_UPDATE);
                    } else if (installedVersionCode == currentVersionCode) {
                        if (Config.DEBUG) {
                        }
                    }

                } catch (Exception e) {

                }
            } else if (obj instanceof JSONArray) {
                try {
                    JSONArray jsonCities = (JSONArray) obj;

                    ArrayList<String> Cities = new ArrayList<>();

                    for (int i = 0; i < jsonCities.length(); i++) {
                        Cities.add(jsonCities.getString(i));
                    }

                    Cities.add(0, "All Events");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Cities);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinner.setAdapter(arrayAdapter);
                    mSpinner.setSelection(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }

            } else if (obj instanceof List) {
                allEvents = (List<EventsVo>) obj;

                // specify an adapter (see also next example)
                mRecyclerViewAdapter = new RecyclerViewAdapter(allEvents, this);
                mRecyclerView.setAdapter(mRecyclerViewAdapter);

                ((PurpleSQ) getActivity().getApplication()).setEventsData(allEvents);
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
        if (obj instanceof ErrorVo) {
            ErrorVo errorVo = (ErrorVo) obj;

            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag(PSQConsts.DIALOG_FRAGMENT_ERROR);
            if (prev != null) {
                ft.remove(prev);
            }

            com.purplesq.purplesq.fragments.ErrorDialogFragment errorDialogFragment = com.purplesq.purplesq.fragments.ErrorDialogFragment.newInstance(errorVo);
            errorDialogFragment.show(ft, PSQConsts.DIALOG_FRAGMENT_ERROR);
        }
    }

    @Override
    public void genericAsyncTaskOnProgress(Object obj) {

    }

    @Override
    public void genericAsyncTaskOnCancelled(Object obj) {
        if (PurpleSQ.isLoadingDialogVisible()) {
            PurpleSQ.dismissLoadingDialog();
        }
    }

    @Override
    public void OnRecyclerViewItemClick(int position) {
        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
        intent.putExtra(PSQConsts.EXTRAS_EVENT_POSITION, position);
        getActivity().startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
        PurpleSQ.showLoadingDialog(getActivity());
        GetAllEventsTask getAllEventsTask = new GetAllEventsTask(mGenericAsyncTaskListener, adapter.getItemAtPosition(position).toString());
        getAllEventsTask.execute((Void) null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}

package com.purplesq.purplesq.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.activities.EventDetailsActivity;
import com.purplesq.purplesq.adapters.RecyclerViewAdapter;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.interfces.RecyclerViewItemClickListener;
import com.purplesq.purplesq.tasks.GetAllCitiesTask;
import com.purplesq.purplesq.tasks.GetAllEventsTask;
import com.purplesq.purplesq.vos.EventsVo;

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


    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    public HomeFragment() {
        // Required empty public constructor
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


        new GetAllCitiesTask(this).execute();

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
    public void genericAsyncTaskOnSuccess(Object obj) {
        if (obj != null) {
            if (obj instanceof JSONArray) {
                try {
                    JSONArray jsonCities = (JSONArray) obj;

                    ArrayList<String> Cities = new ArrayList<>();

                    for (int i = 0; i < jsonCities.length(); i++) {
                        Cities.add(jsonCities.getString(i));
                    }

                    Cities.add(0, "All Events");

                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, Cities);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mSpinner.setAdapter(arrayAdapter);
                    mSpinner.setSelection(0);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                allEvents = (List<EventsVo>) obj;

                // specify an adapter (see also next example)
                mRecyclerViewAdapter = new RecyclerViewAdapter(allEvents, this);
                mRecyclerView.setAdapter(mRecyclerViewAdapter);

                ((PurpleSQ) getActivity().getApplication()).setEventsData(allEvents);
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

    @Override
    public void OnRecyclerViewItemClick(int position) {
        Log.d("Nish", "RecyclerView clicked at  : " + position);

        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
        intent.putExtra("event-position", position);
        getActivity().startActivity(intent);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapter, View view, int position, long id) {
        GetAllEventsTask getAllEventsTask = new GetAllEventsTask(mGenericAsyncTaskListener, adapter.getItemAtPosition(position).toString());
        getAllEventsTask.execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}

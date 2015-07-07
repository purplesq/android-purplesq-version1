package com.purplesq.purplesq.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.purplesq.purplesq.R;
import com.purplesq.purplesq.activities.EventDetailsActivity;
import com.purplesq.purplesq.adapters.RecyclerViewAdapter;
import com.purplesq.purplesq.application.PurpleSQ;
import com.purplesq.purplesq.interfces.GenericAsyncTaskListener;
import com.purplesq.purplesq.interfces.RecyclerViewItemClickListener;
import com.purplesq.purplesq.tasks.GetAllEventsTask;
import com.purplesq.purplesq.vos.EventsVo;

import java.util.List;

public class HomeFragment extends Fragment implements GenericAsyncTaskListener, RecyclerViewItemClickListener {

    private OnFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerViewAdapter;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private List<EventsVo> allEvents;


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
        mRecyclerView = (RecyclerView) remoteView.findViewById(R.id.fragment_home_recycler_view);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        GetAllEventsTask getAllEventsTask = new GetAllEventsTask(this);
        getAllEventsTask.execute();

        return remoteView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void genericAsyncTaskOnSuccess(Object obj) {
        if (obj != null) {
            allEvents = (List<EventsVo>) obj;

            // specify an adapter (see also next example)
            mRecyclerViewAdapter = new RecyclerViewAdapter(allEvents, this);
            mRecyclerView.setAdapter(mRecyclerViewAdapter);

            ((PurpleSQ)getActivity().getApplication()).setEventsData(allEvents);
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}

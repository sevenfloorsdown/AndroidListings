/*
 * Copyright (C) 2017 Christian Rivera
 *
 */

package com.christian.domaindemo;

import android.content.Context;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class PropListingsFragment extends Fragment {
    public interface OnListingsSelectedListener {
        void onListSelectedInteraction(int item);
    }

    OnListingsSelectedListener mSelectionCallback;

    private RecyclerView mRecyclerView;
    private PropRecyclerViewAdapter mAdapter;
    private Toolbar mAppBar;
    private ProgressBar mRecyclerProgressBar;
    private TextView mGeneralMessage;
    private final Object lock = new Object();

    private static final String RECYCLER_EXTRA_STATE = "recycler_state";
    private int mRecyclerState = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String sourceUrl = getString(R.string.default_feed);

        if (PropertyData.PropertyList == null) ingestJsonFeed(sourceUrl);

        if (PropertyData.Favorites == null)
            PropertyData.Favorites = new ArrayList<>();

        if (savedInstanceState != null)
            mRecyclerState = savedInstanceState.getInt(RECYCLER_EXTRA_STATE, -1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.listing_fragment, container, false);

        mRecyclerProgressBar = (ProgressBar) view.findViewById(R.id.recycler_progress_bar);
        mGeneralMessage = (TextView) view.findViewById(R.id.gen_message);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.property_recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new PropRecyclerViewAdapter((MainActivity)getActivity());
        mAppBar = (Toolbar) view.findViewById(R.id.app_toolbar);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        if (PropertyData.PropertyList != null) {
            attachRecyclerAdapter();
        } else {
            synchronized (lock) {
                lock.notify();
            }
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        DisplayMetrics screenReso = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(screenReso);
        int displayWidth = screenReso.widthPixels;

        if (getFragmentManager().findFragmentById(R.id.details_pane) != null) {

            // get the recycler width based on layout weights; dynamically getting them
            // ends up in a chicken-and-egg scenario between drawing the views and binding
            // the data to a yet-to-be-measured container
            int a = getContext().getResources().getInteger(R.integer.listings_fragment_layout_weight);
            int b = a + getContext().getResources().getInteger(R.integer.details_fragment_layout_weight);

            displayWidth = displayWidth * a/b;

            if (mAdapter != null) {
                // RecyclerView adapter already remembers its scroll position
                mAdapter.setSelectedPosition(mRecyclerState);
            }

            // We don't need the appbar disappearing on a large layout
            ((AppBarLayout.LayoutParams)mAppBar.getLayoutParams()).setScrollFlags(0);
        }
        if (mAdapter != null) mAdapter.setReferenceWidth(displayWidth);
    }

    @Override
    public void onSaveInstanceState (Bundle outState) {
        // we could save other things as well
        outState.putSerializable(RECYCLER_EXTRA_STATE, mAdapter.getSelectedPosition());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListingsSelectedListener) {
            mSelectionCallback = (OnListingsSelectedListener) context;
        } else {
            throw new ClassCastException(context.toString()
                    + " must implement MyListFragment.OnListingsSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mSelectionCallback = null;
    }

    private void ingestJsonFeed(String url) {
        JsonObjectRequest jsonFeed = new JsonObjectRequest(
                Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    PropertyData.PropertyList = response.getJSONObject(getString(R.string.listings_results)).
                            getJSONArray(getString(R.string.listings_element));
                    synchronized (lock){
                        try {
                            while (PropertyData.PropertyList == null || mRecyclerView == null) lock.wait();
                            attachRecyclerAdapter();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException e) {
                    PropertyData.PropertyList = null;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // A more comprehensive error-handling routine
                // can be implemented here (e.g. retries, 404s...)
                showErrorMessage(error.getMessage());
            }
        }
        );
        DomainDemoSingleton.getInstance(getActivity()).addToRequestQueue(jsonFeed);
    }

    private void showErrorMessage(String msg) {
        // We could re-format message here or use predefined strings
        // in case non-English languages are supported
        mRecyclerProgressBar.setVisibility(View.GONE);
        mGeneralMessage.setText(msg);
    }

    private void attachRecyclerAdapter() {
        mRecyclerView.setAdapter(mAdapter);
        if (mRecyclerProgressBar != null) mRecyclerProgressBar.setVisibility(View.GONE);
    }

}

/*
 * Copyright (C) 2017 Christian Rivera
 *
 */

package com.christian.domaindemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class PropDetailsFragment extends Fragment {

    static final String ARG_ITEM = "current_item";
    private int mCurrentItem = -1;

    private TextView mHeadline;
    private TextView mDescription;
    private Toolbar mAppBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mCurrentItem = savedInstanceState.getInt(ARG_ITEM);
        }
        View view = inflater.inflate(R.layout.details_fragment, container, false);
        mHeadline = (TextView) view.findViewById(R.id.details_headline);
        mDescription = (TextView) view.findViewById(R.id.details_description);
        mAppBar = (Toolbar) view.findViewById(R.id.app_toolbar2);
        if (mAppBar != null) {
            mAppBar.setNavigationIcon(R.drawable.ic_back);
            mAppBar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getActivity().onBackPressed();
                }
            });
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Bundle args = getArguments();
        if (args != null) {
            updateDetailsPane(args.getInt(ARG_ITEM));
        } else if (mCurrentItem != -1) {
            updateDetailsPane(mCurrentItem);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_ITEM, mCurrentItem);
    }

    public void updateDetailsPane(final int item) {
        mCurrentItem = item;
        String headlineText;
        String descText;

        try {
            JSONObject curItem = PropertyData.PropertyList.getJSONObject(item);

            try {
                headlineText = curItem.getString(getString(R.string.key_ad_id));
            } catch (JSONException e) {
                headlineText = "";
            }

            try {
                descText =  curItem.getString(getString(R.string.key_description));
            } catch (JSONException e) {
                descText = "" ;
            }

            // other information for display can be fetched and processed here then

        } catch (JSONException e) {
            headlineText = "";
            descText = getString(R.string.not_found_text);
        }

        mHeadline.setText(headlineText);
        mDescription.setText(descText);

    }

}

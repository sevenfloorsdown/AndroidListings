/*
 * Copyright (C) 2017 Christian Rivera
 *
 */

package com.christian.domaindemo;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
    implements PropListingsFragment.OnListingsSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if (findViewById(R.id.fragments_container) != null) {
            if (savedInstanceState != null) return;

            PropListingsFragment listingsFragment = new PropListingsFragment();
            listingsFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragments_container, listingsFragment).commit();
        }
    }

    public void onListSelectedInteraction(int item) {
        // pass the selected property card's details onto the details pane
        PropDetailsFragment detailsFragment = (PropDetailsFragment)
                getSupportFragmentManager().findFragmentById(R.id.details_pane);

        if (detailsFragment != null) {
            detailsFragment.updateDetailsPane(item);
        } else {
            // put in new pane
            PropDetailsFragment newFragment = new PropDetailsFragment();
            Bundle args = new Bundle();
            args.putInt(PropDetailsFragment.ARG_ITEM, item);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragments_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}





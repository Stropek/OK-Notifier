package com.przemolab.oknotifier.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.models.Contest;

public class MainActivity extends AppCompatActivity
        implements ContestsListFragment.OnContestClickedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadContestsListFragment();
        } else {
            // TODO: retrieve saved instance state
        }
    }

    private void loadContestsListFragment() {
        ContestsListFragment contestsListFragment = new ContestsListFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.contestsList_fl, contestsListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onContestClicked(Contest contest) {
        //
    }
}

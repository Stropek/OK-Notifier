package com.przemolab.oknotifier.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.fragments.ContestantsListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContestActivity extends AppCompatActivity
    implements ContestantsListFragment.OnListFragmentInteractionListener {

    private ContestantsListFragment contestantsListFragment;

    @BindView(R.id.sync_pb) ProgressBar syncProgressBar;
    @BindView(R.id.contestantsList_fl) FrameLayout contestantsListFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);

        if (savedInstanceState == null) {
            loadContestantsListFragment();
        } else {
            // TODO: retrieve saved instance state
        }

        ButterKnife.bind(this);
    }

    private void loadContestantsListFragment() {
        contestantsListFragment = new ContestantsListFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.contestantsList_fl, contestantsListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onListFragmentInteraction() {

    }
}

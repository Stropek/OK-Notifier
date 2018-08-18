package com.przemolab.oknotifier.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.fragments.ContestantsListFragment;
import com.przemolab.oknotifier.models.Contestant;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContestActivity extends AppCompatActivity
        implements ContestantsListFragment.OnContestantsListEventListener {

    private ContestantsListFragment contestantsListFragment;

    @BindView(R.id.sync_pb) ProgressBar syncProgressBar;
    @BindView(R.id.contestantsList_fl) FrameLayout contestantsListFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            loadContestantsListFragment();
        } else {
            // TODO: retrieve saved instance state
        }

        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contest_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.sync_menu_item:
                contestantsListFragment.onSyncClicked();
                break;
        }

        return true;
    }

    public ContestantsListFragment getContestantsListFragment() {
        ContestantsListFragment fragment = new ContestantsListFragment();

        Intent intent = getIntent();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.ContestId, intent.getStringExtra(Constants.BundleKeys.ContestId));

        fragment.setArguments(bundle);
        return fragment;
    }

    private void loadContestantsListFragment() {
        contestantsListFragment = getContestantsListFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.contestantsList_fl, contestantsListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onSyncStarted() {
        syncProgressBar.setVisibility(ProgressBar.VISIBLE);
        contestantsListFrameLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSyncFinished(List<Contestant> contestants) {
        contestantsListFrameLayout.setVisibility(View.VISIBLE);
        syncProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }
}

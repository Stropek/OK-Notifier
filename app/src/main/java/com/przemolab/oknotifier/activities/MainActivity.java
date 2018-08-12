package com.przemolab.oknotifier.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.models.Contest;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements ContestsListFragment.OnContestsListEventsListener {

    private ContestsListFragment contestsListFragment;

    @BindView(R.id.sync_pb) ProgressBar syncProgressBar;
    @BindView(R.id.contestsList_fl) FrameLayout contestsListFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            loadContestsListFragment();
        } else {
            // TODO: retrieve saved instance state
        }

        ButterKnife.bind(this);
    }

    private void loadContestsListFragment() {
        contestsListFragment = new ContestsListFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.contestsList_fl, contestsListFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sync_menu_item:
                contestsListFragment.onSyncClicked();
                break;
        }

        return true;
    }

    @Override
    public void onSubscribedClicked(Contest contest) {
        contest.setSubscribed(!contest.isSubscribed());
        contestsListFragment.toggleSubscription(contest);
    }

    @Override
    public void onContestClicked(Contest contest) {
        Toast.makeText(this, "Contest from main activity", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSyncStarted() {
        syncProgressBar.setVisibility(ProgressBar.VISIBLE);
        contestsListFrameLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSyncFinished() {
        contestsListFrameLayout.setVisibility(View.VISIBLE);
        syncProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }
}

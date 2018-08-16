package com.przemolab.oknotifier.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.enums.SortOrder;
import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.models.Contest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements ContestsListFragment.OnContestsListEventsListener {

    private ContestsListFragment contestsListFragment;
    private SortOrder sortOrder = SortOrder.SubscribedFirst;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.sync_menu_item) {
            contestsListFragment.onSyncClicked();
        } else {
            SortOrder current = sortOrder;

            switch (item.getItemId()) {
                case R.id.sort_subscribed_first:
                    sortOrder = SortOrder.SubscribedFirst;
                    break;
                case R.id.sort_by_name:
                    sortOrder = SortOrder.ByName;
                    break;
                case R.id.sort_by_start_date:
                    sortOrder = SortOrder.ByStartDate;
                    break;
                case R.id.sort_by_number_of_contestants:
                    sortOrder = SortOrder.ByNumberOfContestants;
                    break;
                case R.id.sort_by_number_of_problems:
                    sortOrder = SortOrder.ByNumberOfProblems;
                    break;
            }

            if (current != sortOrder) {
                loadContestsListFragment();
            }
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
        // TODO: alternate handling for master-slave view
        Intent contestIntent = new Intent(this, ContestActivity.class);
        contestIntent.putExtra(Constants.BundleKeys.ContestId, contest.getId());
        startActivity(contestIntent);
    }

    @Override
    public void onSyncStarted() {
        syncProgressBar.setVisibility(ProgressBar.VISIBLE);
        contestsListFrameLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSyncFinished(List<Contest> contests) {
        contestsListFrameLayout.setVisibility(View.VISIBLE);
        syncProgressBar.setVisibility(ProgressBar.INVISIBLE);

        getSupportLoaderManager().restartLoader(ContestsListFragment.CONTEST_LOADER_ID, null, contestsListFragment);
    }

    public ContestsListFragment getContestsListFragment() {
        ContestsListFragment fragment = new ContestsListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.SortOrder, sortOrder);

        fragment.setArguments(bundle);
        return fragment;
    }

    private void loadContestsListFragment() {
        contestsListFragment = getContestsListFragment();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.contestsList_fl, contestsListFragment);
        fragmentTransaction.commit();
    }
}

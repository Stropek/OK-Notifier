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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.przemolab.oknotifier.BuildConfig;
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

    @BindView(R.id.sync_contests_pb) ProgressBar syncContestsProgressBar;
    @BindView(R.id.contestsList_fl) FrameLayout contestsListFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-1062223867553661~1889097057");

        if (savedInstanceState == null) {
            loadContestsListFragment();
        } else {
            // TODO: retrieve saved instance state
        }

        ButterKnife.bind(this);

        scheduleNotificationService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();
        if (itemId == R.id.sync_menu_item) {
            contestsListFragment.onSyncClicked();
        } else if (itemId == R.id.settings_menu_item) {
            openSettings();
        } else {
            SortOrder current = sortOrder;

            switch (itemId) {
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
        contestIntent.putExtra(Constants.BundleKeys.ContestId, contest.getContestId());
        startActivity(contestIntent);
    }

    @Override
    public void onSyncStarted() {
        syncContestsProgressBar.setVisibility(ProgressBar.VISIBLE);
        contestsListFrameLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onSyncFinished(List<Contest> contests) {
        contestsListFrameLayout.setVisibility(View.VISIBLE);
        syncContestsProgressBar.setVisibility(ProgressBar.INVISIBLE);

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

    private void openSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void scheduleNotificationService() {



//        ComponentName componentName = new ComponentName(this, OpenKattisJobService.class);
//        JobInfo jobInfo = new JobInfo.Builder(OpenKattisJobService.OPEN_KATTIS_JOB_SERVICE_ID, componentName)
//                .setPeriodic(10)
//                .build();
//
//        JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
//        int resultCode = jobScheduler.schedule(jobInfo);
//        if (resultCode == JobScheduler.RESULT_SUCCESS) {
//            Timber.i("OpenKattis: Job scheduled!");
//        } else {
//            Timber.i("OpenKattis: Job not scheduled");
//        }
//
//        for ( JobInfo pendingInfo : jobScheduler.getAllPendingJobs() ) {
//            if (pendingInfo.getContestId() == OpenKattisJobService.OPEN_KATTIS_JOB_SERVICE_ID) {
//                Timber.i("OpenKattis: Job is pending...");
//                break;
//            }
//        }

    }
}

package com.przemolab.oknotifier.activities;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.MobileAds;
import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.enums.SortOrder;
import com.przemolab.oknotifier.fragments.ContestantsListFragment;
import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.services.ContestIntentService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity
        implements ContestsListFragment.OnContestsListEventsListener,
            ContestantsListFragment.OnContestantsListEventListener {

    private boolean isBigScreen;

    private ContestsListFragment contestsListFragment;
    private ContestantsListFragment contestantsListFragment;

    private SortOrder sortOrder = SortOrder.SubscribedFirst;
    private List<Contestant> contestants = new ArrayList<>();
    private String contestId = "";

    @BindView(R.id.syncContests_pb) ProgressBar syncContestsProgressBar;
    @BindView(R.id.contestsList_fl) FrameLayout contestsListFrameLayout;
    @BindView(R.id.noContestSelected_tv) @Nullable TextView noContestSelectedTextView;
    @BindView(R.id.syncStandings_pb) @Nullable ProgressBar syncStandingsProgressBar;
    @BindView(R.id.contestantsList_fl) @Nullable FrameLayout contestantsListFrameLayout;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Constants.BundleKeys.SortOrder, sortOrder);
        outState.putString(Constants.BundleKeys.ContestId, contestId);
        outState.putParcelableArrayList(Constants.BundleKeys.Contestants, new ArrayList<>(contestants));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, "ca-app-pub-1062223867553661~1889097057");
        ButterKnife.bind(this);

        isBigScreen = contestantsListFrameLayout != null;

        if (savedInstanceState != null) {
            sortOrder = (SortOrder) savedInstanceState.getSerializable(Constants.BundleKeys.SortOrder);
            contestId = savedInstanceState.getString(Constants.BundleKeys.ContestId);
            contestants = savedInstanceState.getParcelableArrayList(Constants.BundleKeys.Contestants);
        }

        loadContestsListFragment();
        if (isBigScreen && !contestId.isEmpty()) {
            loadContestantsListFragment();
        }
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
        if (isBigScreen) {
            contestId = contest.getContestId();
            loadContestantsListFragment();
        } else {
            Intent contestIntent = new Intent(this, ContestActivity.class);
            contestIntent.putExtra(Constants.BundleKeys.ContestId, contest.getContestId());
            startActivity(contestIntent);
        }
    }

    @Override
    public void onContestSyncStarted() {
        syncContestsProgressBar.setVisibility(ProgressBar.VISIBLE);
        contestsListFrameLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onContestSyncFinished(List<Contest> contests) {
        contestsListFrameLayout.setVisibility(View.VISIBLE);
        syncContestsProgressBar.setVisibility(ProgressBar.INVISIBLE);

        getSupportLoaderManager().restartLoader(ContestsListFragment.CONTEST_LOADER_ID, null, contestsListFragment);
    }

    @Override
    public void onContestantsSyncStarted() {
        if (isBigScreen) {
            assert syncStandingsProgressBar != null;
            assert contestantsListFrameLayout != null;
            syncStandingsProgressBar.setVisibility(ProgressBar.VISIBLE);
            contestantsListFrameLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onContestantsSyncFinished(List<Contestant> contestants, boolean restartLoader) {
        if (isBigScreen) {
            this.contestants = contestants;

            assert syncStandingsProgressBar != null;
            assert contestantsListFrameLayout != null;
            contestantsListFrameLayout.setVisibility(View.VISIBLE);
            syncStandingsProgressBar.setVisibility(ProgressBar.INVISIBLE);

            if (restartLoader) {
                getSupportLoaderManager().restartLoader(ContestantsListFragment.CONTESTANT_LOADER_ID, null, contestantsListFragment);
            }

            ContestIntentService.startActionUpdateContestWidgets(this);
        }
    }

    public ContestsListFragment getContestsListFragment() {
        ContestsListFragment fragment = new ContestsListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.SortOrder, sortOrder);

        fragment.setArguments(bundle);
        return fragment;
    }

    public ContestantsListFragment getContestantsListFragment() {
        ContestantsListFragment fragment = new ContestantsListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.ContestId, contestId);

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

    private void loadContestantsListFragment() {
        contestantsListFragment = getContestantsListFragment();

        assert noContestSelectedTextView != null;
        noContestSelectedTextView.setVisibility(View.INVISIBLE);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        fragmentTransaction.replace(R.id.contestantsList_fl, contestantsListFragment);
        fragmentTransaction.commit();
    }

    private void openSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}

package com.przemolab.oknotifier.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.fragments.ContestantsListFragment;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.services.ContestIntentService;
import com.przemolab.oknotifier.widgets.ContestWidgetDataProvider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContestActivity extends AppCompatActivity
        implements ContestantsListFragment.OnContestantsListEventListener {

    private ContestantsListFragment contestantsListFragment;
    private ContestWidgetDataProvider contestWidgetDataProvider;

    private String contestId;
    private List<Contestant> contestants;

    @BindView(R.id.syncStandings_pb) ProgressBar syncStandingsProgressBar;
    @BindView(R.id.contestantsList_fl) FrameLayout contestantsListFrameLayout;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(Constants.BundleKeys.ContestId, contestId);
        outState.putParcelableArrayList(Constants.BundleKeys.Contestants, (ArrayList<Contestant>) contestants);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contest);

        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SharedPreferences.Name, Context.MODE_PRIVATE);
        contestWidgetDataProvider = new ContestWidgetDataProvider(sharedPreferences);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            contestId = intent.getStringExtra(Constants.BundleKeys.ContestId);
        } else {
            contestId = savedInstanceState.getString(Constants.BundleKeys.ContestId);
            contestants = savedInstanceState.getParcelableArrayList(Constants.BundleKeys.Contestants);
        }

        loadContestantsListFragment();

        ButterKnife.bind(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contest_activity, menu);

        final Context context = this;
        CheckBox checkBox = (CheckBox) menu.findItem(R.id.set_widget_menu_item).getActionView();
        checkBox.setChecked(contestWidgetDataProvider.isCurrentSource(contestId));
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Contestant bestContestant = new Contestant("", contestId, 0, 0, 0, 0, 0);

                if (!contestants.isEmpty()) {
                    bestContestant = contestants.get(0);
                }

                contestWidgetDataProvider.toggleSource(context, bestContestant, isChecked);
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.sync_menu_item:
                contestantsListFragment.onSyncClicked();
                break;
        }

        return true;
    }

    @Override
    public void onContestantsSyncStarted() {
        syncStandingsProgressBar.setVisibility(ProgressBar.VISIBLE);
        contestantsListFrameLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onContestantsSyncFinished(List<Contestant> contestants, boolean restartLoader) {
        this.contestants = contestants;

        contestantsListFrameLayout.setVisibility(View.VISIBLE);
        syncStandingsProgressBar.setVisibility(ProgressBar.INVISIBLE);

        if (restartLoader) {
            getSupportLoaderManager().restartLoader(ContestantsListFragment.CONTESTANT_LOADER_ID, null, contestantsListFragment);
        }

        ContestIntentService.startActionUpdateContestWidgets(this);
    }

    public ContestantsListFragment getContestantsListFragment() {
        ContestantsListFragment fragment = new ContestantsListFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BundleKeys.ContestId, contestId);

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
}

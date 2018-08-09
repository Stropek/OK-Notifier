package com.przemolab.oknotifier.activities;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.models.Contest;

public class MainActivity extends AppCompatActivity
        implements ContestsListFragment.OnContestClickedListener {

    private ContestsListFragment contestsListFragment;

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
    public void onContestClicked(Contest contest) {
        //
    }
}

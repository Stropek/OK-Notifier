package com.przemolab.oknotifier.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.TextView

import com.google.android.gms.ads.MobileAds
import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.enums.SortOrder
import com.przemolab.oknotifier.fragments.ContestantsListFragment
import com.przemolab.oknotifier.fragments.ContestsListFragment
import com.przemolab.oknotifier.services.ContestIntentService

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import com.przemolab.oknotifier.data.entries.ContestEntry
import com.przemolab.oknotifier.data.entries.ContestantEntry

class MainActivity : AppCompatActivity(), ContestsListFragment.OnContestsListEventsListener, ContestantsListFragment.OnContestantsListEventListener {

    private var isBigScreen: Boolean = false

    private var contestsListFragment: ContestsListFragment? = null
    private var contestantsListFragment: ContestantsListFragment? = null

    private var sortOrder = SortOrder.SubscribedFirst
    private var contestants: List<ContestantEntry>? = ArrayList()
    private var contestId: String? = ""

    @BindView(R.id.syncContests_pb) @JvmField
    internal var syncContestsProgressBar: ProgressBar? = null
    @BindView(R.id.contestsList_fl) @JvmField
    internal var contestsListFrameLayout: FrameLayout? = null
    @BindView(R.id.noContestSelected_tv) @JvmField
    internal var noContestSelectedTextView: TextView? = null
    @BindView(R.id.syncStandings_pb) @JvmField
    internal var syncStandingsProgressBar: ProgressBar? = null
    @BindView(R.id.contestantsList_fl) @JvmField
    internal var contestantsListFrameLayout: FrameLayout? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(Constants.BundleKeys.SortOrder, sortOrder)
        outState.putString(Constants.BundleKeys.ContestId, contestId)
        // TODO:
        // outState.putParcelableArrayList(Constants.BundleKeys.Contestants, ArrayList(contestants!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this, getString(R.string.adMob_id))
        ButterKnife.bind(this)

        isBigScreen = contestantsListFrameLayout != null

        if (savedInstanceState != null) {
            sortOrder = savedInstanceState.getSerializable(Constants.BundleKeys.SortOrder) as SortOrder
            contestId = savedInstanceState.getString(Constants.BundleKeys.ContestId)
            // TODO:
            // contestants = savedInstanceState.getParcelableArrayList(Constants.BundleKeys.Contestants)
        }

        loadContestsListFragment()
        if (isBigScreen && !contestId!!.isEmpty()) {
            loadContestantsListFragment()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main_activity, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId
        if (itemId == R.id.sync_menu_item) {
            contestsListFragment!!.onSyncClicked()
        } else if (itemId == R.id.settings_menu_item) {
            openSettings()
        } else {
            val current = sortOrder

            when (itemId) {
                R.id.sort_subscribed_first -> sortOrder = SortOrder.SubscribedFirst
                R.id.sort_by_name -> sortOrder = SortOrder.ByName
                R.id.sort_by_start_date -> sortOrder = SortOrder.ByStartDate
                R.id.sort_by_number_of_contestants -> sortOrder = SortOrder.ByNumberOfContestants
                R.id.sort_by_number_of_problems -> sortOrder = SortOrder.ByNumberOfProblems
            }

            if (current !== sortOrder) {
                loadContestsListFragment()
            }
        }

        return true
    }

    override fun onSubscribedClicked(contestEntry: ContestEntry) {
        contestEntry.subscribed = !contestEntry.subscribed
        contestsListFragment!!.toggleSubscription(contestEntry)
    }

    override fun onContestClicked(contestEntry: ContestEntry) {
        if (isBigScreen) {
            contestId = contestEntry.contestId
            loadContestantsListFragment()
        } else {
            val contestIntent = Intent(this, ContestActivity::class.java)
            contestIntent.putExtra(Constants.BundleKeys.ContestId, contestEntry.contestId)
            startActivity(contestIntent)
        }
    }

    override fun onContestSyncStarted() {
        syncContestsProgressBar!!.visibility = ProgressBar.VISIBLE
        contestsListFrameLayout!!.visibility = View.INVISIBLE
    }

    override fun onContestSyncFinished(contestEntries: List<ContestEntry>) {
        contestsListFrameLayout!!.visibility = View.VISIBLE
        syncContestsProgressBar!!.visibility = ProgressBar.INVISIBLE

        supportLoaderManager.restartLoader(ContestsListFragment.CONTEST_LOADER_ID, null, contestsListFragment!!)
    }

    override fun onContestantsSyncStarted() {
        if (isBigScreen) {
            assert(syncStandingsProgressBar != null)
            assert(contestantsListFrameLayout != null)
            syncStandingsProgressBar!!.visibility = ProgressBar.VISIBLE
            contestantsListFrameLayout!!.visibility = View.INVISIBLE
        }
    }

    override fun onContestantsSyncFinished(contestants: List<ContestantEntry>?, restartLoader: Boolean) {
        if (isBigScreen) {
            this.contestants = contestants

            assert(syncStandingsProgressBar != null)
            assert(contestantsListFrameLayout != null)
            contestantsListFrameLayout!!.visibility = View.VISIBLE
            syncStandingsProgressBar!!.visibility = ProgressBar.INVISIBLE

            if (restartLoader) {
                supportLoaderManager.restartLoader(ContestantsListFragment.CONTESTANT_LOADER_ID, null, contestantsListFragment!!)
            }

            ContestIntentService.startActionUpdateContestWidgets(this)
        }
    }

    private fun getContestsListFragment(): ContestsListFragment {
        val fragment = ContestsListFragment()

        val bundle = Bundle()
        bundle.putSerializable(Constants.BundleKeys.SortOrder, sortOrder)

        fragment.arguments = bundle
        return fragment
    }

    private fun getContestantsListFragment(): ContestantsListFragment {
        val fragment = ContestantsListFragment()

        val bundle = Bundle()
        bundle.putSerializable(Constants.BundleKeys.ContestId, contestId)

        fragment.arguments = bundle
        return fragment
    }

    private fun loadContestsListFragment() {
        contestsListFragment = getContestsListFragment()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.contestsList_fl, contestsListFragment as ContestsListFragment)
        fragmentTransaction.commit()
    }

    private fun loadContestantsListFragment() {
        contestantsListFragment = getContestantsListFragment()

        assert(noContestSelectedTextView != null)
        noContestSelectedTextView!!.visibility = View.INVISIBLE

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.contestantsList_fl, contestantsListFragment as ContestantsListFragment)
        fragmentTransaction.commit()
    }

    private fun openSettings() {
        val settingsIntent = Intent(this, SettingsActivity::class.java)
        startActivity(settingsIntent)
    }
}

package com.przemolab.oknotifier.activities

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ProgressBar

import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.fragments.ContestantsListFragment
import com.przemolab.oknotifier.models.Contestant
import com.przemolab.oknotifier.services.ContestIntentService
import com.przemolab.oknotifier.widgets.ContestWidgetDataProvider

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife

class ContestActivity : AppCompatActivity(), ContestantsListFragment.OnContestantsListEventListener {

    private var contestantsListFragment: ContestantsListFragment? = null
    private var contestWidgetDataProvider: ContestWidgetDataProvider? = null

    private var contestId: String? = null
    private var contestants: List<Contestant>? = ArrayList()

    @BindView(R.id.syncStandings_pb) @JvmField
    internal var syncStandingsProgressBar: ProgressBar? = null
    @BindView(R.id.contestantsList_fl) @JvmField
    internal var contestantsListFrameLayout: FrameLayout? = null

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(Constants.BundleKeys.ContestId, contestId)
        outState.putParcelableArrayList(Constants.BundleKeys.Contestants, ArrayList(contestants!!))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contest)

        val sharedPreferences = getSharedPreferences(Constants.SharedPreferences.Name, Context.MODE_PRIVATE)
        contestWidgetDataProvider = ContestWidgetDataProvider(sharedPreferences)

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        if (savedInstanceState == null) {
            val intent = intent
            contestId = intent.getStringExtra(Constants.BundleKeys.ContestId)
        } else {
            contestId = savedInstanceState.getString(Constants.BundleKeys.ContestId)
            contestants = savedInstanceState.getParcelableArrayList(Constants.BundleKeys.Contestants)
        }

        loadContestantsListFragment()

        ButterKnife.bind(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contest_activity, menu)

        val context = this
        val checkBox = menu.findItem(R.id.set_widget_menu_item).actionView as CheckBox
        checkBox.isChecked = contestWidgetDataProvider!!.isCurrentSource(contestId!!)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            var bestContestant = Contestant("", contestId!!, 0, 0, 0, 0, 0)

            if (!contestants!!.isEmpty()) {
                bestContestant = contestants!![0]
            }

            contestWidgetDataProvider!!.toggleSource(context, bestContestant, isChecked)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> startActivity(Intent(this, MainActivity::class.java))
            R.id.sync_menu_item -> contestantsListFragment!!.onSyncClicked()
        }

        return true
    }

    override fun onContestantsSyncStarted() {
        syncStandingsProgressBar!!.visibility = ProgressBar.VISIBLE
        contestantsListFrameLayout!!.visibility = View.INVISIBLE
    }

    override fun onContestantsSyncFinished(contestants: List<Contestant>?, restartLoader: Boolean) {
        this.contestants = contestants

        contestantsListFrameLayout!!.visibility = View.VISIBLE
        syncStandingsProgressBar!!.visibility = ProgressBar.INVISIBLE

        if (restartLoader) {
            supportLoaderManager.restartLoader(ContestantsListFragment.CONTESTANT_LOADER_ID, null, contestantsListFragment!!)
        }

        ContestIntentService.startActionUpdateContestWidgets(this)
    }

    private fun getContestantsListFragment(): ContestantsListFragment {
        val fragment = ContestantsListFragment()

        val bundle = Bundle()
        bundle.putSerializable(Constants.BundleKeys.ContestId, contestId)

        fragment.arguments = bundle
        return fragment
    }

    private fun loadContestantsListFragment() {
        contestantsListFragment = getContestantsListFragment()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
        fragmentTransaction.replace(R.id.contestantsList_fl, contestantsListFragment as ContestantsListFragment)
        fragmentTransaction.commit()
    }
}

package com.przemolab.oknotifier.fragments

import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.przemolab.oknotifier.Constants
import com.przemolab.oknotifier.NotifierApp
import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.interfaces.INotifierRepository
import com.przemolab.oknotifier.interfaces.IOpenKattisService
import com.przemolab.oknotifier.sync.RetrieveContestantsTask
import com.przemolab.oknotifier.sync.SqliteContestantLoader
import com.przemolab.oknotifier.adapters.ContestantRecyclerViewAdapter
import java.util.Objects

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.przemolab.oknotifier.data.entries.ContestantEntry

class ContestantsListFragment : Fragment(), LoaderManager.LoaderCallbacks<List<ContestantEntry>> {
    private var contestId: String? = null
    private var onContestantsListEventsListener: OnContestantsListEventListener? = null

    @BindView(R.id.contestantsList_rv) @JvmField
    var contestantsRecyclerView: RecyclerView? = null
    @BindView(R.id.empty_cl) @JvmField
    var emptyLayout: ConstraintLayout? = null

    @Inject @JvmField
    var notifierRepository: INotifierRepository? = null
    @Inject @JvmField
    var openKattisService: IOpenKattisService? = null

    private var contestantRecyclerViewAdapter: ContestantRecyclerViewAdapter? = null

    @OnClick(R.id.sync_ib)
    fun onSyncClicked() {
        RetrieveContestantsTask(openKattisService!!, notifierRepository!!, contestId!!, onContestantsListEventsListener!!).execute()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(Constants.BundleKeys.ContestId, contestId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = Objects.requireNonNull<FragmentActivity>(activity).application as NotifierApp
        app.appComponent.inject(this)

        val arguments = arguments
        contestId = if (savedInstanceState == null) {
            assert(arguments != null)
            arguments!!.getString(Constants.BundleKeys.ContestId)
        } else {
            savedInstanceState.getString(Constants.BundleKeys.ContestId)
        }

        contestantRecyclerViewAdapter = ContestantRecyclerViewAdapter()

        loaderManager.initLoader(CONTESTANT_LOADER_ID, null, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contestants_list, container, false)
        ButterKnife.bind(this, view)

        val context = view.context

        contestantsRecyclerView!!.layoutManager = LinearLayoutManager(context)
        contestantsRecyclerView!!.adapter = contestantRecyclerViewAdapter

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnContestantsListEventListener) {
            onContestantsListEventsListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnContestantsListEventListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        onContestantsListEventsListener = null
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<ContestantEntry>> {
        return SqliteContestantLoader(activity!!, notifierRepository!!, openKattisService!!, contestId!!, onContestantsListEventsListener!!)
    }

    override fun onLoadFinished(loader: Loader<List<ContestantEntry>>, data: List<ContestantEntry>) {
        contestantRecyclerViewAdapter!!.swapData(data)

        if (data.isEmpty()) {
            contestantsRecyclerView!!.visibility = View.GONE
            emptyLayout!!.visibility = View.VISIBLE
        } else {
            emptyLayout!!.visibility = View.GONE
            contestantsRecyclerView!!.visibility = View.VISIBLE
        }
    }

    override fun onLoaderReset(loader: Loader<List<ContestantEntry>>) {
        contestantRecyclerViewAdapter!!.swapData(null)
    }

    interface OnContestantsListEventListener {

        fun onContestantsSyncStarted()

        fun onContestantsSyncFinished(contestants: List<ContestantEntry>?, restartLoader: Boolean)
    }

    companion object {

        const val CONTESTANT_LOADER_ID = 1
    }
}

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
import com.przemolab.oknotifier.sync.RetrieveContestsTask
import com.przemolab.oknotifier.sync.SqliteContestLoader
import com.przemolab.oknotifier.adapters.ContestRecyclerViewAdapter
import com.przemolab.oknotifier.enums.SortOrder
import java.util.Objects

import javax.inject.Inject

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.przemolab.oknotifier.data.entries.ContestEntry

class ContestsListFragment : Fragment(), LoaderManager.LoaderCallbacks<List<ContestEntry>> {
    private var sortOrder = SortOrder.SubscribedFirst

    @BindView(R.id.contestsList_rv) @JvmField
    var contestsRecyclerView: RecyclerView? = null
    @BindView(R.id.empty_cl) @JvmField
    var emptyLayout: ConstraintLayout? = null

    @Inject @JvmField
    var notifierRepository: INotifierRepository? = null
    @Inject @JvmField
    var openKattisService: IOpenKattisService? = null

    private var contestRecyclerViewAdapter: ContestRecyclerViewAdapter? = null

    private var onContestListEventsListener: OnContestsListEventsListener? = null

    @OnClick(R.id.sync_ib)
    fun onSyncClicked() {
        RetrieveContestsTask(openKattisService!!, notifierRepository!!, onContestListEventsListener!!).execute()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putSerializable(Constants.BundleKeys.SortOrder, sortOrder)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = Objects.requireNonNull<FragmentActivity>(activity).application as NotifierApp
        app.appComponent.inject(this)

        val arguments = arguments
        sortOrder = if (savedInstanceState == null) {
            assert(arguments != null)
            arguments!!.get(Constants.BundleKeys.SortOrder) as SortOrder
        } else {
            savedInstanceState.get(Constants.BundleKeys.SortOrder) as SortOrder
        }

        contestRecyclerViewAdapter = ContestRecyclerViewAdapter(onContestListEventsListener)

        loaderManager.initLoader(CONTEST_LOADER_ID, null, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_contest_list, container, false)
        ButterKnife.bind(this, view)

        val context = view.context

        contestsRecyclerView!!.layoutManager = LinearLayoutManager(context)
        contestsRecyclerView!!.adapter = contestRecyclerViewAdapter

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnContestsListEventsListener) {
            onContestListEventsListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnContestsListEventsListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        onContestListEventsListener = null
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<ContestEntry>> {
        return SqliteContestLoader(activity!!, notifierRepository!!, sortOrder)
    }

    override fun onLoadFinished(loader: Loader<List<ContestEntry>>, data: List<ContestEntry>) {
        contestRecyclerViewAdapter!!.swapData(data)

        if (data.isEmpty()) {
            contestsRecyclerView!!.visibility = View.GONE
            emptyLayout!!.visibility = View.VISIBLE
        } else {
            emptyLayout!!.visibility = View.GONE
            contestsRecyclerView!!.visibility = View.VISIBLE
        }
    }

    override fun onLoaderReset(loader: Loader<List<ContestEntry>>) {
        contestRecyclerViewAdapter!!.swapData(null)
    }

    fun toggleSubscription(contestEntry: ContestEntry) {
        notifierRepository!!.updateContest(contestEntry)
        loaderManager.restartLoader(CONTEST_LOADER_ID, null, this)
    }

    interface OnContestsListEventsListener {

        fun onSubscribedClicked(contestEntry: ContestEntry)

        fun onContestClicked(contestEntry: ContestEntry)

        fun onContestSyncStarted()

        fun onContestSyncFinished(contestEntries: List<ContestEntry>)
    }

    companion object {
        const val CONTEST_LOADER_ID = 1
    }
}

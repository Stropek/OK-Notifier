package com.przemolab.oknotifier.adapters

import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView

import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.fragments.ContestsListFragment
import com.przemolab.oknotifier.fragments.ContestsListFragment.OnContestsListEventsListener

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import com.przemolab.oknotifier.data.entries.ContestEntry

class ContestRecyclerViewAdapter(listener: ContestsListFragment.OnContestsListEventsListener?) : RecyclerView.Adapter<ContestRecyclerViewAdapter.ViewHolder>() {

    private val onContestClickedListener: OnContestsListEventsListener? = listener

    private var contests: List<ContestEntry>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contest, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val context = holder.view.context
        val contestEntry = contests!![position]

        if (contestEntry.subscribed) {
            holder.item!!.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGreen))
            holder.subscribeButton!!.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGreen))
            holder.subscribeButton!!.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_remove))
        } else {
            holder.item!!.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
            holder.subscribeButton!!.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGrey))
            holder.subscribeButton!!.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_add))
        }

        holder.name!!.text = contestEntry.name
        holder.dates!!.text = String.format("%s - %s", contestEntry.startDateFormatted, contestEntry.endDateFormatted)
        holder.contestantsNumber!!.text = contestEntry.numberOfContestants.toString()
        holder.problemsNumber!!.text = contestEntry.numberOfProblems.toString()

        holder.view.setOnClickListener {
            onContestClickedListener?.onContestClicked(contestEntry)
        }
        holder.subscribeButton!!.setOnClickListener {
            onContestClickedListener?.onSubscribedClicked(contestEntry)
        }
    }

    override fun getItemCount(): Int {
        return if (contests == null) {
            0
        } else contests!!.size
    }

    fun swapData(data: List<ContestEntry>?) {
        if (data == null)
            contests = ArrayList()
        else {
            contests = data
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder internal constructor(internal var view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.contestItem_fl) @JvmField
        internal var item: FrameLayout? = null
        @BindView(R.id.contestName_tv) @JvmField
        internal var name: TextView? = null
        @BindView(R.id.contestDates_tv) @JvmField
        internal var dates: TextView? = null
        @BindView(R.id.contestants_tv) @JvmField
        internal var contestantsNumber: TextView? = null
        @BindView(R.id.problems_tv) @JvmField
        internal var problemsNumber: TextView? = null
        @BindView(R.id.subscribe_ib) @JvmField
        internal var subscribeButton: ImageButton? = null

        init {
            ButterKnife.bind(this, view)
        }
    }
}

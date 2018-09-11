package com.przemolab.oknotifier.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageButton
import android.widget.TextView

import com.przemolab.oknotifier.R
import com.przemolab.oknotifier.models.Contestant

import java.util.ArrayList

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick

class ContestantRecyclerViewAdapter : RecyclerView.Adapter<ContestantRecyclerViewAdapter.ViewHolder>() {

    private var contestants: List<Contestant>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contestant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val contestant = contestants!![position]

        holder.userName!!.text = contestant.name
        holder.userTime!!.text = contestant.time.toString()
        holder.solved!!.text = contestant.problemsSolved.toString()
        holder.submitted!!.text = contestant.problemsSubmitted.toString()
        holder.failed!!.text = contestant.problemsFailed.toString()
        holder.notTried!!.text = contestant.problemsNotTried.toString()

        holder.expand!!.setOnClickListener { v ->
            val deg = v.rotation + 180f
            v.animate().rotation(deg).interpolator = AccelerateDecelerateInterpolator()
        }
    }

    override fun getItemCount(): Int {
        return if (contestants == null) {
            0
        } else contestants!!.size
    }

    fun swapData(data: List<Contestant>?) {
        if (data == null)
            contestants = ArrayList()
        else {
            contestants = data
            notifyDataSetChanged()
        }
    }

    inner class ViewHolder internal constructor(internal var view: View) : RecyclerView.ViewHolder(view) {

        @BindView(R.id.userName_tv) @JvmField
        internal var userName: TextView? = null
        @BindView(R.id.userTime_tv) @JvmField
        internal var userTime: TextView? = null
        @BindView(R.id.solved_tv) @JvmField
        internal var solved: TextView? = null
        @BindView(R.id.submitted_tv) @JvmField
        internal var submitted: TextView? = null
        @BindView(R.id.failed_tv) @JvmField
        internal var failed: TextView? = null
        @BindView(R.id.notTried_tv) @JvmField
        internal var notTried: TextView? = null
        @BindView(R.id.expand_ib) @JvmField
        internal var expand: ImageButton? = null

        @OnClick(R.id.expand_ib)
        fun expandClicked(button: ImageButton) {
            val deg = button.rotation + 180f
            button.animate().rotation(deg).interpolator = AccelerateDecelerateInterpolator()
        }

        init {
            ButterKnife.bind(this, view)
        }
    }
}
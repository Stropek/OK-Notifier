package com.przemolab.oknotifier.data;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.fragments.ContestsListFragment;
import com.przemolab.oknotifier.fragments.ContestsListFragment.OnContestsListEventsListener;
import com.przemolab.oknotifier.models.Contest;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContestRecyclerViewAdapter extends RecyclerView.Adapter<ContestRecyclerViewAdapter.ViewHolder> {

    private final OnContestsListEventsListener onContestClickedListener;

    private List<? extends Contest> contests;

    public ContestRecyclerViewAdapter(ContestsListFragment.OnContestsListEventsListener listener) {
        this.onContestClickedListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contest, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Resources resources = holder.view.getResources();

        final Contest contest = contests.get(position);

        if (contest.isSubscribed()) {
            holder.item.setBackgroundColor(resources.getColor(R.color.lightGreen));
            holder.subscribeButton.setBackgroundColor(resources.getColor(R.color.lightGreen));
            holder.subscribeButton.setImageDrawable(resources.getDrawable(R.drawable.ic_remove));
        } else {
            holder.item.setBackgroundColor(resources.getColor(R.color.lightGrey));
            holder.subscribeButton.setBackgroundColor(resources.getColor(R.color.lightGrey));
            holder.subscribeButton.setImageDrawable(resources.getDrawable(R.drawable.ic_add));
        }

        holder.name.setText(contest.getName());
        holder.dates.setText(String.format("%s - %s", contest.getStartDateFormatted(), contest.getEndDateFormatted()));
        holder.contestantsNumber.setText(String.valueOf(contest.getNumberOfContestants()));
        holder.problemsNumber.setText(String.valueOf(contest.getNumberOfProblems()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onContestClickedListener != null) {
                    onContestClickedListener.onContestClicked(contest);
                }
            }
        });
        holder.subscribeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onContestClickedListener != null) {
                    onContestClickedListener.onSubscribedClicked(contest);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (contests == null) {
            return 0;
        }
        return contests.size();
    }

    public void swapData(List<? extends Contest> data) {
        if (data == null)
            contests = new ArrayList<>();
        else {
            contests = data;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        @BindView(R.id.contestItem_fl) FrameLayout item;
        @BindView(R.id.contestName_tv) TextView name;
        @BindView(R.id.contestDates_tv) TextView dates;
        @BindView(R.id.contestants_tv) TextView contestantsNumber;
        @BindView(R.id.problems_tv) TextView problemsNumber;
        @BindView(R.id.subscribe_ib) ImageButton subscribeButton;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}

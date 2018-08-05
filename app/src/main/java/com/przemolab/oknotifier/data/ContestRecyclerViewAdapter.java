package com.przemolab.oknotifier.data;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.fragments.ContestsListFragment.OnContestClickedListener;
import com.przemolab.oknotifier.models.Contest;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContestRecyclerViewAdapter extends RecyclerView.Adapter<ContestRecyclerViewAdapter.ViewHolder> {

    private final List<Contest> contests;
    private final OnContestClickedListener onContestClickedListener;

    public ContestRecyclerViewAdapter(List<Contest> items, OnContestClickedListener listener) {
        contests = items;
        onContestClickedListener = listener;
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
        Contest contest = contests.get(position);

        holder.name.setText(contest.getName());
        holder.dates.setText(String.format("%s - %s", contest.getStartDateFormatted(), contest.getEndDateFormatted()));
        holder.contestantsNumber.setText(String.valueOf(contest.getNumberOfContestants()));
        holder.problemsNumber.setText(String.valueOf(contest.getNumberOfProblems()));

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onContestClickedListener) {
                    onContestClickedListener.onContestClicked(holder.contest);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return contests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        Contest contest;
        View view;

        @BindView(R.id.contestName_tv) TextView name;
        @BindView(R.id.contestDates_tv) TextView dates;
        @BindView(R.id.contestants_tv) TextView contestantsNumber;
        @BindView(R.id.problems_tv) TextView problemsNumber;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}

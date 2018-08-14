package com.przemolab.oknotifier.data;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;

import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.models.Contestant;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContestantRecyclerViewAdapter extends RecyclerView.Adapter<ContestantRecyclerViewAdapter.ViewHolder> {

//    private final OnListFragmentInteractionListener mListener;

    private List<Contestant> contestants;

    public ContestantRecyclerViewAdapter() {
//        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contestant, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Resources resources = holder.view.getResources();

        final Contestant contestant = contestants.get(position);

        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float deg = v.getRotation() + 180F;
                v.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (contestants == null) {
            return 0;
        }
        return contestants.size();
    }

    public void swapData(List<Contestant> data) {
        if (data == null)
            contestants = new ArrayList<>();
        else {
            contestants = data;
            notifyDataSetChanged();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        @BindView(R.id.userName_tv) TextView userName;
        @BindView(R.id.userTime_tv) TextView userTime;
        @BindView(R.id.solved_tv) TextView solved;
        @BindView(R.id.submitted_tv) TextView submitted;
        @BindView(R.id.failed_tv) TextView failed;
        @BindView(R.id.notTried_tv) TextView notTried;
        @BindView(R.id.expand_ib) ImageButton expand;

        @OnClick(R.id.expand_ib)
        public void expandClicked(ImageButton button) {
            float deg = button.getRotation() + 180F;
            button.animate().rotation(deg).setInterpolator(new AccelerateDecelerateInterpolator());
        }

        ViewHolder(View view) {
            super(view);
            this.view = view;
            ButterKnife.bind(this, view);
        }
    }
}
package com.przemolab.oknotifier.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.data.ContestRecyclerViewAdapter;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.services.OpenKattisService;

import java.util.Objects;

import javax.inject.Inject;

public class ContestsListFragment extends Fragment {

    @Inject
    public OpenKattisService openKattisService;

    private int columnCount = 1;
    private OnContestClickedListener onContestClickedListener;

    public ContestsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotifierApp app = (NotifierApp) Objects.requireNonNull(getActivity()).getApplication();
        app.appComponent.inject(this);

        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (columnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
            }
            recyclerView.setAdapter(new ContestRecyclerViewAdapter(openKattisService.getOngoingContests(), onContestClickedListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContestClickedListener) {
            onContestClickedListener = (OnContestClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContestClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onContestClickedListener = null;
    }

    public interface OnContestClickedListener {
        void onContestClicked(Contest contest);
    }
}

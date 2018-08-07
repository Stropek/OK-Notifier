package com.przemolab.oknotifier.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.data.ContestLoader;
import com.przemolab.oknotifier.data.ContestRecyclerViewAdapter;
import com.przemolab.oknotifier.modules.ContestRepository;
import com.przemolab.oknotifier.models.Contest;
import com.przemolab.oknotifier.modules.OpenKattisService;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContestsListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<List<Contest>> {

    @BindView(R.id.contests_list_rv) public RecyclerView contestsRecyclerView;
    @BindView(R.id.empty_cl) public ConstraintLayout emptyLayout;

//    @Inject
//    public OpenKattisService openKattisService;
    @Inject
    public ContestRepository contestRepository;

    private ContestRecyclerViewAdapter contestRecyclerViewAdapter = null;

    private static final int CONTEST_LOADER_ID = 1;
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

        contestRecyclerViewAdapter = new ContestRecyclerViewAdapter(onContestClickedListener);

        getLoaderManager().initLoader(CONTEST_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contest_list, container, false);
        ButterKnife.bind(this, view);

        Context context = view.getContext();

        if (columnCount <= 1) {
            contestsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            contestsRecyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }

        contestsRecyclerView.setAdapter(contestRecyclerViewAdapter);

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

    @NonNull
    @Override
    public Loader<List<Contest>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ContestLoader(getActivity(), contestRepository);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Contest>> loader, List<Contest> data) {
        contestRecyclerViewAdapter.swapData(data);

        if (data.isEmpty()) {
            contestsRecyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            contestsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Contest>> loader) {
        contestRecyclerViewAdapter.swapData(null);
    }

    public interface OnContestClickedListener {
        void onContestClicked(Contest contest);
    }
}

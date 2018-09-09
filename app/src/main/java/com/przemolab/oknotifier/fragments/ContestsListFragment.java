package com.przemolab.oknotifier.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.interfaces.INotifierRepository;
import com.przemolab.oknotifier.interfaces.IOpenKattisService;
import com.przemolab.oknotifier.sync.RetrieveContestsTask;
import com.przemolab.oknotifier.sync.SqliteContestLoader;
import com.przemolab.oknotifier.data.ContestRecyclerViewAdapter;
import com.przemolab.oknotifier.enums.SortOrder;
import com.przemolab.oknotifier.models.Contest;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContestsListFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<List<? extends Contest>> {

    public static final int CONTEST_LOADER_ID = 1;
    private SortOrder sortOrder = SortOrder.SubscribedFirst;

    @BindView(R.id.contestsList_rv) public RecyclerView contestsRecyclerView;
    @BindView(R.id.empty_cl) public ConstraintLayout emptyLayout;

    @Inject
    public INotifierRepository notifierRepository;
    @Inject
    public IOpenKattisService openKattisService;

    private ContestRecyclerViewAdapter contestRecyclerViewAdapter = null;

    private OnContestsListEventsListener onContestListEventsListener;

    public ContestsListFragment() {
    }

    @OnClick(R.id.sync_ib)
    public void onSyncClicked() {
        new RetrieveContestsTask(openKattisService, notifierRepository, onContestListEventsListener).execute();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(Constants.BundleKeys.INSTANCE.getSortOrder(), sortOrder);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotifierApp app = (NotifierApp) Objects.requireNonNull(getActivity()).getApplication();
        app.getAppComponent().inject(this);

        Bundle arguments = getArguments();
        if (savedInstanceState == null) {
            assert arguments != null;
            sortOrder = (SortOrder) arguments.get(Constants.BundleKeys.INSTANCE.getSortOrder());
        } else {
            sortOrder = (SortOrder) savedInstanceState.get(Constants.BundleKeys.INSTANCE.getSortOrder());
        }

        contestRecyclerViewAdapter = new ContestRecyclerViewAdapter(onContestListEventsListener);

        getLoaderManager().initLoader(CONTEST_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_contest_list, container, false);
        ButterKnife.bind(this, view);

        Context context = view.getContext();

        contestsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        contestsRecyclerView.setAdapter(contestRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContestsListEventsListener) {
            onContestListEventsListener = (OnContestsListEventsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContestsListEventsListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onContestListEventsListener = null;
    }

    @NonNull
    @Override
    public Loader<List<? extends Contest>> onCreateLoader(int id, @Nullable Bundle args) {
        return new SqliteContestLoader(getActivity(), notifierRepository, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<? extends Contest>> loader, List<? extends Contest> data) {
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
    public void onLoaderReset(@NonNull Loader<List<? extends Contest>> loader) {
        contestRecyclerViewAdapter.swapData(null);
    }

    public void toggleSubscription(Contest contest) {
        notifierRepository.updateContest(contest);
        getLoaderManager().restartLoader(CONTEST_LOADER_ID, null, this);
    }

    public interface OnContestsListEventsListener {

        void onSubscribedClicked(Contest contest);

        void onContestClicked(Contest contest);

        void onContestSyncStarted();

        void onContestSyncFinished(List<Contest> contests);
    }
}

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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.przemolab.oknotifier.BuildConfig;
import com.przemolab.oknotifier.Constants;
import com.przemolab.oknotifier.NotifierApp;
import com.przemolab.oknotifier.R;
import com.przemolab.oknotifier.sync.RetrieveContestantsTask;
import com.przemolab.oknotifier.sync.SqliteContestantLoader;
import com.przemolab.oknotifier.data.ContestantRecyclerViewAdapter;
import com.przemolab.oknotifier.models.Contestant;
import com.przemolab.oknotifier.modules.NotifierRepository;
import com.przemolab.oknotifier.modules.OpenKattisService;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContestantsListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<Contestant>> {

    public static final int CONTESTANT_LOADER_ID = 1;
    private String contestId;
    private int mColumnCount = 1;
    private OnContestantsListEventListener onContestantsListEventsListener;

    @BindView(R.id.contestantsList_rv) public RecyclerView contestantsRecyclerView;
    @BindView(R.id.empty_cl) public ConstraintLayout emptyLayout;

    @Inject
    public NotifierRepository notifierRepository;
    @Inject
    public OpenKattisService openKattisService;

    private ContestantRecyclerViewAdapter contestantRecyclerViewAdapter = null;

    public ContestantsListFragment() {
    }

    @OnClick(R.id.sync_ib)
    public void onSyncClicked() {
        new RetrieveContestantsTask(openKattisService, notifierRepository, contestId, onContestantsListEventsListener).execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NotifierApp app = (NotifierApp) Objects.requireNonNull(getActivity()).getApplication();
        app.appComponent.inject(this);

        Bundle arguments = getArguments();
        if (savedInstanceState == null) {
            assert arguments != null;
            contestId = arguments.getString(Constants.BundleKeys.ContestId);
        } else {
            contestId = savedInstanceState.getString(Constants.BundleKeys.ContestId);
        }

        contestantRecyclerViewAdapter = new ContestantRecyclerViewAdapter();

        getLoaderManager().initLoader(CONTESTANT_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contestants_list, container, false);
        ButterKnife.bind(this, view);

        Context context = view.getContext();

        if (mColumnCount <= 1) {
            contestantsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            contestantsRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        contestantsRecyclerView.setAdapter(contestantRecyclerViewAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnContestantsListEventListener) {
            onContestantsListEventsListener = (OnContestantsListEventListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContestantsListEventListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onContestantsListEventsListener = null;
    }

    @NonNull
    @Override
    public Loader<List<Contestant>> onCreateLoader(int id, @Nullable Bundle args) {
        return new SqliteContestantLoader(getActivity(), notifierRepository, openKattisService, contestId, onContestantsListEventsListener);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Contestant>> loader, List<Contestant> data) {
        contestantRecyclerViewAdapter.swapData(data);

        if (data.isEmpty()) {
            contestantsRecyclerView.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            emptyLayout.setVisibility(View.GONE);
            contestantsRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Contestant>> loader) {
        contestantRecyclerViewAdapter.swapData(null);
    }

    public interface OnContestantsListEventListener {

        void onContestantsSyncStarted();

        void onContestantsSyncFinished(List<Contestant> contestants, boolean restartLoader);
    }
}

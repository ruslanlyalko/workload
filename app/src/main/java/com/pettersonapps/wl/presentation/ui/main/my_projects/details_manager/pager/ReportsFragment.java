package com.pettersonapps.wl.presentation.ui.main.my_projects.details_manager.pager;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.presentation.ui.report.ReportsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReportsFragment extends Fragment {

    private static final String KEY_REPORT = "report";
    private static final String KEY_HOLIDAY = "holiday";
    private static final String KEY_ALLOW_EDIT = "allow_edit";
    private static final String KEY_SHOW_ACTIONS = "show_buttons";
    @BindView(R.id.text_holiday_name) TextView mTextHolidayName;
    @BindView(R.id.card_holiday) MaterialCardView mCardHoliday;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;

    private Unbinder mUnbinder;

    private Holiday mHoliday = null;
    private boolean mAllowEdit;
    private boolean mShowButtons;
    private ArrayList<Report> mReports;
    private ReportsAdapter mReportsAdapter;

    public static ReportsFragment newInstance(@Nullable List<Report> report, @Nullable Holiday holiday, final boolean isAllowEditPastReports, final boolean showButtons) {
        Bundle args = new Bundle();
        if(report != null)
            args.putParcelableArrayList(KEY_REPORT, (ArrayList<? extends Parcelable>) report);
        if(holiday != null)
            args.putParcelable(KEY_HOLIDAY, holiday);
        args.putBoolean(KEY_ALLOW_EDIT, isAllowEditPastReports);
        args.putBoolean(KEY_SHOW_ACTIONS, showButtons);
        ReportsFragment fragment = new ReportsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mReports = getArguments().getParcelableArrayList(KEY_REPORT);
            mHoliday = getArguments().getParcelable(KEY_HOLIDAY);
            mAllowEdit = getArguments().getBoolean(KEY_ALLOW_EDIT, false);
            mShowButtons = getArguments().getBoolean(KEY_SHOW_ACTIONS, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_reports, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        bind(mReports);
        if(mHoliday == null)
            mCardHoliday.setVisibility(View.GONE);
        else {
            mCardHoliday.setVisibility(View.VISIBLE);
            mTextHolidayName.setText(mHoliday.getTitle());
        }
        return rootView;
    }

    private void bind(final ArrayList<Report> reports) {
        mReportsAdapter = new ReportsAdapter(null);
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerReports.setAdapter(mReportsAdapter);
        mReportsAdapter.setData(reports);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}

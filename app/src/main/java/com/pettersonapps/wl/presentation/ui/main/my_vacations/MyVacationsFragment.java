package com.pettersonapps.wl.presentation.ui.main.my_vacations;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.report.ReportsAdapter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MyVacationsFragment extends BaseFragment<MyVacationsPresenter> implements MyVacationsView {

    private static final String KEY_USER = "user";
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.text_common) TextView mTextCommon;
    @BindView(R.id.text_placeholder) TextView mTextPlaceholder;
    private ReportsAdapter mReportsAdapter;

    public static MyVacationsFragment newInstance(User user) {
        Bundle args = new Bundle();
        MyVacationsFragment fragment = new MyVacationsFragment();
        args.putParcelable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_vacations, menu);
    }


    @Override
    protected int getContentView() {
        return R.layout.fragment_my_vacations;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new MyVacationsPresenter(args.getParcelable(KEY_USER)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_vacations);
        hideFab();
        mReportsAdapter = new ReportsAdapter();
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerReports.setAdapter(mReportsAdapter);
        getPresenter().onViewReady();
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> vacationReportsData) {
        vacationReportsData.observe(this, list -> getPresenter().setReports(list));
    }

    @Override
    public void showReportsByYear(final Date firstWorkingDate, final SparseIntArray years) {
        String text = "First Working Day: " + DateUtils.toStringStandardDate(firstWorkingDate) + "\n";
        for (int i = 0; i < years.size(); i++) {
            String day = (years.keyAt(i) + 1) + getDayOfMonthSuffix(years.keyAt(i) + 1);
            int count = years.get(years.keyAt(i));
            text = text + getString(count == 1 ? R.string.day_taken : R.string.days_taken, day, count);
        }
        mTextCommon.setText(text);
    }

    @Override
    public void setReportsToAdapter(final List<Report> list) {
        mReportsAdapter.setData(list);
        mTextPlaceholder.setVisibility((list != null && list.isEmpty()) ? VISIBLE : GONE);
    }

    private String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}

package com.pettersonapps.wl.presentation.ui.main.alerts;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.main.workload.adapter.ReportsAdapter;
import com.pettersonapps.wl.presentation.ui.main.users.adapter.UsersAdapter;
import com.pettersonapps.wl.presentation.ui.main.users.details.UserDetailsActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class AlertsFragment extends BaseFragment<AlertsPresenter> implements AlertsView {

    @BindView(R.id.recycler_users) RecyclerView mRecyclerUsers;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.text_reports_placeholder) TextView mTextReportsPlaceholder;
    @BindView(R.id.text_users_placeholder) TextView mTextUsersPlaceholder;
    private UsersAdapter mUsersAdapter;
    private ReportsAdapter mReportsAdapter;

    public static AlertsFragment newInstance() {
        Bundle args = new Bundle();
        AlertsFragment fragment = new AlertsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_alerts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getPresenter().getDate());
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    Date newDate = DateUtils.getDate(calendar.getTime(), year, monthOfYear, dayOfMonth);
                    getPresenter().onDateChanged(newDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setMaxDate(DateUtils.getYesterday());
                datePickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
                datePickerDialog.show(getBaseActivity().getFragmentManager(), "date");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_alerts;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new AlertsPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_alerts);
        setupAdapters();
        getPresenter().onViewReady();
    }

    private void setupAdapters() {
        mUsersAdapter = new UsersAdapter(new OnItemClickListener() {
            @Override
            public void onItemClicked(final View view, final int position) {
                startActivity(UserDetailsActivity.getLaunchIntent(getContext(), mUsersAdapter.getData().get(position)));
            }

            @Override
            public void onItemLongClicked(final View view, final int position) {
            }
        });
        mRecyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerUsers.setAdapter(mUsersAdapter);
        mReportsAdapter = new ReportsAdapter(null);
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerReports.setAdapter(mReportsAdapter);
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> allWrongReports) {
        allWrongReports.observe(this, list -> {
            if (list != null)
                mTextReportsPlaceholder.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            mReportsAdapter.setData(list);
        });
    }

    @Override
    public void showUsers(final MutableLiveData<List<User>> allUsersWithoutReports) {
        allUsersWithoutReports.observe(this, list -> {
            if (list != null)
                mTextUsersPlaceholder.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            mUsersAdapter.setData(list);
        });
    }

    @Override
    public void showDate(final Date date) {
        setToolbarTitle(getString(R.string.title_alerts) + " (" + DateUtils.toStringDate(date) + ")");
    }
}

package com.pettersonapps.wl.presentation.ui.main.dashboard;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.main.alerts.settings.AlertsSettingsActivity;
import com.pettersonapps.wl.presentation.ui.main.dashboard.export.ExportActivity;
import com.pettersonapps.wl.presentation.ui.main.users.adapter.UsersAdapter;
import com.pettersonapps.wl.presentation.ui.main.users.details.UserDetailsActivity;
import com.pettersonapps.wl.presentation.ui.main.users.edit.UserEditActivity;
import com.pettersonapps.wl.presentation.ui.report.ReportClickListener;
import com.pettersonapps.wl.presentation.ui.report.ReportsAdapter;
import com.pettersonapps.wl.presentation.utils.ColorUtils;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class DashboardFragment extends BaseFragment<DashboardPresenter> implements DashboardView, ReportClickListener {

    @BindView(R.id.spinner_projects) Spinner mSpinnerProjects;
    @BindView(R.id.spinner_users) Spinner mSpinnerUsers;
    @BindView(R.id.spinner_status) Spinner mSpinnerStatus;
    @BindView(R.id.calendar_view) CompactCalendarView mCalendarView;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.text_reports_header) TextView mTextReportsHeader;
    @BindView(R.id.recycler_users) RecyclerView mRecyclerUsers;
    @BindView(R.id.text_month) TextSwitcher mTextMonth;
    @BindView(R.id.text_users_header) TextView mTextUsersHeader;
    @BindView(R.id.layout_results) LinearLayout mLayoutResults;
    @BindView(R.id.layout_filters) LinearLayout mLayoutFilters;

    private ReportsAdapter mReportsAdapter;
    private UsersAdapter mUsersAdapter;
    private Date mPrevDate = new Date();
    private String mPrevDateStr = "";

    public static DashboardFragment newInstance() {
        Bundle args = new Bundle();
        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                startActivity(ExportActivity.getLaunchIntent(getBaseActivity()));
                return true;
            case R.id.action_filter:
                if (mLayoutFilters.getVisibility() == View.VISIBLE) {
                    mSpinnerProjects.setSelection(0);
                    mSpinnerUsers.setSelection(0);
                    mSpinnerStatus.setSelection(0);
                    getPresenter().setFilter(
                            mSpinnerProjects.getSelectedItem().toString(),
                            mSpinnerUsers.getSelectedItem().toString(),
                            mSpinnerStatus.getSelectedItem().toString()
                    );
                    mLayoutFilters.setVisibility(View.GONE);
                } else {
                    mLayoutFilters.setVisibility(View.VISIBLE);
                }
                return true;
            case R.id.action_settings:
                startActivity(AlertsSettingsActivity.getLaunchIntent(getContext()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_dashboard;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new DashboardPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_dashboard);
        hideFab();
        mTextMonth.setText(DateUtils.getMonth(new Date()));
        setupCalendar();
        setupAdapters();
        setupSpinners();
        getPresenter().onViewReady();
    }

    private void setupSpinners() {
        AdapterView.OnItemSelectedListener tt = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (mSpinnerProjects.getSelectedItem() != null
                        && mSpinnerUsers.getSelectedItem() != null
                        && mSpinnerStatus.getSelectedItem() != null) {
                    getPresenter().setFilter(
                            mSpinnerProjects.getSelectedItem().toString(),
                            mSpinnerUsers.getSelectedItem().toString(),
                            mSpinnerStatus.getSelectedItem().toString()
                    );
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        };
        mSpinnerProjects.setOnItemSelectedListener(tt);
        mSpinnerUsers.setOnItemSelectedListener(tt);
        mSpinnerStatus.setOnItemSelectedListener(tt);
    }

    private void setupAdapters() {
        mReportsAdapter = new ReportsAdapter(this);
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerReports.setAdapter(mReportsAdapter);
        mUsersAdapter = new UsersAdapter(new OnItemClickListener() {
            @Override
            public void onItemClicked(final View view, final int position) {
                startActivity(UserDetailsActivity.getLaunchIntent(getContext(), mUsersAdapter.getData().get(position)));
            }

            @Override
            public void onItemLongClicked(final View view, final int position) {
                startActivity(UserEditActivity.getLaunchIntent(getContext(), mUsersAdapter.getData().get(position)));
            }
        });
        mRecyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerUsers.setAdapter(mUsersAdapter);
    }

    private void setupCalendar() {
        mCalendarView.setUseThreeLetterAbbreviation(true);
        mCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        mCalendarView.displayOtherMonthDays(true);
        mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(final Date dateClicked) {
                getPresenter().fetchReports(dateClicked);
            }

            @Override
            public void onMonthScroll(final Date firstDayOfNewMonth) {
                getPresenter().fetchReportsForMonth(firstDayOfNewMonth, DateUtils.getLastDateOfMonth(firstDayOfNewMonth));
                setNewDate(firstDayOfNewMonth);
            }
        });
    }

    private void setNewDate(Date newDate) {
        String month = DateUtils.getMonth(newDate);
        if (month.equals(mPrevDateStr)) return;
        if (newDate.before(mPrevDate)) {
            Animation in = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left);
            Animation out = AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right);
            mTextMonth.setInAnimation(in);
            mTextMonth.setOutAnimation(out);
        } else {
            Animation in = AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right);
            Animation out = AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left);
            mTextMonth.setInAnimation(in);
            mTextMonth.setOutAnimation(out);
        }
        mTextMonth.setText(month);
        mPrevDateStr = month;
        mPrevDate = newDate;
    }

    @Override
    public void showSpinnerProjectsData(final MutableLiveData<List<Project>> projectsData) {
        projectsData.observe(this, projects -> {
            if (projects == null) return;
            List<String> list = new ArrayList<>();
            for (Project project : projects) {
                list.add(project.getTitle());
            }
            list.add(0, DashboardPresenter.KEY_PROJECT);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerProjects.setAdapter(dataAdapter);
        });
    }

    @Override
    public void showSpinnerUsersData(final MutableLiveData<List<User>> usersData) {
        usersData.observe(this, users -> {
            if (users == null) return;
            getPresenter().setUsers(users);
            List<String> list = new ArrayList<>();
            for (User user : users) {
                list.add(user.getName());
            }
            list.add(0, DashboardPresenter.KEY_USER);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerUsers.setAdapter(dataAdapter);
        });
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> reportsData) {
        reportsData.observe(this, reports -> {
            if (reports == null) return;
            getPresenter().setReports(reports);
        });
    }

    @Override
    public void showReportsOnCalendar(final List<Report> reports) {
        mCalendarView.removeAllEvents();
        for (Report report : reports) {
            mCalendarView.addEvent(new Event(ContextCompat.getColor(getContext(),
                    ColorUtils.getTextColorByStatus(getResources(), report.getStatus())), report.getDate().getTime()), true);
        }
        mCalendarView.invalidate();
    }

    @Override
    public void showReportsOnList(final List<Report> list) {
        mReportsAdapter.setData(list);
        mTextReportsHeader.setText(getString(R.string.text_total_filled, list.size()));
    }

    @Override
    public void showUserDetails(final User user) {
        startActivity(UserDetailsActivity.getLaunchIntent(getContext(), user));
    }

    @Override
    public void showUsersWithoutReports(final List<User> allUsersWithoutReports) {
        if (allUsersWithoutReports == null) {
            return;
        }
        mTextUsersHeader.setText(getString(R.string.text_users_without_reports, allUsersWithoutReports.size()));
        mUsersAdapter.setData(allUsersWithoutReports);
    }

    @OnClick({R.id.title, R.id.text_month})
    public void onClick() {
        Date today = new Date();
        mCalendarView.setCurrentDate(today);
        setNewDate(today);
        getPresenter().fetchReportsForMonth(DateUtils.getFirstDateOfMonth(today), DateUtils.getLastDateOfMonth(today));
    }

    @Override
    public void onReportClicked(final Report report) {
        getPresenter().onReportClicked(report.getUserId());
    }

    @OnClick({R.id.text_reports_header, R.id.text_users_header})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_reports_header:
                mRecyclerReports.setVisibility(mRecyclerReports.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
            case R.id.text_users_header:
                mRecyclerUsers.setVisibility(mRecyclerUsers.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                break;
        }
    }
}

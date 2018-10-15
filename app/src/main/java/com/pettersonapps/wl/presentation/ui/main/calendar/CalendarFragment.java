package com.pettersonapps.wl.presentation.ui.main.calendar;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.main.calendar.export.ExportActivity;
import com.pettersonapps.wl.presentation.ui.main.workload.adapter.ReportsAdapter;
import com.pettersonapps.wl.presentation.utils.ColorUtils;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class CalendarFragment extends BaseFragment<CalendarPresenter> implements CalendarView {

    @BindView(R.id.spinner_projects) Spinner mSpinnerProjects;
    @BindView(R.id.spinner_users) Spinner mSpinnerUsers;
    @BindView(R.id.spinner_status) Spinner mSpinnerStatus;
    @BindView(R.id.calendar_view) CompactCalendarView mCalendarView;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.text_placeholder) TextView mTextPlaceholder;
    private ReportsAdapter mReportsAdapter;

    public static CalendarFragment newInstance() {
        Bundle args = new Bundle();
        CalendarFragment fragment = new CalendarFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_calendar, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_export:
                startActivity(ExportActivity.getLaunchIntent(getBaseActivity()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_calendar;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new CalendarPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_calendar);
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
        mReportsAdapter = new ReportsAdapter(null);
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerReports.setAdapter(mReportsAdapter);
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
                setToolbarTitle(getString(R.string.title_calendar) + " (" + DateUtils.getMonth(firstDayOfNewMonth) + ")");
                getPresenter().fetchReportsForMonth(firstDayOfNewMonth, DateUtils.getLastDateOfMonth(firstDayOfNewMonth));
            }
        });
    }

    @Override
    public void showSpinnerProjectsData(final MutableLiveData<List<Project>> projectsData) {
        projectsData.observe(this, projects -> {
            if (projects == null) return;
            List<String> list = new ArrayList<>();
            for (Project project : projects) {
                list.add(project.getTitle());
            }
            list.add(0, "- project -");
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
            List<String> list = new ArrayList<>();
            for (User user : users) {
                list.add(user.getName());
            }
            list.add(0, "- user -");
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
    }
}

package com.pettersonapps.wl.presentation.ui.main.dashboard;

import android.app.AlertDialog;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.login.LoginActivity;
import com.pettersonapps.wl.presentation.ui.main.dashboard.adapter.ReportsAdapter;
import com.pettersonapps.wl.presentation.ui.main.dashboard.report.ReportEditActivity;
import com.pettersonapps.wl.presentation.utils.ColorUtils;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.OnReportClickListener;

import java.util.Date;
import java.util.List;

import butterknife.BindView;

public class DashboardFragment extends BaseFragment<DashboardPresenter> implements DashboardView {

    private static final int RC_REPORT = 1001;
    @BindView(R.id.calendar_view) CompactCalendarView mCalendarView;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.text_holiday_name) TextView mTextHolidayName;
    @BindView(R.id.card_holiday) MaterialCardView mCardHoliday;

    private ReportsAdapter mReportsAdapter;

    public static DashboardFragment newInstance() {
        Bundle args = new Bundle();
        DashboardFragment fragment = new DashboardFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setupCalendar() {
        mCalendarView.setUseThreeLetterAbbreviation(true);
        mCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        mCalendarView.displayOtherMonthDays(true);
        mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(final Date dateClicked) {
                getPresenter().fetchReportsForDate(dateClicked);
            }

            @Override
            public void onMonthScroll(final Date firstDayOfNewMonth) {
                setToolbarTitle(getString(R.string.app_name) + " (" + DateUtils.getMonth(firstDayOfNewMonth) + ")");
            }
        });
    }

    @Override
    public void showUser(final MutableLiveData<User> userData) {
        userData.observe(this, user -> {
            if (user == null) return;
            getPresenter().setUser(user);
            mReportsAdapter.setAllowEdit(user.getIsAllowEditPastReports());
        });
    }

    @Override
    public void showReportsOnCalendar(final MutableLiveData<List<Report>> reportsData) {
        reportsData.observe(this, reports -> {
            if (reports == null) return;
            getPresenter().setReports(reports);
            showCalendarsEvents();
        });
    }

    @Override
    public void showHolidaysOnCalendar(final MutableLiveData<List<Holiday>> holidaysData) {
        holidaysData.observe(this, holidays -> {
            if (holidays == null) return;
            getPresenter().setHolidays(holidays);
            showCalendarsEvents();
        });
    }

    @Override
    public void showHoliday(final String holiday) {
        if (holiday == null)
            mCardHoliday.setVisibility(View.GONE);
        else {
            mCardHoliday.setVisibility(View.VISIBLE);
            mTextHolidayName.setText(holiday);
        }
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> reportsData) {
        reportsData.observe(this, reports -> {
            if ((reports == null || reports.isEmpty())
                    && ((getPresenter().getUser().getIsAllowEditPastReports()) ||
                    (getPresenter().getDate().after(DateUtils.get1DaysAgo().getTime())
                            && getPresenter().getDate().before(DateUtils.get1DaysForward().getTime()))))
                showFab();
            else
                hideFab();
            mReportsAdapter.setData(reports);
        });
    }

    @Override
    public void showErrorAndStartLoginScreen() {
        Toast.makeText(getContext(), R.string.error_blocked, Toast.LENGTH_LONG).show();
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        getBaseActivity().finish();
    }

    @Override
    public void showAdminMenu() {
        //
    }

    @Override
    public void editReport(final User user, final Report report, final List<Holiday> holidays) {
        startActivityForResult(ReportEditActivity.getLaunchIntent(getContext(), user, report, holidays), RC_REPORT);
    }

    @Override
    public void startAddReportScreen(final User user, final Date date, final List<Holiday> holidays) {
        startActivityForResult(ReportEditActivity.getLaunchIntent(getContext(), user, date, holidays), RC_REPORT);
    }

    void showCalendarsEvents() {
        mCalendarView.removeAllEvents();
        List<Report> reports = getPresenter().getReports();
        for (Report report : reports) {
            mCalendarView.addEvent(new Event(ContextCompat.getColor(getContext(),
                    ColorUtils.getTextColorByStatus(getResources(), report.getStatus())), report.getDate().getTime()), true);
        }
        List<Holiday> holidays = getPresenter().getHolidays();
        for (Holiday holiday : holidays) {
            mCalendarView.addEvent(new Event(ContextCompat.getColor(getContext(), R.color.event_holiday),
                    holiday.getDate().getTime()), true);
        }
        mCalendarView.invalidate();
    }

    private void setupAdapters() {
        mReportsAdapter = new ReportsAdapter(new OnReportClickListener() {
            @Override
            public void onReportClicked(final View view, final int position) {
                getPresenter().onReportLongClicked(mReportsAdapter.getData().get(position));
            }

            @Override
            public void onReportRemoveClicked(final View view, final int position) {
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                build.setMessage(R.string.text_delete);
                build.setPositiveButton(R.string.action_delete, (dialog, which) -> {
                    getPresenter().onReportDeleteClicked(mReportsAdapter.getData().get(position));
                    dialog.dismiss();
                });
                build.setNegativeButton(R.string.action_cancel, (dialog, which) -> {
                    dialog.dismiss();
                });
                build.show();
            }
        });
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerReports.setAdapter(mReportsAdapter);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_REPORT) {
            getPresenter().fetchReportsForDate();
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_dashboard, menu);
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
        setToolbarTitle(R.string.app_name);
        setupAdapters();
        setupCalendar();
        getPresenter().onViewReady();
    }

    @Override
    public void onFabClicked() {
        getPresenter().onFabClicked();
    }
}

package com.pettersonapps.wl.presentation.ui.main.workload;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextSwitcher;
import android.widget.Toast;

import com.jetradarmobile.snowfall.SnowfallView;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.login.LoginActivity;
import com.pettersonapps.wl.presentation.ui.main.MainActivity;
import com.pettersonapps.wl.presentation.ui.main.workload.pager.OnReportClickListener;
import com.pettersonapps.wl.presentation.ui.main.workload.pager.ReportsPagerAdapter;
import com.pettersonapps.wl.presentation.ui.main.workload.report.ReportEditActivity;
import com.pettersonapps.wl.presentation.utils.ColorUtils;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.calendar.Event;
import com.pettersonapps.wl.presentation.view.calendar.StatusCalendarView;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class WorkloadFragment extends BaseFragment<WorkloadPresenter> implements WorkloadView, OnReportClickListener {

    private static final int RC_REPORT = 1001;
    @BindView(R.id.calendar_view) StatusCalendarView mCalendarView;
    @BindView(R.id.text_month) TextSwitcher mTextMonth;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.snowfall) SnowfallView mSnowfall;
//    @BindDimen(R.dimen.calendar_height) int mTargetHeight;

    private Date mPrevDate = new Date();
    private String mPrevDateStr = "";
    private ReportsPagerAdapter mReportsPagerAdapter;
    private Report mReportToDelete;

    public static WorkloadFragment newInstance() {
        Bundle args = new Bundle();
        WorkloadFragment fragment = new WorkloadFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void setupCalendar() {
        mCalendarView.setEventIndicatorStyle(StatusCalendarView.FILL_LARGE_INDICATOR);
        mCalendarView.setLocale(TimeZone.getDefault(), Locale.UK);
        mCalendarView.setUseThreeLetterAbbreviation(true);
        mCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        mCalendarView.displayOtherMonthDays(true);
        mTextMonth.setText(DateUtils.getMonth(new Date()));
        mPrevDateStr = DateUtils.getMonth(new Date());
        mCalendarView.setListener(new StatusCalendarView.StatusCalendarViewListener() {
            @Override
            public void onDayClick(final Date dateClicked) {
                getPresenter().fetchReportsForDate(dateClicked);
            }

            @Override
            public void onMonthScroll(final Date firstDayOfNewMonth) {
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
    public void showUser(final MutableLiveData<User> userData) {
        userData.observe(this, user -> {
            if (user == null) return;
            getPresenter().setUser(user);
            mReportsPagerAdapter.setAllowEditPastReports(user.getIsAllowEditPastReports());
            if (user.getIsOldStyleCalendar()) {
                mCalendarView.setEventIndicatorStyle(StatusCalendarView.SMALL_INDICATOR);
//                mCalendarView.setTargetHeight(mTargetHeight);
            } else mCalendarView.setEventIndicatorStyle(StatusCalendarView.FILL_LARGE_INDICATOR);
        });
    }

    @Override
    public void showReportsOnCalendar(final MutableLiveData<List<Report>> reportsData) {
        reportsData.observe(this, reports -> {
            if (reports == null) return;
            getPresenter().setReports(reports);
            mReportsPagerAdapter.setReports(reports);
            showCalendarsEvents();
        });
    }

    @Override
    public void showHolidaysOnCalendar(final MutableLiveData<List<Holiday>> holidaysData) {
        holidaysData.observe(this, holidays -> {
            if (holidays == null) return;
            getPresenter().setHolidays(holidays);
            mReportsPagerAdapter.setHolidays(holidays);
            showCalendarsEvents();
        });
    }

    @Override
    public void showReports(List<Report> reports, final Date date) {
        if ((reports == null || reports.isEmpty())
                && ((getPresenter().getIsAllowEditPastReports() && date.after(DateUtils.get1YearAgo().getTime())) ||
                (getPresenter().getDate().after(DateUtils.get1DaysAgo().getTime())
                        && getPresenter().getDate().before(DateUtils.get1MonthForward().getTime()))))
            showFab();
        else
            hideFab();
        mViewPager.setCurrentItem(mReportsPagerAdapter.getPosByDate(date), false);
    }

    @Override
    public void showErrorAndStartLoginScreen() {
        Toast.makeText(getContext(), R.string.error_blocked, Toast.LENGTH_LONG).show();
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void editReport(final User user, final Report report, final List<Holiday> holidays) {
        startActivityForResult(ReportEditActivity.getLaunchIntent(getContext(), report, holidays), RC_REPORT);
    }

    @Override
    public void copyReport(final User user, final Report report, final List<Holiday> holidays) {
        startActivityForResult(ReportEditActivity.getLaunchIntent(getContext(), report, holidays), RC_REPORT);
    }

    @Override
    public void startAddReportScreen(final User user, final Date date, final List<Holiday> holidays) {
        startActivityForResult(ReportEditActivity.getLaunchIntent(getContext(), date, holidays), RC_REPORT);
    }

    @Override
    public void showWrongDateOnMobileError() {
        showError(getString(R.string.error_check_date));
    }

    @Override
    public void showInternetError() {
        showError(getString(R.string.error_no_internet));
    }

    @Override
    public void showSettings(final MutableLiveData<AppSettings> settings) {
        settings.observe(this, appSettings -> {
            if (appSettings != null)
                mSnowfall.setVisibility(appSettings.getIsSnowing() ? View.VISIBLE : View.INVISIBLE);
        });
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
            mCalendarView.addEvent(new Event(ContextCompat.getColor(getContext(), R.color.bg_event_holiday),
                    holiday.getDate().getTime()), true);
        }
        mCalendarView.invalidate();
    }

    private void setupAdapters() {
        mReportsPagerAdapter = new ReportsPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mReportsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int i, final float v, final int i1) {
            }

            @Override
            public void onPageSelected(final int pos) {
                Date date = mReportsPagerAdapter.getDateByPos(pos);
                mCalendarView.setCurrentDate(date);
                getPresenter().fetchReportsForDate(date);
                setNewDate(DateUtils.getFirstDateOfMonth(date));
            }

            @Override
            public void onPageScrollStateChanged(final int i) {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mReportsPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_dashboard, menu);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_workload;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new WorkloadPresenter());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onViewReady(final Bundle state) {
        setToolbarTitle(R.string.app_name);
        setupAdapters();
        setupCalendar();
        getPresenter().onViewReady();
    }

    @Override
    public void onFabClicked() {
        getPresenter().onFabClicked();
    }

    @Override
    public void onDeleteClicked() {
        getPresenter().onReportDeleteClicked(mReportToDelete);
    }

    @OnClick({R.id.title, R.id.text_month})
    public void onClick() {
        mCalendarView.setCurrentDate(new Date());
        getPresenter().fetchReportsForDate(new Date());
        setNewDate(new Date());
    }

    @OnLongClick(R.id.title)
    public boolean onLongClick() {
        mSnowfall.setVisibility(View.VISIBLE);
        return false;
    }

    @Override
    public void onReportClicked(final Report report) {
        if (report != null)
            getPresenter().onReportClicked(report);
    }

    @Override
    public void onReportCopyClicked(final Report report) {
        if (report != null)
            getPresenter().onReportCopyClicked(report);
    }

    @Override
    public void onReportRemoveClicked(final Report report) {
        if (report != null) {
            ((MainActivity) getBaseActivity()).onShowDeleteMenu();
            mReportToDelete = report;
        }
    }
}

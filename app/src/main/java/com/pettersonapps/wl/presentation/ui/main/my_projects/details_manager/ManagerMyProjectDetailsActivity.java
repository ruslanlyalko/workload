package com.pettersonapps.wl.presentation.ui.main.my_projects.details_manager;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.my_projects.details_manager.pager.ReportsPagerAdapter;
import com.pettersonapps.wl.presentation.ui.main.my_projects.statistics.MyProjectStatisticsActivity;
import com.pettersonapps.wl.presentation.ui.main.workload.pager.ReportPagerAdapter;
import com.pettersonapps.wl.presentation.utils.ColorUtils;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class ManagerMyProjectDetailsActivity extends BaseActivity<ManagerMyProjectDetailsPresenter> implements ManagerMyProjectDetailsView {

    private static final String KEY_PROJECT = "project";
    private static final long ANIMATION_DURATION = 400;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.calendar_view) CompactCalendarView mCalendarView;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.text_month) TextSwitcher mTextMonth;
    @BindView(R.id.text_spent) TextView mTextSpent;
    @BindView(R.id.layout_calendar) LinearLayout mLayoutCalendar;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    private ReportsPagerAdapter mReportPagerAdapter;
    private Date mPrevDate = new Date();
    private String mPrevDateStr = "";

    public static Intent getLaunchIntent(final Context context, Project project) {
        Intent intent = new Intent(context, ManagerMyProjectDetailsActivity.class);
        intent.putExtra(KEY_PROJECT, project);
        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_manager_my_project_details;
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> vacationReportsData) {
        vacationReportsData.observe(this, list -> getPresenter().setReports(list));
    }

    @Override
    public void showReports(List<Report> list) {
        mReportPagerAdapter.setReports(list);
    }

    @Override
    public void showReportOnCalendar(final List<Report> reportsForCurrentDate, final Date date) {
        mViewPager.setCurrentItem(mReportPagerAdapter.getPosByDate(date), false);
    }

    @Override
    public void showHolidaysOnCalendar(final MutableLiveData<List<Holiday>> holidaysData) {
        holidaysData.observe(this, holidays -> {
            if(holidays == null) return;
            getPresenter().setHolidays(holidays);
            mReportPagerAdapter.setHolidays(holidays);
        });
    }


    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void showCalendarsEvents() {
        mCalendarView.removeAllEvents();
        List<Report> reports = getPresenter().getReports();
        for (Report report : reports) {
            mCalendarView.addEvent(new Event(ContextCompat.getColor(getContext(),
                    ColorUtils.getTextColorByStatus(getResources(), report.getStatus())), report.getDateConverted().getTime()), true);
        }
        List<Holiday> holidays = getPresenter().getHolidays();
        for (Holiday holiday : holidays) {
            mCalendarView.addEvent(new Event(ContextCompat.getColor(getContext(), R.color.bg_event_holiday),
                    holiday.getDate().getTime()), true);
        }
        mCalendarView.invalidate();
    }

    @Override
    public void showProjectDetails(final Project project) {
        setToolbarTitle(project.getTitle());
    }

    @Override
    public void showSpentHours(final int spentHours) {
        int hours = spentHours % 8;
        int days = (spentHours - hours) / 8;
        mTextSpent.setText(String.format(Locale.US, "Time spent: %dh", spentHours));
    }

    @Override
    public void showUser(final MutableLiveData<User> myUser) {
        myUser.observe(this, user -> {
            if(user != null) {
                getPresenter().setUser(user);
            }
        });
    }

    @Override
    public void invalidateMenu() {
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_project_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == R.id.action_statistics) {
            startActivity(MyProjectStatisticsActivity.getLaunchIntent(getContext(), getPresenter().getProject()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(KEY_PROJECT, getPresenter().getProject());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ManagerMyProjectDetailsPresenter(intent.getParcelableExtra(KEY_PROJECT)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_project_details);
        setupAdapter();
        setupCalendar();
        getPresenter().onViewReady();
    }

    @OnClick(R.id.text_month)
    public void onClick() {
        mCalendarView.setCurrentDate(new Date());
        getPresenter().fetchReportsForDate(new Date());
        setNewDate(new Date());
    }

    private void setupAdapter() {
        mReportPagerAdapter = new ReportsPagerAdapter(getSupportFragmentManager(), false);
        mViewPager.setAdapter(mReportPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int i, final float v, final int i1) {
            }

            @Override
            public void onPageSelected(final int pos) {
                Date date = mReportPagerAdapter.getDateByPos(pos);
                mCalendarView.setCurrentDate(date);
                getPresenter().fetchReportsForDate(date);
                setNewDate(DateUtils.getFirstDateOfMonth(date));
            }

            @Override
            public void onPageScrollStateChanged(final int i) {
            }
        });
    }

    private void setupCalendar() {
        mCalendarView.setLocale(TimeZone.getDefault(), Locale.UK);
        mCalendarView.setUseThreeLetterAbbreviation(true);
        mCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);
        mCalendarView.displayOtherMonthDays(true);
        mTextMonth.setText(DateUtils.getMonth(new Date()));
        mPrevDateStr = DateUtils.getMonth(new Date());
        mCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
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

    private void setNewDate(final Date newDate) {
        String month = DateUtils.getMonth(newDate);
        if(month.equals(mPrevDateStr)) return;
        if(newDate.before(mPrevDate)) {
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
}

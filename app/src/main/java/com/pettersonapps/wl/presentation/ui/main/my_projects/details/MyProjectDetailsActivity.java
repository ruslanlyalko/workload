package com.pettersonapps.wl.presentation.ui.main.my_projects.details;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Note;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.my_projects.details.adapter.MyNotesAdapter;
import com.pettersonapps.wl.presentation.ui.main.workload.pager.ReportsPagerAdapter;
import com.pettersonapps.wl.presentation.utils.ColorUtils;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.calendar.Event;
import com.pettersonapps.wl.presentation.view.calendar.StatusCalendarView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class MyProjectDetailsActivity extends BaseActivity<MyProjectDetailsPresenter> implements MyProjectDetailsView {

    private static final String KEY_PROJECT = "project";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.calendar_view) StatusCalendarView mCalendarView;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.text_month) TextSwitcher mTextMonth;
    @BindView(R.id.text_spent) TextView mTextSpent;
    @BindView(R.id.layout_calendar) LinearLayout mLayoutCalendar;
    @BindView(R.id.recycler_notes) RecyclerView mRecyclerNotes;
    @BindView(R.id.scroll_view) NestedScrollView mNestedScrollView;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    private ReportsPagerAdapter mReportsPagerAdapter;
    private MyNotesAdapter mAdapterNotes;
    private Date mPrevDate = new Date();
    private String mPrevDateStr = "";

    public static Intent getLaunchIntent(final Context context, Project project) {
        Intent intent = new Intent(context, MyProjectDetailsActivity.class);
        intent.putExtra(KEY_PROJECT, project);
        return intent;
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> vacationReportsData) {
        vacationReportsData.observe(this, list -> getPresenter().setReports(list));
    }

    @Override
    public void showReports(List<Report> list) {
        mReportsPagerAdapter.setReports(list);
    }

    @Override
    public void showReportOnCalendar(final List<Report> reportsForCurrentDate, final Date date) {
        mViewPager.setCurrentItem(mReportsPagerAdapter.getPosByDate(date), false);
    }

    @Override
    public void showHolidaysOnCalendar(final MutableLiveData<List<Holiday>> holidaysData) {
        holidaysData.observe(this, holidays -> {
            if (holidays == null) return;
            getPresenter().setHolidays(holidays);
            mReportsPagerAdapter.setHolidays(holidays);
        });
    }

    @Override
    public void showCalendarsEvents() {
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

    @Override
    public void showProjectDetails(final Project project) {
        setToolbarTitle(project.getTitle());
        mAdapterNotes.setData(project.getNotes());
    }

    @Override
    public void addNewNote() {
        mAdapterNotes.addNote();
    }

    @Override
    public void showSpentHours(final int spentHours) {
        int hours = spentHours % 8;
        int days = (spentHours - hours) / 8;
        mTextSpent.setText(String.format(Locale.US, "Spent: %dd %dh", days, hours));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_project_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_date) {
            if (mLayoutCalendar.getVisibility() == View.VISIBLE) {
                mLayoutCalendar.setVisibility(View.GONE);
                mNestedScrollView.setVisibility(View.VISIBLE);
            } else {
                mLayoutCalendar.setVisibility(View.VISIBLE);
                mNestedScrollView.setVisibility(View.GONE);
                hideKeyboard();
                mToolbar.setElevation(0);
            }
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
        List<Note> data = mAdapterNotes.getData();
        List<Note> dataToSave = new ArrayList<>();
        for (Note note : data) {
            note.setTitle(note.getTitle().trim());
            if (!TextUtils.isEmpty(note.getTitle())) {
                dataToSave.add(note);
            }
        }
        for (int i = 0; i < dataToSave.size(); i++) {
            Note n = dataToSave.get(i);
            n.setKey(String.valueOf(i));
        }
        getPresenter().setNotes(dataToSave);
        Intent intent = new Intent();
        intent.putExtra(KEY_PROJECT, getPresenter().getProject());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_my_project_details;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new MyProjectDetailsPresenter(intent.getParcelableExtra(KEY_PROJECT)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_project_details);
        mNestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) -> {
            if (mNestedScrollView.getScrollY() == 0) {
                mToolbar.setElevation(0);
            } else {
                mToolbar.setElevation(mElevation);
            }
        });
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
        mAdapterNotes = new MyNotesAdapter();
        mRecyclerNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerNotes.setAdapter(mAdapterNotes);
        mReportsPagerAdapter = new ReportsPagerAdapter(getSupportFragmentManager(), false);
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

    private void setNewDate(final Date newDate) {
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

    @OnClick(R.id.text_add_note)
    public void onAddClick() {
        addNewNote();
    }
}

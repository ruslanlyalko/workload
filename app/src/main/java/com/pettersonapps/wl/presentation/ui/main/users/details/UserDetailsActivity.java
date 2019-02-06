package com.pettersonapps.wl.presentation.ui.main.users.details;

import android.arch.lifecycle.MutableLiveData;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.data.models.Vacation;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.my_vacations.adapter.VacationsAdapter;
import com.pettersonapps.wl.presentation.ui.main.users.edit.UserEditActivity;
import com.pettersonapps.wl.presentation.ui.main.users.push.UserPushActivity;
import com.pettersonapps.wl.presentation.ui.main.users.user_projects.UserProjectsActivity;
import com.pettersonapps.wl.presentation.ui.main.workload.pager.ReportsPagerAdapter;
import com.pettersonapps.wl.presentation.utils.ColorUtils;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.utils.ViewUtils;
import com.pettersonapps.wl.presentation.view.calendar.Event;
import com.pettersonapps.wl.presentation.view.calendar.StatusCalendarView;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class UserDetailsActivity extends BaseActivity<UserDetailsPresenter> implements UserDetailsView {

    private static final String KEY_USER = "user";
    private static final int RC_USER_EDIT = 100;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.text_name) TextView mTextName;
    @BindView(R.id.text_email) TextView mTextEmail;
    @BindView(R.id.text_first) TextView mTextFirst;
    @BindView(R.id.text_phone) TextView mTextPhone;
    @BindView(R.id.text_skype) TextView mTextSkype;
    @BindView(R.id.text_birthday) TextView mTextBirthday;
    @BindView(R.id.text_common) TextView mTextVacations;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.scroll_view) NestedScrollView mScrollView;
    @BindView(R.id.divider_comments) View mDividerComments;
    @BindView(R.id.divider_last_10_reports) View mDividerLast10Reports;
    @BindView(R.id.text_comments) TextView mTextComments;
    @BindView(R.id.text_projects) TextView mTextProjects;
    @BindView(R.id.calendar_view) StatusCalendarView mCalendarView;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.text_month) TextSwitcher mTextMonth;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    private ReportsPagerAdapter mReportsPagerAdapter;
    private VacationsAdapter mReportsAdapter;
    private Date mPrevDate = new Date();
    private String mPrevDateStr = "";

    public static Intent getLaunchIntent(final Context context, User user) {
        Intent intent = new Intent(context, UserDetailsActivity.class);
        intent.putExtra(KEY_USER, user);
        return intent;
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> vacationReportsData) {
        vacationReportsData.observe(this, list -> {
            getPresenter().setReports(list);
            mReportsPagerAdapter.setReports(list);
            showCalendarsEvents();
        });
    }

    @Override
    public void showReportsByYear(final SparseIntArray years) {
        String text = "";
        for (int i = 0; i < years.size(); i++) {
            String day = (years.keyAt(i) + 1) + getDayOfMonthSuffix(years.keyAt(i) + 1);
            int count = years.get(years.keyAt(i));
            if(!text.isEmpty()) {
                text = text + "\n";
            }
            text = text + getString(count == 1 ? R.string.day_taken : R.string.days_taken, day, count).trim();
        }
        mProgressBar.setVisibility(View.GONE);
        if(!TextUtils.isEmpty(text))
            mTextVacations.setText(text);
        else
            mTextVacations.setText(R.string.text_no_vacations);
    }

    @Override
    public void showUserDetails(final User user) {
        mTextName.setText(String.format("%s / %s %s", user.getName(), user.getDepartment(), user.getIsBlocked() ? " (Blocked)" : ""));
        mTextEmail.setText(user.getEmail());
        if(TextUtils.isEmpty(user.getPhone())) {
            mTextPhone.setText(R.string.text_not_specified);
            mTextPhone.setVisibility(View.GONE);
        } else {
            mTextPhone.setVisibility(View.VISIBLE);
            mTextPhone.setText(user.getPhone());
        }
        if(TextUtils.isEmpty(user.getSkype())) {
            mTextSkype.setVisibility(View.GONE);
            mTextSkype.setText(R.string.text_not_specified);
        } else {
            mTextSkype.setVisibility(View.VISIBLE);
            mTextSkype.setText(user.getSkype());
        }
        if(TextUtils.isEmpty(user.getComments())) {
            mTextComments.setVisibility(View.GONE);
            mDividerComments.setVisibility(View.GONE);
        } else {
            mTextComments.setText(user.getComments());
            mTextComments.setVisibility(View.VISIBLE);
            mDividerComments.setVisibility(View.VISIBLE);
        }
        mTextFirst.setText(DateUtils.toStringStandardDate(user.getFirstWorkingDate()));
        mTextBirthday.setText(DateUtils.toStringStandardDate(user.getBirthday()));
        if(user.getProjects().isEmpty()) {
            mTextProjects.setText(R.string.placeholder_no_projects);
        } else {
            String projects = "";
            for (Project p : user.getProjects()) {
                if(!projects.isEmpty()) projects = projects.concat(", ");
                projects = projects.concat(p.getTitle());
            }
            mTextProjects.setText(projects);
        }
    }

    @Override
    public void showReportOnCalendar(final List<Report> reportsForCurrentDate, final Date date) {
        mViewPager.setCurrentItem(mReportsPagerAdapter.getPosByDate(date), false);
    }

    @Override
    public void showHolidaysOnCalendar(final MutableLiveData<List<Holiday>> holidaysData) {
        holidaysData.observe(this, holidays -> {
            if(holidays == null) return;
            getPresenter().setHolidays(holidays);
            mReportsPagerAdapter.setHolidays(holidays);
            showCalendarsEvents();
        });
    }

    @Override
    public void setReportsToAdapter(final List<Vacation> vacations) {
        mReportsAdapter.setData(vacations);
    }

    private void showCalendarsEvents() {
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

    private String getDayOfMonthSuffix(final int n) {
        if(n >= 11 && n <= 13) {
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

    @OnClick(R.id.text_projects)
    void onProjectsClicked() {
        startActivityForResult(UserProjectsActivity.getLaunchIntent(this, getPresenter().getUser()), RC_USER_EDIT);
    }

    @OnClick({R.id.text_name, R.id.text_email, R.id.text_phone, R.id.text_skype})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_name:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getPresenter().getUser().getName(), getPresenter().getUser().getName());
                if(clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    showMessage(getString(R.string.text_copied));
                }
                break;
            case R.id.text_email:
                ClipboardManager clipboard1 = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip1 = ClipData.newPlainText(getPresenter().getUser().getEmail(), getPresenter().getUser().getEmail());
                if(clipboard1 != null) {
                    clipboard1.setPrimaryClip(clip1);
                    showMessage(getString(R.string.text_copied));
                }
                break;
            case R.id.text_phone:
                String tel = getPresenter().getUser().getPhone();
                if(TextUtils.isEmpty(tel)) return;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + tel));
                startActivity(intent);
                break;
            case R.id.text_skype:
                String skype = getPresenter().getUser().getSkype();
                if(TextUtils.isEmpty(skype)) return;
                Intent intentSkype = new Intent(Intent.ACTION_VIEW);
                intentSkype.setData(Uri.parse("skype:" + skype + "?chat"));
                startActivity(intentSkype);
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if(requestCode == RC_USER_EDIT && resultCode == RESULT_OK) {
            if(data != null && data.hasExtra(KEY_USER))
                getPresenter().setUser(data.getParcelableExtra(KEY_USER));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == R.id.action_user_projects) {
            onProjectsClicked();
            return true;
        }
        if(item.getItemId() == R.id.action_edit) {
            startActivityForResult(UserEditActivity.getLaunchIntent(this, getPresenter().getUser()), RC_USER_EDIT);
            return true;
        }
        if(item.getItemId() == R.id.action_user_push) {
            startActivity(UserPushActivity.getLaunchIntent(this, getPresenter().getUser()));
            return true;
        }
        Report report = new Report();
        switch (item.getItemId()) {
            case R.id.action_add_vacation:
                report.setStatus(getString(R.string.status_vacation));
                break;
            case R.id.action_add_day_off:
                report.setStatus(getString(R.string.status_day_off));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(getPresenter().getDate());
        DatePickerDialog datePickerDialog1 = DatePickerDialog.newInstance((v, year, monthOfYear, dayOfMonth) -> {
            Date newDate = DateUtils.getDate(calendar1.getTime(), year, monthOfYear, dayOfMonth);
            report.setDateConverted(newDate);
            getPresenter().saveReport(report);
        }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
        datePickerDialog1.setFirstDayOfWeek(Calendar.MONDAY);
//        datePickerDialog1.setMinDate(DateUtils.get1YearAgo());
        datePickerDialog1.setMaxDate(DateUtils.get1MonthForward());
        datePickerDialog1.show(getFragmentManager(), "date");
        return true;
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_details;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new UserDetailsPresenter(intent.getParcelableExtra(KEY_USER)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_user_details);
        mReportsAdapter = new VacationsAdapter();
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerReports.setAdapter(mReportsAdapter);
        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) -> {
            if(mScrollView.getScrollY() == 0) {
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

    @OnClick(R.id.text_common)
    public void onVacationClick() {
        if(mRecyclerReports.getVisibility() == View.VISIBLE) {
            mRecyclerReports.setVisibility(View.GONE);
        } else {
            ViewUtils.expand(mRecyclerReports);
        }
    }
}

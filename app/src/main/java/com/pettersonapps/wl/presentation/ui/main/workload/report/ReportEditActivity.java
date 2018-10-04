package com.pettersonapps.wl.presentation.ui.main.workload.report;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.workload.report.adapter.OnProjectListListener;
import com.pettersonapps.wl.presentation.ui.main.workload.report.adapter.OnProjectSelectClickListener;
import com.pettersonapps.wl.presentation.ui.main.workload.report.adapter.ProjectSelectAdapter;
import com.pettersonapps.wl.presentation.ui.main.workload.report.adapter.ProjectSelectable;
import com.pettersonapps.wl.presentation.ui.main.workload.report.adapter.ProjectsListAdapter;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.SquareButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ReportEditActivity extends BaseActivity<ReportEditPresenter> implements ReportEditView, OnProjectSelectClickListener, OnProjectListListener {

    private static final String KEY_USER = "user";
    private static final String KEY_REPORT = "report";
    private static final String KEY_HOLIDAYS = "holidays";
    private static final String KEY_DATE = "date";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.button_save) SquareButton mButtonSave;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.text_holiday_name) TextView mTextHolidayName;
    @BindView(R.id.bottom_sheet) LinearLayout mLayoutBottomSheet;
    @BindView(R.id.touch_outside) View mTouchOutSide;
    @BindView(R.id.recycler_projects_select) RecyclerView mRecyclerProjectsSelect;
    @BindView(R.id.recycler_projects) RecyclerView mRecyclerProjects;
    @BindView(R.id.spinner_status) Spinner mSpinnerStatus;
    @BindView(R.id.text_from) TextView mTextFrom;
    @BindView(R.id.text_to) TextView mTextTo;
    @BindView(R.id.text_placeholder) TextView mTextPlaceholder;
    @BindView(R.id.image_change_date) AppCompatImageView mImageChangeDate;
    //    @BindView(R.id.edit_search) SearchView mEditSearch;
    ProjectSelectAdapter mProjectSelectAdapter = new ProjectSelectAdapter(this);
    ProjectsListAdapter mProjectsAdapter = new ProjectsListAdapter(this);
    //    private ImageView mCloseBtn;
    private BottomSheetBehavior mSheetBehavior;

    public static Intent getLaunchIntent(final Context activity, User user, Date date, final List<Holiday> holidays) {
        Intent intent = new Intent(activity, ReportEditActivity.class);
        intent.putExtra(KEY_USER, user);
        intent.putExtra(KEY_DATE, date);
        intent.putParcelableArrayListExtra(KEY_HOLIDAYS, (ArrayList<? extends Parcelable>) holidays);
        return intent;
    }

    public static Intent getLaunchIntent(final Context activity, User user, Report report, final List<Holiday> holidays) {
        Intent intent = new Intent(activity, ReportEditActivity.class);
        intent.putExtra(KEY_USER, user);
        intent.putExtra(KEY_REPORT, report);
        intent.putParcelableArrayListExtra(KEY_HOLIDAYS, (ArrayList<? extends Parcelable>) holidays);
        return intent;
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void onBackPressed() {
        if (mSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
            mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else
            super.onBackPressed();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_report_edit;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ReportEditPresenter(intent.getParcelableExtra(KEY_USER), intent.getParcelableExtra(KEY_REPORT), (Date) intent.getSerializableExtra(KEY_DATE),
                intent.getParcelableArrayListExtra(KEY_HOLIDAYS)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(TextUtils.isEmpty(getPresenter().getReport().getKey())
                ? R.string.title_new_workload : R.string.title_edit_workload);
//        mCloseBtn = mEditSearch.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        mRecyclerProjectsSelect.setLayoutManager(new GridLayoutManager(getContext(), 2));
        mRecyclerProjectsSelect.setAdapter(mProjectSelectAdapter);
        mRecyclerProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerProjects.setAdapter(mProjectsAdapter);
        mSheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View view, final int i) {
//                if (i == BottomSheetBehavior.STATE_COLLAPSED) {
//                    hideKeyboard();
//                    mEditSearch.setQuery("", false);
//                    mCloseBtn.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onSlide(@NonNull final View view, final float v) {
                mTouchOutSide.setAlpha(v / 2f);
                getWindow().setStatusBarColor(adjustAlpha(ContextCompat.getColor(getContext(),
                        R.color.colorBackgroundTouchOutside), v / 2f));
                if (v > 0.001f)
                    mTouchOutSide.setVisibility(View.VISIBLE);
                else
                    mTouchOutSide.setVisibility(View.GONE);
            }
        });
//        mEditSearch.setOnCloseListener(() -> true);
//        mEditSearch.setOnSearchClickListener(v -> {
//            mCloseBtn.setVisibility(mEditSearch.getQuery().toString().isEmpty() ? View.GONE : View.VISIBLE);
//        });
//        mEditSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(final String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(final String s) {
//                mCloseBtn.setVisibility(s.isEmpty() ? View.GONE : View.VISIBLE);
//                mProjectsAdapter.getFilter().filter(s);
//                return false;
//            }
//        });
        String[] statuses = getResources().getStringArray(R.array.status);
        SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_status, statuses);
        mSpinnerStatus.setAdapter(adapter);
        mSpinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                if (mSpinnerStatus.getSelectedItem().toString().startsWith("Day")
                        || mSpinnerStatus.getSelectedItem().toString().startsWith("Vacation")
                        || mSpinnerStatus.getSelectedItem().toString().startsWith("Sick")
                        ) {
                    mRecyclerProjectsSelect.setVisibility(View.GONE);
                } else {
                    mRecyclerProjectsSelect.setVisibility(View.VISIBLE);
                }
                if (!(mSpinnerStatus.getSelectedItem().toString().startsWith("Vacation")
                        || mSpinnerStatus.getSelectedItem().toString().startsWith("Day"))) {
                    if (getPresenter().getReport().getDate().after(DateUtils.get1DaysForward().getTime())) {
                        getPresenter().setReportDate(new Date());
                    }
                    if (getPresenter().getDateTo().after(DateUtils.get1DaysForward().getTime())) {
                        getPresenter().setDateTo(new Date());
                    }
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
        getPresenter().onViewReady();
    }

    @OnClick(R.id.button_save)
    public void onSaveClick() {
        String status = String.valueOf(mSpinnerStatus.getSelectedItem());
        getPresenter().onSave(status, mProjectSelectAdapter.getData());
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        mButtonSave.showProgress(true);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mButtonSave.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void afterSuccessfullySaving() {
        showMessage(getString(R.string.text_report_saved));
        onBackPressed();
    }

    @Override
    public void afterSuccessfullyRangeSaving(final int count) {
        showMessage(getString(R.string.text_report_saved_workloads, count));
        onBackPressed();
    }

    @Override
    public void showReportData(final Report report) {
        String[] statuses = getResources().getStringArray(R.array.status);
        for (int i = 0; i < statuses.length; i++) {
            if (statuses[i].equals(report.getStatus())) {
                mSpinnerStatus.setSelection(i);
                break;
            }
        }
        if (report.getT1() > 0) {
            mProjectSelectAdapter.addItem(new ProjectSelectable(report.getP1(), report.getT1()));
        }
        if (report.getT2() > 0) {
            mProjectSelectAdapter.addItem(new ProjectSelectable(report.getP2(), report.getT2()));
        }
        if (report.getT3() > 0) {
            mProjectSelectAdapter.addItem(new ProjectSelectable(report.getP3(), report.getT3()));
        }
        if (report.getT4() > 0) {
            mProjectSelectAdapter.addItem(new ProjectSelectable(report.getP4(), report.getT4()));
        }
    }

    @Override
    public void showHoliday(final String holiday) {
        if (holiday == null)
            mTextHolidayName.setVisibility(View.GONE);
        else {
            mTextHolidayName.setVisibility(View.VISIBLE);
            mTextHolidayName.setText(holiday);
        }
    }

    @Override
    public void errorCantBeZero() {
        showError(getString(R.string.error_cant_be_zero));
    }

    @Override
    public void errorCantBeMoreThan16() {
        showError(getString(R.string.error_cant_be_more_than_16));
    }

    @Override
    public void errorCantHasTwoEqualsProjects() {
        showError(getString(R.string.error_cant_be_two_same_projects));
    }

    @Override
    public void showDateState(final boolean dateStateOneDay) {
        mTextTo.setVisibility(dateStateOneDay ? View.GONE : View.VISIBLE);
        mImageChangeDate.setImageResource(dateStateOneDay ? R.drawable.ic_date_gray : R.drawable.ic_date_range_gray);
    }

    @Override
    public void showDateFrom(Date date) {
        mTextFrom.setText(DateUtils.toStringDate(date));
    }

    @Override
    public void showDateTo(Date date) {
        mTextTo.setText(DateUtils.toStringDate(date));
    }

    @Override
    public void showProjects(final List<Project> list) {
        mProjectsAdapter.setData(list);
        mTextPlaceholder.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void addProject(final String title) {
        mProjectSelectAdapter.addItem(new ProjectSelectable(title));
    }

    @Override
    public void changeProject(final String title, final int position) {
        mProjectSelectAdapter.changeItem(title, position);
    }

    @Override
    public void showWrongDateOnMobileError() {
        showError(getString(R.string.error_check_date));
    }

    @OnClick({R.id.text_from, R.id.text_to, R.id.image_change_date})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_from:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getPresenter().getReport().getDate());
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    Date newDate = DateUtils.getDate(calendar.getTime(), year, monthOfYear, dayOfMonth);
                    getPresenter().setReportDate(newDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                if (!getPresenter().getUser().getIsAllowEditPastReports()) {
                    datePickerDialog.setMinDate(DateUtils.get1DaysAgo());
                    if (mSpinnerStatus.getSelectedItem().toString().startsWith("Vacation")
                            || mSpinnerStatus.getSelectedItem().toString().startsWith("Day")) {
                        datePickerDialog.setMaxDate(DateUtils.get1MonthForward());
                    } else {
                        datePickerDialog.setMaxDate(DateUtils.get1DaysForward());
                    }
                }
                datePickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
                datePickerDialog.show(getFragmentManager(), "to");
                break;
            case R.id.text_to:
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(getPresenter().getDateTo());
                DatePickerDialog datePickerDialog1 = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    Date newDate = DateUtils.getDate(calendar1.getTime(), year, monthOfYear, dayOfMonth);
                    getPresenter().setDateTo(newDate);
                }, calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH));
                if (!getPresenter().getUser().getIsAllowEditPastReports()) {
                    datePickerDialog1.setMinDate(DateUtils.get1DaysAgo());
                    if (mSpinnerStatus.getSelectedItem().toString().startsWith("Vacation")
                            || mSpinnerStatus.getSelectedItem().toString().startsWith("Day")) {
                        datePickerDialog1.setMaxDate(DateUtils.get1MonthForward());
                    } else {
                        datePickerDialog1.setMaxDate(DateUtils.get1DaysForward());
                    }
                }
                datePickerDialog1.setFirstDayOfWeek(Calendar.MONDAY);
                datePickerDialog1.show(getFragmentManager(), "to");
                break;
            case R.id.image_change_date:
                getPresenter().toggleDateState();
                break;
        }
    }

    @Override
    public void onProjectAddClicked(final View view, final int position) {
//        mEditSearch.setIconified(false);
//        mEditSearch.clearFocus();
//        mCloseBtn.setVisibility(View.GONE);
        mSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        getPresenter().setAddProjectMode();
    }

    @Override
    public void onProjectChangeClicked(final View view, final int position) {
//        mEditSearch.setIconified(false);
//        mEditSearch.clearFocus();
//        mCloseBtn.setVisibility(View.GONE);
        mSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        getPresenter().setChangeProjectMode(position);
    }

    @Override
    public void onProjectSelected(final String title) {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        getPresenter().changeProject(title);
    }

    @OnClick(R.id.touch_outside)
    public void onTouchClick() {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}

package com.pettersonapps.wl.presentation.ui.main.my_projects.details.statistics;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.ProgressButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class MyProjectStatisticsActivity extends BaseActivity<MyProjectStatisticsPresenter> implements MyProjectStatisticsView {

    private static final String KEY_PROJECT = "project";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.text_spent) TextView mTextSpent;
    @BindView(R.id.text_from) TextView mTextFrom;
    @BindView(R.id.text_to) TextView mTextTo;
    @BindView(R.id.image_change_date) AppCompatImageView mImageChangeDate;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    @BindView(R.id.button_update) ProgressButton mButtonUpdate;

    public static Intent getLaunchIntent(final Context context, Project project) {
        Intent intent = new Intent(context, MyProjectStatisticsActivity.class);
        intent.putExtra(KEY_PROJECT, project);
        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_my_project_statistics;
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> vacationReportsData) {
        vacationReportsData.observe(this, list -> getPresenter().setReports(list));
    }

    @Override
    public void showProjectDetails(final Project project) {
        setToolbarTitle(getString(R.string.title_statistics, project.getTitle()));
    }

    @Override
    public void showSpentHours(final int spentHours) {
        int hours = spentHours % 8;
        int days = (spentHours - hours) / 8;
        mTextSpent.setText(String.format(Locale.US, "Spent: %dd %dh", days, hours));
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
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new MyProjectStatisticsPresenter(intent.getParcelableExtra(KEY_PROJECT)));
    }

    @Override
    public void showDateFrom(Date date) {
        mTextFrom.setText(DateUtils.toStringNormalDate(date));
    }

    @Override
    public void showDateTo(Date date) {
        mTextTo.setText(DateUtils.toStringNormalDate(date));
    }

    @Override
    public void showDateState(final boolean dateStateOneDay) {
        mTextTo.setVisibility(dateStateOneDay ? View.GONE : View.VISIBLE);
        mImageChangeDate.setImageResource(dateStateOneDay ? R.drawable.ic_day : R.drawable.ic_week);
    }

    @Override
    public void showProgress() {
        mButtonUpdate.showProgress(true);
    }

    @Override
    public void hideProgress() {
        mButtonUpdate.showProgress(false);
    }

    @Override
    public void showProjectInfo(final ProjectInfo projectInfo) {
        mTextSpent.setText(projectInfo.toLargeString());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_project_details);
        getPresenter().onViewReady();
    }

    @OnClick({R.id.text_from, R.id.text_to, R.id.image_change_date})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_from:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getPresenter().getDateFrom());
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    Date newDate = DateUtils.getDate(calendar.getTime(), year, monthOfYear, dayOfMonth);
                    getPresenter().setDateFrom(newDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setMaxDate(DateUtils.get1MonthForward());
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
                datePickerDialog1.setMaxDate(DateUtils.get1MonthForward());
                datePickerDialog1.setFirstDayOfWeek(Calendar.MONDAY);
                datePickerDialog1.show(getFragmentManager(), "to");
                break;
            case R.id.image_change_date:
                getPresenter().toggleDateState();
                break;
        }
    }

    @OnClick(R.id.button_update)
    public void onClick() {
        getPresenter().onUpdateClicked();
    }
}

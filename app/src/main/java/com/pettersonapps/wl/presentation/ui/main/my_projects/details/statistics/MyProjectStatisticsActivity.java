package com.pettersonapps.wl.presentation.ui.main.my_projects.details.statistics;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.my_projects.details.statistics.adapter.StatisticsAdapter;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class MyProjectStatisticsActivity extends BaseActivity<MyProjectStatisticsPresenter> implements MyProjectStatisticsView, OnItemClickListener {

    private static final String KEY_PROJECT = "project";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.text_spent) TextView mTextSpent;
    @BindView(R.id.text_from) TextView mTextFrom;
    @BindView(R.id.text_to) TextView mTextTo;
    @BindView(R.id.image_change_date) AppCompatImageView mImageChangeDate;
    @BindView(R.id.text_department_1) TextView mTextDepartment1;
    @BindView(R.id.text_department_2) TextView mTextDepartment2;
    @BindView(R.id.text_department_3) TextView mTextDepartment3;
    @BindView(R.id.text_department_4) TextView mTextDepartment4;
    @BindView(R.id.text_department_5) TextView mTextDepartment5;
    @BindView(R.id.text_department_6) TextView mTextDepartment6;
    @BindView(R.id.text_department_7) TextView mTextDepartment7;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.recycler_statistics) RecyclerView mRecyclerStatistics;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    private StatisticsAdapter mAdapter = new StatisticsAdapter(this);

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
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showProjectInfo(final ProjectInfo projectInfo) {
        mAdapter.setData(projectInfo);
        mTextDepartment1.setText(getFormattedText(getString(R.string.department_ios), projectInfo.getiOS()));
        mTextDepartment1.setVisibility(projectInfo.getiOS() > 0 ? View.VISIBLE : View.GONE);
        mTextDepartment2.setText(getFormattedText(getString(R.string.department_android), projectInfo.getAndroid()));
        mTextDepartment2.setVisibility(projectInfo.getAndroid() > 0 ? View.VISIBLE : View.GONE);
        mTextDepartment3.setText(getFormattedText(getString(R.string.department_design), projectInfo.getDesign()));
        mTextDepartment3.setVisibility(projectInfo.getDesign() > 0 ? View.VISIBLE : View.GONE);
        mTextDepartment4.setText(getFormattedText(getString(R.string.department_backend), projectInfo.getBackend()));
        mTextDepartment4.setVisibility(projectInfo.getBackend() > 0 ? View.VISIBLE : View.GONE);
        mTextDepartment5.setText(getFormattedText(getString(R.string.department_pm), projectInfo.getPM()));
        mTextDepartment5.setVisibility(projectInfo.getPM() > 0 ? View.VISIBLE : View.GONE);
        mTextDepartment6.setText(getFormattedText(getString(R.string.department_qa), projectInfo.getQA()));
        mTextDepartment6.setVisibility(projectInfo.getQA() > 0 ? View.VISIBLE : View.GONE);
        mTextDepartment7.setText(getFormattedText(getString(R.string.department_other), projectInfo.getOther()));
        mTextDepartment7.setVisibility(projectInfo.getOther() > 0 ? View.VISIBLE : View.GONE);
        mTextSpent.setText(getFormattedText(getString(R.string.text_spent), projectInfo.getTotalCount()));
    }

    private Spanned getFormattedText(final String name, final float time) {
        if(TextUtils.isEmpty(name)) return SpannableString.valueOf("");
        String timeStr = String.format(Locale.US, "%.0fh", time);
        float ex = time % 1;
        if(ex != 0) {
            timeStr = String.format(Locale.US, "%.0fh %dm", time - ex, (int) (ex * 60));
        }
        return Html.fromHtml("<b>" + name + "</b> " + timeStr);
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_project_details);
        mRecyclerStatistics.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerStatistics.setAdapter(mAdapter);
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
                    getPresenter().onUpdateClicked();
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
                    getPresenter().onUpdateClicked();
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

    @Override
    public void onItemClicked(final View view, final int position) {
    }

    @Override
    public void onItemLongClicked(final View view, final int position) {
    }
}

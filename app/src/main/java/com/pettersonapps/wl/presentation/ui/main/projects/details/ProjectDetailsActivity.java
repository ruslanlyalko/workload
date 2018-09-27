package com.pettersonapps.wl.presentation.ui.main.projects.details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.SquareButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class ProjectDetailsActivity extends BaseActivity<ProjectDetailsPresenter> implements ProjectDetailsView {

    private static final String KEY_PROJECT = "project";
    private static final String A_DATE_FORMAT = "dd.MM.yyyy";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.button_update) SquareButton mButtonUpdate;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.input_from) TextInputEditText mInputFrom;
    @BindView(R.id.input_to) TextInputEditText mInputTo;
    @BindView(R.id.text_result) TextView mTextResult;
    @BindView(R.id.chart) BarChart mBarChart;

    public static Intent getLaunchIntent(final Context activity, Project project) {
        Intent intent = new Intent(activity, ProjectDetailsActivity.class);
        intent.putExtra(KEY_PROJECT, project);
        return intent;
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_project_details;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ProjectDetailsPresenter(intent.getParcelableExtra(KEY_PROJECT)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(getPresenter().getProject().getTitle());
        initBarChart();
        getPresenter().onViewReady();
    }

    private void initBarChart() {
        mBarChart.setNoDataText(getString(R.string.placeholder_no_data));
        mBarChart.setNoDataTextColor(ContextCompat.getColor(this, R.color.colorTextGray));
        mBarChart.setDrawGridBackground(false);
        mBarChart.getLegend().setEnabled(false);
        mBarChart.getDescription().setEnabled(false);
        mBarChart.getXAxis().setEnabled(true);
        mBarChart.getAxisRight().setEnabled(false);
        mBarChart.getAxisLeft().setAxisLineColor(Color.TRANSPARENT);
        mBarChart.getAxisLeft().enableGridDashedLine(1, 1, 10);
//        mBarChart.setMaxVisibleValueCount(300);
        mBarChart.setDrawValueAboveBar(true);
        mBarChart.setDoubleTapToZoomEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setTouchEnabled(false);
        mBarChart.setDrawBarShadow(false);
        mBarChart.getAxisLeft().setAxisMinimum(0);
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter((value, axis) -> {
            String[] list = getResources().getStringArray(R.array.departments);
            return list[(int) value];
        });
    }

    @OnClick(R.id.button_update)
    public void onUpdateClick() {
        getPresenter().onUpdateClicked();
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        mButtonUpdate.showProgress(true);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mButtonUpdate.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void showProjectInfo(final ProjectInfo projectInfo) {
        if (projectInfo == null) return;
        mTextResult.setText(projectInfo.toString());
        ArrayList<BarEntry> values = new ArrayList<>();
        values.add(new BarEntry(0, projectInfo.getiOS(), projectInfo.getiOS()));
        values.add(new BarEntry(1, projectInfo.getAndroid(), projectInfo.getAndroid()));
        values.add(new BarEntry(2, projectInfo.getBackend(), projectInfo.getAndroid()));
        values.add(new BarEntry(3, projectInfo.getDesign(), projectInfo.getDesign()));
        values.add(new BarEntry(4, projectInfo.getPM(), projectInfo.getPM()));
        values.add(new BarEntry(5, projectInfo.getQA(), projectInfo.getQA()));
        values.add(new BarEntry(6, projectInfo.getOther(), projectInfo.getOther()));
        //
        int[] colors = getResources().getIntArray(R.array.bar_colors);
        BarDataSet set1;
        if (mBarChart.getData() != null && mBarChart.getData().getDataSetCount() == 1) {
            set1 = (BarDataSet) mBarChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
        } else {
            set1 = new BarDataSet(values, "");
            set1.setColors(colors);
            set1.setDrawIcons(true);
            set1.setDrawValues(true);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setBarWidth(0.90f);
            data.setDrawValues(true);
//             data.setValueFormatter(new LabelValueFormatter());
            data.setValueTextColor(Color.BLACK);
            set1.setValueTextSize(10f);
            mBarChart.setData(data);
        }
        mBarChart.getData().notifyDataChanged();
        mBarChart.notifyDataSetChanged();
        mBarChart.invalidate();
    }

    @Override
    public void showFrom(final Date date) {
        mInputFrom.setText(DateUtils.toString(date, A_DATE_FORMAT));
    }

    @Override
    public void showTo(final Date date) {
        mInputTo.setText(DateUtils.toString(date, A_DATE_FORMAT));
    }

    @OnClick({R.id.input_from, R.id.input_to})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input_from:
                Calendar calendar = getPresenter().getFrom();
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    Date newDate = DateUtils.getDate(calendar.getTime(), year, monthOfYear, dayOfMonth);
                    getPresenter().setFrom(newDate);
                    showFrom(newDate);
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setMaxDate(Calendar.getInstance());
                datePickerDialog.show(getFragmentManager(), "from");
                break;
            case R.id.input_to:
                Calendar calendarTo = getPresenter().getTo();
                DatePickerDialog datePickerDialogTo = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    Date newDate = DateUtils.getDate(calendarTo.getTime(), year, monthOfYear, dayOfMonth);
                    getPresenter().setTo(newDate);
                    showTo(newDate);
                }, calendarTo.get(Calendar.YEAR), calendarTo.get(Calendar.MONTH), calendarTo.get(Calendar.DAY_OF_MONTH));
                datePickerDialogTo.setMaxDate(Calendar.getInstance());
                datePickerDialogTo.show(getFragmentManager(), "to");
                break;
        }
    }
}

package com.pettersonapps.wl.presentation.ui.main.dashboard.export;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.SquareButton;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ExportActivity extends BaseActivity<ExportPresenter> implements ExportView {

    final RxPermissions rxPermissions = new RxPermissions(this);
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.button_export) SquareButton mButtonExport;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.input_from) TextInputEditText mInputFrom;
    @BindView(R.id.input_to) TextInputEditText mInputTo;

    public static Intent getLaunchIntent(final Context activity) {
        return new Intent(activity, ExportActivity.class);
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.button_export)
    public void onExportClick() {
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if (granted) {
                        getPresenter().onExportClicked();
                    } else {
                        showError(getString(R.string.error_denied));
                    }
                });
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        mButtonExport.showProgress(true);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mButtonExport.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void showFrom(final Date date) {
        mInputFrom.setText(DateUtils.toStringStandardDate(date));
    }

    @Override
    public void showTo(final Date date) {
        mInputTo.setText(DateUtils.toStringStandardDate(date));
    }

    @Override
    public void showExportedData(final MutableLiveData<List<Report>> reportsFilter) {
        reportsFilter.observe(this, list -> getPresenter().setExportedData(list));
    }

    @Override
    public void showFile(final File file) {
        if (file != null && file.exists()) {
            try {
                Uri uriForFile = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".wl.provider", file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uriForFile);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        }
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
                datePickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
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
                datePickerDialogTo.setFirstDayOfWeek(Calendar.MONDAY);
                datePickerDialogTo.show(getFragmentManager(), "to");
                break;
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_export;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ExportPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_export);
        getPresenter().onViewReady();
    }
}

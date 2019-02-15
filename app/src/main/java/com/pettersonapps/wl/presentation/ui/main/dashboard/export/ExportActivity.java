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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.dashboard.DashboardPresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.SquareButton;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class ExportActivity extends BaseActivity<ExportPresenter> implements ExportView {

    final RxPermissions rxPermissions = new RxPermissions(this);
    @BindView(R.id.spinner_projects) Spinner mSpinnerProjects;
    @BindView(R.id.spinner_users) Spinner mSpinnerUsers;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.button_export) SquareButton mButtonExport;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.input_from) TextInputEditText mInputFrom;
    @BindView(R.id.input_to) TextInputEditText mInputTo;
    @BindView(R.id.switch_open_after_saving) Switch mSwitchOpenAfterSaving;

    public static Intent getLaunchIntent(final Context activity) {
        return new Intent(activity, ExportActivity.class);
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_export;
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ExportPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_export);
        setupSpinners();
        getPresenter().onViewReady();
    }

    private void setupSpinners() {
        AdapterView.OnItemSelectedListener tt = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                if(mSpinnerProjects.getSelectedItem() != null
                        && mSpinnerUsers.getSelectedItem() != null) {
                    getPresenter().setFilter(
                            mSpinnerProjects.getSelectedItem().toString(),
                            mSpinnerUsers.getSelectedItem().toString()
                    );
                }
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        };
        mSpinnerProjects.setOnItemSelectedListener(tt);
        mSpinnerUsers.setOnItemSelectedListener(tt);
    }


    @OnLongClick(R.id.button_export)
    public boolean onExportLongClick() {
        getPresenter().setRepairReports(true);
        showMessage("Repair started");
        onExportClick();
        return true;
    }

    @Override
    public void showSpinnerProjectsData(final MutableLiveData<List<Project>> projectsData) {
        projectsData.observe(this, projects -> {
            if(projects == null) return;
            List<String> list = new ArrayList<>();
            for (Project project : projects) {
                list.add(project.getTitle());
            }
            list.add(0, ExportPresenter.KEY_PROJECT);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerProjects.setAdapter(dataAdapter);
        });
    }

    @Override
    public void showSpinnerUsersData(final MutableLiveData<List<User>> usersData) {
        usersData.observe(this, users -> {
            if(users == null) return;
            List<String> list = new ArrayList<>();
            for (User user : users) {
                list.add(user.getName());
            }
            list.add(0, ExportPresenter.KEY_USER);
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(),
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinnerUsers.setAdapter(dataAdapter);
        });
    }

    @SuppressLint("CheckResult")
    @OnClick(R.id.button_export)
    public void onExportClick() {
        rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(granted -> {
                    if(granted) {
                        getPresenter().onExportClicked(mSwitchOpenAfterSaving.isChecked());
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
        if(file != null && file.exists()) {
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
}

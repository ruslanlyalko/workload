package com.pettersonapps.wl.presentation.ui.main.users.add;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.MainActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.SquareButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class UserAddActivity extends BaseActivity<UserAddPresenter> implements UserAddView {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.button_register) SquareButton mButtonRegister;
    @BindView(R.id.input_name) TextInputEditText mInputName;
    @BindView(R.id.input_phone) TextInputEditText mInputPhone;
    @BindView(R.id.input_skype) TextInputEditText mInputSkype;
    @BindView(R.id.input_email) TextInputEditText mInputEmail;
    @BindView(R.id.input_password) TextInputEditText mInputPassword;
    @BindView(R.id.input_birthday) TextInputEditText mInputBirthday;
    @BindView(R.id.input_first_working_day) TextInputEditText mInputFirstWorkingDay;
    @BindView(R.id.spinner_department) Spinner mSpinnerDepartment;
    @BindView(R.id.progress) ProgressBar mProgress;

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, UserAddActivity.class);
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_add;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new UserAddPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_add);
        mInputBirthday.setText(DateUtils.toStringStandardDate(new Date()));
        mInputFirstWorkingDay.setText(DateUtils.toStringStandardDate(new Date()));
        getPresenter().onViewReady();
    }

    @OnClick(R.id.button_register)
    public void onSaveClick() {
        if (TextUtils.isEmpty(mInputName.getText())) {
            mInputName.setError(getString(R.string.error_cant_be_empty));
            mInputName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mInputEmail.getText())) {
            mInputEmail.setError(getString(R.string.error_cant_be_empty));
            mInputEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mInputPassword.getText())) {
            mInputPassword.setError(getString(R.string.error_cant_be_empty));
            mInputPassword.requestFocus();
            return;
        }
        if (mInputPassword.getText().toString().length() < 6) {
            mInputPassword.setError(getString(R.string.error_passwords_should_be_at_least_6_symbols));
            mInputPassword.requestFocus();
            return;
        }
        getPresenter().onRegister(this,
                mInputEmail.getText().toString(),
                mInputPassword.getText().toString(),
                mInputName.getText().toString(),
                mInputPhone.getText().toString(),
                mInputSkype.getText().toString(),
                String.valueOf(mSpinnerDepartment.getSelectedItem()));
    }

    @OnClick({R.id.input_birthday, R.id.input_first_working_day})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input_birthday:
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(getPresenter().getUser().getBirthday());
                DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    Date newDate = DateUtils.getDate(calendar.getTime(), year, monthOfYear, dayOfMonth);
                    getPresenter().getUser().setBirthday(newDate);
                    mInputBirthday.setText(DateUtils.toStringStandardDate(newDate));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.setMaxDate(DateUtils.getYesterday());
                datePickerDialog.showYearPickerFirst(true);
                datePickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
                datePickerDialog.show(getFragmentManager(), "birthday");
                break;
            case R.id.input_first_working_day:
                Calendar calendarWorking = Calendar.getInstance();
                calendarWorking.setTime(getPresenter().getUser().getFirstWorkingDate());
                DatePickerDialog workingPickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
                    Date newDate = DateUtils.getDate(calendarWorking.getTime(), year, monthOfYear, dayOfMonth);
                    getPresenter().getUser().setFirstWorkingDate(newDate);
                    mInputFirstWorkingDay.setText(DateUtils.toStringStandardDate(newDate));
                }, calendarWorking.get(Calendar.YEAR), calendarWorking.get(Calendar.MONTH), calendarWorking.get(Calendar.DAY_OF_MONTH));
                workingPickerDialog.setMaxDate(DateUtils.getYesterday());
                workingPickerDialog.showYearPickerFirst(true);
                workingPickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
                workingPickerDialog.show(getFragmentManager(), "working");
                break;
        }
    }

    @Override
    public void showProgress() {
        mButtonRegister.showProgress(true);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mButtonRegister.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void afterSuccessfullySaving() {
        startActivity(MainActivity.getLaunchIntent(this).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public void close() {
        showMessage(getString(R.string.text_user_created));
        onBackPressed();
    }
}

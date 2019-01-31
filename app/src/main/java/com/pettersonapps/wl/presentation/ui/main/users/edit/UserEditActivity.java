package com.pettersonapps.wl.presentation.ui.main.users.edit;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.SquareButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class UserEditActivity extends BaseActivity<UserEditPresenter> implements UserEditView {

    private static final String KEY_USER = "user";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.button_save) SquareButton mButtonSave;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.input_name) TextInputEditText mInputName;
    @BindView(R.id.input_phone) TextInputEditText mInputPhone;
    @BindView(R.id.input_skype) TextInputEditText mInputSkype;
    @BindView(R.id.input_comments) TextInputEditText mInputComments;
    @BindView(R.id.input_birthday) TextInputEditText mInputBirthday;
    @BindView(R.id.input_first_working_day) TextInputEditText mInputFirstWorkingDay;
    @BindView(R.id.spinner_department) Spinner mSpinnerDepartment;
    @BindView(R.id.switch_blocked) Switch mSwitchBlocked;
    @BindView(R.id.switch_allow_edit) Switch mSwitchAllowEdit;
    @BindView(R.id.switch_vip) Switch mSwitchVip;
    @BindView(R.id.switch_manager) Switch mSwitchManager;
    @BindView(R.id.switch_admin) Switch mSwitchAdmin;
    @BindView(R.id.scroll_view) NestedScrollView mScrollView;

    @BindDimen(R.dimen.margin_mini) int mElevation;

    public static Intent getLaunchIntent(final Context activity, User user) {
        Intent intent = new Intent(activity, UserEditActivity.class);
        intent.putExtra(KEY_USER, user);
        return intent;
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_edit;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new UserEditPresenter(intent.getParcelableExtra(KEY_USER)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_edit);
        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) -> {
            if(mScrollView.getScrollY() == 0) {
                mToolbar.setElevation(0);
            } else {
                mToolbar.setElevation(mElevation);
            }
        });
        getPresenter().onViewReady();
    }

    @OnClick(R.id.button_save)
    public void onSaveClick() {
        if(TextUtils.isEmpty(mInputName.getText())) {
            mInputName.setError(getString(R.string.error_cant_be_empty));
            mInputName.requestFocus();
            return;
        }
        getPresenter().onSave(mInputName.getText().toString(),
                mInputPhone.getText().toString(),
                mInputSkype.getText().toString(),
                mInputComments.getText().toString(),
                String.valueOf(mSpinnerDepartment.getSelectedItem()),
                mSwitchBlocked.isChecked(),
                mSwitchAllowEdit.isChecked(),
                mSwitchVip.isChecked(),
                mSwitchAdmin.isChecked(),
                mSwitchManager.isChecked());
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
                datePickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
                datePickerDialog.showYearPickerFirst(true);
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
                workingPickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
                workingPickerDialog.showYearPickerFirst(true);
                workingPickerDialog.show(getFragmentManager(), "working");
                break;
        }
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        mProgress.setVisibility(View.VISIBLE);
        mButtonSave.showProgress(true);
    }

    @Override
    public void hideProgress() {
        mButtonSave.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void afterSuccessfullySaving(final User user) {
        Intent intent = new Intent();
        intent.putExtra(KEY_USER, user);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public void showUserData(final User user) {
        mInputName.setText(user.getName());
        mInputPhone.setText(user.getPhone());
        mInputSkype.setText(user.getSkype());
        mInputComments.setText(user.getComments());
        String[] departments = getResources().getStringArray(R.array.departments);
        for (int i = 0; i < departments.length; i++) {
            if(departments[i].equals(user.getDepartment())) {
                mSpinnerDepartment.setSelection(i);
                break;
            }
        }
        mInputBirthday.setText(DateUtils.toStringStandardDate(user.getBirthday()));
        mInputFirstWorkingDay.setText(DateUtils.toStringStandardDate(user.getFirstWorkingDate()));
        mSwitchAllowEdit.setChecked(user.getIsAllowEditPastReports());
        mSwitchBlocked.setChecked(user.getIsBlocked());
        mSwitchVip.setChecked(user.getIsVip());
        mSwitchAdmin.setChecked(user.getIsAdmin());
        mSwitchManager.setChecked(user.getIsManager());
    }

    @Override
    public void showSettings(final MutableLiveData<AppSettings> settings) {
        settings.observe(this, appSettings -> {
            if(appSettings != null) {
                getPresenter().setSettings(appSettings);
            }
        });
    }
}

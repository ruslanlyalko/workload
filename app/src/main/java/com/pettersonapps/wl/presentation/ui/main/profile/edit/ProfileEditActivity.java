package com.pettersonapps.wl.presentation.ui.main.profile.edit;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.SquareButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class ProfileEditActivity extends BaseActivity<ProfileEditPresenter> implements ProfileEditView {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.input_phone) TextInputEditText mInputPhone;
    @BindView(R.id.input_skype) TextInputEditText mInputSkype;

    @BindView(R.id.input_birthday) TextInputEditText mInputBirthday;
    @BindView(R.id.input_password) TextInputEditText mInputPassword;
    @BindView(R.id.input_password_confirm) TextInputEditText mInputPasswordConfirm;

    @BindView(R.id.button_save) SquareButton mButtonSave;
    @BindView(R.id.progress) ProgressBar mProgress;

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, ProfileEditActivity.class);
    }

    @Override
    public void showUser(final MutableLiveData<User> userData) {
        userData.observe(this, user -> {
            if (user != null) {
                getPresenter().setUser(user);
                setToolbarTitle(user.getName());
                mInputPhone.setText(user.getPhone());
                mInputSkype.setText(user.getSkype());
                mInputBirthday.setText(DateUtils.toStringStandardDate(user.getBirthday()));
            }
        });
    }

    @Override
    public void showProgress() {
        mProgress.setVisibility(View.VISIBLE);
        mButtonSave.showProgress(true);
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

    @OnClick(R.id.button_save)
    public void onSaveClick() {
        if (TextUtils.isEmpty(mInputPhone.getText())) {
            mInputPhone.setError(getString(R.string.error_cant_be_empty));
            mInputPhone.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mInputSkype.getText())) {
            mInputSkype.setError(getString(R.string.error_cant_be_empty));
            mInputSkype.requestFocus();
            return;
        }
        // Password
        if (!TextUtils.isEmpty(mInputPassword.getText()) && mInputPassword.getText().toString().length() < 6) {
            mInputPassword.setError(getString(R.string.error_passwords_should_be_at_least_6_symbols));
            mInputPassword.requestFocus();
        } else if (!TextUtils.isEmpty(mInputPassword.getText()) && !mInputPassword.getText().toString().equals(mInputPasswordConfirm.getText().toString())) {
            mInputPasswordConfirm.setError(getString(R.string.error_passwords_should_equals));
            mInputPasswordConfirm.requestFocus();
        } else
            getPresenter().onSave(mInputSkype.getText().toString(), mInputPhone.getText().toString(), mInputPassword.getText().toString());
    }

    @OnClick(R.id.input_birthday)
    public void onClick(View v) {
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
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_profile_edit;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ProfileEditPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_profile);
        getPresenter().onViewReady();
    }
}

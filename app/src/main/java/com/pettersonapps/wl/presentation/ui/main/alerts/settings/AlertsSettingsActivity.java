package com.pettersonapps.wl.presentation.ui.main.alerts.settings;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.view.SquareButton;

import butterknife.BindView;
import butterknife.OnClick;

public class AlertsSettingsActivity extends BaseActivity<AlertsSettingsPresenter> implements AlertsSettingsView {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.input_email) TextInputEditText mInputEmail;
    @BindView(R.id.input_title) TextInputEditText mInputTitle;
    @BindView(R.id.input_body) TextInputEditText mInputBody;
    @BindView(R.id.switch_snow) Switch mSwitchSnow;
    @BindView(R.id.button_save) SquareButton mButtonSave;
    @BindView(R.id.progress) ProgressBar mProgress;

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, AlertsSettingsActivity.class);
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_alerts_settings;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new AlertsSettingsPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_alerts_settings);
        getPresenter().onViewReady();
    }

    @Override
    public void showSettings(final MutableLiveData<AppSettings> settings) {
        settings.observe(this, appSettings -> {
            if (appSettings != null) {
                getPresenter().setSettings(appSettings);
            }
        });
    }

    @OnClick(R.id.button_save)
    public void onSaveClick() {
        getPresenter().onSave(mInputEmail.getText().toString(),
                mInputTitle.getText().toString(),
                mInputBody.getText().toString(),
                mSwitchSnow.isChecked());
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
    public void populateSettings(final AppSettings settings) {
        mInputEmail.setText(settings.getNotificationEmail());
        mInputTitle.setText(settings.getDefaultPushTitle());
        mInputBody.setText(settings.getDefaultPushBody());
        mSwitchSnow.setChecked(settings.getIsSnowing());
    }

    @Override
    public void afterSaving() {
        onBackPressed();
    }
}

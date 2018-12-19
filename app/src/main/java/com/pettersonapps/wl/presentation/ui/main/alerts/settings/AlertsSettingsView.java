package com.pettersonapps.wl.presentation.ui.main.alerts.settings;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface AlertsSettingsView extends BaseView<AlertsSettingsPresenter> {

    void showSettings(MutableLiveData<AppSettings> settings);

    void showProgress();

    void hideProgress();

    void populateSettings(final AppSettings settings);

    void afterSaving();
}

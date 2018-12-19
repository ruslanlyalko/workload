package com.pettersonapps.wl.presentation.ui.main.alerts.settings;

import com.google.android.gms.tasks.OnSuccessListener;
import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class AlertsSettingsPresenter extends BasePresenter<AlertsSettingsView> {

    private AppSettings mSettings;

    AlertsSettingsPresenter() {
    }

    public void onViewReady() {
        getView().showSettings(getDataManager().getSettings());
    }

    public void setSettings(final AppSettings settings) {
        mSettings = settings;
        getView().populateSettings(settings);
    }

    public void onSave(final String email, final String title, final String body, final boolean isSnowing) {
        if (mSettings == null) return;
        mSettings.setIsSnowig(isSnowing);
        mSettings.setIsSnowing(isSnowing);
        mSettings.setNotificationEmail(email);
        mSettings.setDefaultPushTitle(title);
        mSettings.setDefaultPushBody(body);
        getView().showProgress();
        getDataManager().setSettings(mSettings).addOnSuccessListener(aVoid -> {
            if (getView() != null) {
                getView().afterSaving();

            }
        }).addOnFailureListener(e -> {
            if (getView() != null) {
                getView().hideProgress();
                getView().showError(e.getLocalizedMessage());
            }
        });
    }
}

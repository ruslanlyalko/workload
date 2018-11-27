package com.pettersonapps.wl.presentation.ui.main.settings;

import android.text.TextUtils;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 20.09.2018.
 */
public class SettingsPresenter extends BasePresenter<SettingsView> {

    private User mUser;

    SettingsPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void setUser(final User user) {
        mUser = user;
        getView().populateUserSettings(user);
    }

    public void saveUserNotificationSettings(final String remindMeAt) {
        if (mUser == null || TextUtils.isEmpty(remindMeAt)) return;
        getDataManager().updateRemindMeAt(remindMeAt);
    }

    public void onLogoutClicked() {
        getDataManager().logout();
        getView().showLoginScreen();
    }

    public void saveUserDefaultWorkingTime(final int defaultWorkingTime) {
        if (mUser == null || mUser.getDefaultWorkingTime() == defaultWorkingTime) return;
        getDataManager().updateDefaultWorkingTime(defaultWorkingTime);
    }
}

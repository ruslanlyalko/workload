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

    public void saveUserNotificationSettings(final String notificationHour) {
        if (mUser == null || TextUtils.isEmpty(notificationHour)) return;
        mUser.setNotificationHour(notificationHour.split(":")[0]);
        mUser.setRemindMeAt(notificationHour);
        getDataManager().saveUser(mUser);
    }
}

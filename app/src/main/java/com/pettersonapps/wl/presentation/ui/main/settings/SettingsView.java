package com.pettersonapps.wl.presentation.ui.main.settings;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 20.09.2018.
 */
public interface SettingsView extends BaseView<SettingsPresenter> {

    void showUser(MutableLiveData<User> myUserDate);

    void populateUserSettings(User user);

    void showLoginScreen();

    void showAppOnPlayStore();

    void showSettings(MutableLiveData<AppSettings> settings);
}

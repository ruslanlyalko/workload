package com.pettersonapps.wl.presentation.ui.main.profile;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProfileView extends BaseView<ProfilePresenter> {

    void startProfileEditScreen();

    void showUser(MutableLiveData<User> myUserData);

    void populateUser(User user);

    void showLoginScreen();
}

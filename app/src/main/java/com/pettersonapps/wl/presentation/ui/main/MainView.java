package com.pettersonapps.wl.presentation.ui.main;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface MainView extends BaseView<MainPresenter> {

    void showUser(MutableLiveData<User> myUserData);

    void showMenu(User user);

    void fabClickedFragment();

    void toggleFab(boolean plus);

    void showDashboardFragment();

    void showAlertFragment();

    void showCalendarFragment();

    void showHolidaysFragment();

    void showUsersFragment();

    void showProjectsFragment();

    void showSettingsFragment();

    void showVacationFragment(User user);

    void showProfileFragment(User user);
}

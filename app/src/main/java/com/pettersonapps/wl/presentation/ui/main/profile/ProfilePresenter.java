package com.pettersonapps.wl.presentation.ui.main.profile;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProfilePresenter extends BasePresenter<ProfileView> {

    ProfilePresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void onEditClicked() {
        getView().startProfileEditScreen();
    }

    public void setUser(final User user) {
        getView().populateUser(user);
    }

    public void onLogoutClicked() {
        getDataManager().logout();
        getView().showLoginScreen();
    }
}

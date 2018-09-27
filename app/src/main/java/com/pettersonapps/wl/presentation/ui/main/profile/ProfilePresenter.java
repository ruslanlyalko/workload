package com.pettersonapps.wl.presentation.ui.main.profile;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProfilePresenter extends BasePresenter<ProfileView> {

    private User mUser;

    ProfilePresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
        getView().populateUser(mUser);
    }

    public void onEditClicked() {
        getView().startProfileEditScreen();
    }

    public void setUser(final User user) {
        mUser = user;
        getView().populateUser(user);
    }

    public void onLogoutClicked() {
        getDataManager().logout();
        getView().showLoginScreen();
    }
}

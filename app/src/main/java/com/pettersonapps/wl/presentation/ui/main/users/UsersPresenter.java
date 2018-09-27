package com.pettersonapps.wl.presentation.ui.main.users;

import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UsersPresenter extends BasePresenter<UsersView> {

    UsersPresenter() {
    }

    public void onViewReady() {
        getView().showUsers(getDataManager().getUsers());
    }

    public void onAddClicked() {
        getView().starUserAddScreen();
    }
}

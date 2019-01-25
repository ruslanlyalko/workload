package com.pettersonapps.wl.presentation.ui.main.my_projects;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface MyProjectsView extends BaseView<MyProjectsPresenter> {

    void showUser(User user);

    void showAddProjectScreen();

    void showUser(MutableLiveData<User> myUser);
}

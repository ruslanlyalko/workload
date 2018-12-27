package com.pettersonapps.wl.presentation.ui.main.my_projects;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;
import com.pettersonapps.wl.presentation.ui.main.projects.ProjectsPresenter;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface MyProjectsView extends BaseView<MyProjectsPresenter> {

    void showAddProjectScreen();

    void showUser(MutableLiveData<User> myUser);
}

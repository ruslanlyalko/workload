package com.pettersonapps.wl.presentation.ui.main.projects.project_users;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProjectUsersView extends BaseView<ProjectUsersPresenter> {

    void showProjects(MutableLiveData<List<User>> users);

    void showMyProjects(Project projects);

    void afterSuccessSaving();
}

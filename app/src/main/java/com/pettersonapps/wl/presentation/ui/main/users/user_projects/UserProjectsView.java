package com.pettersonapps.wl.presentation.ui.main.users.user_projects;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface UserProjectsView extends BaseView<UserProjectsPresenter> {

    void showProjects(MutableLiveData<List<Project>> projects);

    void showMyProjects(List<Project> projects);

    void afterSaving(final User user);

}

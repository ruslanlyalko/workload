package com.pettersonapps.wl.presentation.ui.main.my_projects.add;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;
import com.pettersonapps.wl.presentation.ui.main.projects.edit.ProjectEditPresenter;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProjectAddView extends BaseView<ProjectAddPresenter> {

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving(final Project project);


    void showProjects(MutableLiveData<List<Project>> allProjects);

    void showUser(MutableLiveData<User> myUser);

    void setAutocomplete(final List<Project> projects);
}

package com.pettersonapps.wl.presentation.ui.main.projects;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProjectsView extends BaseView<ProjectsPresenter> {

    void showProjects(MutableLiveData<List<Project>> projects);

    void showAddProjectScreen();
}

package com.pettersonapps.wl.presentation.ui.main.projects.project_users;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProjectUsersPresenter extends BasePresenter<ProjectUsersView> {

    private final Project mProject;

    ProjectUsersPresenter(Project project) {
        mProject = project;
    }

    public void onViewReady() {
        getView().showProjects(getDataManager().getAllUsers());
        getView().showMyProjects(mProject);
    }

    public void onSave(List<User> list) {
        // todo
    }
}

package com.pettersonapps.wl.presentation.ui.main.projects.details;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProjectDetailsPresenter extends BasePresenter<ProjectDetailsView> {

    private Project mProject;

    ProjectDetailsPresenter(Project project) {
        mProject = project;
    }

    public void onViewReady() {
    }

    public Project getProject() {
        return mProject;
    }

    public void setProject(final Project project) {
        mProject = project;
    }

    public void onOpenClicked() {
        getView().startManagersScreen(mProject);
    }
}

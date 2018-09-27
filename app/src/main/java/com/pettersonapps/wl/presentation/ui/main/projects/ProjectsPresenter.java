package com.pettersonapps.wl.presentation.ui.main.projects;

import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProjectsPresenter extends BasePresenter<ProjectsView> {

    ProjectsPresenter() {
    }

    public void onViewReady() {
        getView().showProjects(getDataManager().getProjects());
    }

    public void onAddClicked() {
        getView().showAddProjectScreen();
    }
}

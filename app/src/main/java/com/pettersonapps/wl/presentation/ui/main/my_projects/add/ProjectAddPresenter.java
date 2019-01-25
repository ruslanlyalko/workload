package com.pettersonapps.wl.presentation.ui.main.my_projects.add;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProjectAddPresenter extends BasePresenter<ProjectAddView> {

    private List<Project> mProjects;
    private User mUser;

    ProjectAddPresenter() {
    }

    public void onViewReady() {
        getView().showProjects(getDataManager().getAllProjects());
        getView().showUser(getDataManager().getMyUser());
    }

    public void setProjects(final List<Project> projects) {
        mProjects = projects;
        getView().setAutocomplete(projects);
    }

    public void setUser(final User user) {
        mUser = user;
    }

    public void onSave(final String title) {
        if(getProjectByName(title, mUser.getProjects()) != null) {
            getView().showError("You have already added this project");
            return;
        }
        Project project = getProjectByName(title, mProjects);
        if(project == null) {
            getView().showError("Project not found!");
        } else {
            getView().afterSuccessfullySaving(project);
        }
    }

    private Project getProjectByName(final String title, List<Project> projects) {
        for (Project project : projects) {
            if(project.getTitle().replaceAll(" ", "").equalsIgnoreCase(title.replaceAll(" ", ""))) {
                return project;
            }
        }
        return null;
    }
}

package com.pettersonapps.wl.presentation.ui.main.my_projects;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MyProjectsPresenter extends BasePresenter<MyProjectsView> {

    private User mUser;

    MyProjectsPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void onAddClicked() {
        getView().showAddProjectScreen();
    }

    public void setUser(final User user) {
        mUser = user;
        getView().showUser(mUser);
    }

    public void saveChanges(final List<Project> projects) {
        mUser.setProjects(projects);
        getDataManager().saveUser(mUser);
    }

    public void addProject(final Project project) {
        mUser.getProjects().add(project);
        getDataManager().saveUser(mUser);
    }

    public void updateProject(final Project project) {
        for (Project pr : mUser.getProjects()) {
            if (pr.getTitle().equalsIgnoreCase(project.getTitle())) {
                pr.setNotes(project.getNotes());
                break;
            }
        }
        getView().showUser(mUser);
    }
}

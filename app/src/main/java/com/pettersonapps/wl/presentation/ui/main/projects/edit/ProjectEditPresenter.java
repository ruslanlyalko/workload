package com.pettersonapps.wl.presentation.ui.main.projects.edit;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProjectEditPresenter extends BasePresenter<ProjectEditView> {

    private final Project mProject;

    ProjectEditPresenter(Project project) {
        if (project == null)
            project = new Project();
        mProject = project;
    }

    public void onViewReady() {
        getView().setProjectTitle(mProject.getTitle());
    }

    public void onSave(final String title) {
        mProject.setTitle(title);
        getView().showProgress();
        getDataManager().saveProject(mProject)
                .addOnSuccessListener(aVoid -> getView().afterSuccessfullySaving())
                .addOnFailureListener(e -> getView().hideProgress());
    }

    public Project getProject() {
        return mProject;
    }
}

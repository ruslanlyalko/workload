package com.pettersonapps.wl.presentation.ui.main.my_notes.details;

import com.pettersonapps.wl.data.models.Note;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BasePresenter;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MyNotesProjectDetailsPresenter extends BasePresenter<MyNotesProjectDetailsView> {

    private Project mProject;

    MyNotesProjectDetailsPresenter(Project project) {
        mProject = project;
    }

    public void onViewReady() {
        getView().showProjectDetails(mProject);
    }

    public Project getProject() {
        return mProject;
    }

    public void setNotes(final List<Note> data) {
        mProject.setNotes(data);
    }
}

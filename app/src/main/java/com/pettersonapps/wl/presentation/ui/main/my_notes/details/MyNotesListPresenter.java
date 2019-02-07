package com.pettersonapps.wl.presentation.ui.main.my_notes.details;

import com.pettersonapps.wl.data.models.Note;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BasePresenter;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MyNotesListPresenter extends BasePresenter<MyNotesListView> {

    private Project mProject;

    MyNotesListPresenter(Project project) {
        mProject = project;
    }

    public void onViewReady() {
        getView().showProjectDetails(mProject);
    }

    public Project getProject() {
        return mProject;
    }

    void setNotes(final List<Note> data) {
        mProject.setNotes(data);
    }

    void onShareClicked(final List<Note> data, final boolean withCheckMarks) {
        StringBuilder result = new StringBuilder();
        for (Note note : data) {
            String item = (withCheckMarks ? note.getIsChecked() ? "[x] " : "[ ] " : "") + note.getTitle() + "\n";
            result.append(item);
        }
        getView().showShareDialog(result.toString());
    }
}

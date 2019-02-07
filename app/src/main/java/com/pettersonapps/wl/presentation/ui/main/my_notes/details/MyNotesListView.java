package com.pettersonapps.wl.presentation.ui.main.my_notes.details;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface MyNotesListView extends BaseView<MyNotesListPresenter> {

    void showProjectDetails(Project project);
    void showShareDialog(String data);
}

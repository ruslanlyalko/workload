package com.pettersonapps.wl.presentation.ui.main.projects.edit;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProjectEditView extends BaseView<ProjectEditPresenter> {

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving(final Project project);

    void afterSuccessfullyDeleting();

    void setProjectTitle(String title);
}

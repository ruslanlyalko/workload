package com.pettersonapps.wl.presentation.ui.main.projects.details;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProjectDetailsView extends BaseView<ProjectDetailsPresenter> {

    void startManagersScreen(Project project);
}

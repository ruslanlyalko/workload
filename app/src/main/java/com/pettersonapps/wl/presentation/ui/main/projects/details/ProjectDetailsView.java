package com.pettersonapps.wl.presentation.ui.main.projects.details;

import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProjectDetailsView extends BaseView<ProjectDetailsPresenter> {

    void showProgress();

    void hideProgress();

    void showProjectInfo(ProjectInfo projectInfo);

    void showFrom(Date date);

    void showTo(Date date);
}

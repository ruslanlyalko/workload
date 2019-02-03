package com.pettersonapps.wl.presentation.ui.main.my_projects.details.statistics;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface MyProjectStatisticsView extends BaseView<MyProjectStatisticsPresenter> {

    void showReports(MutableLiveData<List<Report>> vacationReportsData);

    void showProjectDetails(Project project);

    void showUser(MutableLiveData<User> myUser);

    void showDateFrom(Date date);

    void showDateTo(Date date);

    void showDateState(boolean dateStateOneDay);

    void showProgress();

    void hideProgress();

    void showProjectInfo(ProjectInfo projectInfo);
}

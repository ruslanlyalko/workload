package com.pettersonapps.wl.presentation.ui.main.workload.report;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ReportEditView extends BaseView<ReportEditPresenter> {

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving();

    void afterSuccessfullyRangeSaving(int count);

    void showReportData(Report report);

    void showHoliday(String holiday);

    void errorCantBeZero();

    void errorCantBeMoreThan16();

    void errorCantHasTwoEqualsProjects();

    void errorCantSaveNotWorkingStatusOnWeekends();

    void showDateState(boolean dateStateOneDay);

    void showDateFrom(Date date);

    void showDateTo(Date date);

    void showProjects(List<Project> projects);

    void addProject(String title);

    void changeProject(String title, int position);

    void showWrongDateOnMobileError();

    void showUser(MutableLiveData<User> myUser);
}

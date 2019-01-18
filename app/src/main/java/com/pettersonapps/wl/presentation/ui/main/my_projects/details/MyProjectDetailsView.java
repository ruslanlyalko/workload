package com.pettersonapps.wl.presentation.ui.main.my_projects.details;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface MyProjectDetailsView extends BaseView<MyProjectDetailsPresenter> {

    void showReports(MutableLiveData<List<Report>> vacationReportsData);

    void showReports(List<Report> list);

    void showReportOnCalendar(List<Report> reportsForCurrentDate, Date date);

    void showHolidaysOnCalendar(MutableLiveData<List<Holiday>> allHolidays);

    void showCalendarsEvents();

    void showProjectDetails(Project project);

    void addNewNote();

    void showSpentHours(int spentHours);
}

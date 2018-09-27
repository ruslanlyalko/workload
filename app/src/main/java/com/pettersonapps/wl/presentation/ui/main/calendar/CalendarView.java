package com.pettersonapps.wl.presentation.ui.main.calendar;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface CalendarView extends BaseView<CalendarPresenter> {

    void showSpinnerProjectsData(MutableLiveData<List<Project>> projects);

    void showSpinnerUsersData(MutableLiveData<List<User>> users);

    void showReportsOnCalendar(MutableLiveData<List<Report>> reports);

    void showReports(List<Report> list);

}

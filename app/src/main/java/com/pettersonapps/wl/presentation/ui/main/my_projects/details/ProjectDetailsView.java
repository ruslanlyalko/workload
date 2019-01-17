package com.pettersonapps.wl.presentation.ui.main.my_projects.details;

import android.arch.lifecycle.MutableLiveData;
import android.util.SparseIntArray;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.data.models.Vacation;
import com.pettersonapps.wl.presentation.base.BaseView;
import com.pettersonapps.wl.presentation.ui.main.users.details.UserDetailsPresenter;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProjectDetailsView extends BaseView<ProjectDetailsPresenter> {

    void showReports(MutableLiveData<List<Report>> vacationReportsData);

    void showReports(List<Report> list);

    void showReportOnCalendar(List<Report> reportsForCurrentDate, Date date);

    void showHolidaysOnCalendar(MutableLiveData<List<Holiday>> allHolidays);

    void setReportsToAdapter(List<Vacation> vacations);
}

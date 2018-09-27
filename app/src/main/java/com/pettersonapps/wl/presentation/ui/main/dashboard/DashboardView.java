package com.pettersonapps.wl.presentation.ui.main.dashboard;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface DashboardView extends BaseView<DashboardPresenter> {

    void showUser(MutableLiveData<User> user);

    void showReportsOnCalendar(MutableLiveData<List<Report>> reports);

    void showHolidaysOnCalendar(MutableLiveData<List<Holiday>> holidays);

    void showHoliday(String holiday);

    void showReports(List<Report> reports);

    void showErrorAndStartLoginScreen();

    void showAdminMenu();

    void editReport(User user, Report report, final List<Holiday> holidays);

    void startAddReportScreen(final User user, final Date date, final List<Holiday> holidays);
}

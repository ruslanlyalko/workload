package com.pettersonapps.wl.presentation.ui.main.workload;

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
public interface WorkloadView extends BaseView<WorkloadPresenter> {

    void showUser(MutableLiveData<User> user);

    void showReportsOnCalendar(MutableLiveData<List<Report>> reports);

    void showHolidaysOnCalendar(MutableLiveData<List<Holiday>> holidays);

    void showReports(List<Report> reports, final Date date);

    void showErrorAndStartLoginScreen();

    void editReport(User user, Report report, final List<Holiday> holidays);

    void copyReport(User user, Report report, List<Holiday> holidays);

    void startAddReportScreen(final User user, final Date date, final List<Holiday> holidays);

    void showWrongDateOnMobileError();

    void showInternetError();
}

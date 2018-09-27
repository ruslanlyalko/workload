package com.pettersonapps.wl.presentation.ui.main.dashboard;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class DashboardPresenter extends BasePresenter<DashboardView> {

    private User mUser;
    private Date mDate = new Date();
    private List<Holiday> mHolidays = new ArrayList<>();
    private List<Report> mReports = new ArrayList<>();

    DashboardPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
        getView().showReportsOnCalendar(getDataManager().getAllMyReports());
        getView().showHolidaysOnCalendar(getDataManager().getHolidays());
        getView().showHoliday(getHoliday(mDate));
        fetchReportsForDate();
    }

    private String getHoliday(final Date date) {
        for (Holiday holiday : mHolidays) {
            if (DateUtils.dateEquals(holiday.getDate(), date)) {
                return holiday.getTitle();
            }
        }
        return null;
    }

    public void fetchReportsForDate() {
        getView().showReports(getDataManager().getMyReportsByDate(mDate));
    }

    public void fetchReportsForDate(Date date) {
        mDate = DateUtils.getDate(date, 1, 1);
        getView().showReports(getDataManager().getMyReportsByDate(mDate));
        getView().showHoliday(getHoliday(mDate));
    }

    public void onReportDeleteClicked(final Report report) {
        getDataManager().removeReport(report).addOnCompleteListener(task -> {
            fetchReportsForDate();
        });
    }

    public void onReportLongClicked(final Report report) {
        if (report.getDate().before(DateUtils.get1DaysAgo().getTime())) return;
        getView().editReport(mUser, report, mHolidays);
    }

    public boolean isAdmin() {
        return mUser != null && mUser.getIsAdmin();
    }

    public void onFabClicked() {
        getView().startAddReportScreen(mUser, mDate, mHolidays);
    }

    public Date getDate() {
        return mDate;
    }

    public List<Holiday> getHolidays() {
        return mHolidays;
    }

    public void setHolidays(final List<Holiday> holidays) {
        mHolidays = holidays;
    }

    public List<Report> getReports() {
        return mReports;
    }

    public void setReports(final List<Report> reports) {
        mReports = reports;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
        if (mUser.getIsBlocked()) {
            getAuth().signOut();
            getView().showErrorAndStartLoginScreen();
        }
        if (mUser.getIsAdmin()) {
            getView().showAdminMenu();
        }
    }
}

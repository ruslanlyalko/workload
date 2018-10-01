package com.pettersonapps.wl.presentation.ui.main.workload;

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
public class WorkloadPresenter extends BasePresenter<WorkloadView> {

    private User mUser;
    private Date mDate = new Date();
    private List<Holiday> mHolidays = new ArrayList<>();
    private List<Report> mReports = new ArrayList<>();

    WorkloadPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
        getView().showReportsOnCalendar(getDataManager().getAllMyReports());
        getView().showHolidaysOnCalendar(getDataManager().getAllHolidays());
        getView().showHoliday(getHoliday(mDate));
    }

    private String getHoliday(final Date date) {
        for (Holiday holiday : mHolidays) {
            if (DateUtils.dateEquals(holiday.getDate(), date)) {
                return holiday.getTitle();
            }
        }
        return null;
    }

    private List<Report> getReportsForCurrentDate() {
        List<Report> result = new ArrayList<>();
        for (Report r : mReports) {
            if (r.getDate().after(DateUtils.getStart(mDate))
                    && r.getDate().before(DateUtils.getEnd(mDate))) {
                result.add(r);
            }
        }
        return result;
    }

    public void fetchReportsForDate(Date date) {
        mDate = DateUtils.getDate(date, 1, 1);
        getView().showReports(getReportsForCurrentDate());
        getView().showHoliday(getHoliday(mDate));
    }

    public void onReportDeleteClicked(final Report report) {
        getDataManager().removeReport(report)
                .addOnCompleteListener(task -> getView().showReports(getReportsForCurrentDate()));
    }

    public void onReportLongClicked(final Report report) {
        if (report.getDate().before(DateUtils.get1DaysAgo().getTime())) return;
        getView().editReport(mUser, report, mHolidays);
    }

    public boolean getIsAllowEditPastReports() {
        return mUser != null && mUser.getIsAllowEditPastReports();
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
        getView().showReports(getReportsForCurrentDate());
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
    }
}

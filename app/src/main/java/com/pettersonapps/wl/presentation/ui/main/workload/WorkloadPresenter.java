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
        getView().showSettings(getDataManager().getSettings());
        getView().showReportsOnCalendar(getDataManager().getAllMyReports());
        getView().showHolidaysOnCalendar(getDataManager().getAllHolidays());
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
        getView().showReports(getReportsForCurrentDate(), mDate);
    }

    public void onReportDeleteClicked(final Report report) {
        getDataManager().isRightDate().addOnSuccessListener(checkDate -> {
            if (checkDate == null) {
                getView().showInternetError();
            } else if (checkDate.getIsRight()) {
                getDataManager().removeReport(report);
                if (getView() == null) return;
                getView().showReports(getReportsForCurrentDate(), mDate);
            } else {
                if (getView() == null) return;
                getView().showWrongDateOnMobileError();
            }
        }).addOnFailureListener(e -> getView().showError(e.getLocalizedMessage()));
    }

    public void onReportClicked(final Report report) {
        if (!getIsAllowEditPastReports() && report.getDate().before(DateUtils.get1DaysAgo().getTime()))
            return;
        getView().editReport(mUser, report, mHolidays);
    }

    public void onReportCopyClicked(final Report report) {
        Report copyReport = new Report(report);
        getView().copyReport(mUser, copyReport, mHolidays);
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
        getView().showReports(getReportsForCurrentDate(), mDate);
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
    }
}

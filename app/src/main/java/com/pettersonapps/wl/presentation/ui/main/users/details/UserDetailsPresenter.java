package com.pettersonapps.wl.presentation.ui.main.users.details;

import android.util.SparseIntArray;

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
public class UserDetailsPresenter extends BasePresenter<UserDetailsView> {

    private User mUser;
    private Date mDate = new Date();
    private List<Report> mReports = new ArrayList<>();
    private List<Holiday> mHolidays;

    UserDetailsPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showUserDetails(mUser);
        getView().showReports(getDataManager().getUserReports(mUser));
        getView().showHolidaysOnCalendar(getDataManager().getAllHolidays());
    }

    public void setReports(final List<Report> reports) {
        mReports = reports;
        SparseIntArray mYears = new SparseIntArray();
        List<Report> vacationReports = new ArrayList<>();
        for (Report report : reports) {
            if (report.getStatus().startsWith("Day")
                    || report.getStatus().startsWith("Vacation")
                    || report.getStatus().startsWith("Sick")) {
                vacationReports.add(report);
                int yearInd = DateUtils.getYearIndex(report.getDate(), mUser.getFirstWorkingDate());
                int value = mYears.get(yearInd);
                value = value + 1;
                mYears.append(yearInd, value);
            }
        }
        getView().showVacationsReports(vacationReports);
        getView().showReportsByYear(mYears);
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
        getView().showUserDetails(mUser);
    }

    public void saveReport(final Report report) {
        report.setUserDepartment(mUser.getDepartment());
        report.setUserId(mUser.getKey());
        report.setUserName(mUser.getName());
        report.setKey(DateUtils.toString(report.getDate(), "yyyyMMdd_'" + mUser.getKey() + "'"));
        getDataManager().saveReport(report);
    }

    public void fetchReportsForDate(final Date date) {
        mDate = DateUtils.getDate(date, 1, 1);
        getView().showReportOnCalendar(getReportsForCurrentDate(), mDate);
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

    public List<Report> getReports() {
        return mReports;
    }

    public List<Holiday> getHolidays() {
        return mHolidays;
    }

    public void setHolidays(final List<Holiday> holidays) {
        mHolidays = holidays;
    }
}

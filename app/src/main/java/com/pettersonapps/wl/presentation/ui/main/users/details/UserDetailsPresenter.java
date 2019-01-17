package com.pettersonapps.wl.presentation.ui.main.users.details;

import android.util.SparseIntArray;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.data.models.Vacation;
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
        fetchReportsForDate(mDate);
        List<Report> listVacationReports = new ArrayList<>();
        SparseIntArray years = new SparseIntArray();
        for (Report report : reports) {
            if (report.getStatus().startsWith("Day")
                    || report.getStatus().startsWith("Vacation")
                    || report.getStatus().startsWith("Sick")) {
                listVacationReports.add(0, report);
                int yearInd = DateUtils.getYearIndex(report.getDate(), mUser.getFirstWorkingDate());
                int value = years.get(yearInd);
                value = value + 1;
                years.append(yearInd, value);
            }
            if (report.getStatus().startsWith("Working")) {
                listVacationReports.add(0, report);
                int yearInd = DateUtils.getYearIndex(report.getDate(), mUser.getFirstWorkingDate());
                int value = years.get(yearInd);
                value = value - 1;
                years.append(yearInd, value);
            }
        }
        getView().setReportsToAdapter(convert(listVacationReports));
        getView().showReportsByYear(years);
    }

    private List<Vacation> convert(final List<Report> reports) {
        List<Vacation> list = new ArrayList<>();
        Date firstReportDate = null;
        Report prevReport = null;
        for (int i = 0; i < reports.size(); i++) {
            Report report = reports.get(i);
            if (prevReport != null) {
                if (DateUtils.daysBetween(prevReport.getDate(), report.getDate()) == 1
                        && prevReport.getStatus().equals(report.getStatus())) {
                    if (firstReportDate == null)
                        firstReportDate = prevReport.getDate();
                } else {
                    if (firstReportDate != null) {
                        list.add(new Vacation(prevReport.getUserDepartment(), prevReport.getUserName(), prevReport.getUserId(), prevReport.getStatus(), prevReport.getDate(), firstReportDate));
                    } else {
                        list.add(new Vacation(prevReport.getUserDepartment(), prevReport.getUserName(), prevReport.getUserId(), prevReport.getStatus(), prevReport.getDate(), prevReport.getDate()));
                    }
                    firstReportDate = null;
                }
            }
            prevReport = report;
        }
        if (prevReport != null)
            if (firstReportDate != null) {
                list.add(new Vacation(prevReport.getUserDepartment(), prevReport.getUserName(), prevReport.getUserId(), prevReport.getStatus(), prevReport.getDate(), firstReportDate));
            } else {
                list.add(new Vacation(prevReport.getUserDepartment(), prevReport.getUserName(), prevReport.getUserId(), prevReport.getStatus(), prevReport.getDate(), prevReport.getDate()));
            }
        return list;
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

    public Date getDate() {
        return mDate;
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

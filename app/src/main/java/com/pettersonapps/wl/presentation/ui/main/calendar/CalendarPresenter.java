package com.pettersonapps.wl.presentation.ui.main.calendar;

import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class CalendarPresenter extends BasePresenter<CalendarView> {

    private List<Report> mReports = new ArrayList<>();
    private Date mDate = new Date();
    private String mProject = "-";
    private String mUser = "-";
    private String mStatus = "-";

    CalendarPresenter() {
    }

    public void onViewReady() {
        getView().showSpinnerProjectsData(getDataManager().getAllProjects());
        getView().showSpinnerUsersData(getDataManager().getAllUsers());
        fetchReportsForMonth(DateUtils.getFirstDateOfMonth(new Date()), DateUtils.getLastDateOfMonth(new Date()));
    }

    public void fetchReportsForMonth(final Date from, final Date to) {
        getView().showReports(getDataManager().getReportsFilter(from, to));
    }

    public void setReports(final List<Report> reports) {
        mReports = reports;
        showFilteredReports();
    }

    public void fetchReports(final Date date) {
        mDate = date;
        showFilteredReports();
    }

    public void setFilter(final String project, final String user, final String status) {
        boolean needReload = false;
        if (!mProject.equals(project)) {
            mProject = project;
            needReload = true;
        }
        if (!mUser.equals(user)) {
            mUser = user;
            needReload = true;
        }
        if (!mStatus.equals(status)) {
            mStatus = status;
            needReload = true;
        }
        if (needReload)
            showFilteredReports();
    }

    private void showFilteredReports() {
        Date from = DateUtils.getStart(mDate);
        Date to = DateUtils.getEnd(mDate);
        List<Report> list = new ArrayList<>();
        List<Report> listToday = new ArrayList<>();
        for (Report report : mReports) {
            if (!mUser.startsWith("-") && !mUser.equalsIgnoreCase(report.getUserName())) {
                continue;
            }
            if (!mStatus.startsWith("-") && !mStatus.equalsIgnoreCase(report.getStatus())) {
                continue;
            }
            if (!mProject.startsWith("-")
                    && !((mProject.equalsIgnoreCase(report.getP1()) && report.getT1() > 0)
                    || ((mProject.equalsIgnoreCase(report.getP2()) && report.getT2() > 0)
                    || ((mProject.equalsIgnoreCase(report.getP3()) && report.getT2() > 0)
                    || ((mProject.equalsIgnoreCase(report.getP4())) && report.getT2() > 0))))) {
                continue;
            }
            list.add(report);
            if (report.getDate().after(from) && report.getDate().before(to))
                listToday.add(report);
        }
        getView().showReportsOnCalendar(list);
        getView().showReportsOnList(listToday);
    }
}

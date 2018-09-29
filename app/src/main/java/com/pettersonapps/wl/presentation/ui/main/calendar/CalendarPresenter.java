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
    private String mProject = "-";
    private String mUser = "-";
    private String mStatus = "-";
    private Date mFrom = new Date();
    private Date mTo = new Date();
    private Date mDate = new Date();

    CalendarPresenter() {
    }

    public void onViewReady() {
        getView().showSpinnerProjectsData(getDataManager().getAllProjects());
        getView().showSpinnerUsersData(getDataManager().getAllUsers());
        fetchReportsEvents(DateUtils.getFirstDateOfMonth(new Date()), DateUtils.getLastDateOfMonth(new Date()));
    }

    public void fetchReportsEvents(final Date from, final Date to) {
        mFrom = from;
        mTo = to;
        getView().showReportsOnCalendar(getDataManager().getReportsFilter(mFrom, mTo, mProject, mUser, mStatus));
    }

    public void fetchReports(final Date date) {
        mDate = date;
        Date from = DateUtils.getStart(date);
        Date to = DateUtils.getEnd(date);
        List<Report> list = new ArrayList<>();
        for (Report report : mReports) {
            if (report.getDate().after(from) && report.getDate().before(to))
                list.add(report);
        }
        getView().showReports(list);
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
            fetchReportsEvents(mFrom, mTo);
    }

    public void setReports(final List<Report> reports) {
        mReports = reports;
        fetchReports(mDate);
    }
}

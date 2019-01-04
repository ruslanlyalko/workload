package com.pettersonapps.wl.presentation.ui.main.calendar;

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
public class CalendarPresenter extends BasePresenter<CalendarView> {

    public static final String KEY_USER = "User";
    public static final String KEY_PROJECT = "Project";
    private static final String KEY_STATUS = "Status";
    private List<Report> mReports = new ArrayList<>();
    private Date mDate = new Date();
    private String mProject = "-";
    private String mUser = "-";
    private String mStatus = "-";
    private List<User> mUsers = new ArrayList<>();

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
            if (!mUser.startsWith(KEY_USER) && !mUser.equalsIgnoreCase(report.getUserName())) {
                continue;
            }
            if (!mStatus.startsWith(KEY_STATUS) && !mStatus.equalsIgnoreCase(report.getStatus())) {
                continue;
            }
            if (!mProject.startsWith(KEY_PROJECT)
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

    public void onReportClicked(final String userId) {
        for (User user : mUsers) {
            if (user.getKey().equalsIgnoreCase(userId)) {
                getView().showUserDetails(user);
                break;
            }
        }
    }

    public void setUsers(final List<User> users) {
        mUsers = users;
    }
}

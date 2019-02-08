package com.pettersonapps.wl.presentation.ui.main.my_projects.details_manager;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Project;
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
public class ManagerMyProjectDetailsPresenter extends BasePresenter<ManagerMyProjectDetailsView> {

    private User mUser;
    private Project mProject;
    private Date mDate = new Date();
    private List<Report> mReports = new ArrayList<>();
    private List<Holiday> mHolidays = new ArrayList<>();

    ManagerMyProjectDetailsPresenter(Project project) {
        mProject = project;
    }

    public void onViewReady() {
        getView().showProgress();
        getView().showUser(getDataManager().getMyUser());
        getView().showReports(getDataManager().getAllReports(mProject.getTitle()));
        getView().showHolidaysOnCalendar(getDataManager().getAllHolidays());
        getView().showProjectDetails(mProject);
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
            if(r.getDateConverted().after(DateUtils.getStart(mDate))
                    && r.getDateConverted().before(DateUtils.getEnd(mDate))) {
                result.add(r);
            }
        }
        return result;
    }

    public List<Report> getReports() {
        return mReports;
    }

    public void setReports(final List<Report> reports) {
        getView().hideProgress();
        mReports.clear();
        mReports.addAll(reports);
        getView().showReports(mReports);
        getView().showCalendarsEvents();
        fetchReportsForDate(mDate);
    }

    public List<Holiday> getHolidays() {
        return mHolidays;
    }

    public void setHolidays(final List<Holiday> holidays) {
        mHolidays = holidays;
        getView().showCalendarsEvents();
    }

    public Project getProject() {
        return mProject;
    }

    public void setUser(final User user) {
        mUser = user;
        getView().invalidateMenu();
    }

    boolean isManager() {
        if(mUser != null)
            return mUser.getIsManager();
        return false;
    }
}

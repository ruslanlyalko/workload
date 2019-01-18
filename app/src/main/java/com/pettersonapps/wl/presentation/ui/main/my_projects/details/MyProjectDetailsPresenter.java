package com.pettersonapps.wl.presentation.ui.main.my_projects.details;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Note;
import com.pettersonapps.wl.data.models.Project;
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
public class MyProjectDetailsPresenter extends BasePresenter<MyProjectDetailsView> {

    private Project mProject;
    private Date mDate = new Date();
    private List<Report> mReports = new ArrayList<>();
    private List<Holiday> mHolidays = new ArrayList<>();

    MyProjectDetailsPresenter(Project project) {
        mProject = project;
    }

    public void onViewReady() {
        getView().showReports(getDataManager().getAllMyReports());
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

    public void setReports(final List<Report> reports) {
        mReports.clear();
        int spentHours = 0;
        for (Report report : reports) {
            if (mProject.getTitle().equals(report.getP1())) {
                spentHours += report.getT1();
                mReports.add(report);
            } else if (mProject.getTitle().equals(report.getP2())) {
                spentHours += report.getT2();
                mReports.add(report);
            } else if (mProject.getTitle().equals(report.getP3())) {
                spentHours += report.getT3();
                mReports.add(report);
            } else if (mProject.getTitle().equals(report.getP4())) {
                spentHours += report.getT4();
                mReports.add(report);
            } else if (mProject.getTitle().equals(report.getP5())) {
                spentHours += report.getT5();
                mReports.add(report);
            } else if (mProject.getTitle().equals(report.getP6())) {
                spentHours += report.getT6();
                mReports.add(report);
            }
        }
        getView().showSpentHours(spentHours);
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

    public void setNotes(final List<Note> data) {
        mProject.setNotes(data);
    }
}

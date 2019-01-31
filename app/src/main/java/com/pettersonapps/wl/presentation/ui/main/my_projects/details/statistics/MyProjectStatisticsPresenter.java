package com.pettersonapps.wl.presentation.ui.main.my_projects.details.statistics;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MyProjectStatisticsPresenter extends BasePresenter<MyProjectStatisticsView> {

    private User mUser;
    private Project mProject;
    private List<Report> mReports = new ArrayList<>();
    private boolean mDateStateOneDay = true;
    private Date mDateFrom = new Date();
    private Date mDateTo = new Date();

    MyProjectStatisticsPresenter(Project project) {
        mProject = project;
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
        getView().showReports(getDataManager().getAllMyReports());
        getView().showProjectDetails(mProject);
        mDateFrom = mProject.getCreatedAt();
        getView().showDateFrom(mDateFrom);
        getView().showDateTo(mDateTo);
        getView().showProjectInfo(new ProjectInfo());
        onUpdateClicked();
    }

    void toggleDateState() {
        mDateStateOneDay = !mDateStateOneDay;
        getView().showDateState(mDateStateOneDay);
        onUpdateClicked();
    }

    public List<Report> getReports() {
        return mReports;
    }

    public void setReports(final List<Report> reports) {
        mReports.clear();
        int spentHours = 0;
        for (Report report : reports) {
            if(mProject.getTitle().equals(report.getP1())) {
                spentHours += report.getT1();
                mReports.add(report);
            } else if(mProject.getTitle().equals(report.getP2())) {
                spentHours += report.getT2();
                mReports.add(report);
            } else if(mProject.getTitle().equals(report.getP3())) {
                spentHours += report.getT3();
                mReports.add(report);
            } else if(mProject.getTitle().equals(report.getP4())) {
                spentHours += report.getT4();
                mReports.add(report);
            } else if(mProject.getTitle().equals(report.getP5())) {
                spentHours += report.getT5();
                mReports.add(report);
            } else if(mProject.getTitle().equals(report.getP6())) {
                spentHours += report.getT6();
                mReports.add(report);
            }
        }
//        getView().showSpentHours(spentHours);
    }

    public Project getProject() {
        return mProject;
    }

    public void setUser(final User user) {
        mUser = user;
    }

    Date getDateFrom() {
        return mDateFrom;
    }

    void setDateFrom(final Date newDate) {
        mDateFrom = newDate;
        getView().showDateFrom(mDateFrom);
        if(mDateFrom.after(mDateTo)) {
            setDateTo(mDateFrom);
        }
    }

    Date getDateTo() {
        return mDateTo;
    }

    void setDateTo(final Date newDate) {
        mDateTo = newDate;
        getView().showDateTo(mDateTo);
        if(mDateTo.before(mDateFrom)) {
            setDateFrom(mDateTo);
        }
    }

    void onUpdateClicked() {
        getView().showProgress();
        getDataManager().getProjectInfo(mProject.getTitle(), mDateFrom, mDateStateOneDay ? new Date() : mDateTo)
                .addOnSuccessListener(projectInfo -> {
                    if(getView() == null) return;
                    getView().hideProgress();
                    getView().showProjectInfo(projectInfo);
                })
                .addOnFailureListener(e -> {
                    if(getView() == null) return;
                    getView().hideProgress();
                });
    }
}

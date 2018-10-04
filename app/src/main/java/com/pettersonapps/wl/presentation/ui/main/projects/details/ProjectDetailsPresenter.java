package com.pettersonapps.wl.presentation.ui.main.projects.details;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProjectDetailsPresenter extends BasePresenter<ProjectDetailsView> {

    private Project mProject;
    private Date mFrom;
    private Date mTo;

    ProjectDetailsPresenter(Project project) {
        mProject = project;
    }

    public void onViewReady() {
        mFrom = DateUtils.get1DaysAgo().getTime();
        mTo = DateUtils.getYesterday().getTime();
        getView().showFrom(mFrom);
        getView().showTo(mTo);
    }

    public Project getProject() {
        return mProject;
    }

    public void onUpdateClicked() {
        getView().showProgress();
        getDataManager().getProjectInfo(mProject.getTitle(), mFrom, mTo)
                .addOnSuccessListener(projectInfo -> {
                    if (getView() == null) return;
                    getView().hideProgress();
                    getView().showProjectInfo(projectInfo);
                })
                .addOnFailureListener(e -> {
                    if (getView() == null) return;
                    getView().hideProgress();
                });
    }

    public Calendar getFrom() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mFrom);
        return calendar;
    }

    public void setFrom(final Date from) {
        mFrom = from;
        if (mTo.before(mFrom)) {
            mTo = mFrom;
            getView().showTo(mTo);
        }
    }

    public Calendar getTo() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mTo);
        return calendar;
    }

    public void setTo(final Date to) {
        mTo = to;
        if (mTo.before(mFrom)) {
            mFrom = mTo;
            getView().showFrom(mFrom);
        }
    }

    public void setProject(final Project project) {
        mProject = project;
    }
}

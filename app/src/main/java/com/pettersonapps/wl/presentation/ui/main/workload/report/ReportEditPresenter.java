package com.pettersonapps.wl.presentation.ui.main.workload.report;

import android.text.TextUtils;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.ui.main.workload.report.adapter.ProjectSelectable;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ReportEditPresenter extends BasePresenter<ReportEditView> {

    private final Report mReport;
    private final ArrayList<Holiday> mHolidays;
    private User mUser;
    private Date mDateTo;
    private boolean mDateStateOneDay = true;
    private boolean mAddProjectMode = true;
    private int mPosition;

    ReportEditPresenter(Report report, Date date, ArrayList<Holiday> holidays) {
        mHolidays = holidays;
        if(report == null) {
            report = new Report();
            report.setDateConverted(date);
            report.setStatus("");
        }
        mReport = report;
        mDateTo = report.getDateConverted();
        if(mReport.getDateConverted().after(DateUtils.get1DaysForward().getTime())
                && !(mReport.getStatus().startsWith("Day") || mReport.getStatus().startsWith("Vacation"))) {
            mReport.setStatus("Vacations");
        }
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
        getView().showHoliday(getHoliday(mReport.getDateConverted()));
        getView().showDateFrom(mReport.getDateConverted());
        getView().showDateTo(mDateTo);
        getView().showReportData(mReport);
    }

    private String getHoliday(final Date date) {
        for (Holiday holiday : mHolidays) {
            if(DateUtils.dateEquals(holiday.getDate(), date)) {
                return holiday.getTitle();
            }
        }
        return null;
    }

    public void onSave(final String status, final List<ProjectSelectable> data) {
        boolean allowSaveData = !(status.startsWith("Day")
                || status.startsWith("Vacation")
                || status.startsWith("No")
                || status.startsWith("Sick"));
        if(data.size() > 0 && allowSaveData) {
            mReport.setP1(data.get(0).getTitle());
            mReport.setT1(data.get(0).getSpent());
        } else {
            mReport.setP1("");
            mReport.setT1(0);
        }
        if(data.size() > 1 && allowSaveData) {
            mReport.setP2(data.get(1).getTitle());
            mReport.setT2(data.get(1).getSpent());
        } else {
            mReport.setP2("");
            mReport.setT2(0);
        }
        if(data.size() > 2 && allowSaveData) {
            mReport.setP3(data.get(2).getTitle());
            mReport.setT3(data.get(2).getSpent());
        } else {
            mReport.setP3("");
            mReport.setT3(0);
        }
        if(data.size() > 3 && allowSaveData) {
            mReport.setP4(data.get(3).getTitle());
            mReport.setT4(data.get(3).getSpent());
        } else {
            mReport.setP4("");
            mReport.setT4(0);
        }
        if(data.size() > 4 && allowSaveData) {
            mReport.setP5(data.get(4).getTitle());
            mReport.setT5(data.get(4).getSpent());
        } else {
            mReport.setP5("");
            mReport.setT5(0);
        }
        if(data.size() > 5 && allowSaveData) {
            mReport.setP6(data.get(5).getTitle());
            mReport.setT6(data.get(5).getSpent());
        } else {
            mReport.setP6("");
            mReport.setT6(0);
        }
        //
        if(hasTwoSameProjects()) {
            getView().errorCantHasTwoEqualsProjects();
            return;
        }
        if(status.startsWith("Work") && getTotalHoursSpent(mReport) == 0) {
            getView().errorCantBeZero();
            return;
        }
        if(status.startsWith("Work") && getTotalHoursSpent(mReport) > 16) {
            getView().errorCantBeMoreThan16();
            return;
        }
        boolean weekendDisallowStatus = (status.startsWith("Day")
                || status.startsWith("Vacation")
                || status.startsWith("Sick"));
        if(weekendDisallowStatus && DateUtils.isWeekends(mReport.getDateConverted())) {
            getView().errorCantSaveNotWorkingStatusOnWeekends();
            return;
        }
        if(!mUser.getIsAllowEditPastReports() && mReport.getDateConverted().before(DateUtils.get1DaysAgo().getTime())) {
            getView().showWrongDateOnMobileError();
            return;
        }
        if(!mUser.getIsAllowEditPastReports() && mReport.getDateConverted().after(DateUtils.get1MonthForward().getTime())) {
            getView().showWrongDateOnMobileError();
            return;
        }
        if(!mUser.getIsAllowEditPastReports() && mReport.getDateConverted().after(DateUtils.get1DaysForward().getTime())
                && !(status.startsWith("Day") || status.startsWith("Vacation"))) {
            getView().showWrongDateOnMobileError();
            return;
        }
        getView().showProgress();
        mReport.setUserId(mUser.getKey());
        mReport.setUserName(mUser.getName());
        mReport.setUserDepartment(mUser.getDepartment());
        mReport.setUpdatedAtConverted(new Date());
        mReport.setStatus(status);
        if(!mDateStateOneDay) {
            saveFewReports();
            return;
        }
        mReport.setKey(DateUtils.toString(mReport.getDateConverted(), "yyyyMMdd_'" + mUser.getKey() + "'"));
        getDataManager().saveReport(mReport)
                .addOnSuccessListener(aVoid -> {
                    if(getView() == null) return;
                    getView().afterSuccessfullySaving();
                })
                .addOnFailureListener(e -> {
                    if(getView() == null) return;
                    getView().showWrongDateOnMobileError();
                    getView().hideProgress();
                });
    }

    private void saveFewReports() {
        Calendar start = Calendar.getInstance();
        start.setTime(mReport.getDateConverted());
        Calendar end = Calendar.getInstance();
        end.setTime(mDateTo);
        end.add(Calendar.DAY_OF_MONTH, 1);
        int count = 0;
        for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
            if(dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY && getHoliday(date) == null) {
                mReport.setDateConverted(date);
                mReport.setKey(DateUtils.toString(mReport.getDateConverted(), "yyyyMMdd_'" + mUser.getKey() + "'"));
                getDataManager().saveReport(mReport);
                count++;
            }
        }
        getView().afterSuccessfullyRangeSaving(count);
    }

    private boolean hasTwoSameProjects() {
        List<String> names = new ArrayList<>();
        if(!TextUtils.isEmpty(mReport.getP1())) {
            names.add(mReport.getP1());
        }
        if(!TextUtils.isEmpty(mReport.getP2())) {
            names.add(mReport.getP2());
        }
        if(!TextUtils.isEmpty(mReport.getP3())) {
            names.add(mReport.getP3());
        }
        if(!TextUtils.isEmpty(mReport.getP4())) {
            names.add(mReport.getP4());
        }
        if(!TextUtils.isEmpty(mReport.getP5())) {
            names.add(mReport.getP5());
        }
        if(!TextUtils.isEmpty(mReport.getP6())) {
            names.add(mReport.getP6());
        }
        for (int i = 0; i < names.size() - 1; i++) {
            for (int j = i + 1; j < names.size(); j++) {
                if(i != j && names.get(i).equals(names.get(j)))
                    return true;
            }
        }
        return false;
    }

    private float getTotalHoursSpent(final Report report) {
        return report.getT1() + report.getT2() + report.getT3() + report.getT4() + report.getT5() + report.getT6();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
        updateProjectsInList();
    }

    public void updateProjectsInList() {
        List<Project> onlyNotHiddenProjects = new ArrayList<>();
        for (Project project : mUser.getProjects()) {
            if(!project.getIsHidden()) {
                onlyNotHiddenProjects.add(project);
            }
        }
        getView().showProjects(onlyNotHiddenProjects);
    }

    public Report getReport() {
        return mReport;
    }

    public void setReportDate(final Date date) {
        mReport.setDateConverted(date);
        getView().showHoliday(getHoliday(mReport.getDateConverted()));
        getView().showDateFrom(date);
        if(date.after(mDateTo)) {
            setDateTo(date);
        }
    }

    public Date getDateTo() {
        return mDateTo;
    }

    public void setDateTo(final Date dateTo) {
        mDateTo = dateTo;
        getView().showDateTo(dateTo);
        if(dateTo.before(mReport.getDateConverted())) {
            setReportDate(mDateTo);
        }
    }

    public void toggleDateState() {
        if(mDateStateOneDay) {
            mDateStateOneDay = false;
        } else {
            mDateStateOneDay = true;
        }
        getView().showDateState(mDateStateOneDay);
    }

    public void setAddProjectMode() {
        mAddProjectMode = true;
    }

    public void setChangeProjectMode(final int position) {
        mAddProjectMode = false;
        mPosition = position;
    }

    public void changeProject(final String title) {
        if(mAddProjectMode) {
            getView().addProject(title, mUser.getDefaultWorkingTime());
        } else {
            getView().changeProject(title, mPosition);
        }
    }
}

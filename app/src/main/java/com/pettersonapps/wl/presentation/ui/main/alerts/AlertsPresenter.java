package com.pettersonapps.wl.presentation.ui.main.alerts;

import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class AlertsPresenter extends BasePresenter<AlertsView> {

    private Date mDate = new Date();
    private AppSettings mSettings = new AppSettings();
    private List<User> mUsers = null;
    private List<Report> mReports = null;

    AlertsPresenter() {
    }

    public void onViewReady() {
        Calendar lastDay = DateUtils.getYesterday();
        if (lastDay.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            lastDay.add(Calendar.DAY_OF_MONTH, -2);
        }
        if (lastDay.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            lastDay.add(Calendar.DAY_OF_MONTH, -1);
        }
        getView().showSettings(getDataManager().getSettings());
        getView().showAllUsers(getDataManager().getAllUsers());
        onDateChanged(lastDay.getTime());
    }

    void onDateChanged(Date date) {
        setDate(date);
        mReports = null;
        checkReports();
        getView().showReports(getDataManager().getReportsFilter(mDate, mDate));
    }

    public void setUsers(final List<User> users) {
        mUsers = users;
        checkReports();
    }

    public void setReports(final List<Report> reports) {
        mReports = reports;
        checkReports();
    }

    private void checkReports() {
        if (mUsers == null || mReports == null) {
            getView().showUsersWithoutReports(null);
            getView().showWrongReports(null);
            getView().showProgress();
            return;
        }
        List<User> listUsers = new ArrayList<>();
        for (User user : mUsers) {
            if (user != null && !user.getIsAdmin() && !user.getIsBlocked() && !user.getIsVip())
                listUsers.add(user);
        }
        List<Report> wrongReports = new ArrayList<>();
        for (Report report : mReports) {
            if (!(report.getStatus().startsWith("Work"))) {//todo
                wrongReports.add(report);
            }
            for (int i = 0; i < listUsers.size(); i++) {
                if (listUsers.get(i).getKey().equals(report.getUserId())) {
                    listUsers.remove(i);
                }
            }
        }
        getView().hideProgress();
        getView().showWrongReports(wrongReports);
        getView().showUsersWithoutReports(listUsers);
    }

    private int getTotalHoursSpent(final Report report) {
        return report.getT1() + report.getT2() + report.getT3() + report.getT4();
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(final Date date) {
        mDate = date;
        getView().showDate(date);
    }

    public void changeEmail(final String newEmail) {
        mSettings.setNotificationEmail(newEmail);
        getDataManager().setSettings(mSettings).addOnSuccessListener(aVoid -> {
            if (getView() != null)
                getView().showMessage("Saved");
        });
    }

    public String getEmail() {
        return mSettings.getNotificationEmail();
    }

    public void setSettings(final AppSettings settings) {
        mSettings = settings;
    }
}

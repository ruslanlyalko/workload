package com.pettersonapps.wl.presentation.ui.main.users.details;

import android.util.SparseIntArray;

import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserDetailsPresenter extends BasePresenter<UserDetailsView> {

    private User mUser;

    UserDetailsPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showUserDetails(mUser);
        getView().showReports(getDataManager().getUserReports(mUser));
    }

    public void setReports(final List<Report> reports) {
        SparseIntArray mYears = new SparseIntArray();
        List<Report> vacationReports = new ArrayList<>();
        for (Report report : reports) {
            if (report.getStatus().startsWith("Day")
                    || report.getStatus().startsWith("Vacation")
                    || report.getStatus().startsWith("Sick")) {
                vacationReports.add(report);
                int yearInd = DateUtils.getYearIndex(report.getDate(), mUser.getFirstWorkingDate());
                int value = mYears.get(yearInd);
                value = value + 1;
                mYears.append(yearInd, value);
            }
        }
        getView().showVacationsReports(vacationReports);
        getView().showReportsByYear(mYears);
        String lastReports = "";
        for (int i = 0; i < Math.min(10, reports.size()); i++) {
            if (DateUtils.isSameMonth(reports.get(i).getDate()))
                lastReports = lastReports.concat(DateUtils.toString(reports.get(i).getDate(), "EE d") + ";  ");
            else
                lastReports = lastReports.concat(DateUtils.toString(reports.get(i).getDate(), "EE d MMM") + ";  ");
        }
        getView().showLast10Reports(lastReports);
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
}

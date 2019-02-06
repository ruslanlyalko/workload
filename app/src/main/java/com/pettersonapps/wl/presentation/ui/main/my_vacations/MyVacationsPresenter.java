package com.pettersonapps.wl.presentation.ui.main.my_vacations;

import android.util.SparseIntArray;

import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.data.models.Vacation;
import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MyVacationsPresenter extends BasePresenter<MyVacationsView> {

    private User mUser;

    MyVacationsPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showReports(getDataManager().getAllMyReports());
    }

    public void setReports(final List<Report> reports) {
        List<Report> listVacationReports = new ArrayList<>();
        SparseIntArray years = new SparseIntArray();
        for (Report report : reports) {
            if(report.getStatus().startsWith("Day")
                    || report.getStatus().startsWith("Vacation")
                    || report.getStatus().startsWith("Sick")) {
                listVacationReports.add(0, report);
                int yearInd = DateUtils.getYearIndex(report.getDateConverted(), mUser.getFirstWorkingDate());
                int value = years.get(yearInd);
                value = value + 1;
                years.append(yearInd, value);
            }
            if(report.getStatus().startsWith("Working")) {
                listVacationReports.add(0, report);
                int yearInd = DateUtils.getYearIndex(report.getDateConverted(), mUser.getFirstWorkingDate());
                int value = years.get(yearInd);
                value = value - 1;
                years.append(yearInd, value);
            }
        }
        getView().setReportsToAdapter(convert(listVacationReports));
        getView().showReportsByYear(mUser.getFirstWorkingDate(), years);
    }

    private List<Vacation> convert(final List<Report> reports) {
        List<Vacation> list = new ArrayList<>();
        Date firstReportDate = null;
        Report prevReport = null;
        for (int i = 0; i < reports.size(); i++) {
            Report report = reports.get(i);
            if(prevReport != null) {
                if(DateUtils.daysBetween(prevReport.getDateConverted(), report.getDateConverted()) == 1
                        && prevReport.getStatus().equals(report.getStatus())) {
                    if(firstReportDate == null)
                        firstReportDate = prevReport.getDateConverted();
                } else {
                    if(firstReportDate != null) {
                        list.add(new Vacation(prevReport.getUserDepartment(), prevReport.getUserName(), prevReport.getUserId(), prevReport.getStatus(), prevReport.getDateConverted(), firstReportDate));
                    } else {
                        list.add(new Vacation(prevReport.getUserDepartment(), prevReport.getUserName(), prevReport.getUserId(), prevReport.getStatus(), prevReport.getDateConverted(), prevReport.getDateConverted()));
                    }
                    firstReportDate = null;
                }
            }
            prevReport = report;
        }
        if(prevReport != null)
            if(firstReportDate != null) {
                list.add(new Vacation(prevReport.getUserDepartment(), prevReport.getUserName(), prevReport.getUserId(), prevReport.getStatus(), prevReport.getDateConverted(), firstReportDate));
            } else {
                list.add(new Vacation(prevReport.getUserDepartment(), prevReport.getUserName(), prevReport.getUserId(), prevReport.getStatus(), prevReport.getDateConverted(), prevReport.getDateConverted()));
            }
        return list;
    }
}

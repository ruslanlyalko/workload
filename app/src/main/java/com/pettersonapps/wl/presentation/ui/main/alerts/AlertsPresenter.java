package com.pettersonapps.wl.presentation.ui.main.alerts;

import com.pettersonapps.wl.presentation.base.BasePresenter;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class AlertsPresenter extends BasePresenter<AlertsView> {

    private Date mDate = new Date();

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
        onDateChanged(lastDay.getTime());
    }

    void onDateChanged(Date date) {
        setDate(date);
        getView().showReports(getDataManager().getAllWrongReports(mDate));
        getView().showUsers(getDataManager().getAllUsersWithoutReports(mDate));
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(final Date date) {
        mDate = date;
        getView().showDate(date);
    }
}

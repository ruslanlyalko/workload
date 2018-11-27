package com.pettersonapps.wl.presentation.ui.main.alerts;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface AlertsView extends BaseView<AlertsPresenter> {

    void showSettings(MutableLiveData<AppSettings> settings);

    void showReports(MutableLiveData<List<Report>> allReports);

    void showAllUsers(MutableLiveData<List<User>> allUsers);

    void showProgress();

    void hideProgress();

    void showDate(Date date);

    void showUsersWithoutReports(List<User> allUsersWithoutReports);

    void showWrongReports(List<Report> allWrongReports);
}

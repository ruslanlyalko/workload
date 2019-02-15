package com.pettersonapps.wl.presentation.ui.main.dashboard.export;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ExportView extends BaseView<ExportPresenter> {

    void showSpinnerProjectsData(MutableLiveData<List<Project>> projects);

    void showSpinnerUsersData(MutableLiveData<List<User>> users);

    void showProgress();

    void hideProgress();

    void showFrom(Date date);

    void showTo(Date date);

    void showExportedData(MutableLiveData<List<Report>> reportsFilter);

    void showFile(File file);
}

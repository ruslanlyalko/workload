package com.pettersonapps.wl.presentation.ui.main.calendar.export;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.io.File;
import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ExportView extends BaseView<ExportPresenter> {

    void showProgress();

    void hideProgress();

    void showFrom(Date date);

    void showTo(Date date);

    void showExportedData(MutableLiveData<List<Report>> reportsFilter);

    void showFile(File file);
}

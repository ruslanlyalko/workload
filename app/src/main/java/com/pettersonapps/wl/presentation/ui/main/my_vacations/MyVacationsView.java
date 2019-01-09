package com.pettersonapps.wl.presentation.ui.main.my_vacations;

import android.arch.lifecycle.MutableLiveData;
import android.util.SparseIntArray;

import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.Vacation;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface MyVacationsView extends BaseView<MyVacationsPresenter> {

    void showReports(MutableLiveData<List<Report>> vacationReportsData);

    void showReportsByYear(final Date firstWorkingDate, SparseIntArray years);

    void setReportsToAdapter(List<Vacation> list);
}

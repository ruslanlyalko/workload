package com.pettersonapps.wl.presentation.ui.main.users.details;

import android.arch.lifecycle.MutableLiveData;
import android.util.SparseIntArray;

import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.Date;
import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface UserDetailsView extends BaseView<UserDetailsPresenter> {

    void showReports(MutableLiveData<List<Report>> vacationReportsData);

    void showReportsByYear(Date firstWorkingDate, SparseIntArray mYears);

    void showUserDetails(User user);
}

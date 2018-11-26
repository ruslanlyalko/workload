package com.pettersonapps.wl.presentation.ui.main.workload.pager;

import com.pettersonapps.wl.data.models.Report;

/**
 * Created by Ruslan Lyalko
 * on 14.01.2018.
 */

public interface OnReportClickListener {

    void onReportClicked(Report report);

    void onReportRemoveClicked(Report report);
}

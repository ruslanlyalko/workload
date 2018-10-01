package com.pettersonapps.wl.presentation.ui.main.workload.report.adapter;

import android.view.View;

/**
 * Created by Ruslan Lyalko
 * on 14.01.2018.
 */

public interface OnProjectSelectClickListener {

    void onProjectAddClicked(View view, final int position);

    void onProjectChangeClicked(View view, final int position);
}

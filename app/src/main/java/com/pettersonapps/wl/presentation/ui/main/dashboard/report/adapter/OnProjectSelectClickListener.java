package com.pettersonapps.wl.presentation.ui.main.dashboard.report.adapter;

import android.view.View;

/**
 * Created by Ruslan Lyalko
 * on 14.01.2018.
 */

public interface OnProjectSelectClickListener {

    void onProjectAddClicked(View view, final int position);

    void onProjectChangeClicked(View view, final int position);
}

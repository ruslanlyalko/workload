package com.pettersonapps.wl.presentation.ui.main.holidays;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface HolidaysView extends BaseView<HolidaysPresenter> {

    void showHolidays(MutableLiveData<List<Holiday>> projects);

    void showAddProjectScreen();
}

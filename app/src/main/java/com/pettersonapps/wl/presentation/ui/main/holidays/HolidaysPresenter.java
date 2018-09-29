package com.pettersonapps.wl.presentation.ui.main.holidays;

import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class HolidaysPresenter extends BasePresenter<HolidaysView> {

    HolidaysPresenter() {
    }

    public void onViewReady() {
        getView().showHolidays(getDataManager().getAllHolidays());
    }

    public void onAddClicked() {
        getView().showAddProjectScreen();
    }
}

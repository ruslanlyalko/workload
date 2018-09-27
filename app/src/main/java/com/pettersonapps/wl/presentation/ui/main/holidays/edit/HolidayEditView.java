package com.pettersonapps.wl.presentation.ui.main.holidays.edit;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface HolidayEditView extends BaseView<HolidayEditPresenter> {

    void showProgress();

    void hideProgress();

    void showHoliday(Holiday holiday);

    void afterSuccessfullySaving();
}

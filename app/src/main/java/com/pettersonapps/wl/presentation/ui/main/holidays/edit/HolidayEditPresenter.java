package com.pettersonapps.wl.presentation.ui.main.holidays.edit;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class HolidayEditPresenter extends BasePresenter<HolidayEditView> {

    private final Holiday mHoliday;

    HolidayEditPresenter(Holiday holiday) {
        if(holiday == null)
            holiday = new Holiday();
        mHoliday = holiday;
    }

    public void onViewReady() {
        getView().showHoliday(mHoliday);
    }

    public void onSave(final String title) {
        mHoliday.setTitle(title);
        getView().showProgress();
        getDataManager().saveHoliday(mHoliday)
                .addOnSuccessListener(aVoid -> {
                    if(getView() == null) return;
                    getView().afterSuccessfullySaving();
                })
                .addOnFailureListener(e -> {
                    if(getView() == null) return;
                    getView().hideProgress();
                });
    }

    public Holiday getHoliday() {
        return mHoliday;
    }

    public void removeHoliday() {
        getDataManager().deleteHoliday(mHoliday)
                .addOnSuccessListener(aVoid -> {
                    if(getView() == null) return;
                    getView().afterSuccessfullySaving();
                });
    }
}

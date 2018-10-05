package com.pettersonapps.wl.presentation.ui.main;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MainPresenter extends BasePresenter<MainView> {

    private User mUser = new User();
    private boolean mStartWithSettings;

    MainPresenter(final boolean startWithSettings) {
        mStartWithSettings = startWithSettings;
    }

    public boolean isStartWithSettings() {
        return mStartWithSettings;
    }

    public void setStartWithSettings(final boolean startWithSettings) {
        mStartWithSettings = startWithSettings;
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void onFabClicked() {
        getView().fabClickedFragment();
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
    }
}

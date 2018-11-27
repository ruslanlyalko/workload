package com.pettersonapps.wl.presentation.ui.main;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pettersonapps.wl.BuildConfig;
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

    public void setUser(final User user, final boolean isNightMode) {
        mUser = user;
        if (user.getVersion() == null || !user.getVersion().equals(BuildConfig.VERSION_NAME)) {
            getDataManager().updateVersion();
        }
        if (user.getToken() == null || !user.getToken().equals(FirebaseInstanceId.getInstance().getToken())) {
            getDataManager().updateToken();
        }
        if (user.getIsNightMode() != isNightMode) {
            getDataManager().updateNightMode(isNightMode);
        }
    }
}

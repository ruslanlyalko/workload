package com.pettersonapps.wl.presentation.ui.splash;

import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class SplashPresenter extends BasePresenter<SplashView> {

    SplashPresenter() {
    }

    public void onViewReady() {
        if (getCurrentUser() != null) {
            getDataManager().getAllMyReports();
            getView().startDashboardScreen();
        } else {
            getView().startLoginScreen();
        }
    }
}

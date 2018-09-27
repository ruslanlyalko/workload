package com.pettersonapps.wl.presentation.ui.main;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class MainPresenter extends BasePresenter<MainView> {

    private User mUser = new User();
    private boolean mIsDashboard = true;

    MainPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void onFabClicked() {
        if (mIsDashboard) {
            getView().fabClickedFragment();
        } else {
            onWorkloadClicked();
        }
    }

    public void onProfileClicked() {
        mIsDashboard = false;
        getView().toggleFab(mIsDashboard);
        getView().showProfileFragment(mUser);
    }

    public void onWorkloadClicked() {
        mIsDashboard = true;
        getView().toggleFab(mIsDashboard);
        getView().showDashboardFragment();
    }

    public void onVacationClicked() {
        mIsDashboard = false;
        getView().showVacationFragment(mUser);
        getView().toggleFab(mIsDashboard);
    }

    public void onAlertsClicked() {
        mIsDashboard = false;
        getView().toggleFab(mIsDashboard);
        getView().showAlertFragment();
    }

    public void onCalendarClicked() {
        mIsDashboard = false;
        getView().toggleFab(mIsDashboard);
        getView().showCalendarFragment();
    }

    public void onHolidaysClicked() {
        mIsDashboard = false;
        getView().toggleFab(mIsDashboard);
        getView().showHolidaysFragment();
    }

    public void onUsersClicked() {
        mIsDashboard = false;
        getView().toggleFab(mIsDashboard);
        getView().showUsersFragment();
    }

    public void onProjectsClicked() {
        mIsDashboard = false;
        getView().toggleFab(mIsDashboard);
        getView().showProjectsFragment();
    }

    public void onSettingsClicked() {
        mIsDashboard = false;
        getView().toggleFab(mIsDashboard);
        getView().showSettingsFragment();
    }

    public void onHomeClicked() {
        getView().showMenu(mUser);
    }

    public boolean isDashboard() {
        return mIsDashboard;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
    }

    public void loadDashboardFragment() {
        mIsDashboard = true;
        getView().toggleFab(mIsDashboard);
        getView().showDashboardFragment();
    }
}

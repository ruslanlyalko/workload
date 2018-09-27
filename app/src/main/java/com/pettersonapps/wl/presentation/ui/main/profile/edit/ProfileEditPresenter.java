package com.pettersonapps.wl.presentation.ui.main.profile.edit;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class ProfileEditPresenter extends BasePresenter<ProfileEditView> {

    private User mUser;

    ProfileEditPresenter() {
    }

    public void onViewReady() {
        getView().showUser(getDataManager().getMyUser());
    }

    public void onSave(final String skype, String phone) {
        getView().showProgress();
        mUser.setSkype(skype);
        mUser.setPhone(phone);
        getDataManager().saveUser(mUser)
                .addOnSuccessListener(aVoid -> getView().afterSuccessfullySaving())
                .addOnFailureListener(e -> getView().hideProgress());
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
    }

    public void onLogoutClicked() {
        getDataManager().logout();
        getView().showLoginScreen();
    }

    public void changePassword(final String newPassword) {
        getDataManager().changePassword(newPassword);
    }
}

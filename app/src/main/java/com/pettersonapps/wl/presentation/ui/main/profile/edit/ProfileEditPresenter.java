package com.pettersonapps.wl.presentation.ui.main.profile.edit;

import android.text.TextUtils;

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

    public void onSave(final String skype, String phone, final String newPassword) {
        getView().showProgress();
        if (!TextUtils.isEmpty(newPassword)) {
            getDataManager().changePassword(newPassword)
                    .addOnFailureListener(e -> {
                        if (getView() == null) return;
                        getView().showMessage(e.getLocalizedMessage());
                        getView().hideProgress();
                    })
                    .addOnSuccessListener(aVoid -> {
                        saveUserData(skype, phone);
                    });
            return;
        }
        saveUserData(skype, phone);
    }

    private void saveUserData(final String skype, String phone) {
        mUser.setSkype(skype);
        mUser.setPhone(phone);
        getDataManager().saveUser(mUser)
                .addOnSuccessListener(aVoid1 -> {
                    if (getView() == null) return;
                    getView().afterSuccessfullySaving();
                })
                .addOnFailureListener(e -> {
                    if (getView() == null) return;
                    getView().hideProgress();
                });
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(final User user) {
        mUser = user;
    }
}

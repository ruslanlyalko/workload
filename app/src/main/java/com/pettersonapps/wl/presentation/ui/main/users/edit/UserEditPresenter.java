package com.pettersonapps.wl.presentation.ui.main.users.edit;

import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.data.models.UserPush;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserEditPresenter extends BasePresenter<UserEditView> {

    private final User mUser;
    private AppSettings mSettings;

    UserEditPresenter(User user) {
        if (user == null)
            throw new RuntimeException("User can't be empty");
        mUser = user;
    }

    public void onViewReady() {
        getView().showUserData(mUser);
        getView().showSettings(getDataManager().getSettings());
    }

    public void onSave(String name, String phone, String skype, String comments, String department, boolean isBlocked, boolean isAllowEdit, final boolean vip) {
        getView().showProgress();
        mUser.setName(name);
        mUser.setPhone(phone);
        mUser.setSkype(skype);
        mUser.setComments(comments);
        mUser.setDepartment(department);
        mUser.setIsBlocked(isBlocked);
        mUser.setIsVip(vip);
        boolean sendPush = !mUser.getIsAllowEditPastReports() && isAllowEdit;
        if (sendPush && mSettings != null) {
            mUser.getPushHistory().add(new UserPush(mSettings.getDefaultPushTitle(), mSettings.getDefaultPushBody()));
        }
        mUser.setIsAllowEditPastReports(isAllowEdit);
        getDataManager().saveUser(mUser)
                .addOnSuccessListener(aVoid -> {
                    if (getView() == null) return;
                    getView().afterSuccessfullySaving(mUser);
                })
                .addOnFailureListener(e -> {
                    if (getView() == null) return;
                    getView().hideProgress();
                });
    }

    public User getUser() {
        return mUser;
    }

    public void setSettings(final AppSettings settings) {
        mSettings = settings;
    }
}

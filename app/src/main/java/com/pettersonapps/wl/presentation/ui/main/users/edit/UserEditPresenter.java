package com.pettersonapps.wl.presentation.ui.main.users.edit;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserEditPresenter extends BasePresenter<UserEditView> {

    private final User mUser;

    UserEditPresenter(User user) {
        if (user == null)
            throw new RuntimeException("User can't be empty");
        mUser = user;
    }

    public void onViewReady() {
        getView().showUserData(mUser);
    }

    public void onSave(String name, String phone, String skype, String comments, String department, boolean isBlocked, boolean isAllowEdit, final boolean vip) {
        getView().showProgress();
        mUser.setName(name);
        mUser.setPhone(phone);
        mUser.setSkype(skype);
        mUser.setComments(comments);
        mUser.setDepartment(department);
        mUser.setIsBlocked(isBlocked);
        mUser.setIsAllowEditPastReports(isAllowEdit);
        mUser.setIsVip(vip);
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
}

package com.pettersonapps.wl.presentation.ui.main.users.push;

import android.text.TextUtils;

import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.data.models.UserPush;
import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserPushPresenter extends BasePresenter<UserPushView> {

    private final User mUser;
    private AppSettings mSettings;

    UserPushPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showSettings(getDataManager().getSettings());
        getView().showUserPushHistory(mUser.getPushHistory());
    }

    public User getUser() {
        return mUser;
    }

    public void onSave(UserPush item) {
        mUser.getPushHistory().add(item);
        getDataManager().saveUser(mUser)
                .addOnSuccessListener(aVoid -> getView().afterSaving(mUser));
    }

    public void onSend(final String title, final String body) {
        if (TextUtils.isEmpty(mUser.getToken())) {
            getView().showError("User is offline!");
            return;
        }
        mUser.getPushHistory().add(new UserPush(title, body));
        getDataManager().saveUser(mUser).addOnSuccessListener(aVoid -> {
            if (getView() != null)
                getView().afterSending();
        }).addOnFailureListener(e -> {
            if (getView() != null)
                getView().showError(e.getLocalizedMessage());
        });
        getView().showUserPushHistory(mUser.getPushHistory());
    }

    public void setSettings(final AppSettings settings) {
        mSettings = settings;
        getView().populateSettings(settings);
    }
}

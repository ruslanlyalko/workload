package com.pettersonapps.wl.presentation.ui.main.users.push;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.data.models.UserPush;
import com.pettersonapps.wl.presentation.base.BaseView;
import com.pettersonapps.wl.presentation.ui.main.users.user_projects.UserProjectsPresenter;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface UserPushView extends BaseView<UserPushPresenter> {

    void afterSaving(final User user);

    void showUserPushHistory(List<UserPush> pushHistory);

    void showSettings(MutableLiveData<AppSettings> settings);

    void populateSettings(AppSettings settings);

    void afterSending();
}

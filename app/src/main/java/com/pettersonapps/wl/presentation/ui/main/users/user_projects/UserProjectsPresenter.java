package com.pettersonapps.wl.presentation.ui.main.users.user_projects;

import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BasePresenter;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class UserProjectsPresenter extends BasePresenter<UserProjectsView> {

    private final User mUser;

    UserProjectsPresenter(User user) {
        mUser = user;
    }

    public void onViewReady() {
        getView().showProjects(getDataManager().getAllProjects());
        getView().showMyProjects(mUser.getProjects());
    }

    public User getUser() {
        return mUser;
    }

    public void onSave(List<Project> list) {
        mUser.setProjects(list);
        getDataManager().saveUser(mUser)
                .addOnSuccessListener(aVoid -> getView().afterSaving(mUser));
    }
}

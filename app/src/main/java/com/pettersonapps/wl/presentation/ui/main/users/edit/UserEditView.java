package com.pettersonapps.wl.presentation.ui.main.users.edit;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface UserEditView extends BaseView<UserEditPresenter> {

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving(final User user);

    void showUserData(User user);
}

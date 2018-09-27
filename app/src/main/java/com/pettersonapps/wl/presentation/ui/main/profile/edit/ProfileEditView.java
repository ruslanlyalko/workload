package com.pettersonapps.wl.presentation.ui.main.profile.edit;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface ProfileEditView extends BaseView<ProfileEditPresenter> {

    void showUser(MutableLiveData<User> user);

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving();
}

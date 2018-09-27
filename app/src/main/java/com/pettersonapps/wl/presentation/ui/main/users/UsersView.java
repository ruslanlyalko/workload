package com.pettersonapps.wl.presentation.ui.main.users;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface UsersView extends BaseView<UsersPresenter> {

    void showUsers(MutableLiveData<List<User>> users);

    void starUserAddScreen();
}

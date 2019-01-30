package com.pettersonapps.wl.presentation.ui.main.my_notes;

import android.arch.lifecycle.MutableLiveData;

import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface MyNotesView extends BaseView<MyNotesPresenter> {

    void showUser(User user);

    void showUser(MutableLiveData<User> myUser);
}

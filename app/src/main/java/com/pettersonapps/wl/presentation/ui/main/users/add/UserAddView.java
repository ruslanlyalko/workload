package com.pettersonapps.wl.presentation.ui.main.users.add;

import com.pettersonapps.wl.presentation.base.BaseView;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public interface UserAddView extends BaseView<UserAddPresenter> {

    void showProgress();

    void hideProgress();

    void afterSuccessfullySaving();

    void close();

}

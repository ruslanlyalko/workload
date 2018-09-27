package com.pettersonapps.wl.presentation.base;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pettersonapps.wl.data.DataManager;
import com.pettersonapps.wl.data.DataManagerImpl;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class BasePresenter<V extends BaseView> {

    private V mBaseView;

    private DataManager mDataManager;

    protected BasePresenter() {
        mDataManager = DataManagerImpl.newInstance();
    }

    public void attachView(final V baseView) {
        mBaseView = baseView;
    }

    public void detachView() {
        mBaseView = null;
    }

    protected V getView() {
        return mBaseView;
    }

    protected FirebaseAuth getAuth() {return FirebaseAuth.getInstance();}

    protected FirebaseUser getCurrentUser() {return FirebaseAuth.getInstance().getCurrentUser();}

    protected DataManager getDataManager() {
        return mDataManager;
    }
}

package com.pettersonapps.wl.presentation.ui.login;

import android.text.TextUtils;

import com.pettersonapps.wl.presentation.base.BasePresenter;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class LoginPresenter extends BasePresenter<LoginView> {

    LoginPresenter() {
    }

    public void onViewReady() {
    }

    public void onLogin(final String email, final String password) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            getView().errorEmpty();
            return;
        }
        getView().showProgress();
        getAuth().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    getDataManager().updateToken();
                    getView().startMainScreen();
                })
                .addOnFailureListener(e -> {
                    getView().hideProgress();
                    getView().showForgotPasswordButton();
                    getView().errorWrongCredentials();
                });
    }

    public void onForgot(final String email) {
        getAuth().sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> getView().showMessage("Email sent!"))
                .addOnFailureListener(e -> getView().showError("Please provide valid email address in field above"));
    }
}

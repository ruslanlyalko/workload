package com.pettersonapps.wl.presentation.ui.login;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.MainActivity;
import com.pettersonapps.wl.presentation.view.SquareButton;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity<LoginPresenter> implements LoginView {

    @BindView(R.id.title) TextView mTitle;
    @BindView(R.id.input_email) TextInputEditText mInputEmail;
    @BindView(R.id.input_password) TextInputEditText mInputPassword;
    @BindView(R.id.button_login) SquareButton mButtonLogin;
    @BindView(R.id.progress) ProgressBar mProgress;
    @BindView(R.id.text_forgot) TextView mTextForgot;

    public static Intent getLaunchIntent(final Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @OnClick(R.id.button_login)
    public void onLoginClick() {
        getPresenter().onLogin(String.valueOf(mInputEmail.getText()), String.valueOf(mInputPassword.getText()));
    }

    @Override
    public void showForgotPasswordButton() {
        mTextForgot.setVisibility(View.VISIBLE);
    }

    @Override
    public void errorWrongCredentials() {
        showError(getString(R.string.error_wrong_credentials));
    }

    @Override
    public void errorEmpty() {
        showError(getString(R.string.error_wrong_credentials));
    }

    @Override
    public void startMainScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AutofillManager afm = getSystemService(AutofillManager.class);
            if (afm != null) {
                afm.commit();
            }
        }
        startActivity(MainActivity.getLaunchIntent(this));
        finish();
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        mProgress.setVisibility(View.VISIBLE);
        mButtonLogin.showProgress(true);
    }

    @Override
    public void hideProgress() {
        mButtonLogin.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    protected boolean hasHomeButton() {
        return false;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_login;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new LoginPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        mTitle.setText(R.string.app_name);
        getPresenter().onViewReady();
    }

    @OnClick(R.id.text_forgot)
    public void onClick() {
        getPresenter().onForgot(mInputEmail.getText().toString());
    }
}

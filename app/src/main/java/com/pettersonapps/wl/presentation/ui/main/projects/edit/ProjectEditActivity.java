package com.pettersonapps.wl.presentation.ui.main.projects.edit;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.view.SquareButton;

import butterknife.BindView;
import butterknife.OnClick;

public class ProjectEditActivity extends BaseActivity<ProjectEditPresenter> implements ProjectEditView {

    private static final String KEY_PROJECT = "project";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.input_title) TextInputEditText mInputTitle;
    @BindView(R.id.button_save) SquareButton mButtonSave;
    @BindView(R.id.progress) ProgressBar mProgress;

    public static Intent getLaunchIntent(final Context activity) {
        return new Intent(activity, ProjectEditActivity.class);
    }

    public static Intent getLaunchIntent(final Context activity, Project project) {
        Intent intent = new Intent(activity, ProjectEditActivity.class);
        intent.putExtra(KEY_PROJECT, project);
        return intent;
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_project_edit;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ProjectEditPresenter(intent.getParcelableExtra(KEY_PROJECT)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(getPresenter().getProject().getKey() == null ? R.string.title_add : R.string.title_edit);
        getPresenter().onViewReady();
    }

    @OnClick(R.id.button_save)
    public void onClick() {
        if (TextUtils.isEmpty(mInputTitle.getText()))
            return;
        getPresenter().onSave(mInputTitle.getText().toString());
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        mButtonSave.showProgress(true);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mButtonSave.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void afterSuccessfullySaving() {
        onBackPressed();
    }

    @Override
    public void setProjectTitle(final String title) {
        mInputTitle.setText(title);
    }
}

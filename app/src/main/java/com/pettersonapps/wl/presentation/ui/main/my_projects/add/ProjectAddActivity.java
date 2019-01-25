package com.pettersonapps.wl.presentation.ui.main.my_projects.add;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.view.SquareButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class ProjectAddActivity extends BaseActivity<ProjectAddPresenter> implements ProjectAddView {

    private static final String KEY_PROJECT = "project";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.input_title) AutoCompleteTextView mInputTitle;
    @BindView(R.id.button_save) SquareButton mButtonSave;
    @BindView(R.id.progress) ProgressBar mProgress;

    public static Intent getLaunchIntent(final Context activity) {
        return new Intent(activity, ProjectAddActivity.class);
    }

    @OnClick(R.id.button_save)
    public void onClick() {
        if(TextUtils.isEmpty(mInputTitle.getText())) {
            showError(getString(R.string.error_cant_be_empty));
            return;
        }
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
    public void afterSuccessfullySaving(final Project project) {
        Intent intent = new Intent();
        intent.putExtra(KEY_PROJECT, project);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public void showProjects(final MutableLiveData<List<Project>> allProjects) {
        allProjects.observe(this, projects -> getPresenter().setProjects(projects));
    }

    @Override
    public void showUser(final MutableLiveData<User> myUser) {
        myUser.observe(this, user -> getPresenter().setUser(user));
    }

    @Override
    public void setAutocomplete(final List<Project> projects) {
        List<String> list = new ArrayList<>();
        for (Project project : projects) {
            list.add(project.getTitle());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.select_dialog_item, list);
        mInputTitle.setThreshold(3);
        mInputTitle.setAdapter(adapter);
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_project_add;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ProjectAddPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_add_project);
        getPresenter().onViewReady();
    }
}

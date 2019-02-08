package com.pettersonapps.wl.presentation.ui.main.projects.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.my_projects.details_manager.ManagerMyProjectDetailsActivity;
import com.pettersonapps.wl.presentation.ui.main.projects.edit.ProjectEditActivity;
import com.pettersonapps.wl.presentation.ui.main.projects.project_users.ProjectUsersActivity;
import com.pettersonapps.wl.presentation.view.ProgressButton;

import butterknife.BindView;
import butterknife.OnClick;

public class ProjectDetailsActivity extends BaseActivity<ProjectDetailsPresenter> implements ProjectDetailsView {

    private static final String KEY_PROJECT = "project";
    private static final int RC_PROJECT_EDIT = 100;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.button_open) ProgressButton mButtonOpen;

    public static Intent getLaunchIntent(final Context activity, Project project) {
        Intent intent = new Intent(activity, ProjectDetailsActivity.class);
        intent.putExtra(KEY_PROJECT, project);
        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_project_details;
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ProjectDetailsPresenter(intent.getParcelableExtra(KEY_PROJECT)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(getPresenter().getProject().getTitle());
        getPresenter().onViewReady();
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if(requestCode == RC_PROJECT_EDIT && resultCode == RESULT_OK) {
            if(data != null && data.hasExtra(KEY_PROJECT))
                getPresenter().setProject(data.getParcelableExtra(KEY_PROJECT));
            setToolbarTitle(getPresenter().getProject().getTitle());
        }
        if(requestCode == RC_PROJECT_EDIT && resultCode == RESULT_FIRST_USER) {
            onBackPressed();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == R.id.action_edit) {
            startActivityForResult(ProjectEditActivity.getLaunchIntent(this, getPresenter().getProject()), RC_PROJECT_EDIT);
            return true;
        }
        if(item.getItemId() == R.id.action_user_projects) {
            startActivity(ProjectUsersActivity.getLaunchIntent(this, getPresenter().getProject()));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.button_open)
    public void onClick() {
        getPresenter().onOpenClicked();
    }

    @Override
    public void startManagersScreen(final Project project) {
        startActivity(ManagerMyProjectDetailsActivity.getLaunchIntent(getContext(), project));
    }
}

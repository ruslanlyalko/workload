package com.pettersonapps.wl.presentation.ui.main.projects.project_users;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.projects.adapter.CheckUsersAdapter;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;

public class ProjectUsersActivity extends BaseActivity<ProjectUsersPresenter> implements ProjectUsersView {

    private static final String KEY_PROJECT = "project";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_users) RecyclerView mRecyclerUsers;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    private CheckUsersAdapter mCheckProjectsAdapter;

    public static Intent getLaunchIntent(final Context context, Project project) {
        Intent intent = new Intent(context, ProjectUsersActivity.class);
        intent.putExtra(KEY_PROJECT, project);
        return intent;
    }

    @Override
    public void showProjects(final MutableLiveData<List<User>> users) {
        users.observe(this, list -> mCheckProjectsAdapter.setData(list));
    }

    @Override
    public void showMyProjects(final Project project) {
        mCheckProjectsAdapter.setCurrentProject(project);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_project_users, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
//            getPresenter().onSave(mCheckProjectsAdapter.get());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_project_users;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new ProjectUsersPresenter(intent.getParcelableExtra(KEY_PROJECT)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_project_users);
        mCheckProjectsAdapter = new CheckUsersAdapter();
        mRecyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerUsers.setAdapter(mCheckProjectsAdapter);
        mRecyclerUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
                if (mRecyclerUsers.canScrollVertically(-1)) {
                    mToolbar.setElevation(mElevation);
                } else {
                    mToolbar.setElevation(0);
                }
            }
        });
        getPresenter().onViewReady();
    }
}

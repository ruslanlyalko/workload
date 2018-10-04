package com.pettersonapps.wl.presentation.ui.main.users.user_projects;

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
import com.pettersonapps.wl.presentation.ui.main.users.adapter.CheckProjectsAdapter;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;

public class UserProjectsActivity extends BaseActivity<UserProjectsPresenter> implements UserProjectsView {

    private static final String KEY_USER = "user";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_projects) RecyclerView mRecyclerReports;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    private CheckProjectsAdapter mCheckProjectsAdapter;

    public static Intent getLaunchIntent(final Context context, User user) {
        Intent intent = new Intent(context, UserProjectsActivity.class);
        intent.putExtra(KEY_USER, user);
        return intent;
    }

    @Override
    public void showProjects(final MutableLiveData<List<Project>> project) {
        project.observe(this, list -> mCheckProjectsAdapter.setData(list));
    }

    @Override
    public void showMyProjects(final List<Project> list) {
        mCheckProjectsAdapter.setCheckedData(list);
    }

    @Override
    public void afterSaving(final User user) {
        Intent intent = new Intent();
        intent.putExtra(KEY_USER, user);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_projects, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            getPresenter().onSave(mCheckProjectsAdapter.getDataChecked());
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
        return R.layout.activity_user_projects;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new UserProjectsPresenter(intent.getParcelableExtra(KEY_USER)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_user_projects);
        mCheckProjectsAdapter = new CheckProjectsAdapter();
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerReports.setAdapter(mCheckProjectsAdapter);
        mRecyclerReports.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
                if (mRecyclerReports.canScrollVertically(-1)) {
                    mToolbar.setElevation(mElevation);
                } else {
                    mToolbar.setElevation(0);
                }
            }
        });
        getPresenter().onViewReady();
    }
}

package com.pettersonapps.wl.presentation.ui.main.my_projects;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.main.my_projects.adapter.MyProjectsAdapter;
import com.pettersonapps.wl.presentation.ui.main.my_projects.adapter.ProjectClickListener;
import com.pettersonapps.wl.presentation.ui.main.my_projects.add.ProjectAddActivity;
import com.pettersonapps.wl.presentation.ui.main.my_projects.details.ProjectDetailsActivity;

import butterknife.BindView;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static android.app.Activity.RESULT_OK;

public class MyProjectsFragment extends BaseFragment<MyProjectsPresenter> implements MyProjectsView, ProjectClickListener {

    private static final String KEY_PROJECT = "project";
    private static final int RC_ADD = 101;
    private static final int RC_PROJECT_DETAILS = 1002;
    @BindView(R.id.recycler_projects) RecyclerView mRecyclerProjects;
    @BindView(R.id.edit_search) SearchView mEditSearch;
    private MyProjectsAdapter mAdapter;

    public static MyProjectsFragment newInstance() {
        Bundle args = new Bundle();
        MyProjectsFragment fragment = new MyProjectsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void showUser(User user) {
        mAdapter.setData(user.getProjects());
    }

    @Override
    public void showAddProjectScreen() {
        startActivityForResult(ProjectAddActivity.getLaunchIntent(getContext()), RC_ADD);
    }

    @Override
    public void showUser(final MutableLiveData<User> myUser) {
        myUser.observe(this, user -> getPresenter().setUser(user));
    }

    @Override
    public void onProjectClicked(final Project project) {
        startActivityForResult(ProjectDetailsActivity.getLaunchIntent(getContext(), project), RC_PROJECT_DETAILS);
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == RC_ADD && resultCode == RESULT_OK) {
            Project project = data.getParcelableExtra(KEY_PROJECT);
            getPresenter().addProject(project);
        }
        if (requestCode == RC_PROJECT_DETAILS && resultCode == RESULT_OK) {
            Project project = data.getParcelableExtra(KEY_PROJECT);
            getPresenter().updateProject(project);
        }
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_projects, menu);
    }

    @Override
    public void onDestroyView() {
        getPresenter().saveChanges(mAdapter.getData());
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                mEditSearch.setVisibility(View.VISIBLE);
                mEditSearch.setIconified(false);
                mEditSearch.requestFocus();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_my_projects;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new MyProjectsPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_my_projects);
        showFab();
        mEditSearch.setOnQueryTextListener(null);
        mEditSearch.setQuery("", false);
        mEditSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String s) {
                mAdapter.getFilter().filter(s);
                return false;
            }
        });
        mEditSearch.setOnCloseListener(() -> {
            hideKeyboard();
            mEditSearch.setVisibility(View.GONE);
            return true;
        });
        mAdapter = new MyProjectsAdapter(this);
        mRecyclerProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerProjects.setAdapter(mAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerProjects, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        getPresenter().onViewReady();
    }

    @Override
    public void onFabClicked() {
        getPresenter().onAddClicked();
    }
}

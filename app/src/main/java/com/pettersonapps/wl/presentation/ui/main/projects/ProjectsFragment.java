package com.pettersonapps.wl.presentation.ui.main.projects;

import android.arch.lifecycle.MutableLiveData;
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
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.main.projects.adapter.ProjectsAdapter;
import com.pettersonapps.wl.presentation.ui.main.projects.details.ProjectDetailsActivity;
import com.pettersonapps.wl.presentation.ui.main.projects.edit.ProjectEditActivity;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class ProjectsFragment extends BaseFragment<ProjectsPresenter> implements ProjectsView, OnItemClickListener {

    @BindView(R.id.recycler_projects) RecyclerView mRecyclerProjects;
    @BindView(R.id.edit_search) SearchView mEditSearch;
    private ProjectsAdapter mAdapter;

    public static ProjectsFragment newInstance() {
        Bundle args = new Bundle();
        ProjectsFragment fragment = new ProjectsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_projects, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                getPresenter().onAddClicked();
                return true;
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
        return R.layout.fragment_projects;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new ProjectsPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_projects);
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
        mAdapter = new ProjectsAdapter(this);
        mRecyclerProjects.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerProjects.setAdapter(mAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerProjects, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        getPresenter().onViewReady();
    }

    @Override
    public void showProjects(final MutableLiveData<List<Project>> projectsData) {
        projectsData.observe(this, mAdapter::setData);
    }

    @Override
    public void showAddProjectScreen() {
        startActivity(ProjectEditActivity.getLaunchIntent(getContext()));
    }

    @Override
    public void onItemClicked(final View view, final int position) {
        startActivity(ProjectDetailsActivity.getLaunchIntent(getContext(), mAdapter.getDataFiltered().get(position)));
    }

    @Override
    public void onItemLongClicked(final View view, final int position) {
        startActivity(ProjectEditActivity.getLaunchIntent(getContext(), mAdapter.getDataFiltered().get(position)));
    }
}

package com.pettersonapps.wl.presentation.ui.main.users;

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
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.main.users.adapter.UsersAdapter;
import com.pettersonapps.wl.presentation.ui.main.users.add.UserAddActivity;
import com.pettersonapps.wl.presentation.ui.main.users.details.UserDetailsActivity;
import com.pettersonapps.wl.presentation.ui.main.users.edit.UserEditActivity;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class UsersFragment extends BaseFragment<UsersPresenter> implements UsersView, OnItemClickListener {

    @BindView(R.id.recycler_users) RecyclerView mRecyclerUsers;
    @BindView(R.id.edit_search) SearchView mEditSearch;
    private UsersAdapter mAdapter;

    public static UsersFragment newInstance() {
        Bundle args = new Bundle();
        UsersFragment fragment = new UsersFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_users, menu);
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
    public void onFabClicked() {
        getPresenter().onAddClicked();
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_users;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new UsersPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_users);
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
        mAdapter = new UsersAdapter(this);
        mRecyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerUsers.setAdapter(mAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerUsers, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        getPresenter().onViewReady();
    }

    @Override
    public void showUsers(final MutableLiveData<List<User>> usersData) {
        usersData.observe(this, mAdapter::setData);
    }

    @Override
    public void starUserAddScreen() {
        startActivity(UserAddActivity.getLaunchIntent(getContext()));
    }

    @Override
    public void onItemClicked(final View view, final int position) {
        startActivity(UserDetailsActivity.getLaunchIntent(getContext(), mAdapter.getDataFiltered().get(position)));
    }

    @Override
    public void onItemLongClicked(final View view, final int position) {
        startActivity(UserEditActivity.getLaunchIntent(getContext(), mAdapter.getDataFiltered().get(position)));
    }
}

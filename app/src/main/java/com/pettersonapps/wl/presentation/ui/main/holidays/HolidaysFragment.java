package com.pettersonapps.wl.presentation.ui.main.holidays;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.main.holidays.adapter.HolidaysAdapter;
import com.pettersonapps.wl.presentation.ui.main.holidays.edit.HolidayEditActivity;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;

import java.util.List;

import butterknife.BindView;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class HolidaysFragment extends BaseFragment<HolidaysPresenter> implements HolidaysView, OnItemClickListener {

    @BindView(R.id.recycler_holidays) RecyclerView mRecyclerHolidays;
    private HolidaysAdapter mAdapter;

    public static HolidaysFragment newInstance() {
        Bundle args = new Bundle();
        HolidaysFragment fragment = new HolidaysFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_holidays, menu);
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_holidays;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new HolidaysPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_holidays);
        showFab();
        mAdapter = new HolidaysAdapter(this);
        mRecyclerHolidays.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerHolidays.setAdapter(mAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerHolidays, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        getPresenter().onViewReady();
    }

    @Override
    public void onFabClicked() {
        getPresenter().onAddClicked();
    }

    @Override
    public void showHolidays(final MutableLiveData<List<Holiday>> projectsData) {
        projectsData.observe(this, mAdapter::setData);
    }

    @Override
    public void showAddProjectScreen() {
        startActivity(HolidayEditActivity.getLaunchIntent(getContext()));
    }

    @Override
    public void onItemClicked(final View view, final int position) {
        startActivity(HolidayEditActivity.getLaunchIntent(getContext(), mAdapter.getData().get(position)));
    }

    @Override
    public void onItemLongClicked(final View view, final int position) {
    }
}

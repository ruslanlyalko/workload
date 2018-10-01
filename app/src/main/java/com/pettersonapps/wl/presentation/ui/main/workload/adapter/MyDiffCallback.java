package com.pettersonapps.wl.presentation.ui.main.workload.adapter;

import android.support.v7.util.DiffUtil;

import com.pettersonapps.wl.data.models.Report;

import java.util.List;

/**
 * Created by Ruslan Lyalko
 * on 25.09.2018.
 */
class MyDiffCallback extends DiffUtil.Callback {

    private List<Report> mOldList;
    private List<Report> mNewList;

    public MyDiffCallback(final List<Report> oldList, final List<Report> newList) {
        mOldList = oldList;
        mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(final int i, final int i1) {
        return mOldList.get(i).getKey().equals(mNewList.get(i).getKey());
    }

    @Override
    public boolean areContentsTheSame(final int i, final int i1) {
        return mOldList.get(i).equals(mNewList.get(i));
    }
}

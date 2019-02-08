package com.pettersonapps.wl.presentation.ui.main.my_projects.statistics.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.ProjectInfo;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class StatisticsAdapter extends RecyclerView.Adapter<StatisticsAdapter.ViewHolder> {

    private final OnItemClickListener mOnItemClickListener;
    private ProjectInfo mProjectInfo = new ProjectInfo();
    private List<Pair<String, Double>> mDepartments = new ArrayList<>();
    private boolean mIsUsersMode = true;

    public StatisticsAdapter(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public ProjectInfo getData() {
        return mProjectInfo;
    }

    public void setData(final ProjectInfo data) {
        mProjectInfo = data;
        Collections.sort(mProjectInfo.getUsers(), (o1, o2) -> o1.getDepartment().compareTo(o2.getDepartment()));
        mDepartments = getDepartmentsData();
        notifyDataSetChanged();
    }

    private List<Pair<String, Double>> getDepartmentsData() {
        List<Pair<String, Double>> list = new ArrayList<>();
        if(mProjectInfo.getiOS() > 0)
            list.add(Pair.create("iOS", mProjectInfo.getiOS()));
        if(mProjectInfo.getAndroid() > 0)
            list.add(Pair.create("Android", mProjectInfo.getAndroid()));
        if(mProjectInfo.getBackend() > 0)
            list.add(Pair.create("Backend,Web", mProjectInfo.getBackend()));
        if(mProjectInfo.getDesign() > 0)
            list.add(Pair.create("Design", mProjectInfo.getDesign()));
        if(mProjectInfo.getPM() > 0)
            list.add(Pair.create("PM", mProjectInfo.getPM()));
        if(mProjectInfo.getQA() > 0)
            list.add(Pair.create("QA", mProjectInfo.getQA()));
        if(mProjectInfo.getOther() > 0)
            list.add(Pair.create("Other", mProjectInfo.getOther()));
        return list;
    }

    public boolean isUsersMode() {
        return mIsUsersMode;
    }

    public void setUsersMode(final boolean usersMode) {
        mIsUsersMode = usersMode;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_statistics, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(mIsUsersMode) {
            holder.bind(mProjectInfo.getUsers().get(position).getName(), mProjectInfo.getUsers().get(position).getDepartment(), mProjectInfo.getUsers().get(position).getTime());
        } else {
            Pair<String, Double> p = mDepartments.get(position);
            holder.bind(p.first, p.second);
        }
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mIsUsersMode ? mProjectInfo.getUsers().size() : mDepartments.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_title) TextView mTextTitle;
        @BindView(R.id.text_subtitle) TextView mTextSubTitle;
        @BindView(R.id.text_count) TextView mTextCount;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final String title, double count) {
            mTextTitle.setText(title);
            mTextSubTitle.setVisibility(View.GONE);
            mTextCount.setText(getFormattedText(count));
        }

        public void bind(final String title, final String subtitle, double count) {
            mTextTitle.setText(title);
            mTextSubTitle.setVisibility(View.VISIBLE);
            mTextSubTitle.setText(subtitle);
            mTextCount.setText(getFormattedText(count));
        }

        private String getFormattedText(final double time) {
            String timeStr = String.format(Locale.US, "%.0fh", time);
            double ex = time % 1;
            if(ex != 0) {
                timeStr = String.format(Locale.US, "%.0fh %dm", time - ex, (int) (ex * 60));
            }
            return timeStr;
        }

        @OnClick(R.id.layout_root)
        void onClicked(View view) {
            if(mOnItemClickListener != null)
                mOnItemClickListener.onItemClicked(view, getAdapterPosition());
        }

        @OnLongClick(R.id.layout_root)
        boolean onLongClick(View v) {
            if(mOnItemClickListener != null)
                mOnItemClickListener.onItemLongClicked(v, getAdapterPosition());
            return true;
        }
    }
}
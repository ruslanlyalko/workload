package com.pettersonapps.wl.presentation.ui.main.users.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class CheckProjectsAdapter extends RecyclerView.Adapter<CheckProjectsAdapter.ViewHolder> {

    private List<Project> mData = new ArrayList<>();
    private List<Project> mDataChecked = new ArrayList<>();

    public CheckProjectsAdapter() {
    }

    public List<Project> getData() {
        return mData;
    }

    public void setData(final List<Project> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public List<Project> getDataChecked() {
        List<Project> projects = new ArrayList<>();
        for (Project pr : mDataChecked) {
            if (getDataIndex(pr.getTitle()) != -1)
                projects.add(pr);
        }
        return projects;
    }

    public void setCheckedData(final List<Project> data) {
        mDataChecked = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project_check, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private int getIndex(String title) {
        for (int i = 0; i < mDataChecked.size(); i++) {
            if (mDataChecked.get(i).getTitle().equals(title))
                return i;
        }
        return -1;
    }

    private int getDataIndex(String title) {
        for (int i = 0; i < mData.size(); i++) {
            if (mData.get(i).getTitle().equals(title))
                return i;
        }
        return -1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.switch_title) Switch mSwitch;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Project project) {
            mSwitch.setText(project.getTitle());
            mSwitch.setChecked(getIndex(project.getTitle()) != -1);
            mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (getIndex(project.getTitle()) == -1) {
                        mDataChecked.add(project);
                    }
                } else {
                    int ind = getIndex(project.getTitle());
                    if (ind != -1) {
                        mDataChecked.remove(ind);
                    }
                }
            });
        }
    }
}
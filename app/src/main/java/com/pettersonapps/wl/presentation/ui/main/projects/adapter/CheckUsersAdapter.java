package com.pettersonapps.wl.presentation.ui.main.projects.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class CheckUsersAdapter extends RecyclerView.Adapter<CheckUsersAdapter.ViewHolder> {

    private List<User> mData = new ArrayList<>();
    private Project mCurrentProject = new Project();
    private List<User> mDataChanged = new ArrayList<>();

    public CheckUsersAdapter() {
    }

    public List<User> getData() {
        return mData;
    }

    public void setData(final List<User> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public List<User> getChangedUsers() {
        return mDataChanged;
    }

    public void setCurrentProject(final Project data) {
        mCurrentProject = data;
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

    private int getIndex(List<Project> projects) {
        for (int i = 0; i < projects.size(); i++) {
            if (projects.get(i).getTitle().equals(mCurrentProject.getTitle()))
                return i;
        }
        return -1;
    }
//
//    private int getDataIndex(String title) {
//        for (int i = 0; i < mData.size(); i++) {
//            if (mData.get(i).getName().equals(title))
//                return i;
//        }
//        return -1;
//    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.switch_title) Switch mSwitch;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final User user) {
            mSwitch.setText(user.getName());
            mSwitch.setOnCheckedChangeListener(null);
            mSwitch.setChecked(getIndex(user.getProjects()) != -1);
            mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    if (getIndex(user.getProjects()) == -1) {
                        user.getProjects().add(mCurrentProject);
                        Collections.sort(user.getProjects(), (p1, p2) -> p1.getTitle().compareTo(p2.getTitle()));
                        addUserToUserWithChangedProject(user);
                    }
                } else {
                    int ind = getIndex(user.getProjects());
                    if (ind != -1) {
                        user.getProjects().remove(ind);
                    }
                    addUserToUserWithChangedProject(user);
                }
            });
        }
    }

    private void addUserToUserWithChangedProject(final User user) {
        for (int i = 0; i < mDataChanged.size(); i++) {
            if (mDataChanged.get(i).getName().equals(user.getName())) {
                return;
            }
        }
        mDataChanged.add(user);
    }
}
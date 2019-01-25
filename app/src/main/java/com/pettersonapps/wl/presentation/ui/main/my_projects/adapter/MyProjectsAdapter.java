package com.pettersonapps.wl.presentation.ui.main.my_projects.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Switch;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Note;
import com.pettersonapps.wl.data.models.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class MyProjectsAdapter extends RecyclerView.Adapter<MyProjectsAdapter.ViewHolder> implements Filterable {

    private List<Project> mData = new ArrayList<>();
    private List<Project> mDataFiltered = new ArrayList<>();
    private String mQuery;
    private ProjectClickListener mOnProjectClickListener;

    public MyProjectsAdapter(ProjectClickListener onProjectClickListener) {
        mOnProjectClickListener = onProjectClickListener;
    }

    public List<Project> getData() {
        return mData;
    }

    public void setData(final List<Project> data) {
        if(mData.isEmpty()) {
            mData = data;
            mDataFiltered = data;
            notifyItemRangeInserted(0, mData.size());
        } else {
            mData = data;
            mDataFiltered = data;
            notifyDataSetChanged();
            if(!TextUtils.isEmpty(mQuery))
                getFilter().filter(mQuery);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_project, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mDataFiltered.get(position));
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mDataFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                mQuery = charSequence.toString();
                if(mQuery.isEmpty()) {
                    mDataFiltered = mData;
                } else {
                    List<Project> filteredList = new ArrayList<>();
                    for (Project user : mData) {
                        if(user.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                            filteredList.add(user);
                        }
                    }
                    mDataFiltered = filteredList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mDataFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mDataFiltered = (ArrayList<Project>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.switch_title) Switch mSwitch;
        @BindView(R.id.text_title) TextView mTextTitle;
        @BindView(R.id.text_subtitle) TextView mTextSubTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Project project) {
            mTextTitle.setText(project.getTitle());
            if(project.getNotes().size() == 0)
                mTextSubTitle.setText("");
            else
                mTextSubTitle.setText(String.format(Locale.US, "(%d/%d)", getCheckedCount(project.getNotes()), project.getNotes().size()));
            mSwitch.setOnCheckedChangeListener(null);
            mSwitch.setChecked(!project.getIsHidden());
            mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                project.setIsHidden(!isChecked);
                change(project);
            });
        }

        private int getCheckedCount(final List<Note> notes) {
            int result = 0;
            for (Note note : notes) {
                if(note.getIsChecked())
                    result += 1;
            }
            return result;
        }

        private void change(final Project project) {
            for (Project pr : mData) {
                if(pr.getKey().equals(project.getKey())) {
                    pr.setIsHidden(project.getIsHidden());
                }
            }
        }

        @OnClick(R.id.layout_root)
        void onClick() {
            if(mOnProjectClickListener != null)
                mOnProjectClickListener.onProjectClicked(mDataFiltered.get(getAdapterPosition()));
        }
    }
}
package com.pettersonapps.wl.presentation.ui.main.projects.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.ViewHolder> implements Filterable {

    private final OnItemClickListener mOnItemClickListener;
    private List<Project> mData = new ArrayList<>();
    private List<Project> mDataFiltered = new ArrayList<>();

    public ProjectsAdapter(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<Project> getData() {
        return mData;
    }

    public void setData(final List<Project> data) {
        mData = data;
        mDataFiltered = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
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
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mDataFiltered = mData;
                } else {
                    List<Project> filteredList = new ArrayList<>();
                    for (Project user : mData) {
                        if (user.getTitle().toLowerCase().contains(charSequence.toString().toLowerCase())) {
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

        @BindView(R.id.text_title) TextView mTextTitle;
//        @BindView(R.id.text_subtitle) TextView mTextSubtitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Project report) {
            mTextTitle.setText(report.getTitle());
        }

        @OnClick(R.id.layout_root)
        void onClicked(View view) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClicked(view, getAdapterPosition());
        }

        @OnLongClick(R.id.layout_root)
        boolean onLongClick(View v) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemLongClicked(v, getAdapterPosition());
            return true;
        }
    }
}
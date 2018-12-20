package com.pettersonapps.wl.presentation.ui.main.workload.report.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.pettersonapps.wl.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Ruslan Lyalko
 * on 21.09.2018.
 */
public class ProjectSelectAdapter extends RecyclerView.Adapter<ProjectSelectAdapter.ViewHolder> {

    private final OnProjectSelectClickListener mOnProjectSelectClickListener;
    private List<ProjectSelectable> mData = new ArrayList<>();

    public ProjectSelectAdapter(final OnProjectSelectClickListener onProjectSelectClickListener) {
        mOnProjectSelectClickListener = onProjectSelectClickListener;
    }

    public List<ProjectSelectable> getData() {
        return mData;
    }

    public void setData(final List<ProjectSelectable> data) {
        mData = data;
        notifyDataSetChanged();
    }

    public void addItem(ProjectSelectable projectSelectable, int defaultWorkingTime) {
        if (mData.size() > 0) {
            int totalHour = 0;
            for (ProjectSelectable p : mData) {
                totalHour += p.getSpent();
            }
            projectSelectable.setSpent(Math.max(1, 8 - totalHour));
        } else {
            projectSelectable.setSpent(defaultWorkingTime);
        }
        mData.add(projectSelectable);
        if (mData.size() == 6)
            notifyItemChanged(mData.size() - 1);
        else
            notifyItemInserted(mData.size() - 1);
    }

    public void changeItem(final String title, final int position) {
        mData.get(position).setTitle(title);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project_select, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < mData.size())
            holder.bind(mData.get(position));
        else
            holder.bind(null);
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return Math.min(mData.size() + 1, 6);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final String[] mHours;
        @BindView(R.id.text_title) TextView mTextTitle;
        @BindView(R.id.text_minus) TextView mTextMinus;
        @BindView(R.id.spinner_hours) Spinner mSpinnerHours;
        @BindView(R.id.text_plus) TextView mTextPlus;
        @BindView(R.id.layout_root) MaterialCardView mLayoutRoot;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mHours = mSpinnerHours.getResources().getStringArray(R.array.hours);
        }

        void bind(@Nullable final ProjectSelectable project) {
            if (project != null) {
                mLayoutRoot.setVisibility(View.VISIBLE);
                mTextTitle.setText(project.getTitle());
                SpinnerAdapter adapter = new ArrayAdapter<>(mTextMinus.getContext(), R.layout.spinner_item, mHours);
                mSpinnerHours.setAdapter(adapter);
                selectHour(String.format(Locale.US, "%.0fh", project.getSpent()));
                mSpinnerHours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                        String s = mSpinnerHours.getSelectedItem().toString();
                        int spent = Integer.parseInt(s.substring(0, s.length() - 1));
                        mData.get(getAdapterPosition()).setSpent(spent);
                    }

                    @Override
                    public void onNothingSelected(final AdapterView<?> parent) {
                    }
                });
            } else {
                // Add type
                mLayoutRoot.setVisibility(View.GONE);
            }
        }

        private void selectHour(final String hour) {
            for (int i = 0; i < mHours.length; i++) {
                if (mHours[i].equals(hour)) {
                    mSpinnerHours.setSelection(i);
                    break;
                }
            }
        }

        @OnClick(R.id.image_remove)
        void onRemoveClicked() {
            if (getAdapterPosition() >= mData.size()) return;
            mData.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
            if (mData.size() == 5) {
                notifyItemInserted(5);
            }
        }

        @OnClick({R.id.text_plus, R.id.text_minus})
        void onPlusMinusClicked(View view) {
            float spent = mData.get(getAdapterPosition()).getSpent();
            switch (view.getId()) {
                case R.id.text_plus:
                    if (spent < 12) {
                        mData.get(getAdapterPosition()).setSpent(spent + 1);
                        selectHour(String.format(Locale.US, "%.0fh", spent + 1));
                    }
                    break;
                case R.id.text_minus:
                    if (spent > 1) {
                        mData.get(getAdapterPosition()).setSpent(spent - 1);
                        selectHour(String.format(Locale.US, "%.0fh", spent - 1));
                    }
                    break;
            }
        }

        @OnClick(R.id.text_title)
        void onClicked(View view) {
            if (mOnProjectSelectClickListener != null)
                mOnProjectSelectClickListener.onProjectChangeClicked(view, getAdapterPosition());
        }

        @OnClick(R.id.text_add_project)
        void onAddClicked(View view) {
            if (mOnProjectSelectClickListener != null)
                mOnProjectSelectClickListener.onProjectAddClicked(view, getAdapterPosition());
        }
    }
}
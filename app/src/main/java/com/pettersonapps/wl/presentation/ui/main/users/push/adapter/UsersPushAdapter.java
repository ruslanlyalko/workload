package com.pettersonapps.wl.presentation.ui.main.users.push.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.UserPush;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class UsersPushAdapter extends RecyclerView.Adapter<UsersPushAdapter.ViewHolder> {

    private List<UserPush> mData = new ArrayList<>();

    public UsersPushAdapter() {
    }

    public List<UserPush> getData() {
        return mData;
    }

    public void setData(final List<UserPush> data) {
        mData = data;
        Collections.sort(mData, (o2, o1) -> o1.getCreatedAt().compareTo(o2.getCreatedAt()));
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_push, parent, false);
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

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_title) TextView mTextTitle;
        @BindView(R.id.text_subtitle) TextView mTextSubtitle;
        @BindView(R.id.text_date) TextView mTextDate;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final UserPush push) {
            mTextTitle.setText(push.getTitle());
            mTextSubtitle.setText(push.getBody());
            mTextDate.setText(DateUtils.toStringDateTime(mTextDate.getContext(), push.getCreatedAt()));
        }
    }
}
package com.pettersonapps.wl.presentation.ui.main.holidays.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;
import com.pettersonapps.wl.presentation.utils.DateUtils;

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
public class HolidaysAdapter extends RecyclerView.Adapter<HolidaysAdapter.ViewHolder> {

    private final OnItemClickListener mOnItemClickListener;
    private List<Holiday> mData = new ArrayList<>();

    public HolidaysAdapter(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public List<Holiday> getData() {
        return mData;
    }

    public void setData(final List<Holiday> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_holiday, parent, false);
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

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bind(final Holiday holiday) {
            mTextTitle.setText(holiday.getTitle());
            mTextSubtitle.setText(DateUtils.toStringStandardDate(holiday.getDate()));
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
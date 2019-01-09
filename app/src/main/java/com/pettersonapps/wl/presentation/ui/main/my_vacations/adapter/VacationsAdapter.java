package com.pettersonapps.wl.presentation.ui.main.my_vacations.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Vacation;
import com.pettersonapps.wl.presentation.utils.ColorUtils;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class VacationsAdapter extends RecyclerView.Adapter<VacationsAdapter.ViewHolder> {

    private List<Vacation> mData = new ArrayList<>();

    public VacationsAdapter() {
    }

    public List<Vacation> getData() {
        return mData;
    }

    public void setData(final List<Vacation> data) {
        mData = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vacation, parent, false);
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

        private final Context mContext;
        @BindView(R.id.layout_root) MaterialCardView mCardRoot;
        @BindView(R.id.text_title) TextView mTextTitle;
        @BindView(R.id.text_subtitle) TextView mTextSubtitle;
        @BindView(R.id.text_date) TextView mTextDate;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mContext = view.getContext();
        }

        void bind(final Vacation vacation) {
            mTextTitle.setTextColor(ContextCompat.getColor(mContext, ColorUtils.getTextColorByStatus(mTextDate.getResources(), vacation.getStatus())));
            mTextTitle.setText(vacation.getStatus());
            int days = Math.abs(DateUtils.daysBetween(vacation.getTo(), vacation.getFrom())) + 1;
            if (vacation.getStatus().startsWith("Working")) {
                mTextSubtitle.setText(String.format(Locale.US, "Days back: %d", days));
            } else {
                mTextSubtitle.setText(String.format(Locale.US, "Days taken: %d", days));
            }
            mTextDate.setText(DateUtils.toStringDates(vacation.getFrom(), vacation.getTo()));
        }
    }
}
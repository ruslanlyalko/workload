package com.pettersonapps.wl.presentation.ui.main.users.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.data.models.User;
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
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> implements SectionTitleProvider, Filterable {

    private final OnItemClickListener mOnItemClickListener;
    private List<User> mData = new ArrayList<>();
    private List<User> mDataFiltered = new ArrayList<>();
    private int mLastAnimatedPosition;
    private Animation mAnimation;
    private String mQuery;
    private boolean mShowAdditionalData;

    public UsersAdapter(final OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setShowAdditionalData(final boolean showAdditionalData) {
        mShowAdditionalData = showAdditionalData;
        notifyDataSetChanged();
    }

    public List<User> getData() {
        return mData;
    }

    public void setData(final List<User> data) {
        Collections.sort(data, (o1, o2) -> Boolean.compare(o1.getIsBlocked(), o2.getIsBlocked()));
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

    public List<User> getDataFiltered() {
        return mDataFiltered;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        if(mAnimation == null)
            mAnimation = AnimationUtils.loadAnimation(v.getContext(), R.anim.item_animation_fall_down);
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
                    List<User> filteredList = new ArrayList<>();
                    for (User user : mData) {
                        if(user.getName().toLowerCase().contains(charSequence.toString().toLowerCase())
                                || (user.getDepartment().toLowerCase().contains(charSequence.toString().toLowerCase()))
                                || (mShowAdditionalData && user.getVersion().toLowerCase().contains(charSequence.toString().toLowerCase()))) {
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
                mDataFiltered = (ArrayList<User>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public String getSectionTitle(final int position) {
        return getData().get(position).getName().substring(0, 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_logo) ImageView mImageLogo;
        @BindView(R.id.text_title) TextView mTextTitle;
        @BindView(R.id.text_subtitle) TextView mTextSubtitle;
        @BindView(R.id.text_letters) TextView mTextLetters;
        @BindView(R.id.image_edit) ImageView mImageEdit;
        @BindView(R.id.image_offline) ImageView mImageOffline;
        @BindView(R.id.image_admin) ImageView mImageAdmin;
        @BindView(R.id.image_vip) ImageView mImageVip;
        @BindView(R.id.image_manager) ImageView mImageManager;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        void bind(final User user) {
            mTextTitle.setText(user.getName());
            if(mShowAdditionalData) {
                String text = String.format(Locale.US, "%d. %s v%s %s %s %s",
                        getAdapterPosition() + 1,
                        user.getDepartment(),
                        user.getVersion(),
                        user.getIsNightMode() ? "Night" : "",
                        user.getDefaultWorkingTime() != 8 ? "[4]" : "",
                        hasNotes(user.getProjects())
                );
                mTextSubtitle.setText(text);
            } else
                mTextSubtitle.setText(user.getDepartment());
            if(!user.getIsBlocked()) {
                mImageLogo.setImageResource(R.drawable.bg_oval_green);
            } else {
                mImageLogo.setImageResource(R.drawable.bg_oval_yellow);
            }
            mTextLetters.setText(getAbbreviation(user.getName()));
            mImageAdmin.setVisibility(user.getIsAdmin() ? View.VISIBLE : View.GONE);
            mImageEdit.setVisibility(user.getIsAllowEditPastReports() ? View.VISIBLE : View.GONE);
            mImageVip.setVisibility(user.getIsVip() ? View.VISIBLE : View.GONE);
            mImageOffline.setVisibility(TextUtils.isEmpty(user.getToken()) ? View.VISIBLE : View.GONE);
            mImageManager.setVisibility(user.getIsManager() ? View.VISIBLE : View.GONE);
//            runEnterAnimation(itemView, getAdapterPosition());
        }

        private String hasNotes(final List<Project> projects) {
            int count = 0;
            for (Project pro : projects) {
                count += pro.getNotes().size();
            }
            if(count > 0)
                return String.format(Locale.US, "{%d}", count);
            return "";
        }

        private String getAbbreviation(final String name) {
            if(TextUtils.isEmpty(name)) return "";
            String[] list = name.split(" ");
            String result = list[0].substring(0, 1);
            if(list.length > 1)
                result = result + list[1].substring(0, 1);
            return result.toUpperCase();
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

        private void runEnterAnimation(View view, int position) {
            if(position >= mDataFiltered.size()) {
                return;
            }
            if(position > mLastAnimatedPosition) {
                mLastAnimatedPosition = position;
                view.startAnimation(mAnimation);
            }
        }
    }
}
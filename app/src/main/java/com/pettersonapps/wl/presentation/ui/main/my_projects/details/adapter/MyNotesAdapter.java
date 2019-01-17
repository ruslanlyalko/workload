package com.pettersonapps.wl.presentation.ui.main.my_projects.details.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Note;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class MyNotesAdapter extends RecyclerView.Adapter<MyNotesAdapter.ViewHolder> {

    private List<Note> mData = new ArrayList<>();

    public MyNotesAdapter() {
    }

    public List<Note> getData() {
        return mData;
    }

    public void setData(final List<Note> data) {
        if (mData.isEmpty()) {
            mData = data;
            notifyItemRangeInserted(0, mData.size());
        } else {
            mData = data;
            notifyDataSetChanged();
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
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

    public void addNote() {
        mData.add(new Note(String.valueOf(mData.size())));
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.check_box) CheckBox mCheckBox;
        @BindView(R.id.edit_title) EditText mEditTitle;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        public void bind(final Note note) {
            mEditTitle.setText(note.getTitle());
            mCheckBox.setOnCheckedChangeListener(null);
            mCheckBox.setChecked(note.getIsChecked());
            mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                note.setIsChecked(isChecked);
                change(note);
            });
            mEditTitle.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after) {
                }

                @Override
                public void onTextChanged(final CharSequence s, final int start, final int before, final int count) {
                    note.setTitle(s.toString());
                    change(note);
                }

                @Override
                public void afterTextChanged(final Editable s) {
                }
            });
        }

        private void change(final Note note) {
            mData.get(getAdapterPosition()).setIsChecked(note.getIsChecked());
            mData.get(getAdapterPosition()).setTitle(note.getTitle());
//            for (Note pr : mData) {
//                if (pr.getKey().equals(note.getKey())) {
//                    pr.setIsChecked(note.getIsChecked());
//                }
//            }
        }
    }
}
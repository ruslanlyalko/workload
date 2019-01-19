package com.pettersonapps.wl.presentation.ui.main.my_projects.details.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Note;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class MyNotesAdapter extends RecyclerView.Adapter<MyNotesAdapter.ViewHolder> {

    private List<Note> mData = new ArrayList<>();
    private boolean mShowFocusOnLastItem;

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
        holder.bind(position < mData.size() ? mData.get(position) : null);
    }

    @Override
    public int getItemViewType(final int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return mData.size() + 1;
    }

    public void addNote() {
        mData.add(new Note(String.valueOf(mData.size())));
        mShowFocusOnLastItem = true;
        notifyItemInserted(mData.size() - 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.check_box) CheckBox mCheckBox;
        @BindView(R.id.edit_title) EditText mEditTitle;
        @BindView(R.id.image_remove) ImageView mImageRemove;
        @BindView(R.id.text_add_note) TextView mTextAddNote;
        private Context mContext;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mContext = view.getContext();
        }

        public void bind(final Note note) {
            if (note == null) {
                mCheckBox.setVisibility(View.GONE);
                mEditTitle.setVisibility(View.GONE);
                mImageRemove.setVisibility(View.GONE);
                mTextAddNote.setVisibility(View.VISIBLE);
                return;
            }
            mTextAddNote.setVisibility(View.GONE);
            mCheckBox.setVisibility(View.VISIBLE);
            mEditTitle.setVisibility(View.VISIBLE);
            mEditTitle.setTag(note);
            mEditTitle.setText(note.getTitle());
            mCheckBox.setOnCheckedChangeListener(null);
            mCheckBox.setChecked(note.getIsChecked());
            mCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Note aNote = (Note) mEditTitle.getTag();
                aNote.setIsChecked(isChecked);
                change(aNote);
                if (isChecked)
                    mEditTitle.setPaintFlags(mEditTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    mEditTitle.setPaintFlags(mEditTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            });
            if (note.getIsChecked())
                mEditTitle.setPaintFlags(mEditTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            else
                mEditTitle.setPaintFlags(mEditTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            if (mShowFocusOnLastItem && getAdapterPosition() == mData.size() - 1) {
                mShowFocusOnLastItem = false;
                mEditTitle.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager != null && !inputMethodManager.isAcceptingText()) {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        }

        @OnTextChanged(R.id.edit_title)
        void onTextChanged(CharSequence s) {
            Note aNote = (Note) mEditTitle.getTag();
            if (aNote == null) return;
            aNote.setTitle(s.toString());
            change(aNote);
        }

        @OnFocusChange(R.id.edit_title)
        void onFocusChanged(boolean isFocused) {
            mImageRemove.setVisibility(isFocused ? View.VISIBLE : View.GONE);
        }

        @OnClick(R.id.text_add_note)
        void onAddClicked() {
            addNote();
        }

        @OnClick(R.id.image_remove)
        void onRemoveClicked() {
            mData.remove(getAdapterPosition());
            for (int i = getAdapterPosition(); i < mData.size(); i++) {
                mData.get(i).setKey(String.valueOf(i));
            }
            notifyItemRemoved(getAdapterPosition());
        }

        private void change(final Note note) {
            for (Note pr : mData) {
                if (pr.getKey().equals(note.getKey())) {
                    pr.setIsChecked(note.getIsChecked());
                    pr.setTitle(note.getTitle());
                    break;
                }
            }
        }
    }
}
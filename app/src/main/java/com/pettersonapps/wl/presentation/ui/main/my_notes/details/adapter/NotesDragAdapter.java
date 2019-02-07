package com.pettersonapps.wl.presentation.ui.main.my_notes.details.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Note;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;

/**
 * Created by Ruslan Lyalko
 * on 08.08.2018.
 */
public class NotesDragAdapter extends BaseItemDraggableAdapter<Note, NotesDragAdapter.ViewHolder> {

    private boolean mShowFocusOnLastItem;

    public NotesDragAdapter() {
        super(R.layout.item_note, new ArrayList<>());
    }

    @Override
    protected void convert(final ViewHolder helper, final Note item) {
        helper.bind(item);
    }

    public void addNote() {
        addData(new Note(String.valueOf(mData.size())));
        mShowFocusOnLastItem = true;
    }

    public class ViewHolder extends BaseViewHolder {

        @BindView(R.id.check_box) CheckBox mCheckBox;
        @BindView(R.id.edit_title) EditText mEditTitle;
        @BindView(R.id.image_remove) ImageView mImageRemove;
        private Context mContext;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mContext = view.getContext();
        }

        public void bind(final Note note) {
            if(note == null)
                return;
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
                if(isChecked)
                    mEditTitle.setPaintFlags(mEditTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                else
                    mEditTitle.setPaintFlags(mEditTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            });
            if(note.getIsChecked())
                mEditTitle.setPaintFlags(mEditTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            else
                mEditTitle.setPaintFlags(mEditTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            if(mShowFocusOnLastItem && getAdapterPosition() == mData.size() - 1) {
                mShowFocusOnLastItem = false;
                mEditTitle.requestFocus();
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                if(inputMethodManager != null && !inputMethodManager.isAcceptingText()) {
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
            }
        }

        @OnTextChanged(R.id.edit_title)
        void onTextChanged(CharSequence s) {
            Note aNote = (Note) mEditTitle.getTag();
            if(aNote == null) return;
            aNote.setTitle(s.toString());
            change(aNote);
        }

        @OnFocusChange(R.id.edit_title)
        void onFocusChanged(boolean isFocused) {
            mImageRemove.setVisibility(isFocused ? View.VISIBLE : View.GONE);
        }

        @OnClick(R.id.image_remove)
        void onRemoveClicked() {
            boolean removeLastItem = false;
            if(getAdapterPosition() == mData.size() - 1) {
                removeLastItem = true;
            }
            if(removeLastItem && mData.size() == 1) {
                InputMethodManager imm = (InputMethodManager) mEditTitle.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                if(imm != null) {
                    imm.hideSoftInputFromWindow(mEditTitle.getWindowToken(), 0);
                }
            }
            mData.remove(getAdapterPosition());
            for (int i = getAdapterPosition(); i < mData.size(); i++) {
                mData.get(i).setKey(String.valueOf(i));
            }
            notifyItemRemoved(getAdapterPosition());
            if(removeLastItem) {
                mShowFocusOnLastItem = true;
                if(!mData.isEmpty()) {
                    notifyItemChanged(mData.size() - 1);
                }
            }
        }

        private void change(final Note note) {
            for (Note pr : mData) {
                if(pr.getKey().equals(note.getKey())) {
                    pr.setIsChecked(note.getIsChecked());
                    pr.setTitle(note.getTitle());
                    break;
                }
            }
        }
    }
}
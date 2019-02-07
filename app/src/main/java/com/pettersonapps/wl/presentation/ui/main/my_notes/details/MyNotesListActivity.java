package com.pettersonapps.wl.presentation.ui.main.my_notes.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Note;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.my_notes.details.adapter.NotesDragAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;

public class MyNotesListActivity extends BaseActivity<MyNotesListPresenter> implements MyNotesListView {

    private static final String KEY_PROJECT = "project";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_notes) RecyclerView mRecyclerNotes;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    private NotesDragAdapter mAdapterNotes = new NotesDragAdapter();

    public static Intent getLaunchIntent(final Context context, Project project) {
        Intent intent = new Intent(context, MyNotesListActivity.class);
        intent.putExtra(KEY_PROJECT, project);
        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_my_notes_list;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new MyNotesListPresenter(intent.getParcelableExtra(KEY_PROJECT)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setupAdapter();
        getPresenter().onViewReady();
    }

    private void setupAdapter() {
        mRecyclerNotes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
                toggleElevation();
            }
        });
        mRecyclerNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerNotes.setAdapter(mAdapterNotes);
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(mAdapterNotes);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerNotes);
        mAdapterNotes.enableDragItem(itemTouchHelper, R.id.view_drag, false);
        View view = getFooterView();
        view.setOnClickListener(v -> mAdapterNotes.addNote());
        mAdapterNotes.addFooterView(view);
    }

    private View getFooterView() {
        return getLayoutInflater().inflate(R.layout.item_note_footer, (ViewGroup) mRecyclerNotes.getParent(), false);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my_notes_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == R.id.action_share) {
            getPresenter().onShareClicked(mAdapterNotes.getData(), true);
            return true;
        }
        if(item.getItemId() == R.id.action_share_text) {
            getPresenter().onShareClicked(mAdapterNotes.getData(), false);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProjectDetails(final Project project) {
        setToolbarTitle(project.getTitle());
        mAdapterNotes.setNewData(project.getNotes());
    }

    @Override
    public void showShareDialog(final String data) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, data);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    public void onBackPressed() {
        List<Note> data = mAdapterNotes.getData();
        List<Note> dataToSave = new ArrayList<>();
        for (Note note : data) {
            note.setTitle(note.getTitle().trim());
            if(!TextUtils.isEmpty(note.getTitle())) {
                dataToSave.add(note);
            }
        }
        for (int i = 0; i < dataToSave.size(); i++) {
            Note n = dataToSave.get(i);
            n.setKey(String.valueOf(i));
        }
        getPresenter().setNotes(dataToSave);
        Intent intent = new Intent();
        intent.putExtra(KEY_PROJECT, getPresenter().getProject());
        setResult(RESULT_OK, intent);
        super.onBackPressed();
    }

    private void toggleElevation() {
        if(mRecyclerNotes.canScrollVertically(-1)) {
            mToolbar.setElevation(mElevation);
        } else {
            mToolbar.setElevation(0);
        }
    }
}

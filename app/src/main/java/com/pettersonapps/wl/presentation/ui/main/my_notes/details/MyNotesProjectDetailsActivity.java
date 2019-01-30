package com.pettersonapps.wl.presentation.ui.main.my_notes.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Note;
import com.pettersonapps.wl.data.models.Project;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.my_notes.details.adapter.MyNotesAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class MyNotesProjectDetailsActivity extends BaseActivity<MyNotesProjectDetailsPresenter> implements MyNotesProjectDetailsView {

    private static final String KEY_PROJECT = "project";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_notes) RecyclerView mRecyclerNotes;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    private MyNotesAdapter mAdapterNotes;

    public static Intent getLaunchIntent(final Context context, Project project) {
        Intent intent = new Intent(context, MyNotesProjectDetailsActivity.class);
        intent.putExtra(KEY_PROJECT, project);
        return intent;
    }

    @Override
    public void showProjectDetails(final Project project) {
        setToolbarTitle(project.getTitle());
        mAdapterNotes.setData(project.getNotes());
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

    @Override
    protected int getContentView() {
        return R.layout.activity_my_notes_project_details;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new MyNotesProjectDetailsPresenter(intent.getParcelableExtra(KEY_PROJECT)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_project_details);
        mRecyclerNotes.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
                toggleElevation();
            }
        });
        setupAdapter();
        getPresenter().onViewReady();
    }

    private void toggleElevation() {
        if(mRecyclerNotes.canScrollVertically(-1)) {
            mToolbar.setElevation(mElevation);
        } else {
            mToolbar.setElevation(0);
        }
    }

    private void setupAdapter() {
        mAdapterNotes = new MyNotesAdapter();
        mRecyclerNotes.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerNotes.setAdapter(mAdapterNotes);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerNotes, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }
}

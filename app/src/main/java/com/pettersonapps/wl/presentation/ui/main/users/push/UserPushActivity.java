package com.pettersonapps.wl.presentation.ui.main.users.push;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.data.models.UserPush;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.users.push.adapter.UsersPushAdapter;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

public class UserPushActivity extends BaseActivity<UserPushPresenter> implements UserPushView {

    private static final String KEY_USER = "user";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.recycler_push) RecyclerView mRecyclerPush;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    @BindView(R.id.input_title) TextInputEditText mInputTitle;
    @BindView(R.id.input_body) TextInputEditText mInputBody;
    private UsersPushAdapter mUsersPushAdapter;

    public static Intent getLaunchIntent(final Context context, User user) {
        Intent intent = new Intent(context, UserPushActivity.class);
        intent.putExtra(KEY_USER, user);
        return intent;
    }

    @Override
    public void afterSaving(final User user) {
        Intent intent = new Intent();
        intent.putExtra(KEY_USER, user);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    public void showUserPushHistory(final List<UserPush> pushHistory) {
        mUsersPushAdapter.setData(pushHistory);
    }

    @Override
    public void showSettings(final MutableLiveData<AppSettings> settings) {
        settings.observe(this, appSettings -> {
            if(appSettings != null) {
                getPresenter().setSettings(appSettings);
            }
        });
    }

    @Override
    public void populateSettings(final AppSettings settings) {
        mInputTitle.setText(settings.getDefaultPushTitle());
        mInputBody.setText(settings.getDefaultPushBody());
    }

    @Override
    public void afterSending() {
        showMessage(getString(R.string.sent));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_push, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == R.id.action_send) {
            if(TextUtils.isEmpty(mInputTitle.getText())) {
                showError("Title can't be empty!");
                return false;
            }
            if(TextUtils.isEmpty(mInputBody.getText())) {
                showError("Body can't be empty!");
                return false;
            }
            getPresenter().onSend(mInputTitle.getText().toString(), mInputBody.getText().toString());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_user_push;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new UserPushPresenter(intent.getParcelableExtra(KEY_USER)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_user_push);
        mUsersPushAdapter = new UsersPushAdapter();
        mRecyclerPush.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerPush.setAdapter(mUsersPushAdapter);
        OverScrollDecoratorHelper.setUpOverScroll(mRecyclerPush, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
        mRecyclerPush.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
                if(mRecyclerPush.canScrollVertically(-1)) {
                    mToolbar.setElevation(mElevation);
                } else {
                    mToolbar.setElevation(0);
                }
            }
        });
        getPresenter().onViewReady();
    }
}

package com.pettersonapps.wl.presentation.ui.main.users.details;

import android.arch.lifecycle.MutableLiveData;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.dashboard.adapter.ReportsAdapter;
import com.pettersonapps.wl.presentation.ui.main.users.user_projects.UserProjectsActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.Date;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class UserDetailsActivity extends BaseActivity<UserDetailsPresenter> implements UserDetailsView {

    private static final String KEY_USER = "user";
    private static final int RC_PROJECTS = 100;
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.text_name) TextView mTextName;
    @BindView(R.id.text_email) TextView mTextEmail;
    @BindView(R.id.text_phone) TextView mTextPhone;
    @BindView(R.id.text_skype) TextView mTextSkype;
    @BindView(R.id.text_birthday) TextView mTextBirthday;
    @BindView(R.id.text_common) TextView mTextCommon;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.scroll_view) NestedScrollView mScrollView;
    @BindDimen(R.dimen.margin_mini) int mElevation;
    private ReportsAdapter mReportsAdapter;

    public static Intent getLaunchIntent(final Context context, User user) {
        Intent intent = new Intent(context, UserDetailsActivity.class);
        intent.putExtra(KEY_USER, user);
        return intent;
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> vacationReportsData) {
        vacationReportsData.observe(this, list -> {
            getPresenter().setReports(list);
            mReportsAdapter.setData(list);
        });
    }

    @Override
    public void showReportsByYear(final Date firstWorkingDate, final SparseIntArray years) {
        String text = "First Working Day: " + DateUtils.toStringStandardDate(firstWorkingDate) + "\n";
        for (int i = 0; i < years.size(); i++) {
            String day = (years.keyAt(i) + 1) + getDayOfMonthSuffix(years.keyAt(i) + 1);
            int count = years.get(years.keyAt(i));
            text = text + getString(count == 1 ? R.string.day_taken : R.string.days_taken, day, count);
        }
        mTextCommon.setText(text);
    }

    @Override
    public void showUserDetails(final User user) {
        mTextName.setText(String.format("%s / %s", user.getName(), user.getDepartment()));
        mTextEmail.setText(user.getEmail());
        mTextPhone.setText(user.getPhone());
        mTextSkype.setText(user.getSkype());
        mTextBirthday.setText(DateUtils.toStringStandardDate(user.getBirthday()));
    }

    private String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    @OnClick({R.id.text_email, R.id.text_phone})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_email:
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(getPresenter().getUser().getEmail(), getPresenter().getUser().getEmail());
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    showMessage(getString(R.string.text_copied));
                }
                break;
            case R.id.text_phone:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + getPresenter().getUser().getPhone()));
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, @Nullable final Intent data) {
        if (requestCode == RC_PROJECTS && resultCode == RESULT_OK) {
            if (data != null && data.hasExtra(KEY_USER))
                getPresenter().setUser(data.getParcelableExtra(KEY_USER));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user_details, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if (item.getItemId() == R.id.action_user_projects) {
            startActivityForResult(UserProjectsActivity.getLaunchIntent(this, getPresenter().getUser()), RC_PROJECTS);
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
        return R.layout.activity_user_details;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new UserDetailsPresenter(intent.getParcelableExtra(KEY_USER)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_user_details);
        mReportsAdapter = new ReportsAdapter(null);
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerReports.setAdapter(mReportsAdapter);
        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (nestedScrollView, i, i1, i2, i3) -> {
            if (mScrollView.getScrollY() == 0) {
                mToolbar.setElevation(0);
            } else {
                mToolbar.setElevation(mElevation);
            }
        });
        getPresenter().onViewReady();
    }
}

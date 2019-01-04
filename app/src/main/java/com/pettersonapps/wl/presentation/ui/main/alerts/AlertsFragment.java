package com.pettersonapps.wl.presentation.ui.main.alerts;

import android.app.AlertDialog;
import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.AppSettings;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.main.alerts.settings.AlertsSettingsActivity;
import com.pettersonapps.wl.presentation.ui.main.users.adapter.UsersAdapter;
import com.pettersonapps.wl.presentation.ui.main.users.details.UserDetailsActivity;
import com.pettersonapps.wl.presentation.ui.report.ReportClickListener;
import com.pettersonapps.wl.presentation.ui.report.ReportsAdapter;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.OnItemClickListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;

public class AlertsFragment extends BaseFragment<AlertsPresenter> implements AlertsView, ReportClickListener {

    @BindView(R.id.recycler_users) RecyclerView mRecyclerUsers;
    @BindView(R.id.recycler_reports) RecyclerView mRecyclerReports;
    @BindView(R.id.text_reports_placeholder) TextView mTextReportsPlaceholder;
    @BindView(R.id.text_users_placeholder) TextView mTextUsersPlaceholder;
    @BindView(R.id.text_users_header) TextView mTextUsersHeader;
    @BindView(R.id.text_date) TextSwitcher mTextDate;
    @BindDimen(R.dimen.margin_default) int mDefault;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.layout_no_standard) LinearLayout mLayoutNoStandard;
    @BindView(R.id.divider) View mDivider;
    @BindView(R.id.layout_without) LinearLayout mLayoutWithout;
    private UsersAdapter mUsersAdapter;
    private ReportsAdapter mReportsAdapter;
    private Date mPrevDate;

    public static AlertsFragment newInstance() {
        Bundle args = new Bundle();
        AlertsFragment fragment = new AlertsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_alerts, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_date:
                changeDate();
                return true;
            case R.id.action_settings:
                startActivity(AlertsSettingsActivity.getLaunchIntent(getContext()));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void changeEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.text_change_email);
        final EditText input = new EditText(getContext());
        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(
                LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(mDefault, 0, mDefault, 0);
        input.setLayoutParams(params);
        input.setText(getPresenter().getEmail());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            getPresenter().changeEmail(input.getText().toString());
            hideKeyboard();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
            hideKeyboard();
        });
        builder.show();
    }

    private void changeDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getPresenter().getDate());
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
            Date newDate = DateUtils.getDate(calendar.getTime(), year, monthOfYear, dayOfMonth);
            getPresenter().onDateChanged(newDate);
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setMaxDate(DateUtils.getYesterday());
        datePickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
        datePickerDialog.show(getBaseActivity().getFragmentManager(), "date");
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_alerts;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new AlertsPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_alerts);
        hideFab();
        setupAdapters();
        getPresenter().onViewReady();
    }

    private void setupAdapters() {
        mUsersAdapter = new UsersAdapter(new OnItemClickListener() {
            @Override
            public void onItemClicked(final View view, final int position) {
                startActivity(UserDetailsActivity.getLaunchIntent(getContext(), mUsersAdapter.getData().get(position)));
            }

            @Override
            public void onItemLongClicked(final View view, final int position) {
            }
        });
        mRecyclerUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerUsers.setAdapter(mUsersAdapter);
        mReportsAdapter = new ReportsAdapter(this);
        mRecyclerReports.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerReports.setAdapter(mReportsAdapter);
    }

    @Override
    public void showSettings(final MutableLiveData<AppSettings> settings) {
        settings.observe(this, set -> getPresenter().setSettings(set));
    }

    @Override
    public void showReports(final MutableLiveData<List<Report>> allReports) {
        allReports.observe(this, list -> getPresenter().setReports(list));
    }

    @Override
    public void showAllUsers(final MutableLiveData<List<User>> allUsers) {
        allUsers.observe(this, users -> getPresenter().setUsers(users));
    }

    @Override
    public void showUsersWithoutReports(final List<User> allUsersWithoutReports) {
        if (allUsersWithoutReports == null) {
            return;
        }
        mTextUsersPlaceholder.setVisibility(allUsersWithoutReports.isEmpty() ? View.VISIBLE : View.GONE);
        mTextUsersHeader.setText(getString(R.string.text_users_without_reports, allUsersWithoutReports.size()));
        mUsersAdapter.setData(allUsersWithoutReports);
    }

    @Override
    public void showWrongReports(final List<Report> allWrongReports) {
        if (allWrongReports == null) {
            return;
        }
        mTextReportsPlaceholder.setVisibility(allWrongReports.isEmpty() ? View.VISIBLE : View.GONE);
        mReportsAdapter.setData(allWrongReports);
    }

    @Override
    public void showUserDetails(final User user) {
        startActivity(UserDetailsActivity.getLaunchIntent(getContext(), user));
    }

    @Override
    public void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
        mLayoutNoStandard.setVisibility(View.GONE);
        mDivider.setVisibility(View.GONE);
        mLayoutWithout.setVisibility(View.GONE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
        mLayoutNoStandard.setVisibility(View.VISIBLE);
        mDivider.setVisibility(View.VISIBLE);
        mLayoutWithout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showDate(final Date newDate) {
        if (mPrevDate != null) {
            if (DateUtils.dateEquals(newDate, mPrevDate)) return;
            if (newDate.before(mPrevDate)) {
                mTextDate.setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_in_left));
                mTextDate.setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.slide_out_right));
            } else {
                mTextDate.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_right));
                mTextDate.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_left));
            }
        } else {
            mTextDate.setInAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
            mTextDate.setOutAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        }
        mTextDate.setText(DateUtils.toStringDate(newDate));
        mPrevDate = newDate;
    }

    @OnClick(R.id.text_date)
    public void onClick() {
        changeDate();
    }

    @Override
    public void onReportClicked(final Report report) {
        getPresenter().onReportClicked(report.getUserId());
    }
}

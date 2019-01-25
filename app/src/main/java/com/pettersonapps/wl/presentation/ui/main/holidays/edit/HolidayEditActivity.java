package com.pettersonapps.wl.presentation.ui.main.holidays.edit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;
import com.pettersonapps.wl.presentation.view.SquareButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class HolidayEditActivity extends BaseActivity<HolidayEditPresenter> implements HolidayEditView {

    private static final String KEY_HOLIDAY = "holiday";
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.input_title) TextInputEditText mInputTitle;
    @BindView(R.id.input_date) TextInputEditText mInputDate;
    @BindView(R.id.button_save) SquareButton mButtonSave;
    @BindView(R.id.progress) ProgressBar mProgress;

    public static Intent getLaunchIntent(final Context activity) {
        return new Intent(activity, HolidayEditActivity.class);
    }

    public static Intent getLaunchIntent(final Context activity, Holiday holiday) {
        Intent intent = new Intent(activity, HolidayEditActivity.class);
        intent.putExtra(KEY_HOLIDAY, holiday);
        return intent;
    }

    @Override
    protected Toolbar getToolbar() {
        return mToolbar;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_holiday_edit;
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new HolidayEditPresenter(intent.getParcelableExtra(KEY_HOLIDAY)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(getPresenter().getHoliday().getKey() == null ? R.string.title_add : R.string.title_edit);
        getPresenter().onViewReady();
    }

    @OnClick(R.id.button_save)
    public void onClick() {
        if(TextUtils.isEmpty(mInputTitle.getText()))
            return;
        getPresenter().onSave(mInputTitle.getText().toString());
    }

    @Override
    public void showProgress() {
        hideKeyboard();
        mButtonSave.showProgress(true);
        mProgress.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mButtonSave.showProgress(false);
        mProgress.setVisibility(View.GONE);
    }

    @Override
    public void showHoliday(final Holiday holiday) {
        mInputTitle.setText(holiday.getTitle());
        mInputDate.setText(DateUtils.toStringStandardDate(holiday.getDate()));
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_holiday_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu) {
        menu.findItem(R.id.action_delete).setVisible(getPresenter().getHoliday().getKey() != null);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        if(item.getItemId() == R.id.action_delete) {
            AlertDialog.Builder build = new AlertDialog.Builder(getContext());
            build.setMessage(R.string.text_delete);
            build.setPositiveButton(R.string.action_delete, (dialog, which) -> {
                getPresenter().removeHoliday();
                dialog.dismiss();
            });
            build.setNegativeButton(R.string.action_cancel, (dialog, which) -> {
                dialog.dismiss();
            });
            build.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void afterSuccessfullySaving() {
        onBackPressed();
    }

    @OnClick(R.id.input_date)
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(getPresenter().getHoliday().getDate());
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance((view, year, monthOfYear, dayOfMonth) -> {
            Date newDate = DateUtils.getDate(calendar.getTime(), year, monthOfYear, dayOfMonth);
            getPresenter().getHoliday().setDate(newDate);
            mInputDate.setText(DateUtils.toStringStandardDate(newDate));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setFirstDayOfWeek(Calendar.MONDAY);
        datePickerDialog.show(getFragmentManager(), "date");
    }
}

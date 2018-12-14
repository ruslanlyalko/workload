package com.pettersonapps.wl.presentation.ui.main.settings;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.pettersonapps.wl.BuildConfig;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.login.LoginActivity;
import com.pettersonapps.wl.presentation.ui.main.MainActivity;
import com.pettersonapps.wl.presentation.utils.PreferencesHelper;

import butterknife.BindView;

public class SettingsFragment extends BaseFragment<SettingsPresenter> implements SettingsView {

    @BindView(R.id.spinner_notification) Spinner mSpinnerNotification;
    @BindView(R.id.switch_night_mode) Switch mSwitchNightMode;
    @BindView(R.id.switch_old_style_calendar) Switch mSwitchOldStyleCalendar;
    @BindView(R.id.text_version) TextView mTextVersion;
    @BindView(R.id.spinner_default) Spinner mSpinnerDefault;

    public static SettingsFragment newInstance() {
        Bundle args = new Bundle();
        SettingsFragment fragment = new SettingsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_settings;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new SettingsPresenter());
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_settings);
        hideFab();
        //
        String[] remindHours = getResources().getStringArray(R.array.notification_hours);
        SpinnerAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_settings, remindHours);
        mSpinnerNotification.setAdapter(adapter);
        String[] defaultWorkingTime = getResources().getStringArray(R.array.default_time);
        SpinnerAdapter adapterTime = new ArrayAdapter<>(getContext(), R.layout.spinner_item_settings, defaultWorkingTime);
        mSpinnerDefault.setAdapter(adapterTime);
        mSwitchNightMode.setChecked(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES);
        mSpinnerNotification.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                getPresenter().saveUserNotificationSettings(mSpinnerNotification.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
        mSpinnerDefault.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                getPresenter().saveUserDefaultWorkingTime(position == 0 ? 8 : 4);
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
        mSwitchNightMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            PreferencesHelper.getInstance(getContext()).setNightMode(isChecked);
            startActivity(MainActivity.getLaunchIntent(getContext(), true));
            getBaseActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        mSwitchOldStyleCalendar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            getPresenter().saveOldStyleCalendar(isChecked);
        });
        mTextVersion.setText(getString(R.string.version_name, BuildConfig.VERSION_NAME));
        getPresenter().onViewReady();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                ((MainActivity) getBaseActivity()).onShowLogoutMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void showUser(final MutableLiveData<User> data) {
        data.observe(this, getPresenter()::setUser);
    }

    @Override
    public void populateUserSettings(User user) {
        String[] list = getResources().getStringArray(R.array.notification_hours);
        for (int i = 0; i < list.length; i++) {
            if (list[i].startsWith(user.getRemindMeAt())) {
                mSpinnerNotification.setSelection(i);
                break;
            }
        }
        mSpinnerDefault.setSelection(user.getDefaultWorkingTime() == 8 ? 0 : 1);
        mSwitchOldStyleCalendar.setChecked(user.getIsOldStyleCalendar());
    }

    @Override
    public void showLoginScreen() {
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}

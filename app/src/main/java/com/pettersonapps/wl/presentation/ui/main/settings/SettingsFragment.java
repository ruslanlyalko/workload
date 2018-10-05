package com.pettersonapps.wl.presentation.ui.main.settings;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.main.MainActivity;
import com.pettersonapps.wl.presentation.utils.PreferencesHelper;

import butterknife.BindView;

public class SettingsFragment extends BaseFragment<SettingsPresenter> implements SettingsView {

    @BindView(R.id.spinner_notification) Spinner mSpinnerNotification;
    @BindView(R.id.switch_night_mode) Switch mSwitchNightMode;

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
        mSwitchNightMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            else
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            PreferencesHelper.getInstance(getContext()).setNightMode(isChecked);
            startActivity(MainActivity.getLaunchIntent(getContext(), true));
            getBaseActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
        getPresenter().onViewReady();
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_settings, menu);
    }

    @Override
    public void showUser(final MutableLiveData<User> data) {
        data.observe(this, getPresenter()::setUser);
    }

    @Override
    public void populateUserSettings(User user) {
        String[] list = getResources().getStringArray(R.array.notification_hours);
        for (int i = 0; i < list.length; i++) {
            if (list[i].startsWith(user.getNotificationHour())) {
                mSpinnerNotification.setSelection(i);
                break;
            }
        }
        mSwitchNightMode.setVisibility(user.getIsAdmin() ? View.VISIBLE : View.GONE);
    }
}

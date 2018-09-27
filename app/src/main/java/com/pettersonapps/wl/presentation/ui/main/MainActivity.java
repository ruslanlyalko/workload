package com.pettersonapps.wl.presentation.ui.main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.design.bottomappbar.BottomAppBar;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseActivity;
import com.pettersonapps.wl.presentation.ui.main.alerts.AlertsFragment;
import com.pettersonapps.wl.presentation.ui.main.calendar.CalendarFragment;
import com.pettersonapps.wl.presentation.ui.main.dashboard.DashboardFragment;
import com.pettersonapps.wl.presentation.ui.main.holidays.HolidaysFragment;
import com.pettersonapps.wl.presentation.ui.main.my_vacations.MyVacationsFragment;
import com.pettersonapps.wl.presentation.ui.main.profile.ProfileFragment;
import com.pettersonapps.wl.presentation.ui.main.projects.ProjectsFragment;
import com.pettersonapps.wl.presentation.ui.main.settings.SettingsFragment;
import com.pettersonapps.wl.presentation.ui.main.users.UsersFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    private static final int MIN_DISTANCE = 100;
    private static final int TAB_PROFILE = 5;
    @BindView(R.id.bottom_app_bar) BottomAppBar mBottomAppBar;
    @BindView(R.id.fab) FloatingActionButton mFab;
    @BindView(R.id.image_menu) AppCompatImageView mImageMenu;
    @BindView(R.id.bottom_sheet) LinearLayout mLayoutBottomSheet;
    @BindView(R.id.image_logo) ImageView mImageLogo;
    @BindView(R.id.text_title) TextView mTextTitle;
    @BindView(R.id.text_subtitle) TextView mTextSubtitle;
    @BindView(R.id.text_letters) TextView mTextLetters;
    @BindView(R.id.navigation_list) NavigationView mNavigationList;
    @BindView(R.id.touch_outside) View mTouchOutSide;
    @BindView(R.id.layout_profile) RelativeLayout mLayoutProfile;
    private BottomSheetBehavior mSheetBehavior;
    private int mLastSelectedItem = R.id.action_workload;
    private float mOldY;
    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View v, final MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mOldY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    float y2 = event.getY();
                    float deltaY = y2 - mOldY;
//                    if (deltaY > MIN_DISTANCE) {
//                        // top2bottom
//                    } else
                    if (deltaY < (0 - MIN_DISTANCE)) {
                        showMenu(getPresenter().getUser());
                    } else {
//                        v.performClick();
                    }
                    break;
            }
            return false;
        }
    };

    public static Intent getLaunchIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    @ColorInt
    public static int adjustAlpha(@ColorInt int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    @OnClick(R.id.fab)
    public void onClick() {
        getPresenter().onFabClicked();
    }

    @Override
    public void showUser(final MutableLiveData<User> myUserData) {
        myUserData.observe(this, user -> getPresenter().setUser(user));
    }

    @Override
    public void showMenu(final User user) {
        mTextTitle.setText(user.getName());
        mTextSubtitle.setText(user.getEmail());
        mTextLetters.setText(getAbbreviation(user.getName()));
        mNavigationList.getMenu().clear();
        mLayoutProfile.setSelected(mLastSelectedItem == TAB_PROFILE);
        mTextTitle.setSelected(mLastSelectedItem == TAB_PROFILE);
        mNavigationList.inflateMenu(getPresenter().getUser().getIsAdmin() ? R.menu.menu_nav_admin : R.menu.menu_nav);
        mNavigationList.setCheckedItem(mLastSelectedItem);
        mNavigationList.setNavigationItemSelectedListener(menuItem -> {
            mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mLastSelectedItem = menuItem.getItemId();
            switch (menuItem.getItemId()) {
                case R.id.action_workload:
                    getPresenter().onWorkloadClicked();
                    return true;
                case R.id.action_vacations:
                    getPresenter().onVacationClicked();
                    return true;
                case R.id.action_alerts:
                    getPresenter().onAlertsClicked();
                    return true;
                case R.id.action_calendar:
                    getPresenter().onCalendarClicked();
                    return true;
                case R.id.action_holidays:
                    getPresenter().onHolidaysClicked();
                    return true;
                case R.id.action_users:
                    getPresenter().onUsersClicked();
                    return true;
                case R.id.action_projects:
                    getPresenter().onProjectsClicked();
                    return true;
                case R.id.action_settings:
                    getPresenter().onSettingsClicked();
                    return true;
                default:
                    return false;
            }
        });
        mSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    @Override
    public void fabClickedFragment() {
        onFabClickedFragment();
    }

    @Override
    public void toggleFab(final boolean isDashboard) {
        if (isDashboard) {
            if (mBottomAppBar.getFabAlignmentMode() != BottomAppBar.FAB_ALIGNMENT_MODE_CENTER) {
                mImageMenu.setVisibility(View.VISIBLE);
                mFab.hide();
                mBottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_CENTER);
                mFab.setImageResource(R.drawable.ic_add);
                mFab.show();
            }
        } else {
            if (mBottomAppBar.getFabAlignmentMode() != BottomAppBar.FAB_ALIGNMENT_MODE_END) {
//                mImageMenu.setVisibility(View.GONE);
                mFab.hide();
                mBottomAppBar.setFabAlignmentMode(BottomAppBar.FAB_ALIGNMENT_MODE_END);
                mFab.setImageResource(R.drawable.ic_reply);
                mFab.show();
            }
        }
    }

    @Override
    public void showDashboardFragment() {
        mLastSelectedItem = R.id.action_workload;
        replaceFragment(DashboardFragment.newInstance());
    }

    @Override
    public void showAlertFragment() {
        replaceFragment(AlertsFragment.newInstance());
    }

    @Override
    public void showCalendarFragment() {
        replaceFragment(CalendarFragment.newInstance());
    }

    @Override
    public void showHolidaysFragment() {
        replaceFragment(HolidaysFragment.newInstance());
    }

    @Override
    public void showUsersFragment() {
        replaceFragment(UsersFragment.newInstance());
    }

    @Override
    public void showProjectsFragment() {
        replaceFragment(ProjectsFragment.newInstance());
    }

    @Override
    public void showSettingsFragment() {
        replaceFragment(SettingsFragment.newInstance());
    }

    @Override
    public void showVacationFragment(final User user) {
        replaceFragment(MyVacationsFragment.newInstance(user));
    }

    @Override
    public void showProfileFragment(final User user) {
        replaceFragment(ProfileFragment.newInstance(user));
    }

    private String getAbbreviation(final String name) {
        if (TextUtils.isEmpty(name)) return "";
        String[] list = name.split(" ");
        String result = list[0].substring(0, 1);
        if (list.length > 1)
            result = result + list[1].substring(0, 1);
        return result.toUpperCase();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu:
                getPresenter().onHomeClicked();
                return true;
            case R.id.action_vacations:
                getPresenter().onVacationClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected Toolbar getToolbar() {
        return mBottomAppBar;
    }

    @Override
    public void onBackPressed() {
        if (mSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED)
            mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else if (getPresenter().isDashboard())
            super.onBackPressed();
        else
            getPresenter().loadDashboardFragment();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onHomeClicked() {
        getPresenter().onHomeClicked();
    }

    @Override
    protected void initPresenter(final Intent intent) {
        setPresenter(new MainPresenter());
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        mImageMenu.setOnTouchListener(mOnTouchListener);
        mBottomAppBar.setOnTouchListener(mOnTouchListener);
        mBottomAppBar.setNavigationIcon(null);
        mSheetBehavior = BottomSheetBehavior.from(mLayoutBottomSheet);
        mSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull final View view, final int i) {
            }

            @Override
            public void onSlide(@NonNull final View view, final float v) {
                mTouchOutSide.setAlpha(v / 2f);
                getWindow().setStatusBarColor(adjustAlpha(ContextCompat.getColor(getContext(),
                        R.color.colorBackgroundTouchOutside), v / 2f));
                if (v > 0.001f)
                    mTouchOutSide.setVisibility(View.VISIBLE);
                else
                    mTouchOutSide.setVisibility(View.GONE);
            }
        });
        getPresenter().onViewReady();
        if (savedInstanceState == null)
            showDashboardFragment();
    }

    @Override
    public void showFab() {
        mFab.show();
    }

    @Override
    public void hideFab() {
        mFab.hide();
    }

    @OnClick(R.id.image_menu)
    public void onMenuClick() {
        getPresenter().onHomeClicked();
    }

    @OnClick(R.id.layout_profile)
    public void onProfileClick() {
        mLastSelectedItem = TAB_PROFILE;
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        getPresenter().onProfileClicked();
    }

    @OnClick(R.id.touch_outside)
    public void onTouchClick() {
        mSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}

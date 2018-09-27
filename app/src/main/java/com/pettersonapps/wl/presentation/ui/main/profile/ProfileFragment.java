package com.pettersonapps.wl.presentation.ui.main.profile;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.User;
import com.pettersonapps.wl.presentation.base.BaseFragment;
import com.pettersonapps.wl.presentation.ui.login.LoginActivity;
import com.pettersonapps.wl.presentation.ui.main.profile.edit.ProfileEditActivity;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import butterknife.BindView;

public class ProfileFragment extends BaseFragment<ProfilePresenter> implements ProfileView {

    private static final String KEY_USER = "user";
    @BindView(R.id.text_name) TextView mTextName;
    @BindView(R.id.text_email) TextView mTextEmail;
    @BindView(R.id.text_phone) TextView mTextPhone;
    @BindView(R.id.text_skype) TextView mTextSkype;
    @BindView(R.id.text_department) TextView mTextDepartment;
    @BindView(R.id.text_birthday) TextView mTextBirthday;
    @BindView(R.id.text_first) TextView mTextFirst;

    public static ProfileFragment newInstance(User user) {
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        args.putParcelable(KEY_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        inflater.inflate(R.menu.menu_profile, menu);
    }

    @Override
    public void startProfileEditScreen() {
        startActivity(ProfileEditActivity.getLaunchIntent(getContext()));
    }

    @Override
    public void showUser(final MutableLiveData<User> myUserData) {
        myUserData.observe(this, user -> getPresenter().setUser(user));
    }

    @Override
    public void populateUser(final User user) {
        mTextName.setText(user.getName());
        mTextEmail.setText(user.getEmail());
        mTextPhone.setText(user.getPhone());
        mTextSkype.setText(user.getSkype());
        mTextDepartment.setText(user.getDepartment());
        mTextBirthday.setText(DateUtils.toStringStandardDate(user.getBirthday()));
        mTextFirst.setText(DateUtils.toStringStandardDate(user.getFirstWorkingDate()));
    }

    @Override
    public void showLoginScreen() {
        startActivity(LoginActivity.getLaunchIntent(getContext()).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                getPresenter().onEditClicked();
                return true;
            case R.id.action_logout:
                getPresenter().onLogoutClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected int getContentView() {
        return R.layout.fragment_profile;
    }

    @Override
    protected void initPresenter(final Bundle args) {
        setPresenter(new ProfilePresenter(args.getParcelable(KEY_USER)));
    }

    @Override
    protected void onViewReady(final Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_profile);
        getPresenter().onViewReady();
    }
}

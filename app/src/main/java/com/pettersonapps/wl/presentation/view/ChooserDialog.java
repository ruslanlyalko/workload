package com.pettersonapps.wl.presentation.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pettersonapps.wl.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Ruslan Lyalko
 * on 20.09.2018.
 */

public class ChooserDialog extends BottomSheetDialogFragment implements DialogInterface.OnShowListener {

    private static final String KEY_RESOURCE = "res";
    private static final String KEY_TITLE = "title";
    private static final String KEY_SUBTITLE = "subtitle";

    public static ChooserDialog newInstance(String title, String subtitle, @MenuRes int resId, OnItemSelectedListener listener) {
        ChooserDialog dialog = new ChooserDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_RESOURCE, resId);
        bundle.putString(KEY_TITLE, title);
        bundle.putString(KEY_SUBTITLE, subtitle);
        dialog.setArguments(bundle);
        dialog.setListener(listener);
        return dialog;
    }

    @BindView(R.id.nv_dc_list) protected NavigationView nvDcList;
    @BindView(R.id.tv_dc_title) protected TextView tvDcTitle;
    @BindView(R.id.tv_dc_subtitle) protected TextView tvDcSubTitle;

    private Unbinder unbinder;
    private OnItemSelectedListener listener;
    @MenuRes
    private int menuRes;
    private String title;
    private String subtitle;

    @Override
    public int getTheme() {
        return R.style.ChooserTheme;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) parseArguments(getArguments());
    }

    protected void parseArguments(Bundle args) {
        title = args.getString(KEY_TITLE);
        subtitle = args.getString(KEY_SUBTITLE);
        menuRes = args.getInt(KEY_RESOURCE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chooser, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(this);
        return dialog;
    }

    @SuppressLint("CheckResult")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        nvDcList.inflateMenu(menuRes);
        nvDcList.setNavigationItemSelectedListener(this::onItemSelected);
        if(title == null || title.equals(""))
            tvDcTitle.setVisibility(View.GONE);
        else
            tvDcTitle.setText(title);
        if(subtitle == null || subtitle.equals(""))
            tvDcSubTitle.setVisibility(View.GONE);
        else
            tvDcSubTitle.setText(subtitle);
    }

    @Override
    public void onDestroyView() {
        if(unbinder != null) unbinder.unbind();
        super.onDestroyView();
    }

    protected boolean onItemSelected(@NonNull MenuItem item) {
        listener.onNavigationItemSelected(item);
        dismiss();
        return true;
    }

    public void setListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onShow(final DialogInterface dialog) {
        getDialog()
                .getWindow()
                .findViewById(R.id.design_bottom_sheet)
                .setBackgroundResource(android.R.color.transparent);
    }

    public interface OnItemSelectedListener {

        void onNavigationItemSelected(@NonNull MenuItem item);
    }
}

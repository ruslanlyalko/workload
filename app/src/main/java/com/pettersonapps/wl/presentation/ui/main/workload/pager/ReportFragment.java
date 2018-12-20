package com.pettersonapps.wl.presentation.ui.main.workload.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDelegate;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.pettersonapps.wl.R;
import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.presentation.utils.ColorUtils;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ReportFragment extends Fragment {

    private static final String KEY_REPORT = "report";
    private static final String KEY_HOLIDAY = "holiday";
    private static final String KEY_ALLOW_EDIT = "allow_edit";
    private static final String KEY_SHOW_ACTIONS = "show_buttons";
    @BindView(R.id.layout_root) MaterialCardView mCardRoot;
    @BindView(R.id.text_title) TextView mTextTitle;
    @BindView(R.id.text_name) TextView mTextName;
    @BindView(R.id.text_date) TextView mTextDate;
    @BindView(R.id.image_copy) ImageView mImageCopy;
    @BindView(R.id.image_delete) ImageView mImageDelete;
    @BindView(R.id.text_project_1) TextView mTextProject1;
    @BindView(R.id.text_project_2) TextView mTextProject2;
    @BindView(R.id.text_project_3) TextView mTextProject3;
    @BindView(R.id.text_project_4) TextView mTextProject4;
    @BindView(R.id.text_project_5) TextView mTextProject5;
    @BindView(R.id.text_project_6) TextView mTextProject6;
    @BindView(R.id.text_holiday_name) TextView mTextHolidayName;
    @BindView(R.id.card_holiday) MaterialCardView mCardHoliday;

    private Unbinder mUnbinder;

    private Report mReport = null;
    private Holiday mHoliday = null;
    private boolean mAllowEdit;
    private boolean mShowButtons;

    public static ReportFragment newInstance(@Nullable Report report, @Nullable Holiday holiday, final boolean isAllowEditPastReports, final boolean showButtons) {
        Bundle args = new Bundle();
        if (report != null)
            args.putParcelable(KEY_REPORT, report);
        if (holiday != null)
            args.putParcelable(KEY_HOLIDAY, holiday);
        args.putBoolean(KEY_ALLOW_EDIT, isAllowEditPastReports);
        args.putBoolean(KEY_SHOW_ACTIONS, showButtons);
        ReportFragment fragment = new ReportFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mReport = getArguments().getParcelable(KEY_REPORT);
            mHoliday = getArguments().getParcelable(KEY_HOLIDAY);
            mAllowEdit = getArguments().getBoolean(KEY_ALLOW_EDIT, false);
            mShowButtons = getArguments().getBoolean(KEY_SHOW_ACTIONS, false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater,
                             @Nullable final ViewGroup container,
                             @Nullable final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_reports, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        bind(mReport);
        if (mHoliday == null)
            mCardHoliday.setVisibility(View.GONE);
        else {
            mCardHoliday.setVisibility(View.VISIBLE);
            mTextHolidayName.setText(mHoliday.getTitle());
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    void bind(final Report report) {
        if (report == null) {
            mCardRoot.setVisibility(GONE);
            return;
        }
        mCardRoot.setVisibility(VISIBLE);
        mTextTitle.setTextColor(ContextCompat.getColor(getContext(), ColorUtils.getTextColorByStatus(mTextDate.getResources(), report.getStatus())));
        mTextTitle.setText(report.getStatus());
//        boolean showName = (TextUtils.isEmpty(report.getP3()) && TextUtils.isEmpty(report.getP4()));
//        mTextName.setVisibility(showName ? VISIBLE : GONE);
        mTextName.setText(String.format("%s / %s", report.getUserName(), report.getUserDepartment()));
        mTextProject1.setVisibility(TextUtils.isEmpty(report.getP1()) ? GONE : VISIBLE);
        mTextProject2.setVisibility(TextUtils.isEmpty(report.getP2()) ? GONE : VISIBLE);
        mTextProject3.setVisibility(TextUtils.isEmpty(report.getP3()) ? GONE : VISIBLE);
        mTextProject4.setVisibility(TextUtils.isEmpty(report.getP4()) ? GONE : VISIBLE);
        mTextProject5.setVisibility(TextUtils.isEmpty(report.getP5()) ? GONE : VISIBLE);
        mTextProject6.setVisibility(TextUtils.isEmpty(report.getP6()) ? GONE : VISIBLE);
        mTextProject1.setText(getFormattedText(report.getP1(), report.getT1()));
        mTextProject2.setText(getFormattedText(report.getP2(), report.getT2()));
        mTextProject3.setText(getFormattedText(report.getP3(), report.getT3()));
        mTextProject4.setText(getFormattedText(report.getP4(), report.getT4()));
        mTextProject5.setText(getFormattedText(report.getP5(), report.getT5()));
        mTextProject6.setText(getFormattedText(report.getP6(), report.getT6()));
        mTextDate.setText(DateUtils.toStringDate(report.getDate()));
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            mImageCopy.setImageResource(R.drawable.ic_copy_bl);
        } else {
            mImageCopy.setImageResource(R.drawable.ic_copy_wh);
        }
        mTextDate.setVisibility(GONE);
        if (mShowButtons && (report.getDate().before(DateUtils.getStart(new Date())))) {
            mImageCopy.setVisibility(VISIBLE);
        } else {
            mImageCopy.setVisibility(GONE);
        }
        mImageDelete.setVisibility(mShowButtons && (mAllowEdit || report.getDate().after(DateUtils.get1DaysAgo().getTime()))
                ? VISIBLE : GONE);
    }

    @OnClick(R.id.layout_root)
    void onItemClick() {
        if (getParentFragment() instanceof OnReportClickListener)
            ((OnReportClickListener) getParentFragment()).onReportClicked(mReport);
    }

    @OnClick(R.id.image_delete)
    void onClicked() {
        if (getParentFragment() instanceof OnReportClickListener)
            ((OnReportClickListener) getParentFragment()).onReportRemoveClicked(mReport);
    }

    @OnClick(R.id.image_copy)
    void onCopyClicked() {
        if (getParentFragment() instanceof OnReportClickListener)
            ((OnReportClickListener) getParentFragment()).onReportCopyClicked(mReport);
    }

    private Spanned getFormattedText(final String name, final float time) {
        if (TextUtils.isEmpty(name)) return SpannableString.valueOf("");
        String timeStr = String.format(Locale.US, "%.0fh", time);
        float ex = time % 1;
        if (ex != 0) {
            timeStr = String.format(Locale.US, "%.0fh %dm", time - ex, (int) (ex * 60));
        }
        return Html.fromHtml("<b>" + name + "</b> " + timeStr);
    }
}

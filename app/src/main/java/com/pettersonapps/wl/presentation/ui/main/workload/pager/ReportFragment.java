package com.pettersonapps.wl.presentation.ui.main.workload.pager;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
    @BindView(R.id.layout_root) MaterialCardView mCardRoot;
    @BindView(R.id.text_title) TextView mTextTitle;
    @BindView(R.id.text_name) TextView mTextName;
    @BindView(R.id.text_date) TextView mTextDate;
    @BindView(R.id.image_delete) ImageView mImageDelete;
    @BindView(R.id.text_project_1) TextView mTextProject1;
    @BindView(R.id.text_project_2) TextView mTextProject2;
    @BindView(R.id.text_project_3) TextView mTextProject3;
    @BindView(R.id.text_project_4) TextView mTextProject4;
    @BindView(R.id.text_holiday_name) TextView mTextHolidayName;
    @BindView(R.id.card_holiday) MaterialCardView mCardHoliday;

    private Unbinder mUnbinder;

    private Report mReport = null;
    private Holiday mHoliday = null;
    private boolean mAllowEdit;

    public static ReportFragment newInstance(@Nullable Report report, @Nullable Holiday holiday, final boolean isAllowEditPastReports) {
        Bundle args = new Bundle();
        if (report != null)
            args.putParcelable(KEY_REPORT, report);
        if (holiday != null)
            args.putParcelable(KEY_HOLIDAY, holiday);
        args.putBoolean(KEY_ALLOW_EDIT, isAllowEditPastReports);
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
        mTextProject1.setText(getFormattedText(report.getP1(), report.getT1()));
        mTextProject2.setText(getFormattedText(report.getP2(), report.getT2()));
        mTextProject3.setText(getFormattedText(report.getP3(), report.getT3()));
        mTextProject4.setText(getFormattedText(report.getP4(), report.getT4()));
        mTextDate.setText(DateUtils.toStringDate(report.getDate()));
        mImageDelete.setVisibility((mAllowEdit || report.getDate().after(DateUtils.get1DaysAgo().getTime()))
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

    private Spanned getFormattedText(final String name, final int time) {
        if (TextUtils.isEmpty(name)) return SpannableString.valueOf("");
        return Html.fromHtml("<b>" + name + "</b> " + time + "h");
    }
}

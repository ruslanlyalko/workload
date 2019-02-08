package com.pettersonapps.wl.presentation.ui.main.my_projects.details_manager.pager;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.pettersonapps.wl.data.models.Holiday;
import com.pettersonapps.wl.data.models.Report;
import com.pettersonapps.wl.presentation.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class ReportsPagerAdapter extends FragmentStatePagerAdapter {

    private final boolean mShowButtons;
    private List<Report> mReports = new ArrayList<>();
    private List<Holiday> mHolidays = new ArrayList<>();
    private boolean mIsAllowEditPastReports;

    public ReportsPagerAdapter(final FragmentManager fragmentManager, final boolean showButtons) {
        super(fragmentManager);
        mShowButtons = showButtons;
    }

    public void setReports(final List<Report> reports) {
        mReports = reports;
        notifyDataSetChanged();
    }

    public void setHolidays(final List<Holiday> holidays) {
        mHolidays = holidays;
        notifyDataSetChanged();
    }

    public void setAllowEditPastReports(final boolean allowEditPastReports) {
        mIsAllowEditPastReports = allowEditPastReports;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(final int position) {
        return ReportsFragment.newInstance(getReports(position), getHoliday(position), mIsAllowEditPastReports, mShowButtons);
    }

    @NonNull
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        return super.instantiateItem(container, position % getMaxReportsCount());
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        super.destroyItem(container, position % getMaxReportsCount(), object);
    }

    private Holiday getHoliday(final int position) {
        if(position > getMaxReportsCount()) return null;
        if(position < 0) return null;
        Date current = getDateByPos(position);
        for (Holiday holiday : mHolidays) {
            if(DateUtils.dateEquals(current, holiday.getDate()))
                return holiday;
        }
        return null;
    }

    public List<Report> getReports(final int position) {
        if(position > getMaxReportsCount()) return null;
        if(position < 0) return null;
        List<Report> result = new ArrayList<>();
        Date current = getDateByPos(position);
        Date start = DateUtils.getStart(current);
        Date end = DateUtils.getEnd(current);
        for (Report r : mReports) {
            if(r.getDateConverted().after(start)
                    && r.getDateConverted().before(end)) {
                result.add(r);
            }
        }
        Collections.sort(result, (o1, o2) -> o1.getUserDepartment().compareTo(o2.getUserDepartment()));
        return result;
    }

    @Override
    public int getCount() {
        return getMaxReportsCount();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public Date getDateByPos(final int position) {
        Calendar firstDay = Calendar.getInstance();
        firstDay.set(Calendar.YEAR, 2016);
        firstDay.set(Calendar.MONTH, Calendar.JANUARY);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        //add number of days
        firstDay.add(Calendar.DAY_OF_YEAR, position);
        return firstDay.getTime();
    }

    public int getPosByDate(final Date date) {
        Calendar firstDay = Calendar.getInstance();
        firstDay.set(Calendar.YEAR, 2016);
        firstDay.set(Calendar.MONTH, Calendar.JANUARY);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Calendar lastDay = Calendar.getInstance();
        lastDay.setTime(date);
        return DateUtils.daysBetween(lastDay, firstDay);
    }

    private int getMaxReportsCount() {
        Calendar firstDay = Calendar.getInstance();
        firstDay.set(Calendar.YEAR, 2016);
        firstDay.set(Calendar.MONTH, Calendar.JANUARY);
        firstDay.set(Calendar.DAY_OF_MONTH, 1);
        Calendar lastDay = Calendar.getInstance();
        lastDay.add(Calendar.YEAR, 2);
        return DateUtils.daysBetween(lastDay, firstDay);
    }
}

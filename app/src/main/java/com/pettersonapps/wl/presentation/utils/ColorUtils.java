package com.pettersonapps.wl.presentation.utils;

import android.content.res.Resources;

import com.pettersonapps.wl.R;

/**
 * Created by Ruslan Lyalko
 * on 26.09.2018.
 */
public class ColorUtils {

    public static int getTextColorByStatus(Resources r, String status) {
        String[] statuses = r.getStringArray(R.array.status);
        int colorRes;
        if(status.equals(statuses[0])) {
            colorRes = R.color.bg_status_worked;
        } else if(status.equals(statuses[1])) {
            colorRes = R.color.bg_status_worked_home;
        } else if(status.equals(statuses[2])) {
            colorRes = R.color.bg_status_working_off;
        } else if(status.equals(statuses[3])) {
            colorRes = R.color.bg_status_no_project;
        } else if(status.equals(statuses[4])) {
            colorRes = R.color.bg_status_day_off;
        } else if(status.equals(statuses[5]))
            colorRes = R.color.bg_status_vacation;
        else
            colorRes = R.color.bg_status_sick_leave;
        return colorRes;
    }
}

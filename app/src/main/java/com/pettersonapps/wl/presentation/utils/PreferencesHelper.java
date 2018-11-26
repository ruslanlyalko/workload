package com.pettersonapps.wl.presentation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ruslan Lyalko
 * on 05.10.2018.
 */
public class PreferencesHelper {

    private static final String KEY_NIGHT_MODE = "night";
    private static final String KEY_4_HOUR = "hour_4";
    private static PreferencesHelper ourInstance;
    private final SharedPreferences mPrefs;

    private PreferencesHelper(Context context) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static PreferencesHelper getInstance(Context context) {
        if (ourInstance == null)
            ourInstance = new PreferencesHelper(context);
        return ourInstance;
    }

    public boolean getNightMode() {
        return mPrefs.getBoolean(KEY_NIGHT_MODE, false);
    }

    public void setNightMode(boolean isNightModeEnabled) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(KEY_NIGHT_MODE, isNightModeEnabled);
        editor.apply();
        editor.commit();
    }

    public boolean getDefaultTimeIs4() {
        return mPrefs.getBoolean(KEY_4_HOUR, false);
    }

    public void setDefaultTime(boolean is4Hour) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(KEY_4_HOUR, is4Hour);
        editor.apply();
        editor.commit();
    }
}

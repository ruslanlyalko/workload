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
}

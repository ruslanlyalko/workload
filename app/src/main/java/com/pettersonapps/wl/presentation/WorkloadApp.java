package com.pettersonapps.wl.presentation;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.database.FirebaseDatabase;
import com.pettersonapps.wl.R;
import com.pettersonapps.wl.presentation.utils.PreferencesHelper;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static com.pettersonapps.wl.BuildConfig.DEBUG;

/**
 * Created by Ruslan Lyalko
 * on 05.09.2018.
 */
public class WorkloadApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        if (!DEBUG)
            Fabric.with(this, new Crashlytics());
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getString(R.string.google_regular))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
        AppCompatDelegate.setDefaultNightMode(PreferencesHelper.getInstance(this).getNightMode()
                ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
    }
}

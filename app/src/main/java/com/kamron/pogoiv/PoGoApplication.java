package com.kamron.pogoiv;

import android.app.Application;

import com.kamron.pogoiv.updater.FontsOverride;

import timber.log.Timber;

public class PoGoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        CrashlyticsWrapper.init(getApplicationContext());

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashlyticsWrapper.CrashReportingTree());
        }

        // Fonts overriding application wide
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/Lato-Medium.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/Lato-Medium.ttf");
    }
}

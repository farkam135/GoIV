package com.kamron.pogoiv;

import android.app.Application;

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
    }
}

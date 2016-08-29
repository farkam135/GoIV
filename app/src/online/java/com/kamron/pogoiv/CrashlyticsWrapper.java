package com.kamron.pogoiv;

import android.content.Context;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class CrashlyticsWrapper {

    public static void init(Context context) {
        if (BuildConfig.isInternetAvailable && GoIVSettings.getInstance(context).shouldSendCrashReports()) {
            // Set up Crashlytics, disabled for debug builds
            Crashlytics crashlyticsKit = new Crashlytics.Builder()
                    .core(new CrashlyticsCore.Builder()
                            .disabled(BuildConfig.DEBUG).build())
                    .build();

            // Initialize Fabric with the debug-disabled crashlytics.
            Fabric.with(context, crashlyticsKit);
        }
    }

    public static class CrashReportingTree extends Timber.Tree {

        private final GoIVSettings goIVSettings;

        public CrashReportingTree(Context context) {
            goIVSettings = GoIVSettings.getInstance(context);
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {


            if (BuildConfig.isInternetAvailable && goIVSettings.shouldSendCrashReports()) {
                if (t != null) {
                    Crashlytics.logException(t);
                } else if (!TextUtils.isEmpty(message)) {
                    Crashlytics.log(message);
                }
            }

        }
    }
}

package com.kamron.pogoiv.utils;

import android.content.Context;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.GoIVSettings;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

public class CrashlyticsWrapperImpl extends CrashlyticsWrapper {

    CrashlyticsWrapperImpl() {
    }

    @Override
    public void init(Context context) {
        if (BuildConfig.INTERNET_AVAILABLE
                && GoIVSettings.getInstance(context).shouldSendCrashReports()) {
            Crashlytics crashlyticsKit = new Crashlytics.Builder()
                    .core(new CrashlyticsCore.Builder()
                            .disabled(BuildConfig.DEBUG).build())
                    .build();

            // Initialize Fabric with the debug-disabled Crashlytics.
            Fabric.with(context, crashlyticsKit);

            Timber.plant(new CrashReportingTree());
        }
    }

    private static class CrashReportingTree extends Timber.Tree {
        private CrashReportingTree() {
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (t != null) {
                Crashlytics.logException(t);
            }
            if (!TextUtils.isEmpty(message)) {
                Crashlytics.log(message);
            }
        }
    }
}

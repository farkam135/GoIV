package com.kamron.pogoiv.utils;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.GoIVSettings;

import timber.log.Timber;

public class CrashlyticsWrapperImpl extends CrashlyticsWrapper {

    CrashlyticsWrapperImpl() {
    }

    @Override
    public void init(Context context) {
        if (BuildConfig.INTERNET_AVAILABLE && GoIVSettings.getInstance(context).shouldSendCrashReports()) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

            Timber.plant(new CrashReportingTree());
        }
    }

    private static class CrashReportingTree extends Timber.Tree {
        private CrashReportingTree() {
        }

        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            if (t != null) {
                FirebaseCrashlytics.getInstance().recordException(t);
            }
            if (!TextUtils.isEmpty(message)) {
                FirebaseCrashlytics.getInstance().log(message);
            }
        }
    }
}

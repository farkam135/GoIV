package com.kamron.pogoiv;

import android.content.Context;

import timber.log.Timber;

public class CrashlyticsWrapper {

    public static void init(Context context) {
        // there is no crashlytics in offline builds
    }

    public static class CrashReportingTree extends Timber.Tree {

        public CrashReportingTree(Context context) {
        }

        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            // there is no logging of crash reports in offline builds
        }
    }
}

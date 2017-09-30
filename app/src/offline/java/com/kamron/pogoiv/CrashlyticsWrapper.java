package com.kamron.pogoiv;

import android.content.Context;

import com.kamron.pogoiv.utils.CommonLogger;

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
            // there is no online logging of crash reports in offline builds
            CommonLogger.log(priority, tag, message, t);
        }
    }
}

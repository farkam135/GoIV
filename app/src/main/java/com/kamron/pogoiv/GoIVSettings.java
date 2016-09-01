package com.kamron.pogoiv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

public class GoIVSettings {

    public static final String PREFS_GO_IV_SETTINGS = "GoIV_settings";
    public static final String LAUNCH_POKEMON_GO = "launchPokemonGo";
    public static final String SHOW_CONFIRMATION_DIALOG = "showConfirmationDialog";
    public static final String MANUAL_SCREENSHOT_MODE = "manualScreenshotMode";
    public static final String DELETE_SCREENSHOTS = "deleteScreenshots";
    public static final String COPY_TO_CLIPBOARD = "copyToClipboard";
    public static final String SEND_CRASH_REPORTS = "sendCrashReports";
    public static final String AUTO_UPDATE_ENABLED = "autoUpdateEnabled";

    private static GoIVSettings instance;

    private final SharedPreferences prefs;

    public static GoIVSettings getInstance(Context context) {
        if (instance == null) {
            instance = new GoIVSettings(context.getApplicationContext());
        }
        return instance;
    }

    private GoIVSettings(Context context) {
        prefs = context.getSharedPreferences(PREFS_GO_IV_SETTINGS, Context.MODE_PRIVATE);
    }

    public boolean shouldLaunchPokemonGo() {
        return prefs.getBoolean(LAUNCH_POKEMON_GO, true);
    }

    public boolean shouldShouldConfirmationDialogs() {
        return prefs.getBoolean(SHOW_CONFIRMATION_DIALOG, true);
    }

    public boolean isManualScreenshotModeEnabled() {
        //XXX unify with code in SettingsActivity.java
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH) {
            return true;
        } else {
            return prefs.getBoolean(MANUAL_SCREENSHOT_MODE, false);
        }
    }

    public boolean shouldDeleteScreenshots() {
        return prefs.getBoolean(DELETE_SCREENSHOTS, true);
    }

    public boolean shouldCopyToClipboard() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD, false);
    }

    public boolean shouldSendCrashReports() {
        return prefs.getBoolean(SEND_CRASH_REPORTS, true);
    }

    public boolean isAutoUpdateEnabled() {
        return prefs.getBoolean(AUTO_UPDATE_ENABLED, true);
    }
}

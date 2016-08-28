package com.kamron.pogoiv;

import android.content.Context;
import android.content.SharedPreferences;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class GoIVSettings {

    private boolean launchPokemonGo;
    private boolean showConfirmationDialog;
    private boolean manualScreenshotMode;
    private boolean deleteScreenshots;
    private boolean copyToClipboard;
    private boolean sendCrashReports;
    private boolean autoUpdateEnabled;

    public static final String PREFS_GO_IV_SETTINGS = "GoIV_settings";

    public static final String LAUNCH_POKEMON_GO = "launchPokemonGo";
    public static final String SHOW_CONFIRMATION_DIALOG = "showConfirmationDialog";
    public static final String MANUAL_SCREENSHOT_MODE = "manualScreenshotMode";
    public static final String DELETE_SCREENSHOTS = "deleteScreenshots";
    public static final String COPY_TO_CLIPBOARD = "copyToClipboard";
    public static final String SEND_CRASH_REPORTS = "sendCrashReports";
    public static final String AUTO_UPDATE_ENABLED = "autoUpdateEnabled";

    public static GoIVSettings getSettings(Context context) {
        SharedPreferences settingsPreferences = context.getSharedPreferences(PREFS_GO_IV_SETTINGS, Context.MODE_PRIVATE);

        return new GoIVSettings(settingsPreferences.getBoolean(LAUNCH_POKEMON_GO, true),
                settingsPreferences.getBoolean(SHOW_CONFIRMATION_DIALOG, true),
                settingsPreferences.getBoolean(MANUAL_SCREENSHOT_MODE, false),
                settingsPreferences.getBoolean(DELETE_SCREENSHOTS, true),
                settingsPreferences.getBoolean(COPY_TO_CLIPBOARD, false),
                settingsPreferences.getBoolean(SEND_CRASH_REPORTS, true),
                settingsPreferences.getBoolean(AUTO_UPDATE_ENABLED, true));
    }
}

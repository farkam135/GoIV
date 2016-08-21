package com.kamron.pogoiv;

import android.content.Context;
import android.content.SharedPreferences;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class GoIVSettings {

    private boolean launchPokemonGo;
    private boolean showConfirmationDialog;
    private boolean deleteScreenshots;
    private boolean copyToClipboard;
    private boolean sendCrashReports;
    private boolean autoUpdateEnabled;

    public static final String LAUNCH_POKEMON_GO = "launchPokemonGo";
    public static final String SHOW_CONFIRMATION_DIALOG = "showConfirmationDialog";
    public static final String DELETE_SCREENSHOTS = "deleteScreenshots";
    public static final String COPY_TO_CLIPBOARD = "copyToClipboard";
    public static final String SEND_CRASH_REPORTS = "sendCrashReports";
    public static final String AUTO_UPDATE_ENABLED = "autoUpdateEnabled";

    public static void saveSettings(Context context, GoIVSettings newSettings) {
        SharedPreferences settingsPreferences = context.getSharedPreferences("GoIV_settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor settingsEditor = settingsPreferences.edit();

        settingsEditor.putBoolean(LAUNCH_POKEMON_GO, newSettings.getLaunchPokemonGo());
        settingsEditor.putBoolean(SHOW_CONFIRMATION_DIALOG, newSettings.getShowConfirmationDialog());
        settingsEditor.putBoolean(DELETE_SCREENSHOTS, newSettings.getDeleteScreenshots());
        settingsEditor.putBoolean(COPY_TO_CLIPBOARD, newSettings.getCopyToClipboard());
        settingsEditor.putBoolean(SEND_CRASH_REPORTS, newSettings.getSendCrashReports());
        settingsEditor.putBoolean(AUTO_UPDATE_ENABLED, newSettings.getAutoUpdateEnabled());

        settingsEditor.apply();
    }

    public static GoIVSettings getSettings(Context context) {
        SharedPreferences settingsPreferences = context.getSharedPreferences("GoIV_settings", Context.MODE_PRIVATE);

        return new GoIVSettings(settingsPreferences.getBoolean(LAUNCH_POKEMON_GO, true),
                settingsPreferences.getBoolean(SHOW_CONFIRMATION_DIALOG, true),
                settingsPreferences.getBoolean(DELETE_SCREENSHOTS, true),
                settingsPreferences.getBoolean(COPY_TO_CLIPBOARD, true),
                settingsPreferences.getBoolean(SEND_CRASH_REPORTS, true),
                settingsPreferences.getBoolean(AUTO_UPDATE_ENABLED, true));
    }
}

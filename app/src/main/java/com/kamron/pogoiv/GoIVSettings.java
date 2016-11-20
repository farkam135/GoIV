package com.kamron.pogoiv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.kamron.pogoiv.clipboard.ClipboardToken;
import com.kamron.pogoiv.clipboard.tokens.IVPercentageToken;
import com.kamron.pogoiv.clipboard.tokens.IVPercentageTokenMode;
import com.kamron.pogoiv.clipboard.tokens.PokemonNameToken;
import com.kamron.pogoiv.clipboard.tokens.SeparatorToken;
import com.kamron.pogoiv.clipboard.tokens.UnicodeToken;

import java.util.ArrayList;

public class GoIVSettings {

    public static final String PREFS_GO_IV_SETTINGS = "GoIV_settings";
    public static final String LAUNCH_POKEMON_GO = "launchPokemonGo";
    public static final String SHOW_CONFIRMATION_DIALOG = "showConfirmationDialog";
    public static final String MANUAL_SCREENSHOT_MODE = "manualScreenshotMode";
    public static final String DELETE_SCREENSHOTS = "deleteScreenshots";
    public static final String COPY_TO_CLIPBOARD = "copyToClipboard";
    public static final String COPY_TO_CLIPBOARD_SINGLE = "copyToClipboardSingle";
    public static final String SEND_CRASH_REPORTS = "sendCrashReports";
    public static final String AUTO_UPDATE_ENABLED = "autoUpdateEnabled";
    public static final String POKESPAM_ENABLED = "pokeSpamEnabled";
    public static final String TEAM_NAME = "teamName";
    public static final String APPRAISAL_WINDOW_POSITION = "appraisalWindowPosition";
    public static final String GOIV_CLIPBOARDSETTINGS = "GoIV_ClipboardSettings";
    public static final String GOIV_CLIPBOARDSINGLESETTINGS = "GoIV_ClipboardSingleSettings";
    public static final String SHOW_TRANSLATED_POKEMON_NAME = "showTranslatedPokemonName";

    private static GoIVSettings instance;
    private Context context;

    private final SharedPreferences prefs;

    private GoIVSettings(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_GO_IV_SETTINGS, Context.MODE_PRIVATE);
    }

    public static GoIVSettings getInstance(Context context) {
        if (instance == null) {
            instance = new GoIVSettings(context.getApplicationContext());
        }
        return instance;
    }

    public boolean shouldLaunchPokemonGo() {
        return prefs.getBoolean(LAUNCH_POKEMON_GO, true);
    }

    public boolean shouldShouldConfirmationDialogs() {
        return prefs.getBoolean(SHOW_CONFIRMATION_DIALOG, true);
    }

    public boolean isManualScreenshotModeEnabled() {
        //XXX unify with code in SettingsActivity.java
        return Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH
                || prefs.getBoolean(MANUAL_SCREENSHOT_MODE, false);
    }

    public int playerTeam() {
        return prefs.getInt(TEAM_NAME, -1);
    }

    public void setPlayerTeam(int value) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(GoIVSettings.TEAM_NAME, value);
        editor.apply();
    }

    public String getClipboardPreference() {
        //Below code gets the string representation of the "default" clipboard setting

        StringBuilder defaultString = new StringBuilder(); // Name (3 char)+ MIN-MAX + Unicode not filled (MAX IV)
        //pokemon name max 3 characters
        defaultString.append(new PokemonNameToken(false, 3).getStringRepresentation());

        //lowrep
        defaultString.append(new IVPercentageToken(IVPercentageTokenMode.MIN).getStringRepresentation());
        //dashRepresentation
        defaultString.append(new SeparatorToken("-").getStringRepresentation());
        //highrep
        defaultString.append(new IVPercentageToken(IVPercentageTokenMode.MAX).getStringRepresentation());

        //Unicode iv circled numbers not filled in ex ⑦⑦⑦
        defaultString.append(new UnicodeToken(false).getStringRepresentation());

        return prefs.getString(GOIV_CLIPBOARDSETTINGS, defaultString.toString());
    }

    public void setClipboardPreference(ArrayList<ClipboardToken> tokens) {
        SharedPreferences.Editor editor = prefs.edit();
        String saveString = "";
        for (ClipboardToken token : tokens) {
            saveString += token.getStringRepresentation();
        }
        editor.putString(GoIVSettings.GOIV_CLIPBOARDSETTINGS, saveString);
        editor.apply();
    }
    //Clipboard single is the add-on setting if you want different clipboards for 1 or many results
    public void setClipboardSinglePreference(ArrayList<ClipboardToken> tokens) {
        SharedPreferences.Editor editor = prefs.edit();
        String saveString = "";
        for (ClipboardToken token : tokens) {
            saveString += token.getStringRepresentation();
        }
        editor.putString(GoIVSettings.GOIV_CLIPBOARDSINGLESETTINGS, saveString);
        editor.apply();
    }

    public String getClipboardSinglePreference() {
        //Default is the same as non-single preference.
        String s = getClipboardPreference();
        return prefs.getString(GOIV_CLIPBOARDSINGLESETTINGS, s);
    }


    public boolean shouldDeleteScreenshots() {
        return prefs.getBoolean(DELETE_SCREENSHOTS, true);
    }

    public boolean shouldCopyToClipboard() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD, false);
    }

    public boolean shouldCopyToClipboardSingle() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD_SINGLE, false);
    }

    public boolean shouldSendCrashReports() {
        return prefs.getBoolean(SEND_CRASH_REPORTS, true);
    }

    public boolean isAutoUpdateEnabled() {
        return prefs.getBoolean(AUTO_UPDATE_ENABLED, true);
    }

    public boolean isPokeSpamEnabled() {
        return prefs.getBoolean(POKESPAM_ENABLED, true);
    }

    public boolean isShowTranslatedPokemonName() {
        if (context.getResources().getBoolean(R.bool.use_default_pokemonsname_as_ocrstring)) {
            return prefs.getBoolean(SHOW_TRANSLATED_POKEMON_NAME, false);
        }
        return false;
    }
}

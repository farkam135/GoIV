package com.kamron.pogoiv;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import androidx.annotation.NonNull;

import com.google.common.base.Strings;
import com.kamron.pogoiv.clipboardlogic.ClipboardToken;
import com.kamron.pogoiv.clipboardlogic.ClipboardTokenHandler;
import com.kamron.pogoiv.clipboardlogic.tokens.IVPercentageToken;
import com.kamron.pogoiv.clipboardlogic.tokens.IVPercentageTokenMode;
import com.kamron.pogoiv.clipboardlogic.tokens.PokemonNameToken;
import com.kamron.pogoiv.clipboardlogic.tokens.SeparatorToken;
import com.kamron.pogoiv.clipboardlogic.tokens.UnicodeToken;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldResults;
import com.kamron.pogoiv.scanlogic.Data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A singleton class which is used to access and modify the "persistent" settings saved in phone memory.
 */
public class GoIVSettings {

    public static final String PREFS_GO_IV_SETTINGS = "GoIV_settings";
    public static final String PREF_LEVEL = "level";
    public static final String LAUNCH_POKEMON_GO = "launchPokemonGo";
    public static final String SHOW_CONFIRMATION_DIALOG = "showConfirmationDialog";
    public static final String MANUAL_SCREENSHOT_MODE = "manualScreenshotMode";
    public static final String DELETE_SCREENSHOTS = "deleteScreenshots";
    public static final String COPY_TO_CLIPBOARD = "copyToClipboard";
    public static final String FAST_COPY_TO_CLIPBOARD = "fastCopyToClipboard";
    public static final String COPY_TO_CLIPBOARD_SINGLE = "copyToClipboardSingle";
    public static final String COPY_TO_CLIPBOARD_PERFECTIV = "copyToClipboardPerfectIv";
    public static final String SEND_CRASH_REPORTS = "sendCrashReports";
    public static final String AUTO_UPDATE_ENABLED = "autoUpdateEnabled";
    public static final String POKESPAM_ENABLED = "pokeSpamEnabled";
    public static final String TEAM_NAME = "teamName";
    public static final String APPRAISAL_WINDOW_POSITION = "appraisalWindowPosition";
    public static final String MOVESET_WINDOW_POSITION = "movesetWindowPosition";
    public static final String GOIV_CLIPBOARDSETTINGS = "GoIV_ClipboardSettings";
    public static final String GOIV_CLIPBOARDSINGLESETTINGS = "GoIV_ClipboardSingleSettings";
    public static final String GOIV_CLIPBOARDPERFECTIVSETTINGS = "GoIV_ClipboardPerfectIvSettings";
    public static final String SHOW_TRANSLATED_POKEMON_NAME = "showTranslatedPokemonName";
    public static final String HAS_WARNED_USER_NO_SCREENREC = "GOIV_hasWarnedUserNoScreenRec";
    public static final String COPY_TO_CLIPBOARD_SHOW_TOAST = "copyToClipboardShowToast";
    public static final String AUTO_APPRAISAL_SCAN_DELAY = "appraisalScanDelay";
    public static final String AUTO_OPEN_APPRAISE_DIALOGUE = "autoOpenAppraiseDialogue";
    public static final String QUICK_IV_PREVIEW = "quick_iv_preview";
    public static final String QUICK_IV_PREVIEW_CLIPBOARD = "quick_iv_preview_clipboard";
    public static final String MANUAL_SCREEN_CALIBRATION_ACTIVE = "manual_screen_calibration_active";
    public static final String MANUAL_SCREEN_CALIBRATION_VERSION = "manual_screen_calibration_version";
    public static final String DOWNLOADED_MOVESET_INFO = "downloaded_moveset_info_goiv";

    // Increment this value when you want to make all users recalibrate GoIV
    public static int LATEST_SCREEN_CALIBRATION_VERSION = 2;    


    private static GoIVSettings instance;
    private final SharedPreferences prefs;
    private Context context;

    private GoIVSettings(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREFS_GO_IV_SETTINGS, Context.MODE_MULTI_PROCESS);
    }

    public static @NonNull GoIVSettings getInstance(Context context) {
        if (instance == null) {
            instance = new GoIVSettings(context.getApplicationContext());
        }
        return instance;
    }

    public static void reloadPreferences(Context context) {
        instance = new GoIVSettings(context);
    }

    public int getLevel() {
        return prefs.getInt(PREF_LEVEL, Data.MINIMUM_TRAINER_LEVEL);
    }

    public void setLevel(int level) {
        prefs.edit().putInt(PREF_LEVEL, level).apply();
    }

    public boolean hasManualScanCalibration() {
        return prefs.getBoolean(MANUAL_SCREEN_CALIBRATION_ACTIVE, false);
    }

    public boolean hasUpToDateManualScanCalibration() {
        return prefs.getInt(MANUAL_SCREEN_CALIBRATION_VERSION, 0) == LATEST_SCREEN_CALIBRATION_VERSION;
    }

    public String getCalibrationValue(String valueName) {
        return prefs.getString(valueName, "Error- no value saved");
    }

    public void saveScreenCalibrationResults(ScanFieldResults results) {
        results.finalAdjustments();

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(ScanFieldNames.POKEMON_NAME_AREA,
                results.pokemonNameArea.toString());
        editor.putString(ScanFieldNames.POKEMON_TYPE_AREA,
                results.pokemonTypeArea.toString());
        editor.putString(ScanFieldNames.POKEMON_GENDER_AREA,
                results.pokemonGenderArea.toString());
        editor.putString(ScanFieldNames.CANDY_NAME_AREA,
                results.candyNameArea.toString());
        editor.putString(ScanFieldNames.POKEMON_HP_AREA,
                results.pokemonHpArea.toString());
        editor.putString(ScanFieldNames.POKEMON_CP_AREA,
                results.pokemonCpArea.toString());
        editor.putString(ScanFieldNames.POKEMON_CANDY_AMOUNT_AREA,
                results.pokemonCandyAmountArea.toString());
        editor.putString(ScanFieldNames.POKEMON_EVOLUTION_COST_AREA,
                results.pokemonEvolutionCostArea.toString());
        editor.putString(ScanFieldNames.POKEMON_POWER_UP_STARDUST_COST,
                results.pokemonPowerUpStardustCostArea.toString());
        editor.putString(ScanFieldNames.POKEMON_POWER_UP_CANDY_COST,
                results.pokemonPowerUpCandyCostArea.toString());
        editor.putString(ScanFieldNames.ARC_RADIUS,
                String.valueOf(results.arcRadius));
        editor.putString(ScanFieldNames.ARC_INIT_POINT,
                results.arcCenter.toString());
        editor.putString(ScanFieldNames.SCREEN_INFO_CARD_WHITE_PIXEL,
                results.infoScreenCardWhitePixelPoint.toString());
        editor.putString(ScanFieldNames.SCREEN_INFO_CARD_WHITE_HEX,
                String.format("#%06X", (0xFFFFFF & results.infoScreenCardWhitePixelColor)));
        editor.putString(ScanFieldNames.SCREEN_INFO_FAB_GREEN_PIXEL,
                results.infoScreenFabGreenPixelPoint.toString());
        editor.putString(ScanFieldNames.SCREEN_INFO_FAB_GREEN_HEX,
                String.format("#%06X", (0xFFFFFF & results.infoScreenFabGreenPixelColor)));
        editor.putBoolean(GoIVSettings.MANUAL_SCREEN_CALIBRATION_ACTIVE, true);
        editor.putInt(GoIVSettings.MANUAL_SCREEN_CALIBRATION_VERSION, LATEST_SCREEN_CALIBRATION_VERSION);
        editor.apply();
    }

    public boolean shouldLaunchPokemonGo() {
        return prefs.getBoolean(LAUNCH_POKEMON_GO, true);
    }

    public boolean shouldShowConfirmationDialogs() {
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
        String prefValue = prefs.getString(GOIV_CLIPBOARDSETTINGS, "");
        if (!Strings.isNullOrEmpty(prefValue)) {
            return prefValue;
        }

        //Below code gets the string representation of the "default" clipboard setting
        ArrayList<ClipboardToken> defaultTokens = new ArrayList<>();
        // Name (3 char) + MIN-MAX + Unicode not filled (MAX IV)
        defaultTokens.add(new PokemonNameToken(false, 3));
        defaultTokens.add(new IVPercentageToken(IVPercentageTokenMode.MIN));
        defaultTokens.add(new SeparatorToken("-"));
        defaultTokens.add(new IVPercentageToken(IVPercentageTokenMode.MAX));
        defaultTokens.add(new UnicodeToken(false));

        return ClipboardTokenHandler.tokenListToRepresentation(defaultTokens);
    }

    public String getClipboardSinglePreference() {
        String prefValue = prefs.getString(GOIV_CLIPBOARDSINGLESETTINGS, "");
        if (!Strings.isNullOrEmpty(prefValue)) {
            return prefValue;
        }

        //Below code gets the string representation of the "default" single clipboard setting
        ArrayList<ClipboardToken> defaultTokens = new ArrayList<>();
        // Name (5 char) + AVG + Unicode not filled (MAX IV)
        defaultTokens.add(new PokemonNameToken(false, 5));
        defaultTokens.add(new IVPercentageToken(IVPercentageTokenMode.AVG));
        defaultTokens.add(new UnicodeToken(false));

        return ClipboardTokenHandler.tokenListToRepresentation(defaultTokens);
    }

    public String getClipboardPerfectIvPreference() {
        String prefValue = prefs.getString(GOIV_CLIPBOARDPERFECTIVSETTINGS, "");
        if (!Strings.isNullOrEmpty(prefValue)) {
            return prefValue;
        }

        //Below code gets the string representation of the "default" perfect IV clipboard setting
        ArrayList<ClipboardToken> defaultTokens = new ArrayList<>();
        // Name (7 char) + average IV (should be 100%)
        defaultTokens.add(new PokemonNameToken(false, 5));
        defaultTokens.add(new IVPercentageToken(IVPercentageTokenMode.AVG));

        return ClipboardTokenHandler.tokenListToRepresentation(defaultTokens);
    }

    public void setClipboardPreference(String tokenListRepresentation) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GOIV_CLIPBOARDSETTINGS, tokenListRepresentation);
        editor.apply();
    }


    public String getSavedMovesetInfo() {
        return prefs.getString(DOWNLOADED_MOVESET_INFO, "");

    }

    public void setSavedMovesetInfo(String info) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(info, DOWNLOADED_MOVESET_INFO);
        editor.apply();
    }

    public void setClipboardSinglePreference(String tokenListRepresentation) {
        //Clipboard single is the add-on setting if you want different clipboards for 1 or many results
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GOIV_CLIPBOARDSINGLESETTINGS, tokenListRepresentation);
        editor.apply();
    }

    public void setClipboardPerfectIvPreference(String tokenListRepresentation) {
        //Clipboard single is the add-on setting if you want different clipboards for 1 or many results
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(GOIV_CLIPBOARDPERFECTIVSETTINGS, tokenListRepresentation);
        editor.apply();
    }

    public boolean hasShownNoScreenRecWarning() {
        return prefs.getBoolean(HAS_WARNED_USER_NO_SCREENREC, false);
    }

    public void setHasShownScreenRecWarning() {
        prefs.edit().putBoolean(HAS_WARNED_USER_NO_SCREENREC, true).apply();
    }

    public boolean shouldDeleteScreenshots() {
        return prefs.getBoolean(DELETE_SCREENSHOTS, true);
    }

    public boolean shouldCopyToClipboard() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD, true);
    }
    public boolean shouldFastCopyToClipboard() {
        return prefs.getBoolean(FAST_COPY_TO_CLIPBOARD, false);
    }

    public boolean shouldCopyToClipboardSingle() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD_SINGLE, false);
    }

    public boolean shouldCopyToClipboardPerfectIV() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD_PERFECTIV, false);
    }

    public boolean shouldSendCrashReports() {
        return prefs.getBoolean(SEND_CRASH_REPORTS, true);
    }

    public boolean isAutoUpdateEnabled() {
        return prefs.getBoolean(AUTO_UPDATE_ENABLED, true);
    }

    public boolean isPokeSpamEnabled() {
        //return prefs.getBoolean(POKESPAM_ENABLED, false);
        return false; // Disabling PokeSpam because of Beyond Update
    }

    public boolean shouldAutoOpenExpandedAppraise() {
        return prefs.getBoolean(AUTO_OPEN_APPRAISE_DIALOGUE, false);
    }

    public boolean shouldReplaceQuickIvPreviewWithClipboard() {
        return prefs.getBoolean(QUICK_IV_PREVIEW_CLIPBOARD, false);
    }


    public boolean isShowTranslatedPokemonName() {
        if (context.getResources().getBoolean(R.bool.use_default_pokemonsname_as_ocrstring)) {
            return prefs.getBoolean(SHOW_TRANSLATED_POKEMON_NAME, false);
        }
        return false;
    }

    public boolean shouldCopyToClipboardShowToast() {
        return prefs.getBoolean(COPY_TO_CLIPBOARD_SHOW_TOAST, true);
    }


    public boolean shouldShowQuickIVPreview() {
        return prefs.getBoolean(QUICK_IV_PREVIEW, true);
    }

    public int getAutoAppraisalScanDelay() {
        return prefs.getInt(AUTO_APPRAISAL_SCAN_DELAY, 400);
    }

    @SuppressWarnings("unchecked")
    public Map<String, String> loadAppraisalCache() {
        Map<String, String> appraisalCache = new HashMap<>();

        File fileName = new File(context.getCacheDir(), "appraisalCache.ser");

        FileInputStream fis = null;
        ObjectInputStream in = null;
        try {
            fis = new FileInputStream(fileName);
            in = new ObjectInputStream(fis);
            appraisalCache = (Map<String, String>) in.readObject();
        } catch (Exception ignored) {
            //Fall-through
        } finally {

            try {
                if (fis != null) {
                    fis.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ignored) {
                //Fall-through
            }
        }

        return appraisalCache;
    }

    public void saveAppraisalCache(Map<String, String> appraisalCache) {
        File fileName = new File(context.getCacheDir(), "appraisalCache.ser");

        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(fileName);
            out = new ObjectOutputStream(fos);
            out.writeObject(appraisalCache);
        } catch (Exception ignored) {
            //Fall-through
        } finally {
            try {
                if (fos != null) {
                    fos.flush();
                    fos.close();
                }
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (Exception ignored) {
                //Fall-through
            }
        }
    }
}

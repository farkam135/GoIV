package com.kamron.pogoiv;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.base.Optional;
import com.kamron.pogoiv.clipboardlogic.ClipboardTokenHandler;
import com.kamron.pogoiv.pokeflycomponents.AutoAppraisal;
import com.kamron.pogoiv.pokeflycomponents.GoIVNotificationManager;
import com.kamron.pogoiv.pokeflycomponents.IVPopupButton;
import com.kamron.pogoiv.pokeflycomponents.IVPreviewPrinter;
import com.kamron.pogoiv.pokeflycomponents.ScreenWatcher;
import com.kamron.pogoiv.pokeflycomponents.fractions.AppraisalFraction;
import com.kamron.pogoiv.pokeflycomponents.fractions.IVCombinationsFraction;
import com.kamron.pogoiv.pokeflycomponents.fractions.IVResultFraction;
import com.kamron.pogoiv.pokeflycomponents.fractions.InputFraction;
import com.kamron.pogoiv.pokeflycomponents.fractions.MovesetFraction;
import com.kamron.pogoiv.pokeflycomponents.fractions.PowerUpFraction;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.OcrHelper;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanPoint;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.PokemonNameCorrector;
import com.kamron.pogoiv.scanlogic.ScanResult;
import com.kamron.pogoiv.utils.CopyUtils;
import com.kamron.pogoiv.utils.LevelRange;
import com.kamron.pogoiv.utils.fractions.FractionManager;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.kamron.pogoiv.GoIVSettings.APPRAISAL_WINDOW_POSITION;
import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.SINGLE_RESULT;

/**
 * Currently, the central service in Pokemon Go, dealing with everything except
 * the initial activity.
 * Created by Kamron on 7/25/2016.
 */

public class Pokefly extends Service {

    public static final String ACTION_UPDATE_UI = "com.kamron.pogoiv.ACTION_UPDATE_UI";
    private static final String ACTION_SEND_INFO = "com.kamron.pogoiv.ACTION_SEND_INFO";
    private static final String ACTION_START = "com.kamron.pogoiv.ACTION_START";
    private static final String ACTION_STOP = "com.kamron.pogoiv.ACTION_STOP";

    private static final String KEY_STATUS_BAR_HEIGHT = "key_status_bar_height";
    private static final String KEY_TRAINER_LEVEL = "key_trainer_level";

    private static final String KEY_SEND_INFO_NAME = "key_send_info_name";
    private static final String KEY_SEND_INFO_TYPE = "key_send_info_type";
    private static final String KEY_SEND_INFO_CANDY = "key_send_info_candy";
    private static final String KEY_SEND_INFO_GENDER = "key_send_info_gender";
    private static final String KEY_SEND_INFO_HP = "key_send_info_hp";
    private static final String KEY_SEND_INFO_CP = "key_send_info_cp";
    private static final String KEY_SEND_INFO_LEVEL_LOWER = "key_send_info_level_low";
    private static final String KEY_SEND_INFO_LEVEL_HIGHER = "key_send_info_level_high";
    private static final String KEY_SEND_SCREENSHOT_FILE = "key_send_screenshot_file";
    private static final String KEY_SEND_INFO_CANDY_AMOUNT = "key_send_info_candy_amount";
    private static final String KEY_SEND_UPGRADE_CANDY_COST = "key_send_upgrade_candy_cost";
    private static final String KEY_SEND_UNIQUE_ID = "key_send_unique_id";
    private static final String KEY_SEND_POWERUP_STARTDUST_COST = "key_send_powerup_stardust";
    private static final String KEY_SEND_POWERUP_CANDYCOST = "key_send_powerup_candycost";

    private static final String ACTION_PROCESS_BITMAP = "com.kamron.pogoiv.PROCESS_BITMAP";
    private static final String KEY_BITMAP = "bitmap";
    private static final String KEY_SCREENSHOT_FILE = "ss-file";

    private static final String PREF_USER_CORRECTIONS = "com.kamron.pogoiv.USER_CORRECTIONS";


    private static boolean running = false;

    private int trainerLevel;

    private boolean receivedInfo = false;

    private FractionManager fractionManager;
    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    private ClipboardManager clipboard;
    private SharedPreferences sharedPref;
    private ScreenGrabber screen;
    private ScreenShotHelper screenShotHelper;
    private OcrHelper ocr;


    private boolean infoShownSent = false;
    private boolean infoShownReceived = false;

    //Pokefly components
    private ScreenWatcher screenWatcher;
    private IVPopupButton ivButton;
    private ClipboardTokenHandler clipboardTokenHandler;
    private IVPreviewPrinter ivPreviewPrinter;

    private ImageView arcPointer;

    private PokeInfoCalculator pokeInfoCalculator;

    private AutoAppraisal autoAppraisal;

    @BindView(R.id.fractionContainer)
    FrameLayout fractionContainer;


    public Optional<Pokemon> pokemon = Optional.absent();
    private String pokemonName;
    private String pokemonType;
    private String candyName;
    public Pokemon.Gender pokemonGender = Pokemon.Gender.N;
    public Optional<Integer> pokemonCandy = Optional.absent();
    public Optional<Integer> pokemonCP = Optional.absent();
    public Optional<Integer> pokemonHP = Optional.absent();
    private Optional<Integer> candyUpgradeCost = Optional.absent();
    public String pokemonUniqueID = "";
    public LevelRange estimatedPokemonLevelRange = new LevelRange(1.0);
    private @NonNull Optional<String> screenShotPath = Optional.absent();
    private IVScanResult lastIvScanResult;
    public boolean startedInManualScreenshotMode = false;


    @SuppressWarnings("deprecation")
    private final WindowManager.LayoutParams arcParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
    private int statusBarHeight = 0;

    @SuppressWarnings("deprecation")
    private final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            PixelFormat.TRANSPARENT);


    public static boolean isRunning() {
        return running;
    }

    public static Intent createStopIntent(@NonNull Context context) {
        Intent intent = new Intent(context, Pokefly.class);
        intent.setAction(ACTION_STOP);
        return intent;
    }

    public static Intent createStartIntent(@NonNull Context context, int statusBarHeight, int trainerLevel) {
        Intent intent = new Intent(context, Pokefly.class);
        intent.setAction(ACTION_START);
        intent.putExtra(KEY_STATUS_BAR_HEIGHT, statusBarHeight);
        intent.putExtra(KEY_TRAINER_LEVEL, trainerLevel);
        return intent;
    }

    public static Intent createNoInfoIntent() {
        return new Intent(ACTION_SEND_INFO);
    }

    public static void populateInfoIntent(Intent intent, ScanResult scanResult, @NonNull Optional<String> filePath) {
        intent.putExtra(KEY_SEND_INFO_NAME, scanResult.getPokemonName());
        intent.putExtra(KEY_SEND_INFO_TYPE, scanResult.getPokemonType());
        intent.putExtra(KEY_SEND_INFO_CANDY, scanResult.getCandyName());
        intent.putExtra(KEY_SEND_INFO_GENDER, scanResult.getPokemonGender());
        intent.putExtra(KEY_SEND_INFO_HP, scanResult.getPokemonHP());
        intent.putExtra(KEY_SEND_INFO_CP, scanResult.getPokemonCP());
        intent.putExtra(KEY_SEND_INFO_LEVEL_LOWER, scanResult.getEstimatedPokemonLevel().min);
        intent.putExtra(KEY_SEND_INFO_LEVEL_HIGHER, scanResult.getEstimatedPokemonLevel().max);
        intent.putExtra(KEY_SEND_SCREENSHOT_FILE, filePath);
        intent.putExtra(KEY_SEND_INFO_CANDY_AMOUNT, scanResult.getPokemonCandyAmount());
        intent.putExtra(KEY_SEND_UPGRADE_CANDY_COST, scanResult.getEvolutionCandyCost());
        intent.putExtra(KEY_SEND_UNIQUE_ID, scanResult.getPokemonUniqueID());
        intent.putExtra(KEY_SEND_POWERUP_CANDYCOST, scanResult.getPokemonPowerUpCandyCost());
        intent.putExtra(KEY_SEND_POWERUP_STARTDUST_COST, scanResult.getPokemonPowerUpStardustCost());
    }

    public static Intent createProcessBitmapIntent(Bitmap bitmap, String file) {
        Intent intent = new Intent(ACTION_PROCESS_BITMAP);
        intent.putExtra(KEY_BITMAP, bitmap);
        intent.putExtra(KEY_SCREENSHOT_FILE, file);
        return intent;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    public IVPopupButton getIvButton() {
        return ivButton;
    }

    public ClipboardTokenHandler getClipboardTokenHandler() {
        return clipboardTokenHandler;
    }

    public OcrHelper getOcr() {
        return ocr;
    }

    public int getTrainerLevel() {
        return trainerLevel;
    }

    public IVPreviewPrinter getIvPreviewPrinter() {
        return ivPreviewPrinter;
    }

    public boolean getInfoShownSent() {
        return infoShownSent;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        running = true;

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_UPDATE_UI));

        pokeInfoCalculator = PokeInfoCalculator.getInstance(GoIVSettings.getInstance(this), getResources());
        displayMetrics = getResources().getDisplayMetrics();
        initOcr();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        sharedPref = getSharedPreferences(PREF_USER_CORRECTIONS, Context.MODE_PRIVATE);

        LocalBroadcastManager.getInstance(this).registerReceiver(displayInfo, new IntentFilter(ACTION_SEND_INFO));
        LocalBroadcastManager.getInstance(this).registerReceiver(processBitmap,
                new IntentFilter(ACTION_PROCESS_BITMAP));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            //We should not reach here with no intent, this wil error later due to view being null so we throw error now
            //https://github.com/farkam135/GoIV/issues/477
            throw new java.lang.IllegalArgumentException("No intent found.");
        }

        running = true;
        GoIVNotificationManager goIVNotificationManager = new GoIVNotificationManager(this);
        ivPreviewPrinter = new IVPreviewPrinter(this);
        clipboardTokenHandler = new ClipboardTokenHandler(this);

        if (ACTION_STOP.equals(intent.getAction())) {
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                stopForeground(STOP_FOREGROUND_DETACH);
            }
            stopSelf();
            goIVNotificationManager.showPausedNotification();

        } else if (ACTION_START.equals(intent.getAction())) {
            GoIVSettings.reloadPreferences(this);
            statusBarHeight = intent.getIntExtra(KEY_STATUS_BAR_HEIGHT, 0);
            trainerLevel = intent.getIntExtra(KEY_TRAINER_LEVEL, Data.MINIMUM_TRAINER_LEVEL);

            setupDisplaySizeInfo();

            createFlyingComponents();

            startedInManualScreenshotMode = GoIVSettings.getInstance(this).isManualScreenshotModeEnabled();
            /* Assumes MainActivity initialized ScreenGrabber before starting this service. */
            if (!startedInManualScreenshotMode) {
                screen = ScreenGrabber.getInstance();
                autoAppraisal = new AutoAppraisal(screen, this);
                screenWatcher = new ScreenWatcher(this, fractionManager, autoAppraisal);
                screenWatcher.watchScreen();

            } else {
                screenShotHelper = ScreenShotHelper.start(Pokefly.this);
            }
            goIVNotificationManager.showRunningNotification();
        }
        //We have intent data, it's possible this service will be killed and we would want to recreate it
        //https://github.com/farkam135/GoIV/issues/477
        return START_REDELIVER_INTENT;
    }

    private void setupDisplaySizeInfo() {
        ScanPoint arcInit = new ScanPoint((int) (displayMetrics.widthPixels * 0.5),
                (int) Math.floor(displayMetrics.heightPixels * 0.35664));
        if (displayMetrics.heightPixels == 2392 || displayMetrics.heightPixels == 800) {
            arcInit.yCoord--;
        } else if (displayMetrics.heightPixels == 1920) {
            arcInit.yCoord++;
        }

        int arcRadius = (int) Math.round(displayMetrics.heightPixels * 0.2285);
        if (displayMetrics.heightPixels == 1776 || displayMetrics.heightPixels == 960
                || displayMetrics.heightPixels == 800) {
            arcRadius++;
        }

        Data.setupArcPoints(arcInit, arcRadius, trainerLevel);
    }

    /**
     * Creates the infolayout, ivbutton, arcpointer and arc adjuster.
     */
    private void createFlyingComponents() {
        createInfoLayout();
        ivButton = new IVPopupButton(this);
        createArcPointer();

        fractionManager = new FractionManager(this, R.style.AppTheme_Dialog, fractionContainer);
    }


    private boolean infoLayoutArcPointerVisible = false;

    private void showInfoLayoutArcPointer() {
        if (!infoLayoutArcPointerVisible && arcPointer != null && fractionContainer != null) {
            infoLayoutArcPointerVisible = true;
            windowManager.addView(arcPointer, arcParams);
            windowManager.addView(fractionContainer, layoutParams);
        }
    }

    private void hideInfoLayoutArcPointer() {
        if (infoLayoutArcPointerVisible) {
            windowManager.removeView(arcPointer);
            windowManager.removeView(fractionContainer);
            infoLayoutArcPointerVisible = false;
            fractionManager.remove();
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(displayInfo);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(processBitmap);

        fractionManager.remove();

        if (!startedInManualScreenshotMode) {
            screenWatcher.unwatchScreen();
            if (screen != null) {
                screen.exit();
                screen = null;
            }
        } else {
            screenShotHelper.stop();
            screenShotHelper = null;
        }
        ivButton.setShown(false, infoShownSent);
        hideInfoLayoutArcPointer();

        ocr.exit();
        //Now ocr contains an invalid instance hence let's clear it.
        ocr = null;

        running = false;
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_UPDATE_UI));

        super.onDestroy();
    }

    /**
     * Creates the arc pointer view and sets all the variables required to accurately overlay
     * Pokemon Go's arc pointer.
     */
    private void createArcPointer() {
        Drawable dot = ContextCompat.getDrawable(this, R.drawable.level_arc_dot);
        arcParams.gravity = Gravity.TOP | Gravity.START;
        arcParams.width = dot.getIntrinsicWidth();
        arcParams.height = dot.getIntrinsicHeight();
        arcPointer = new ImageView(this);
        arcPointer.setImageDrawable(dot);
    }


    /**
     * setArcPointer
     * Sets the arc pointer to the specified degree.
     *
     * @param pokeLevel The pokemon level to set the arc pointer to.
     */
    public void setArcPointer(double pokeLevel) {

        int index = Data.maxPokeLevelToIndex(pokeLevel);

        //If the pokemon is overleveled (Raid catch or weather modifier the arc indicator will be stuck at max)
        if (index >= Data.arcX.length) {
            index = Data.arcX.length - 1;
        }
        arcParams.x = Data.arcX[index] - arcParams.width / 2;
        arcParams.y = Data.arcY[index] - arcParams.height / 2 - statusBarHeight;
        //That is, (int) (arcCenter + (radius * Math.cos(angleInRadians))) and
        //(int) (arcInitialY + (radius * Math.sin(angleInRadians))).
        windowManager.updateViewLayout(arcPointer, arcParams);
    }


    /**
     * Sets the internal state that tells the broadcastrecievers to behave when the user has pressed the iv button.
     */
    public void setIVButtonClickedStates() {
        receivedInfo = false;
        infoShownSent = true;
        infoShownReceived = false;
    }

    /**
     * creates the info layout which contains all the scanned data views and allows for correction.
     */
    private void createInfoLayout() {
        View v = LayoutInflater.from(this).inflate(R.layout.dialog_info_window, null);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        ButterKnife.bind(this, v);
    }

    /**
     * Set the current Info Window location.
     *
     * @param newParams New Info Window layout params for appraisal mode.
     */
    public void setWindowPosition(WindowManager.LayoutParams newParams) {
        windowManager.updateViewLayout(fractionContainer, newParams);
    }

    /**
     * Saves the current Info Window location to shared preferences.
     *
     * @param appraisalWindowPosition Current Info Window Y offset for appraisal mode.
     */
    public void saveWindowPosition(int appraisalWindowPosition) {
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putInt(APPRAISAL_WINDOW_POSITION, appraisalWindowPosition);
        edit.apply();
    }


    private static final int MAX_DRAWABLE_LEVEL = 10000;

    /**
     * Moves the overlay up or down.
     *
     * @param moveUp true if move up, false if move down
     */
    private void moveOverlay(Boolean moveUp) {
        if (moveUp && layoutParams.gravity != Gravity.TOP) {
            layoutParams.gravity = Gravity.TOP;
            layoutParams.y = sharedPref.getInt(APPRAISAL_WINDOW_POSITION, 0);
            windowManager.updateViewLayout(fractionContainer, layoutParams);
        } else if (!moveUp && layoutParams.gravity != Gravity.BOTTOM) {
            layoutParams.gravity = Gravity.BOTTOM;
            layoutParams.y = 0;
            windowManager.updateViewLayout(fractionContainer, layoutParams);
        }
    }

    /**
     * Closes the android keyboard... But this method only works if focus is on a direct child of infolayout.
     * <p>
     * Why the fuck does android not have a good standard method for this.
     */
    private void closeKeyboard() {

        //Get a list of all views inside the infoLayout
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < fractionContainer.getChildCount(); i++) {
            views.add(fractionContainer.getChildAt(i));
        }

        //Tell each view inside infoLayout to close the keyboard if they currently have focus.
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        for (View view : views) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void computeIv() {
        if (!pokemon.isPresent() || !pokemonHP.isPresent() || !pokemonCP.isPresent()) {
            Toast.makeText(this, R.string.missing_inputs, Toast.LENGTH_SHORT).show();
            return;
        }

        deleteScreenShotIfRequired();

        lastIvScanResult = pokeInfoCalculator.getIVPossibilities(pokemon.get(), estimatedPokemonLevelRange,
                pokemonHP.get(), pokemonCP.get(), pokemonGender);

        lastIvScanResult.refineWithAvailableInfoFrom(autoAppraisal);

        // Don't run clipboard logic if scan failed - some tokens might crash the program.
        if (lastIvScanResult.iVCombinations.size() > 0) {
            addClipboardInfoIfSettingOn(lastIvScanResult);
        }

        closeKeyboard();
        navigateToIVResultFraction();
    }

    /**
     * Checks if the app is in battery saver mode, and if the user hasnt set the setting to avoid deleting
     * screenshot, and then deletes the screenshot.
     */
    public void deleteScreenShotIfRequired() {
        if (startedInManualScreenshotMode && screenShotPath.isPresent()) {
            if (GoIVSettings.getInstance(this).shouldDeleteScreenshots()) {
                screenShotHelper.deleteScreenShot(screenShotPath.get());
            }
        }
    }

    /**
     * Adds the iv range of the pokemon to the clipboard if the clipboard setting is on.
     */
    public void addClipboardInfoIfSettingOn(IVScanResult ivScanResult) {
        GoIVSettings settings = GoIVSettings.getInstance(this);

        if (settings.shouldCopyToClipboard()) {
            String clipResult = clipboardTokenHandler.getClipboardText(ivScanResult, pokeInfoCalculator);

            if (settings.shouldCopyToClipboardShowToast()) {
                Toast toast = Toast.makeText(this, String.format(getString(R.string.clipboard_copy_toast), clipResult),
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }

            ClipData clip = ClipData.newPlainText(clipResult, clipResult);
            clipboard.setPrimaryClip(clip);
        }
    }

    /**
     * Adds the iv range of the pokemon to the clipboard if the clipboard setting is on.
     */
    public void addSpecificClipboard(IVScanResult ivScanResult, IVCombination ivCombination) {


        String clipResult;
        IVScanResult singleIVScanResult = new IVScanResult(ivScanResult.pokemon, ivScanResult.estimatedPokemonLevel,
                ivScanResult.scannedCP, ivScanResult.scannedGender);
        singleIVScanResult.addIVCombination(ivCombination.att, ivCombination.def, ivCombination.sta);
        clipResult = clipboardTokenHandler.getResults(singleIVScanResult, pokeInfoCalculator, SINGLE_RESULT);


        Toast toast = Toast.makeText(this, String.format(getString(R.string.clipboard_copy_toast), clipResult),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        ClipData clip = ClipData.newPlainText(clipResult, clipResult);
        clipboard.setPrimaryClip(clip);

    }

    /**
     * resets the info dialogue to its default state.
     */
    public void closeInfoDialog() {
        hideInfoLayoutArcPointer();
        resetPokeflyStateMachine();
        if (!startedInManualScreenshotMode) {
            autoAppraisal.reset();
            ivButton.setShown(true, infoShownSent);
        }
    }

    /**
     * Reset service state so that a new pokemon info can be requested.
     */
    private void resetPokeflyStateMachine() {
        receivedInfo = false;
        infoShownSent = false;
    }

    private void initOcr() {
        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir == null) {
            externalFilesDir = getFilesDir();
        }
        String extDir = externalFilesDir.toString();
        if (!new File(extDir + "/tessdata/eng.traineddata").exists()) {
            CopyUtils.copyAssetFolder(getAssets(), "tessdata", extDir + "/tessdata");
        }

        ocr = OcrHelper.init(extDir, pokeInfoCalculator, GoIVSettings.getInstance(this));
    }


    /**
     * scanPokemon
     * Performs OCR on an image of a pokemon and sends the pulled info to PokeFly to display.
     *
     * @param pokemonImage   The image of the pokemon
     * @param screenShotPath The screenshot path if it is a file, used to delete once checked
     */
    private void scanPokemon(@NonNull Bitmap pokemonImage, @NonNull Optional<String> screenShotPath) {
        //WARNING: this method *must* always send an intent at the end, no matter what, to avoid the application
        // hanging.

        Intent info = Pokefly.createNoInfoIntent();
        try {
            ScanResult res = ocr.scanPokemon(GoIVSettings.getInstance(this), pokemonImage, trainerLevel);
            if (res.isFailed()) {
                Toast.makeText(Pokefly.this, getString(R.string.scan_pokemon_failed), Toast.LENGTH_SHORT).show();
            }
            Pokefly.populateInfoIntent(info, res, screenShotPath);
        } finally {
            LocalBroadcastManager.getInstance(Pokefly.this).sendBroadcast(info);
        }
    }

    /**
     * Called by intent from pokefly, captures the screen and runs it through scanPokemon.
     */
    public void takeScreenshot() {
        Bitmap bmp = screen.grabScreen();
        if (bmp == null) {
            return;
        }
        scanPokemon(bmp, Optional.<String>absent());
    }

    /**
     * A picture was shared and needs to be processed. Process it and initiate UI.
     * IV Button was pressed, take screenshot and send back pokemon info.
     */
    private final BroadcastReceiver processBitmap = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bitmap bitmap = intent.getParcelableExtra(KEY_BITMAP);
            if (bitmap == null) {
                return;
            }
            String screenShotPathRaw = intent.getStringExtra(KEY_SCREENSHOT_FILE);
            Optional<String> screenShotPath;
            if (screenShotPathRaw != null) {
                screenShotPath = Optional.of(screenShotPathRaw);
            } else {
                screenShotPath = Optional.absent();
            }

            // this should allow processing of images where the displaymetrics don't match, for example a different
            // phone, it is known that the red dot might not display correctly.
            if (displayMetrics.heightPixels != bitmap.getHeight() || displayMetrics.widthPixels != bitmap.getWidth()) {
                Matrix matrix = new Matrix();
                float ratio = displayMetrics.widthPixels / (float) bitmap.getWidth();
                matrix.postScale(ratio, ratio);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels,
                        matrix, true);
            }

            scanPokemon(bitmap, screenShotPath);
        }
    };

    public void navigateToInputFraction() {
        fractionManager.show(new InputFraction(this, pokeInfoCalculator, pokemonName, pokemonType, candyName,
                pokemonGender, pokemonCandy, pokemonCP, pokemonHP, candyUpgradeCost));
        moveOverlay(false);
    }

    public void navigateToAppraisalFraction() {
        fractionManager.show(new AppraisalFraction(this, autoAppraisal, layoutParams));
        moveOverlay(true);
    }

    public void navigateToIVResultFraction() {
        fractionManager.show(new IVResultFraction(this, lastIvScanResult));
        moveOverlay(false);
    }

    public void navigateToIVCombinationsFraction() {
        fractionManager.show(new IVCombinationsFraction(this, lastIvScanResult));
        moveOverlay(false);
    }

    public void navigateToPowerUpFraction() {
        fractionManager.show(new PowerUpFraction(this));
        moveOverlay(false);
    }

    public void navigateToMovesetFraction() {
        fractionManager.show(new MovesetFraction(this, lastIvScanResult));
        moveOverlay(false);
    }

    /**
     * displayInfo
     * Receiver called once MainActivity's scan is complete, sets all pokemon info and shows the
     * info layout.
     */
    private final BroadcastReceiver displayInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!receivedInfo) {
                if (intent.hasExtra(KEY_SEND_INFO_NAME) && intent.hasExtra(KEY_SEND_INFO_CP) && intent.hasExtra(
                        KEY_SEND_INFO_HP) && intent.hasExtra(KEY_SEND_INFO_LEVEL_LOWER)) {
                    receivedInfo = true;

                    pokemonName = intent.getStringExtra(KEY_SEND_INFO_NAME);
                    pokemonType = intent.getStringExtra(KEY_SEND_INFO_TYPE);
                    candyName = intent.getStringExtra(KEY_SEND_INFO_CANDY);


                    @SuppressWarnings("unchecked") Optional<String> lScreenShotFile =
                            (Optional<String>) intent.getSerializableExtra(KEY_SEND_SCREENSHOT_FILE);
                    @SuppressWarnings("unchecked") Optional<Integer> lPokemonCP =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_CP);
                    @SuppressWarnings("unchecked") Optional<Integer> lPokemonHP =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_HP);
                    @SuppressWarnings("unchecked") Pokemon.Gender lPokemonGender =
                            (Pokemon.Gender) intent.getSerializableExtra(KEY_SEND_INFO_GENDER);
                    @SuppressWarnings("unchecked") Optional<Integer> lCandyAmount =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_CANDY_AMOUNT);
                    @SuppressWarnings("unchecked") Optional<Integer> lCandyUpgradeCost =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_UPGRADE_CANDY_COST);
                    @SuppressWarnings("unchecked") String lUniqueID =
                            (String) intent.getSerializableExtra(KEY_SEND_UNIQUE_ID);

                    screenShotPath = lScreenShotFile;
                    pokemonCP = lPokemonCP;
                    pokemonHP = lPokemonHP;
                    pokemonGender = lPokemonGender;
                    pokemonCandy = lCandyAmount;
                    candyUpgradeCost = lCandyUpgradeCost;
                    pokemonUniqueID = lUniqueID;


                    double estimatedPokemonLevelMin = intent.getDoubleExtra(KEY_SEND_INFO_LEVEL_LOWER, 1);
                    double estimatedPokemonLevelMax = intent.getDoubleExtra(KEY_SEND_INFO_LEVEL_HIGHER, 1);

                    estimatedPokemonLevelRange = new LevelRange(estimatedPokemonLevelMin, estimatedPokemonLevelMax);

                    if (!infoShownReceived) {
                        GoIVSettings settings = GoIVSettings.getInstance(Pokefly.this);
                        if (!startedInManualScreenshotMode) {
                            infoShownReceived = true;
                        }
                        showInfoLayoutArcPointer();
                        if (!startedInManualScreenshotMode && settings.shouldAutoOpenExpandedAppraise()) {
                            // Ensure arc pointer is in the right place
                            setArcPointer(estimatedPokemonLevelRange.min);
                            // Guess the pokemon name
                            PokemonNameCorrector.PokeDist possiblePoke = new PokemonNameCorrector(pokeInfoCalculator)
                                    .getPossiblePokemon(pokemonName, candyName, candyUpgradeCost, pokemonType);
                            pokemon = Optional.of(possiblePoke.pokemon);
                            // Now that we've done what's usually taken care by InputFraction, we can skip it
                            navigateToAppraisalFraction();
                        } else {
                            navigateToInputFraction();
                        }
                    }

                } else {
                    resetPokeflyStateMachine();
                }
            }
        }
    };
}

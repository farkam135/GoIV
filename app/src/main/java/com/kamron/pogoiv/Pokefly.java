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
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.ContextThemeWrapper;
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
import com.kamron.pogoiv.pokeflycomponents.AppraisalManager;
import com.kamron.pogoiv.pokeflycomponents.GoIVNotificationManager;
import com.kamron.pogoiv.pokeflycomponents.IVPopupButton;
import com.kamron.pogoiv.pokeflycomponents.IVPreviewPrinter;
import com.kamron.pogoiv.pokeflycomponents.MovesetsManager;
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
import com.kamron.pogoiv.scanlogic.MovesetData;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.PokemonNameCorrector;
import com.kamron.pogoiv.scanlogic.ScanData;
import com.kamron.pogoiv.scanlogic.ScanResult;
import com.kamron.pogoiv.utils.CopyUtils;
import com.kamron.pogoiv.utils.LevelRange;
import com.kamron.pogoiv.utils.fractions.FractionManager;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Currently, the central service in Pokemon Go, dealing with everything except
 * the initial activity.
 * Created by Kamron on 7/25/2016.
 */

public class Pokefly extends Service {

    public static final String ACTION_UPDATE_UI = "com.kamron.pogoiv.ACTION_UPDATE_UI";
    private static final String ACTION_SEND_INFO = "com.kamron.pogoiv.ACTION_SEND_INFO";
    private static final String ACTION_START = "com.kamron.pogoiv.ACTION_START";
    public static final String ACTION_STOP = "com.kamron.pogoiv.ACTION_STOP";

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
    private static final String KEY_SEND_EVOLUTION_CANDY_COST = "key_send_evolution_candy_cost";
    private static final String KEY_SEND_UNIQUE_ID = "key_send_unique_id";
    private static final String KEY_SEND_POWERUP_STARTDUST_COST = "key_send_powerup_stardust";
    private static final String KEY_SEND_POWERUP_CANDYCOST = "key_send_powerup_candycost";
    private static final String KEY_SEND_MOVESET_QUICK = "key_send_moveset_quick";
    private static final String KEY_SEND_MOVESET_CHARGE = "key_send_moveset_charge";
    private static final String KEY_SEND_IS_LUCKY = "key_send_is_lucky";

    private static final String ACTION_PROCESS_BITMAP = "com.kamron.pogoiv.PROCESS_BITMAP";
    private static final String KEY_BITMAP = "bitmap";
    private static final String KEY_SCREENSHOT_FILE = "ss-file";

    private static final String PREF_USER_CORRECTIONS = "com.kamron.pogoiv.USER_CORRECTIONS";


    private static final ScanScreenRunnable scanScreenRunnable = new ScanScreenRunnable();

    private static boolean running = false;
    public static ScanData scanData;
    public static ScanResult scanResult;

    private int trainerLevel;

    private boolean receivedInfo = false;
    private boolean infoShownSent = false;
    private boolean infoShownReceived = false;

    public boolean startedInManualScreenshotMode = false;
    private String screenShotPath = null;

    private FractionManager fractionManager;
    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    private ClipboardManager clipboard;
    private SharedPreferences sharedPref;
    private ScreenGrabber screen;
    private ScreenShotHelper screenShotHelper;
    private OcrHelper ocr;

    // Pokefly components
    private ScreenWatcher screenWatcher;
    private IVPopupButton ivButton;
    private ClipboardTokenHandler clipboardTokenHandler;
    private IVPreviewPrinter ivPreviewPrinter;
    private ImageView arcPointer;
    private PokeInfoCalculator pokeInfoCalculator;
    private AppraisalManager appraisalManager;
    private PokemonNameCorrector nameCorrector;
    private View sizeDetector1;
    private View sizeDetector2;


    @BindView(R.id.infoLayout)
    CardView infoLayout;
    @BindView(R.id.fractionContainer)
    FrameLayout fractionContainer;


    @SuppressWarnings("deprecation")
    private final WindowManager.LayoutParams arcParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);

    @SuppressWarnings("deprecation")
    private final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            PixelFormat.TRANSPARENT);

    @SuppressWarnings("deprecation")
    private static final WindowManager.LayoutParams sizeDetectorParams = new WindowManager.LayoutParams(
            0,
            0,
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSPARENT);


    public static boolean isRunning() {
        return running;
    }

    public static Intent createStopIntent(@NonNull Context context) {
        Intent intent = new Intent(context, Pokefly.class);
        intent.setAction(ACTION_STOP);
        return intent;
    }

    public static Intent createStartIntent(@NonNull Context context, int trainerLevel) {
        Intent intent = new Intent(context, Pokefly.class);
        intent.setAction(ACTION_START);
        intent.putExtra(KEY_TRAINER_LEVEL, trainerLevel);
        return intent;
    }

    public static Intent createNoInfoIntent() {
        return new Intent(ACTION_SEND_INFO);
    }

    public static void populateInfoIntent(Intent intent, ScanData scanData, @NonNull Optional<String> filePath) {
        intent.putExtra(KEY_SEND_INFO_NAME, scanData.getPokemonName());
        intent.putExtra(KEY_SEND_INFO_TYPE, scanData.getPokemonType());
        intent.putExtra(KEY_SEND_INFO_CANDY, scanData.getCandyName());
        intent.putExtra(KEY_SEND_INFO_GENDER, scanData.getPokemonGender());
        intent.putExtra(KEY_SEND_INFO_HP, scanData.getPokemonHP());
        intent.putExtra(KEY_SEND_INFO_CP, scanData.getPokemonCP());
        intent.putExtra(KEY_SEND_INFO_LEVEL_LOWER, scanData.getEstimatedPokemonLevel().min);
        intent.putExtra(KEY_SEND_INFO_LEVEL_HIGHER, scanData.getEstimatedPokemonLevel().max);
        intent.putExtra(KEY_SEND_SCREENSHOT_FILE, filePath);
        intent.putExtra(KEY_SEND_INFO_CANDY_AMOUNT, scanData.getPokemonCandyAmount());
        intent.putExtra(KEY_SEND_EVOLUTION_CANDY_COST, scanData.getEvolutionCandyCost());
        intent.putExtra(KEY_SEND_UNIQUE_ID, scanData.getPokemonUniqueID());
        intent.putExtra(KEY_SEND_IS_LUCKY, scanData.getIsLucky());
        intent.putExtra(KEY_SEND_POWERUP_CANDYCOST, scanData.getPokemonPowerUpCandyCost());
        intent.putExtra(KEY_SEND_POWERUP_STARTDUST_COST, scanData.getPokemonPowerUpStardustCost());
        if (scanData.getFastMove() != null && scanData.getChargeMove() != null) {
            intent.putExtra(KEY_SEND_MOVESET_QUICK, scanData.getFastMove());
            intent.putExtra(KEY_SEND_MOVESET_CHARGE, scanData.getChargeMove());
        }
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

    public ScreenWatcher getScreenWatcher() {
        return screenWatcher;
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

        nameCorrector = PokemonNameCorrector.getInstance(this);
        pokeInfoCalculator = PokeInfoCalculator.getInstance();
        displayMetrics = getResources().getDisplayMetrics();
        initOcr();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        sharedPref = getSharedPreferences(PREF_USER_CORRECTIONS, Context.MODE_PRIVATE);

        MovesetsManager.init(this);

        LocalBroadcastManager.getInstance(this).registerReceiver(displayInfo, new IntentFilter(ACTION_SEND_INFO));
        LocalBroadcastManager.getInstance(this).registerReceiver(processBitmap,
                new IntentFilter(ACTION_PROCESS_BITMAP));

        sizeDetector1 = new View(this);
        sizeDetector2 = new View(this);
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
            trainerLevel = intent.getIntExtra(KEY_TRAINER_LEVEL, Data.MINIMUM_TRAINER_LEVEL);

            setupDisplaySizeInfo();

            createFlyingComponents();

            startedInManualScreenshotMode = GoIVSettings.getInstance(this).isManualScreenshotModeEnabled();
            /* Assumes MainActivity initialized ScreenGrabber before starting this service. */
            if (!startedInManualScreenshotMode) {
                screen = ScreenGrabber.getInstance();
                appraisalManager = new AppraisalManager(screen, this);
                screenWatcher = new ScreenWatcher(this, fractionManager, appraisalManager);
                screenWatcher.watchScreen();

            } else {
                appraisalManager = new AppraisalManager(null, this);
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
        windowManager.addView(ivButton, IVPopupButton.layoutParams);

        sizeDetectorParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowManager.addView(sizeDetector1, sizeDetectorParams);

        sizeDetectorParams.gravity = Gravity.BOTTOM | Gravity.RIGHT;
        windowManager.addView(sizeDetector2, sizeDetectorParams);

        createArcPointer();

        fractionManager = new FractionManager(
                this, R.style.AppTheme_Dialog, layoutParams, infoLayout, fractionContainer);
    }


    private boolean infoLayoutArcPointerVisible = false;

    private void showInfoLayoutArcPointer() {
        if (!infoLayoutArcPointerVisible && arcPointer != null && infoLayout != null) {
            infoLayoutArcPointerVisible = true;
            windowManager.addView(arcPointer, arcParams);
            windowManager.addView(infoLayout, layoutParams);
        }
    }

    private void hideInfoLayoutArcPointer() {
        if (infoLayoutArcPointerVisible) {
            if (fractionManager != null) {
                fractionManager.remove();
            }
            windowManager.removeView(arcPointer);
            windowManager.removeView(infoLayout);
            infoLayoutArcPointerVisible = false;
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(displayInfo);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(processBitmap);

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
        windowManager.removeView(ivButton);
        windowManager.removeView(sizeDetector1);
        windowManager.removeView(sizeDetector2);
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
        arcParams.y = Data.arcY[index] - arcParams.height / 2 - getCurrentStatusBarHeight();
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
        Context themedContext = new ContextThemeWrapper(this, R.style.AppTheme_Dialog);
        View v = LayoutInflater.from(themedContext).inflate(R.layout.dialog_info_window, null);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        ButterKnife.bind(this, v);
    }

    /**
     * Closes the android keyboard... But this method only works if focus is on a direct child of infolayout.
     * <p>
     * Why the fuck does android not have a good standard method for this.
     */
    private void closeKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Get a list of all views inside the infoLayout
        for (int i = 0; i < infoLayout.getChildCount(); i++) {
            //Tell each view inside infoLayout to close the keyboard if they currently have focus.
            imm.hideSoftInputFromWindow(infoLayout.getChildAt(i).getWindowToken(), 0);
        }
    }

    public int getCurrentStatusBarHeight() {
        int[] out = new int[2];
        sizeDetector1.getLocationOnScreen(out);
        return out[1];
    }

    public int getCurrentNavigationBarHeight() {
        Point screen = new Point();
        windowManager.getDefaultDisplay().getRealSize(screen);
        int[] out = new int[2];
        sizeDetector2.getLocationOnScreen(out);
        return screen.y - out[1];
    }

    public void computeIv() {
        if (scanData == null
                || !scanData.getPokemonCP().isPresent()
                || !scanData.getPokemonHP().isPresent()) {
            Toast.makeText(this, R.string.missing_inputs, Toast.LENGTH_SHORT).show();
            return;
        }

        deleteScreenShotIfRequired();

        //noinspection ConstantConditions
        scanResult = new ScanResult(nameCorrector, scanData);

        pokeInfoCalculator.getIVPossibilities(scanResult);
        scanResult.refineWithAvailableInfoFrom(appraisalManager);

        // Don't run clipboard logic if scan failed - some tokens might crash the program.
        if (scanResult.getIVCombinationsCount() > 0) {
            addClipboardInfoIfSettingOn(scanResult);
        }

        closeKeyboard();
        navigateToIVResultFraction();
    }

    /**
     * Checks if the app is in battery saver mode, and if the user hasnt set the setting to avoid deleting
     * screenshot, and then deletes the screenshot.
     */
    public void deleteScreenShotIfRequired() {
        if (startedInManualScreenshotMode && screenShotPath != null) {
            if (GoIVSettings.getInstance(this).shouldDeleteScreenshots()) {
                screenShotHelper.deleteScreenShot(screenShotPath);
            }
        }
    }

    /**
     * Adds the iv range of the pokemon to the clipboard if the clipboard setting is on.
     */
    public void addClipboardInfoIfSettingOn(ScanResult scanResult) {
        GoIVSettings settings = GoIVSettings.getInstance(this);

        if (settings.shouldCopyToClipboard()) {
            String clipResult = clipboardTokenHandler.getClipboardText(scanResult, pokeInfoCalculator);

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
    public void addSpecificIVClipboard(IVCombination ivCombination) {
        if (scanResult != null) {
            scanResult.selectedIVCombination = ivCombination;

            String clipResult = clipboardTokenHandler.getClipboardText(scanResult, pokeInfoCalculator);

            Toast toast = Toast.makeText(this, String.format(getString(R.string.clipboard_copy_toast), clipResult),
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();

            clipboard.setPrimaryClip(ClipData.newPlainText(clipResult, clipResult));
        }
    }

    /**
     * Adds the users clipboard setting to the users clipboard.
     */
    public void addSpecificMovesetClipboard(MovesetData movesetData) {
        if (scanResult != null) {
            scanResult.selectedMoveset = movesetData;

            GoIVSettings settings = GoIVSettings.getInstance(this);
            if (settings.shouldCopyToClipboard()) {
                String clipResult = clipboardTokenHandler.getClipboardText(scanResult, pokeInfoCalculator);
                clipboard.setPrimaryClip(ClipData.newPlainText(clipResult, clipResult));

                if (settings.shouldCopyToClipboardShowToast()) {
                    Toast toast = Toast.makeText(this,
                            String.format(getString(R.string.clipboard_copy_toast), clipResult), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        }
    }

    /**
     * resets the info dialogue to its default state.
     */
    public void closeInfoDialog() {
        hideInfoLayoutArcPointer();
        resetPokeflyStateMachine();
        appraisalManager.reset();
        if (!startedInManualScreenshotMode) {
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

        ocr = OcrHelper.init(this, extDir, pokeInfoCalculator);
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
            ScanData data = ocr.scanPokemon(GoIVSettings.getInstance(this), pokemonImage, trainerLevel, true);
            if (data.isFailed()) {
                Toast.makeText(Pokefly.this, getString(R.string.scan_pokemon_failed), Toast.LENGTH_SHORT).show();
            }
            Pokefly.populateInfoIntent(info, data, screenShotPath);
        } finally {
            LocalBroadcastManager.getInstance(Pokefly.this).sendBroadcast(info);
        }
    }

    /**
     * Called by IV button, captures the screen and runs it through scanPokemon.
     */
    public void requestScan() {
        // Cancel any pending screen check
        screenWatcher.cancelPendingScreenScan();

        // Run this in a separate thread so the UI can be updated so that IV button hides before moveset scan starts
        scanScreenRunnable.updateRefs(this, screen);
        new Handler().postDelayed(scanScreenRunnable, 80); // Wait 2 frames (at 25ps) before scanning
    }

    private static class ScanScreenRunnable implements Runnable {
        WeakReference<Pokefly> pokeflyRef;
        WeakReference<ScreenGrabber> screenGrabberRef;

        void updateRefs(Pokefly pokefly, ScreenGrabber screenGrabber) {
            pokeflyRef = new WeakReference<>(pokefly);
            screenGrabberRef = new WeakReference<>(screenGrabber);
        }

        @Override public void run() {
            ScreenGrabber screenGrabber = screenGrabberRef.get();
            if (screenGrabber != null) {
                Bitmap bmp = screenGrabber.grabScreen();
                if (bmp == null) {
                    return;
                }
                Pokefly pokefly = pokeflyRef.get();
                if (pokefly != null) {
                    pokefly.scanPokemon(bmp, Optional.<String>absent());
                }
            }
        }
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

    public void navigateToPreferredStartFraction() {
        if (GoIVSettings.getInstance(this).shouldAutoOpenExpandedAppraise()) {
            navigateToAppraisalFraction();
        } else {
            navigateToInputFraction();
        }
    }

    public void navigateToInputFraction() {
        fractionManager.show(new InputFraction(this));
    }

    public void navigateToAppraisalFraction() {
        fractionManager.show(new AppraisalFraction(this, sharedPref, appraisalManager));
    }

    public void navigateToIVResultFraction() {
        fractionManager.show(new IVResultFraction(this));
    }

    public void navigateToIVCombinationsFraction() {
        fractionManager.show(new IVCombinationsFraction(this));
    }

    public void navigateToPowerUpFraction() {
        fractionManager.show(new PowerUpFraction(this));
    }

    public void navigateToMovesetFraction() {
        fractionManager.show(new MovesetFraction(this, sharedPref));
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

                    String pokemonName = intent.getStringExtra(KEY_SEND_INFO_NAME);
                    String pokemonType = intent.getStringExtra(KEY_SEND_INFO_TYPE);
                    String candyName = intent.getStringExtra(KEY_SEND_INFO_CANDY);

                    @SuppressWarnings("unchecked") Optional<String> lScreenShotFile =
                            (Optional<String>) intent.getSerializableExtra(KEY_SEND_SCREENSHOT_FILE);
                    @SuppressWarnings("unchecked") Optional<Integer> pokemonCP =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_CP);
                    @SuppressWarnings("unchecked") Optional<Integer> pokemonHP =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_HP);
                    @SuppressWarnings("unchecked") Pokemon.Gender pokemonGender =
                            (Pokemon.Gender) intent.getSerializableExtra(KEY_SEND_INFO_GENDER);
                    @SuppressWarnings("unchecked") Optional<Integer> candyAmount =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_CANDY_AMOUNT);
                    @SuppressWarnings("unchecked") Optional<Integer> evolutionCandyCost =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_EVOLUTION_CANDY_COST);
                    @SuppressWarnings("unchecked") Optional<Integer> powerUpCandyCost =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_POWERUP_CANDYCOST);
                    @SuppressWarnings("unchecked") Optional<Integer> powerUpStardustCost =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_POWERUP_STARTDUST_COST);
                    @SuppressWarnings("unchecked") boolean isLucky =
                            (boolean) intent.getSerializableExtra(KEY_SEND_IS_LUCKY);
                    @SuppressWarnings("unchecked") String uniqueID =
                            (String) intent.getSerializableExtra(KEY_SEND_UNIQUE_ID);

                    if (lScreenShotFile.isPresent()) {
                        screenShotPath = lScreenShotFile.get();
                    } else {
                        screenShotPath = null;
                    }

                    String moveFast = intent.getStringExtra(KEY_SEND_MOVESET_QUICK);
                    String moveCharge = intent.getStringExtra(KEY_SEND_MOVESET_CHARGE);
                    if (moveFast == null || moveCharge == null) {
                        moveFast = "";
                        moveCharge = "";
                    }

                    double estimatedPokemonLevelMin = intent.getDoubleExtra(KEY_SEND_INFO_LEVEL_LOWER, 1);
                    double estimatedPokemonLevelMax = intent.getDoubleExtra(KEY_SEND_INFO_LEVEL_HIGHER, 1);

                    scanData = new ScanData(
                            new LevelRange(estimatedPokemonLevelMin, estimatedPokemonLevelMax),
                            pokemonName,
                            pokemonType,
                            candyName,
                            pokemonGender,
                            pokemonHP,
                            pokemonCP,
                            candyAmount,
                            evolutionCandyCost,
                            powerUpStardustCost,
                            powerUpCandyCost,
                            moveFast,
                            moveCharge,
                            isLucky,
                            uniqueID);

                    if (!infoShownReceived) {
                        GoIVSettings settings = GoIVSettings.getInstance(Pokefly.this);
                        if (!startedInManualScreenshotMode) {
                            infoShownReceived = true;
                        }
                        // Show the overlay window and the arc pointer
                        showInfoLayoutArcPointer();
                        // Ensure arc pointer is in the right place
                        setArcPointer(scanData.getEstimatedPokemonLevel().min);
                        // Read user preferences and navigate accordingly
                        if (settings.shouldShowConfirmationDialogs()) {
                            // Will navigate either to InputFraction or AppraisalFraction
                            navigateToPreferredStartFraction();
                        } else {
                            // Skip the input screens and navigate directly to the results
                            computeIv();
                            navigateToIVResultFraction();
                        }
                    }

                } else {
                    resetPokeflyStateMachine();
                }
            }
        }
    };
}

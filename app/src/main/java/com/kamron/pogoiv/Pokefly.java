package com.kamron.pogoiv;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.kamron.pogoiv.logic.CPRange;
import com.kamron.pogoiv.logic.Data;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.PokeSpam;
import com.kamron.pogoiv.logic.Pokemon;
import com.kamron.pogoiv.logic.PokemonNameCorrector;
import com.kamron.pogoiv.logic.ScanContainer;
import com.kamron.pogoiv.logic.ScanResult;
import com.kamron.pogoiv.logic.UpgradeCost;
import com.kamron.pogoiv.widgets.IVResultsAdapter;
import com.kamron.pogoiv.widgets.PokemonSpinnerAdapter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apptik.widget.MultiSlider;

import static com.kamron.pogoiv.GoIVSettings.APPRAISAL_WINDOW_POSITION;

/**
 * Currently, the central service in Pokemon Go, dealing with everything except
 * the initial activity.
 * Created by Kamron on 7/25/2016.
 */

public class Pokefly extends Service {

    public static final String ACTION_UPDATE_UI = "com.kamron.pogoiv.ACTION_UPDATE_UI";
    public static final String ACTION_STOP = "com.kamron.pogoiv.ACTION_STOP";
    private static final String ACTION_SEND_INFO = "com.kamron.pogoiv.ACTION_SEND_INFO";

    private static final String KEY_TRAINER_LEVEL = "key_trainer_level";
    private static final String KEY_STATUS_BAR_HEIGHT = "key_status_bar_height";
    private static final String KEY_BATTERY_SAVER = "key_battery_saver";

    private static final String KEY_SEND_INFO_NAME = "key_send_info_name";
    private static final String KEY_SEND_INFO_CANDY = "key_send_info_candy";
    private static final String KEY_SEND_INFO_HP = "key_send_info_hp";
    private static final String KEY_SEND_INFO_CP = "key_send_info_cp";
    private static final String KEY_SEND_INFO_LEVEL = "key_send_info_level";
    private static final String KEY_SEND_SCREENSHOT_FILE = "key_send_screenshot_file";
    private static final String KEY_SEND_INFO_CANDY_AMOUNT = "key_send_info_candy_amount";

    private static final String ACTION_PROCESS_BITMAP = "com.kamron.pogoiv.PROCESS_BITMAP";
    private static final String KEY_BITMAP = "bitmap";
    private static final String KEY_SCREENSHOT_FILE = "ss-file";

    private static final String PREF_USER_CORRECTIONS = "com.kamron.pogoiv.USER_CORRECTIONS";

    private static final int NOTIFICATION_REQ_CODE = 8959;

    private static boolean running = false;

    private int trainerLevel = -1;
    private boolean batterySaver = false;

    private boolean receivedInfo = false;

    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    private ClipboardManager clipboard;
    private SharedPreferences sharedPref;
    private ScreenGrabber screen;
    private ScreenShotHelper screenShotHelper;
    private OcrHelper ocr;

    private Point[] area = new Point[2];

    private boolean infoShownSent = false;
    private boolean infoShownReceived = false;
    private boolean ivButtonShown = false;

    private ImageView ivButton;
    private ImageView arcPointer;
    private LinearLayout infoLayout;

    private LinearLayout touchView;
    private WindowManager.LayoutParams touchViewParams;
    private Handler screenScanHandler;
    private Runnable screenScanRunnable;
    private static final int SCREEN_SCAN_DELAY_MS = 1000;
    private static final int SCREEN_SCAN_RETRIES = 3;
    private int screenScanRetries;

    private PokeInfoCalculator pokeInfoCalculator;

    //results pokemon picker auto complete
    @BindView(R.id.autoCompleteTextView1)
    AutoCompleteTextView autoCompleteTextView1;

    @BindView(R.id.pokePickerToggleSpinnerVsInput)
    Button pokePickerToggleSpinnerVsInput;


    private PokemonSpinnerAdapter pokeInputSpinnerAdapter;
    @BindView(R.id.spnPokemonName)
    Spinner pokeInputSpinner;

    @BindView(R.id.tvSeeAllPossibilities)
    TextView seeAllPossibilities;
    @BindView(R.id.etCp)
    EditText pokemonCPEdit;
    @BindView(R.id.etHp)
    EditText pokemonHPEdit;
    @BindView(R.id.etCandy)
    EditText pokemonCandyEdit;
    @BindView(R.id.sbArcAdjust)
    SeekBar arcAdjustBar;
    @BindView(R.id.llButtonsInitial)
    LinearLayout initialButtonsLayout;
    @BindView(R.id.llButtonsOnCheck)
    LinearLayout onCheckButtonsLayout;


    @BindView(R.id.appraisalIvRange)
    Spinner appraisalIvRange;
    @BindView(R.id.appraisalPercentageRange)
    Spinner appraisalPercentageRange;

    // Layouts
    @BindView(R.id.inputBox)
    LinearLayout inputBox;
    @BindView(R.id.resultsBox)
    LinearLayout resultsBox;
    @BindView(R.id.expandedResultsBox)
    LinearLayout expandedResultsBox;
    @BindView(R.id.allPossibilitiesBox)
    LinearLayout allPossibilitiesBox;

    @BindView(R.id.appraisalBox)
    LinearLayout appraisalBox;

    // Result data
    private PokemonSpinnerAdapter extendedEvolutionSpinnerAdapter;

    @BindView(R.id.extendedEvolutionSpinner)
    Spinner extendedEvolutionSpinner;

    @BindView(R.id.resultsMinPercentage)
    TextView resultsMinPercentage;
    @BindView(R.id.resultsAvePercentage)
    TextView resultsAvePercentage;
    @BindView(R.id.resultsMaxPercentage)
    TextView resultsMaxPercentage;
    @BindView(R.id.resultsPokemonLevel)
    TextView resultsPokemonLevel;
    @BindView(R.id.exResCandy)
    TextView exResCandy;
    @BindView(R.id.exResLevel)
    TextView exResLevel;
    @BindView(R.id.resultsPokemonName)
    TextView resultsPokemonName;
    @BindView(R.id.resultsCombinations)
    TextView resultsCombinations;
    @BindView(R.id.exResultCP)
    TextView exResultCP;
    @BindView(R.id.exResultHP)
    TextView exResultHP;
    @BindView(R.id.exResultPercentPerfection)
    TextView exResultPercentPerfection;
    @BindView(R.id.explainCPPercentageComparedToMaxIV)
    TextView explainCPPercentageComparedToMaxIV;
    @BindView(R.id.exResStardust)
    TextView exResStardust;
    @BindView(R.id.exResPrevScan)
    TextView exResPrevScan;
    @BindView(R.id.exResCompare)
    TextView exResCompare;
    @BindView(R.id.resultsMoreInformationText)
    TextView resultsMoreInformationText;
    @BindView(R.id.expandedLevelSeekbar)
    SeekBar expandedLevelSeekbar;
    @BindView(R.id.expandedLevelSeekbarBackground)
    MultiSlider expandedLevelSeekbarBackground;
    @BindView(R.id.llSingleMatch)
    LinearLayout llSingleMatch;
    @BindView(R.id.tvAvgIV)
    TextView tvAvgIV;
    @BindView(R.id.resultsAttack)
    TextView resultsAttack;
    @BindView(R.id.resultsDefense)
    TextView resultsDefense;
    @BindView(R.id.resultsHP)
    TextView resultsHP;
    @BindView(R.id.llMaxIV)
    LinearLayout llMaxIV;
    @BindView(R.id.llMinIV)
    LinearLayout llMinIV;
    @BindView(R.id.llMultipleIVMatches)
    LinearLayout llMultipleIVMatches;
    @BindView(R.id.refine_by_last_scan)
    LinearLayout refine_by_last_scan;

    @BindView(R.id.inputAppraisalExpandBox)
    TextView inputAppraisalExpandBox;

    @BindView(R.id.rvResults)
    RecyclerView rvResults;

    //PokeSpam
    @BindView(R.id.llPokeSpamDialogInputContentBox)
    LinearLayout pokeSpamDialogInputContentBox;
    @BindView(R.id.llPokeSpam)
    LinearLayout pokeSpamView;
    @BindView(R.id.exResPokeSpam)
    TextView exResPokeSpam;

    // Refine by appraisal
    @BindView(R.id.attCheckbox)
    CheckBox attCheckbox;
    @BindView(R.id.defCheckbox)
    CheckBox defCheckbox;
    @BindView(R.id.staCheckbox)
    CheckBox staCheckbox;

    @BindView(R.id.positionHandler)
    ImageView positionHandler;

    private String pokemonName;
    private String candyName;
    private Optional<Integer> pokemonCandy = Optional.absent();
    private Optional<Integer> pokemonCP = Optional.absent();
    private Optional<Integer> pokemonHP = Optional.absent();
    private double estimatedPokemonLevel = 1.0;
    private @NonNull Optional<String> screenShotPath = Optional.absent();

    private PokemonNameCorrector corrector;

    private final WindowManager.LayoutParams arcParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
    private int pointerHeight = 0;
    private int pointerWidth = 0;
    private int statusBarHeight = 0;

    private final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            PixelFormat.TRANSPARENT);

    private final WindowManager.LayoutParams ivButtonParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    public static boolean isRunning() {
        return running;
    }

    public static Intent createIntent(Activity activity, int trainerLevel, int statusBarHeight, boolean batterySaver) {
        Intent intent = new Intent(activity, Pokefly.class);
        intent.putExtra(KEY_TRAINER_LEVEL, trainerLevel);
        intent.putExtra(KEY_STATUS_BAR_HEIGHT, statusBarHeight);
        intent.putExtra(KEY_BATTERY_SAVER, batterySaver);
        return intent;
    }

    public static Intent createNoInfoIntent() {
        return new Intent(ACTION_SEND_INFO);
    }

    public static void populateInfoIntent(Intent intent, ScanResult scanResult, @NonNull Optional<String> filePath) {
        intent.putExtra(KEY_SEND_INFO_NAME, scanResult.getPokemonName());
        intent.putExtra(KEY_SEND_INFO_CANDY, scanResult.getCandyName());
        intent.putExtra(KEY_SEND_INFO_HP, scanResult.getPokemonHP());
        intent.putExtra(KEY_SEND_INFO_CP, scanResult.getPokemonCP());
        intent.putExtra(KEY_SEND_INFO_LEVEL, scanResult.getEstimatedPokemonLevel());
        intent.putExtra(KEY_SEND_SCREENSHOT_FILE, filePath);
        intent.putExtra(KEY_SEND_INFO_CANDY_AMOUNT, scanResult.getPokemonCandyAmount());
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

    @Override
    public void onCreate() {
        super.onCreate();

        running = true;

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_UPDATE_UI));

        pokeInfoCalculator = PokeInfoCalculator.getInstance(
                getResources().getStringArray(R.array.pokemon),
                getResources().getIntArray(R.array.attack),
                getResources().getIntArray(R.array.defense),
                getResources().getIntArray(R.array.stamina),
                getResources().getIntArray(R.array.devolutionNumber),
                getResources().getIntArray(R.array.evolutionCandyCost));
        displayMetrics = this.getResources().getDisplayMetrics();
        initOcr();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        sharedPref = getSharedPreferences(PREF_USER_CORRECTIONS, Context.MODE_PRIVATE);
        corrector = initCorrectorFromPrefs(pokeInfoCalculator, sharedPref);

        LocalBroadcastManager.getInstance(this).registerReceiver(displayInfo, new IntentFilter(ACTION_SEND_INFO));
        LocalBroadcastManager.getInstance(this).registerReceiver(processBitmap,
                new IntentFilter(ACTION_PROCESS_BITMAP));
    }

    @SuppressWarnings("unchecked")
    private static PokemonNameCorrector initCorrectorFromPrefs(PokeInfoCalculator pokeInfoCalculator,
                                                               SharedPreferences sharedPref) {
        return new PokemonNameCorrector(pokeInfoCalculator, (Map<String, String>) sharedPref.getAll());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY;
        }

        running = true;

        if (ACTION_STOP.equals(intent.getAction())) {
            stopSelf();
        } else if (intent.hasExtra(KEY_TRAINER_LEVEL)) {
            trainerLevel = intent.getIntExtra(KEY_TRAINER_LEVEL, 1);
            statusBarHeight = intent.getIntExtra(KEY_STATUS_BAR_HEIGHT, 0);
            batterySaver = intent.getBooleanExtra(KEY_BATTERY_SAVER, false);
            makeNotification();
            createInfoLayout();
            createIVButton();
            createArcPointer();
            createArcAdjuster();
            /* Assumes MainActivity initialized ScreenGrabber before starting this service. */
            if (!batterySaver) {
                screen = ScreenGrabber.getInstance();
                watchScreen();
            } else {
                screenShotHelper = ScreenShotHelper.start(Pokefly.this);
            }
        }

        return START_STICKY;
    }

    private void watchScreen() {
        area[0] = new Point(                // these values used to get "white" left of "power up"
                Math.round(displayMetrics.widthPixels / 24),
                (int) Math.round(displayMetrics.heightPixels / 1.24271845));
        area[1] = new Point(                // these values used to get greenish color in transfer button
                (int) Math.round(displayMetrics.widthPixels / 1.15942029),
                (int) Math.round(displayMetrics.heightPixels / 1.11062907));

        screenScanHandler = new Handler();
        screenScanRunnable = new Runnable() {
            @Override
            public void run() {
                if (screenScanRetries > 0) {
                    boolean ret = scanPokemonScreen();
                    if (ret) {
                        screenScanRetries = 0; //skip further retries.
                    } else {
                        screenScanRetries--;
                        screenScanHandler.postDelayed(screenScanRunnable, SCREEN_SCAN_DELAY_MS);
                    }
                }
            }
        };

        touchView = new LinearLayout(this);
        touchViewParams = new WindowManager.LayoutParams(
                1,
                1,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT);
        touchViewParams.gravity = Gravity.LEFT | Gravity.TOP;

        touchView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_OUTSIDE) {
                    screenScanHandler.removeCallbacks(screenScanRunnable);
                    screenScanHandler.postDelayed(screenScanRunnable, SCREEN_SCAN_DELAY_MS);
                    screenScanRetries = SCREEN_SCAN_RETRIES;
                }
                return false;
            }
        });

        windowManager.addView(touchView, touchViewParams);
    }

    /**
     * Undoes the effects of watchScreen.
     */
    private void unwatchScreen() {
        windowManager.removeView(touchView);
        touchViewParams = null;
        touchView = null;
        screenScanHandler.removeCallbacks(screenScanRunnable);
        screenScanRunnable = null;
        screenScanHandler = null;
    }

    /**
     * scanPokemonScreen
     * Scans the device screen to check area[0] for the white and area[1] for the transfer button.
     * If both exist then the user is on the pokemon screen.
     */
    private boolean scanPokemonScreen() {
        @ColorInt int[] pixels = screen.grabPixels(area);

        if (pixels != null) {
            boolean shouldShow =
                    pixels[0] == Color.rgb(250, 250, 250) && pixels[1] == Color.rgb(28, 135, 150);
            setIVButtonDisplay(shouldShow);
            return shouldShow;
        }
        return false;
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
            windowManager.removeView(arcPointer);
            windowManager.removeView(infoLayout);
            infoLayoutArcPointerVisible = false;
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(displayInfo);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(processBitmap);

        if (!batterySaver) {
            unwatchScreen();
            if (screen != null) {
                screen.exit();
                screen = null;
            }
        } else {
            screenShotHelper.stop();
            screenShotHelper = null;
        }
        setIVButtonDisplay(false);
        hideInfoLayoutArcPointer();

        ocr.exit();
        //Now ocr contains an invalid instance hence let's clear it.
        ocr = null;

        running = false;
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_UPDATE_UI));

        super.onDestroy();
    }

    /**
     * Creates the GoIV notification.
     */
    private void makeNotification() {
        Intent openAppIntent = new Intent(this, MainActivity.class);

        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                this, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action openAppAction = new NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_more,
                getString(R.string.notification_open_app),
                openAppPendingIntent).build();

        Intent stopServiceIntent = new Intent(this, Pokefly.class);
        stopServiceIntent.setAction(ACTION_STOP);

        PendingIntent stopServicePendingIntent = PendingIntent.getService(
                this, 0, stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action stopServiceAction = new NotificationCompat.Action.Builder(
                android.R.drawable.ic_menu_close_clear_cancel,
                getString(R.string.main_stop),
                stopServicePendingIntent).build();

        Notification notification = new NotificationCompat.Builder(this)
                .setOngoing(true)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setColor(getColorC(R.color.colorPrimary))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(getString(R.string.notification_title, trainerLevel))
                .setContentIntent(openAppPendingIntent)
                .addAction(openAppAction)
                .addAction(stopServiceAction)
                .build();

        startForeground(NOTIFICATION_REQ_CODE, notification);
    }

    /**
     * Undeprecated version of getDrawable using the most appropriate underlying API.
     *
     * @param id ID of drawable to get
     * @return Desired drawable.
     */
    @SuppressWarnings("deprecation")
    private Drawable getDrawableC(@DrawableRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getDrawable(id);
        } else {
            return getResources().getDrawable(id);
        }
    }

    /**
     * Undeprecated version of getColor using the most appropriate underlying API.
     *
     * @param id ID of color to get
     * @return Desired color.
     */
    @SuppressWarnings("deprecation")
    private @ColorInt int getColorC(@ColorRes int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return getColor(id);
        } else {
            return getResources().getColor(id);
        }
    }

    /**
     * Creates the arc pointer view and sets all the variables required to accurately overlay
     * Pokemon Go's arc pointer.
     */
    private void createArcPointer() {
        arcParams.gravity = Gravity.TOP | Gravity.START;
        arcPointer = new ImageView(this);
        arcPointer.setImageResource(R.drawable.dot);

        Drawable dot = getDrawableC(R.drawable.dot);
        pointerHeight = dot.getIntrinsicHeight() / 2;
        pointerWidth = dot.getIntrinsicWidth() / 2;
    }


    /**
     * setArcPointer
     * Sets the arc pointer to the specified degree.
     *
     * @param pokeLevel The pokemon level to set the arc pointer to.
     */
    private void setArcPointer(double pokeLevel) {
        int index = Data.levelToLevelIdx(pokeLevel);
        arcParams.x = Data.arcX[index] - pointerWidth;
        arcParams.y = Data.arcY[index] - pointerHeight - statusBarHeight;
        //That is, (int) (arcCenter + (radius * Math.cos(angleInRadians))) and
        //(int) (arcInitialY + (radius * Math.sin(angleInRadians))).
        windowManager.updateViewLayout(arcPointer, arcParams);
    }

    /**
     * Creates the arc adjuster used to move the arc pointer in the scan screen.
     */
    private void createArcAdjuster() {
        arcAdjustBar.setMax(Data.trainerLevelToMaxPokeLevelIdx(trainerLevel));

        arcAdjustBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                estimatedPokemonLevel = Data.levelIdxToLevel(progress);
                setArcPointer(estimatedPokemonLevel);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Creates the IV Button view.
     */
    private void createIVButton() {
        ivButton = new ImageView(this);
        ivButton.setImageResource(R.drawable.button);

        ivButtonParams.gravity = Gravity.BOTTOM | Gravity.START;
        ivButtonParams.x = dpToPx(20);
        ivButtonParams.y = dpToPx(15);

        ivButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setIVButtonDisplay(false);
                    takeScreenshot();
                    receivedInfo = false;
                    infoShownSent = true;
                    infoShownReceived = false;
                }
                return false;
            }
        });
    }

    /**
     * creates the info layout which contains all the scanned data views and allows for correction.
     */
    private void createInfoLayout() {

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        infoLayout = (LinearLayout) inflater.inflate(R.layout.dialog_info_window, null);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        ButterKnife.bind(this, infoLayout);

        createInputLayout();
        createResultLayout();
        createAllIvLayout();

        initPositionHandler();
    }

    /**
     * Creates an OnTouchListener for positionHandler and evaluates its events in order to determine
     * the actions required to move the window and save its new location.
     */
    private void initPositionHandler() {
        positionHandler.setOnTouchListener(new View.OnTouchListener() {
            WindowManager.LayoutParams newParams = layoutParams;
            double y, startingY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        y = newParams.y;
                        startingY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        newParams.y = (int) (y + (event.getRawY() - startingY));
                        windowManager.updateViewLayout(infoLayout, newParams);
                        break;

                    case MotionEvent.ACTION_UP:
                        saveWindowPosition(newParams.y);

                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Saves the current Info Window location to shared preferences.
     * @param appraisalWindowPosition Current Info Window Y offset for appraisal mode.
     */
    private void saveWindowPosition(int appraisalWindowPosition) {
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putInt(APPRAISAL_WINDOW_POSITION, appraisalWindowPosition);
        edit.apply();
    }

    /**
     * Creates and initializes the components in the "screen" in he floating dialog that shows all possible iv
     * combinations.
     */
    private void createAllIvLayout() {
        // Setting up Recyclerview for further use.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvResults.hasFixedSize();

        rvResults.setLayoutManager(layoutManager);
        rvResults.setItemAnimator(new DefaultItemAnimator());

    }

    /**
     * Creates and initializes the components in the first "screen" in the floating dialog, the input dialog.
     */
    private void createInputLayout() {
        pokeInputSpinnerAdapter = new PokemonSpinnerAdapter(this, R.layout.spinner_pokemon, new ArrayList<Pokemon>());
        pokeInputSpinner.setAdapter(pokeInputSpinnerAdapter);

        initializePokemonAutoCompleteTextView();

        populateTeamAppraisalSpinners();
    }

    /**
     * Creates and initializes the components in the second "screen" in the floating dialog, the result dialog.
     */
    private void createResultLayout() {
        createExtendedResultEvolutionSpinner();
        createExtendedResultLevelSeekbar();
    }

    /**
     * Creates and initializes the level seekbarr in the evolution and powerup prediction section in the results
     * screen.
     */
    private void createExtendedResultLevelSeekbar() {
        expandedLevelSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    populateAdvancedInformation(ScanContainer.scanContainer.currScan);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    /**
     * Creates and initializes the evolution spinner in the evolution and powerup prediction section in the results
     * screen.
     */
    private void createExtendedResultEvolutionSpinner() {
        //The evolution picker for seeing estimates of how much cp and cost a pokemon will have at a different evolution
        extendedEvolutionSpinnerAdapter = new PokemonSpinnerAdapter(this, R.layout.spinner_evolution,
                new ArrayList<Pokemon>());
        extendedEvolutionSpinner.setAdapter(extendedEvolutionSpinnerAdapter);

        extendedEvolutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                populateAdvancedInformation(ScanContainer.scanContainer.currScan);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                populateAdvancedInformation(ScanContainer.scanContainer.currScan);
            }

        });

    }

    /**
     * Changes the text in the appraisal spinners depending on what team the user is on.
     */
    private void populateTeamAppraisalSpinners() {
        //Create the adapters for the spinners
        ArrayAdapter<CharSequence> adapterIvRange;
        ArrayAdapter<CharSequence> adapterPercentage;

        //Load the correct phrases from the text resources depending on what team is stored in app settings
        if (GoIVSettings.getInstance(getBaseContext()).playerTeam() == 0) {
            adapterIvRange = ArrayAdapter.createFromResource(this,
                    R.array.mystic_ivrange, R.layout.goiv_spinner_item);
            adapterPercentage = ArrayAdapter.createFromResource(this,
                    R.array.mystic_percentage, R.layout.goiv_spinner_item);

        } else if (GoIVSettings.getInstance(getBaseContext()).playerTeam() == 1) {
            adapterIvRange = ArrayAdapter.createFromResource(this,
                    R.array.valor_ivrange, R.layout.goiv_spinner_item);
            adapterPercentage = ArrayAdapter.createFromResource(this,
                    R.array.valor_percentage, R.layout.goiv_spinner_item);
        } else {
            adapterIvRange = ArrayAdapter.createFromResource(this,
                    R.array.instinct_ivrange, R.layout.goiv_spinner_item);
            adapterPercentage = ArrayAdapter.createFromResource(this,
                    R.array.instinct_percentage, R.layout.goiv_spinner_item);
        }

        appraisalIvRange.setAdapter(adapterIvRange);
        appraisalPercentageRange.setAdapter(adapterPercentage);


        appraisalIvRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                //We don't want anything to happen when the user has selected an item that does not exist or the
                // spinner disappears, but interface requires implementation so here's an empty method.
            }
        });
        appraisalPercentageRange.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

    }

    @OnClick({R.id.pokePickerToggleSpinnerVsInput})
    /**
     * In the input screen, switches between the two methods the user has of picking pokemon - a dropdown list, or
     * typing
     */
    public void toggleSpinnerVsInput() {
        if (autoCompleteTextView1.getVisibility() == View.GONE) {
            autoCompleteTextView1.setVisibility(View.VISIBLE);
            autoCompleteTextView1.requestFocus();
            pokeInputSpinner.setVisibility(View.GONE);
        } else {
            resetToSpinner();
        }
    }

    private void resetToSpinner() {
        autoCompleteTextView1.setVisibility(View.GONE);
        pokeInputSpinner.setVisibility(View.VISIBLE);
    }

    private static final int MAX_DRAWABLE_LEVEL = 10000;

    private void toggleVisibility(TextView expanderText, LinearLayout expandedBox, boolean animate) {
        setVisibility(expanderText, expandedBox, animate, expandedBox.getVisibility() != View.VISIBLE);
    }

    private void setVisibility(TextView expanderText, LinearLayout expandedBox, boolean animate, boolean visible) {
        int boxVisibility;
        Drawable arrowDrawable;
        if (visible) {
            boxVisibility = View.VISIBLE;
            arrowDrawable = getDrawableC(R.drawable.arrow_expand);
        } else {
            boxVisibility = View.GONE;
            arrowDrawable = getDrawableC(R.drawable.arrow_collapse);
        }
        expanderText.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
        if (animate) {
            Animator arrowAnimator =
                    ObjectAnimator.ofInt(arrowDrawable, "level", 0, MAX_DRAWABLE_LEVEL).setDuration(100);
            arrowAnimator.start();
        } else {
            arrowDrawable.setLevel(MAX_DRAWABLE_LEVEL);
        }
        expandedBox.setVisibility(boxVisibility);
    }


    @OnClick({R.id.resultsMoreInformationText})
    public void toggleMoreResultsBox() {
        toggleVisibility(resultsMoreInformationText, expandedResultsBox, true);
    }

    @OnClick({R.id.inputAppraisalExpandBox})
    /**
     * Method called when user presses the text to expand the appraisal box on the input screen
     */
    public void toggleAppraisalBox() {
        toggleVisibility(inputAppraisalExpandBox, appraisalBox, true);
        positionHandler.setVisibility(appraisalBox.getVisibility());
        moveOverlayUpOrDownToMatchAppraisalBox();
    }

    /**
     * Moves the overlay up or down.
     *
     * @param moveUp true if move up, false if move down
     */
    private void moveOverlay(Boolean moveUp) {
        WindowManager.LayoutParams newParams = (WindowManager.LayoutParams) infoLayout.getLayoutParams();
        if (moveUp) {
            newParams.gravity = Gravity.TOP;
            newParams.y = sharedPref.getInt(APPRAISAL_WINDOW_POSITION,0);
        } else {
            newParams.gravity = Gravity.BOTTOM;
            newParams.y = 0;
        }
        windowManager.updateViewLayout(infoLayout, newParams);
    }

    /**
     * Moves the entire overlay up if the appraisal box is visible.
     */
    private void moveOverlayUpOrDownToMatchAppraisalBox() {
        if (windowManager == null) {
            return; //do nothing if window is not initiated
        }
        if (infoLayout.getLayoutParams() == null) {
            return;
        }

        //move up if on input screen & appraisal box is open, else move down
        moveOverlay(inputBox.getVisibility() == View.VISIBLE && appraisalBox.getVisibility() == View.VISIBLE);
    }

    private void adjustArcPointerBar(double estimatedPokemonLevel) {
        setArcPointer(estimatedPokemonLevel);
        arcAdjustBar.setProgress(Data.levelToLevelIdx(estimatedPokemonLevel));
    }

    @OnClick(R.id.btnDecrementLevel)
    public void decrementLevel() {
        if (estimatedPokemonLevel > 1.0) {
            estimatedPokemonLevel -= 0.5;
        }
        adjustArcPointerBar(estimatedPokemonLevel);
    }

    @OnClick(R.id.btnIncrementLevel)
    public void incrementLevel() {
        if (estimatedPokemonLevel < Data.trainerLevelToMaxPokeLevel(trainerLevel)) {
            estimatedPokemonLevel += 0.5;
        }
        adjustArcPointerBar(estimatedPokemonLevel);
    }

    @OnClick(R.id.btnIncrementLevelExpanded)
    public void incrementLevelExpanded() {
        expandedLevelSeekbar.setProgress(expandedLevelSeekbar.getProgress() + 1);
        populateAdvancedInformation(ScanContainer.scanContainer.currScan);
    }

    @OnClick(R.id.explainCPPercentageComparedToMaxIV)
    public void explainCPPercentageComparedToMaxIV() {
        Toast.makeText(getApplicationContext(), R.string.perfection_explainer, Toast.LENGTH_LONG).show();
    }

    @OnClick(R.id.btnDecrementLevelExpanded)
    public void decrementLevelExpanded() {
        expandedLevelSeekbar.setProgress(expandedLevelSeekbar.getProgress() - 1);
        populateAdvancedInformation(ScanContainer.scanContainer.currScan);
    }

    /**
     * Parse numeric inputs.
     *
     * @return true if the numeric inputs are valid.
     */
    private boolean parseNumericInputs() {
        try {
            pokemonHP = Optional.of(Integer.parseInt(pokemonHPEdit.getText().toString()));
            pokemonCP = Optional.of(Integer.parseInt(pokemonCPEdit.getText().toString()));
        } catch (NumberFormatException e) {
            return false;
        }
        //do not require pokemon candy to be filled
        try {
            pokemonCandy = Optional.of(Integer.parseInt(pokemonCandyEdit.getText().toString()));
        } catch (NumberFormatException e) {
            pokemonCandy = Optional.absent();
        }
        return true;
    }

    @OnClick(R.id.btnCheckIv)
    /**
     * Method called when user presses "check iv" in the input screen, which takes the user to the result screen
     */
    public void checkIv() {
        //warn user and stop calculation if scan/input failed/is wrong
        if (!parseNumericInputs() || !pokemonHP.isPresent() || !pokemonCP.isPresent()) {
            Toast.makeText(this, R.string.missing_inputs, Toast.LENGTH_SHORT).show();
            return;
        }

        deleteScreenShotIfRequired();

        Pokemon pokemon = interpretWhichPokemonUserInput();
        if (pokemon == null) {
            return;
        }

        rememberUserInputForPokemonNameIfNewNickname(pokemon);

        IVScanResult ivScanResult = pokeInfoCalculator.getIVPossibilities(pokemon, estimatedPokemonLevel,
                pokemonHP.get(),
                pokemonCP.get());

        refineByAvailableAppraisalInfo(ivScanResult);

        // If no possible combinations, inform the user and abort.
        // However, if tooManyPossibilities, a zero count does *not* mean no possibilities.
        if (!ivScanResult.tooManyPossibilities && ivScanResult.getCount() == 0) {
            Toast.makeText(this, R.string.ivtext_no_possibilities, Toast.LENGTH_SHORT).show();
            return;
        }

        addToRangeToClipboardIfSettingOn(ivScanResult);
        populateResultsBox(ivScanResult);
        boolean enableCompare = ScanContainer.scanContainer.prevScan != null;
        exResCompare.setEnabled(enableCompare);
        exResCompare.setTextColor(getColorC(enableCompare ? R.color.colorPrimary : R.color.unimportantText));

        moveOverlay(false); //we dont want overlay to stay on top if user had appraisal box
        transitionOverlayViewFromInputToResults();
    }

    /**
     * Makes the input components invisible, and makes the result components visible.
     */
    private void transitionOverlayViewFromInputToResults() {
        resultsBox.setVisibility(View.VISIBLE);
        inputBox.setVisibility(View.GONE);

        initialButtonsLayout.setVisibility(View.GONE);
        onCheckButtonsLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Adds the pokemon nickname to the ocr auto correction if the nickname does not match the pokemon name & does
     * not match an existing pokemon.
     * The method reads the OCR'd pokemon name from the variable pokemonName
     *
     * @param pokemon the pokemon to add the nickname to
     */
    private void rememberUserInputForPokemonNameIfNewNickname(Pokemon pokemon) {
        // TODO: Move this into an event listener that triggers when the user actually changes the selection.
        if (!pokemonName.equals(pokemon.name) && pokeInfoCalculator.get(pokemonName) == null) {
            putCorrection(pokemonName, pokemon.name);
        }
    }

    /**
     * Checks whether the user input a pokemon using the spinner or the text input on the input screen
     * null if no correct input was provided (user typed non-existant pokemon or spinner error)
     * If user typed in incorrect pokemon, a toast will be displayed.
     *
     * @return The pokemon the user selected/typed or null if user put wrong input
     */
    private Pokemon interpretWhichPokemonUserInput() {
        //below picks a pokemon from either the pokemon spinner or the user text input
        Pokemon pokemon;
        if (pokeInputSpinner.getVisibility() == View.VISIBLE) { //user picked pokemon from spinner
            //This could be pokemon = pokeInputSpinner.getSelectedItem(); if they didn't give it type Object.
            pokemon = pokeInputSpinnerAdapter.getItem(pokeInputSpinner.getSelectedItemPosition());
        } else { //user typed manually
            String userInput = autoCompleteTextView1.getText().toString();
            pokemon = pokeInfoCalculator.get(userInput);
            if (pokemon == null) { //no such pokemon was found, show error toast and abort showing results
                Toast.makeText(this, userInput + getString(R.string.wrong_pokemon_name_input),
                        Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        return pokemon;
    }

    /**
     * Checks if the app is in battery saver mode, and if the user hasnt set the setting to avoid deleting
     * screenshot, and then deletes the screenshot.
     */
    private void deleteScreenShotIfRequired() {
        if (batterySaver && screenShotPath.isPresent()) {
            if (GoIVSettings.getInstance(getBaseContext()).shouldDeleteScreenshots()) {
                screenShotHelper.deleteScreenShot(screenShotPath.get());
            }
        }
    }

    private void putCorrection(String ocredPokemonName, String correctedPokemonName) {
        corrector.putCorrection(ocredPokemonName, correctedPokemonName);
        SharedPreferences.Editor edit = sharedPref.edit();
        edit.putString(ocredPokemonName, correctedPokemonName);
        edit.apply();
    }

    /**
     * Refines the combinations in an ivscanresult by reading the input
     * in the appraisalbox and calling the appropriate methods in ivscanresults.refineX
     *
     * @param ivScanResult the scan result to refine
     */
    private void refineByAvailableAppraisalInfo(IVScanResult ivScanResult) {
        if (attCheckbox.isChecked() || defCheckbox.isChecked() || staCheckbox.isChecked()) {
            ivScanResult.refineByHighest(attCheckbox.isChecked(), defCheckbox.isChecked(), staCheckbox.isChecked());
        }

        if (appraisalPercentageRange.getSelectedItemPosition() != 0) {
            ivScanResult.refineByAppraisalPercentageRange(appraisalPercentageRange.getSelectedItemPosition());
        }
        if (appraisalIvRange.getSelectedItemPosition() != 0) {
            ivScanResult.refineByAppraisalIVRange(appraisalIvRange.getSelectedItemPosition());
        }
    }

    /**
     * Adds the iv range of the pokemon to the clipboard if the clipboard setting is on.
     */
    private void addToRangeToClipboardIfSettingOn(IVScanResult ivScanResult) {
        if (GoIVSettings.getInstance(getApplicationContext()).shouldCopyToClipboard()) {
            if (!ivScanResult.tooManyPossibilities) {
                String clipText = ivScanResult.getLowestIVCombination().percentPerfect + "-"
                        + ivScanResult.getHighestIVCombination().percentPerfect;
                ClipData clip = ClipData.newPlainText(clipText, clipText);
                clipboard.setPrimaryClip(clip);
            }
        }

    }

    /**
     * Initialises the autocompletetextview which allows people to search for pokemon names.
     */
    private void initializePokemonAutoCompleteTextView() {
        String[] pokeList = getResources().getStringArray(R.array.pokemon);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.autocomplete_pokemon_list_item,
                pokeList);
        autoCompleteTextView1.setAdapter(adapter);
        autoCompleteTextView1.setThreshold(1);
    }

    /**
     * Sets all the information in the result box.
     */
    private void populateResultsBox(IVScanResult ivScanResult) {
        ivScanResult.sortCombinations();
        populateResultsHeader(ivScanResult);


        if (ivScanResult.getCount() == 1) {
            populateSingleIVMatch(ivScanResult);
        } else { // More than a match
            populateMultipleIVMatch(ivScanResult);
        }
        setResultScreenPercentageRange(ivScanResult); //color codes the result
        adjustSeekbarsThumbs();

        hideSeeAllLinkIfFlagSet(ivScanResult);
        populateAdvancedInformation(ivScanResult);
        populatePrevScanNarrowing();
    }

    /**
     * Hides the "See all" iv possibilities link if the iv scan result reports that there are too many possibilities.
     *
     * @param ivScanResult The iv scan result to examine if it makes sense to have a "show all" button.
     */
    private void hideSeeAllLinkIfFlagSet(IVScanResult ivScanResult) {
        if (ivScanResult.tooManyPossibilities) {
            seeAllPossibilities.setVisibility(View.GONE);
        } else {
            seeAllPossibilities.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Adjusts expandedLevelSeekbar and expandedLevelSeekbar thumbs.
     * expandedLevelSeekbar - Adjustable single thumb seekbar to allow users to check for more Pokemon stats at
     * different Pokemon level
     * expandedLevelSeekbarBackground - Static double thumb seekbar as background to identify area of Pokemon stats
     * above Pokemon level at current trainer level
     */
    private void adjustSeekbarsThumbs() {
        // Set Seekbar max value to max Pokemon level at trainer level 40
        expandedLevelSeekbar.setMax(levelToSeekbarProgress(40));

        // Set Thumb value to current Pokemon level
        expandedLevelSeekbar.setProgress(levelToSeekbarProgress(estimatedPokemonLevel));

        // Set Seekbar Background max value to max Pokemon level at trainer level 40
        expandedLevelSeekbarBackground.setMax(levelToSeekbarProgress(40));

        // Set Thumb 1 drawable to an orange marker and value at the max possible Pokemon level at the current
        // trainer level
        expandedLevelSeekbarBackground.getThumb(0).setThumb(getDrawableC(R.drawable
                .orange_seekbar_thumb_marker));
        expandedLevelSeekbarBackground.getThumb(0).setValue(
                levelToSeekbarProgress(Data.trainerLevelToMaxPokeLevel(trainerLevel)));

        // Set Thumb 2 to invisible and value at max Pokemon level at trainer level 40
        expandedLevelSeekbarBackground.getThumb(1).setInvisibleThumb(true);
        expandedLevelSeekbarBackground.getThumb(1).setValue(levelToSeekbarProgress(40));

        // Set empty on touch listener to prevent changing values of Thumb 1
        expandedLevelSeekbarBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }

    /**
     * Shows the "refine by leveling up" part if he previous pokemon could be an upgraded version.
     */
    private void populatePrevScanNarrowing() {
        if (ScanContainer.scanContainer.isScanRefinable()) {
            refine_by_last_scan.setVisibility(View.VISIBLE);
            exResPrevScan.setText(String.format(getString(R.string.last_scan),
                    ScanContainer.scanContainer.getPrevScanName()));
        } else {
            refine_by_last_scan.setVisibility(View.GONE);
        }

    }

    /**
     * Shows the name and level of the pokemon in the results dialog.
     */
    private void populateResultsHeader(IVScanResult ivScanResult) {
        resultsPokemonName.setText(ivScanResult.pokemon.name);
        resultsPokemonLevel.setText(getString(R.string.level_num, ivScanResult.estimatedPokemonLevel));
    }

    /**
     * Populates the reuslt screen with the layout as if its multiple results.
     */
    private void populateMultipleIVMatch(IVScanResult ivScanResult) {
        llMaxIV.setVisibility(View.VISIBLE);
        llMinIV.setVisibility(View.VISIBLE);
        llSingleMatch.setVisibility(View.GONE);
        llMultipleIVMatches.setVisibility(View.VISIBLE);
        tvAvgIV.setText(getString(R.string.avg));
        if (ivScanResult.tooManyPossibilities) {
            resultsCombinations.setText(getString(R.string.too_many_iv_combinations));
        } else {
            resultsCombinations.setText(
                    String.format(getString(R.string.possible_iv_combinations), ivScanResult.iVCombinations.size()));
        }

        populateAllIvPossibilities(ivScanResult);

    }

    /**
     * Adds all options in the all iv possibilities list.
     */
    private void populateAllIvPossibilities(IVScanResult ivScanResult) {
        IVResultsAdapter ivResults = new IVResultsAdapter(ivScanResult);
        rvResults.setAdapter(ivResults);
    }

    /**
     * Populates the result screen with the layout as if it's a single result.
     */
    private void populateSingleIVMatch(IVScanResult ivScanResult) {
        llMaxIV.setVisibility(View.GONE);
        llMinIV.setVisibility(View.GONE);
        tvAvgIV.setText(getString(R.string.iv));
        resultsAttack.setText(String.valueOf(ivScanResult.iVCombinations.get(0).att));
        resultsDefense.setText(String.valueOf(ivScanResult.iVCombinations.get(0).def));
        resultsHP.setText(String.valueOf(ivScanResult.iVCombinations.get(0).sta));

        GuiUtil.setTextColorByIV(resultsAttack, ivScanResult.iVCombinations.get(0).att);
        GuiUtil.setTextColorByIV(resultsDefense, ivScanResult.iVCombinations.get(0).def);
        GuiUtil.setTextColorByIV(resultsHP, ivScanResult.iVCombinations.get(0).sta);

        llSingleMatch.setVisibility(View.VISIBLE);
        llMultipleIVMatches.setVisibility(View.GONE);
    }

    private int getSeekbarOffset() {
        return (int) (2 * estimatedPokemonLevel);
    }

    private double seekbarProgressToLevel(int progress) {
        return (progress + getSeekbarOffset()) / 2.0;
        //seekbar only supports integers, so the seekbar works between 2 and 80.
    }

    /**
     * @param level a valid pokemon level (hence <= 40).
     * @return a seekbar progress index.
     */
    private int levelToSeekbarProgress(double level) {
        return (int) (2 * level - getSeekbarOffset());
    }

    /**
     * Sets the growth estimate text boxes to correpond to the
     * pokemon evolution and level set by the user.
     */
    private void populateAdvancedInformation(IVScanResult ivScanResult) {
        double selectedLevel = seekbarProgressToLevel(expandedLevelSeekbar.getProgress());
        Pokemon selectedPokemon = initPokemonSpinnerIfNeeded(ivScanResult.pokemon);

        setEstimateCpTextBox(ivScanResult, selectedLevel, selectedPokemon);
        setEstimateHPTextBox(ivScanResult, selectedLevel, selectedPokemon);
        setPokemonPerfectionPercentageText(ivScanResult, selectedPokemon);
        setEstimateCostTextboxes(ivScanResult, selectedLevel, selectedPokemon);
        exResLevel.setText(String.valueOf(selectedLevel));
        setEstimateLevelTextColor(selectedLevel);

        setAndCalculatePokeSpamText(ivScanResult);
    }

    /**
     * Sets the pokemon perfection % text in the powerup and evolution results box.
     *
     * @param ivScanResult    The object containing the ivs to base current pokemon on.
     * @param selectedPokemon The pokemon to compare selected iv with max iv to.
     */
    private void setPokemonPerfectionPercentageText(IVScanResult ivScanResult, Pokemon selectedPokemon) {
        CPRange cpRange = pokeInfoCalculator.getCpRangeAtLevel(selectedPokemon, ivScanResult.lowAttack, ivScanResult
                .lowDefense, ivScanResult.lowStamina, ivScanResult.highAttack, ivScanResult.highDefense, ivScanResult
                .highStamina, 40);
        double maxCP = pokeInfoCalculator.getCpRangeAtLevel(selectedPokemon, 15, 15, 15, 15, 15, 15, 40).high;
        double perfection = (100.0 * cpRange.getAvg()) / maxCP;
        DecimalFormat df = new DecimalFormat("#.#");
        String perfectionString = df.format(perfection) + "%";
        exResultPercentPerfection.setText(perfectionString);
    }

    /**
     * Sets the "expected HP  textview" to the estimat HP in the powerup and evolution estimate box.
     *
     * @param ivScanResult  the ivscanresult of the current pokemon
     * @param selectedLevel The goal level the pokemon in ivScanresult pokemon should reach
     */
    private void setEstimateHPTextBox(IVScanResult ivScanResult, double selectedLevel, Pokemon selectedPokemon) {
        int newHP = pokeInfoCalculator.getHPAtLevel(ivScanResult, selectedLevel, selectedPokemon);
        int oldHP = pokeInfoCalculator.getHPAtLevel(ivScanResult, estimatedPokemonLevel, ivScanResult.pokemon);
        int hpDiff = newHP - oldHP;
        String sign = (hpDiff >= 0) ? "+" : ""; //add plus in front if positive.
        String hpText = newHP + " (" + sign + hpDiff + ")";
        exResultHP.setText(hpText);
    }

    /**
     * setAndCalculatePokeSpamText sets pokespamtext and makes it visible.
     *
     * @param ivScanResult IVScanResult object that contains the scan results, mainly needed to get candEvolutionCost
     *                     variable
     */
    private void setAndCalculatePokeSpamText(IVScanResult ivScanResult) {
        if (GoIVSettings.getInstance(getApplicationContext()).isPokeSpamEnabled()
                && ivScanResult.pokemon != null) {
            if (ivScanResult.pokemon.candyEvolutionCost < 0) {
                exResPokeSpam.setText(getString(R.string.pokespam_not_available));
                pokeSpamView.setVisibility(View.VISIBLE);
                return;
            }

            PokeSpam pokeSpamCalculator = new PokeSpam(pokemonCandy.or(0), ivScanResult.pokemon.candyEvolutionCost);
            String text = getString(R.string.pokespam_formatted_message,
                    pokeSpamCalculator.getTotalEvolvable(), pokeSpamCalculator.getEvolveRows(),
                    pokeSpamCalculator.getEvolveExtra());
            exResPokeSpam.setText(text);
            pokeSpamView.setVisibility(View.VISIBLE);
        } else {
            exResPokeSpam.setText("");
            pokeSpamView.setVisibility(View.GONE);
        }
    }


    /**
     * Sets the "expected cp textview" to (+x) or (-y) in the powerup and evolution estimate box depending on what's
     * appropriate.
     *
     * @param ivScanResult    the ivscanresult of the current pokemon
     * @param selectedLevel   The goal level the pokemon in ivScanresult pokemon should reach
     * @param selectedPokemon The goal pokemon evolution he ivScanresult pokemon should reach
     */
    private void setEstimateCpTextBox(IVScanResult ivScanResult, double selectedLevel, Pokemon selectedPokemon) {
        CPRange expectedRange = pokeInfoCalculator.getCpRangeAtLevel(selectedPokemon,
                ivScanResult.lowAttack, ivScanResult.lowDefense, ivScanResult.lowStamina,
                ivScanResult.highAttack, ivScanResult.highDefense, ivScanResult.highStamina, selectedLevel);
        int realCP = ivScanResult.scannedCP;
        int expectedAverage = expectedRange.getAvg();

        String exResultCPStr = String.valueOf(expectedAverage);

        int diffCP = expectedAverage - realCP;
        if (diffCP >= 0) {
            exResultCPStr += " (+" + diffCP + ")";
        } else {
            exResultCPStr += " (" + diffCP + ")";
        }
        exResultCP.setText(exResultCPStr);
    }

    /**
     * Sets the candy cost and stardust cost textfields in the powerup and evolution estimate box. The textviews are
     * populated with the cost in dust and candy required to go from the pokemon in ivscanresult to the desired
     * selecterdLevel and selectedPokemon.
     *
     * @param ivScanResult    The pokemon to base the estimate on.
     * @param selectedLevel   The level the pokemon needs to reach.
     * @param selectedPokemon The target pokemon. (example, ivScan pokemon can be weedle, selected can be beedrill.)
     */
    private void setEstimateCostTextboxes(IVScanResult ivScanResult, double selectedLevel, Pokemon selectedPokemon) {
        UpgradeCost cost = pokeInfoCalculator.getUpgradeCost(selectedLevel, estimatedPokemonLevel);
        int evolutionCandyCost = pokeInfoCalculator.getCandyCostForEvolution(ivScanResult.pokemon, selectedPokemon);
        String candyCostText = cost.candy + evolutionCandyCost + "";
        exResCandy.setText(candyCostText);
        exResStardust.setText(String.valueOf(cost.dust));
    }

    /**
     * Sets the text color of the level next to the slider in the estimate box to normal or orange depending on if
     * the user can level up the pokemon that high with his current trainer level. For example, if the user has
     * trainer level 20, then his pokemon can reach a max level of 21.5 - so any goalLevel above 21.5 would become
     * orange.
     *
     * @param selectedLevel The level to reach.
     */
    private void setEstimateLevelTextColor(double selectedLevel) {
        // If selectedLevel exeeds trainer capabilities then show text in orange
        if (selectedLevel > Data.trainerLevelToMaxPokeLevel(trainerLevel)) {
            exResLevel.setTextColor(getColorC(R.color.orange));
        } else {
            exResLevel.setTextColor(getColorC(R.color.importantText));
        }
    }

    /**
     * Initialize the pokemon spinner in the evolution and powerup box in the result window, and return picked pokemon.
     * <p/>
     * The method will populate the spinner with the correct pokemon evolution line, and disable the spinner if there's
     * the evolution line contains only one pokemon. The method will also select by default either the evolution of
     * the scanned pokemon (if there is one) or the pokemon itself.
     * <p/>
     * This method only does anything if it detects that the spinner was not previously initialized.
     *
     * @param scannedPokemon the pokemon to use for selecting a good default, if init is performed
     */
    private Pokemon initPokemonSpinnerIfNeeded(Pokemon scannedPokemon) {
        ArrayList<Pokemon> evolutionLine = pokeInfoCalculator.getEvolutionLine(scannedPokemon);
        extendedEvolutionSpinnerAdapter.updatePokemonList(evolutionLine);

        int spinnerSelectionIdx = extendedEvolutionSpinner.getSelectedItemPosition();

        if (spinnerSelectionIdx == -1) {
            // This happens at the beginning or after changing the pokemon list.
            //if initialising list, act as if scanned pokemon is marked
            for (int i = 0; i < evolutionLine.size(); i++) {
                if (evolutionLine.get(i).number == scannedPokemon.number) {
                    spinnerSelectionIdx = i;
                    break;
                }
            }
            if (!scannedPokemon.evolutions.isEmpty()) {
                //Equivalently, if this pokemon is not the last of its evolution line.
                spinnerSelectionIdx++;
            }
            //Invariant: evolutionLine.get(spinnerSelectionIdx).number == scannedPokemon.number., hence
            //evolutionLine.get(spinnerSelectionIdx) == scannedPokemon.
            extendedEvolutionSpinner.setSelection(spinnerSelectionIdx);
            extendedEvolutionSpinner.setEnabled(evolutionLine.size() > 1);
        }
        return evolutionLine.get(spinnerSelectionIdx);
    }

    /**
     * Fixes the three boxes that show iv range color and text.
     *
     * @param ivScanResult the scan result used to populate the TextViews
     */
    private void setResultScreenPercentageRange(IVScanResult ivScanResult) {
        int low = 0;
        int ave = 0;
        int high = 0;
        if (ivScanResult.iVCombinations.size() != 0) {
            low = ivScanResult.getLowestIVCombination().percentPerfect;
            ave = ivScanResult.getAveragePercent();
            high = ivScanResult.getHighestIVCombination().percentPerfect;
        }
        GuiUtil.setTextColorByPercentage(resultsMinPercentage, low);
        GuiUtil.setTextColorByPercentage(resultsAvePercentage, ave);
        GuiUtil.setTextColorByPercentage(resultsMaxPercentage, high);


        if (ivScanResult.iVCombinations.size() > 0) {
            resultsMinPercentage.setText(getString(R.string.percent, low));
            resultsAvePercentage.setText(getString(R.string.percent, ave));
            resultsMaxPercentage.setText(getString(R.string.percent, high));
        } else {
            String unknown_percent = getString(R.string.unknown_percent);
            resultsMinPercentage.setText(unknown_percent);
            resultsAvePercentage.setText(unknown_percent);
            resultsMaxPercentage.setText(unknown_percent);
        }
    }

    @OnClick({R.id.btnCancelInfo, R.id.btnCloseInfo})
    /**
     * resets the info dialogue to its default state
     */
    public void cancelInfoDialog() {
        hideInfoLayoutArcPointer();
        attCheckbox.setChecked(false);
        defCheckbox.setChecked(false);
        staCheckbox.setChecked(false);

        appraisalIvRange.setSelection(0);
        appraisalPercentageRange.setSelection(0);

        resetPokeflyStateMachine();
        resetInfoDialogue();
        if (!batterySaver) {
            setIVButtonDisplay(true);
        }
    }

    /**
     * Displays the all possibilities dialog.
     */
    @OnClick(R.id.tvSeeAllPossibilities)
    public void displayAllPossibilities() {
        resultsBox.setVisibility(View.GONE);
        allPossibilitiesBox.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.exResCompare)
    public void reduceScanByComparison() {
        IVScanResult thisScan = ScanContainer.scanContainer.currScan;
        IVScanResult prevScan = ScanContainer.scanContainer.prevScan;
        if (prevScan != null) {
            ArrayList<IVCombination> newResult = ScanContainer.scanContainer.getLatestIVIntersection();
            // Since the only change was an intersection, if the sizes are equal the content's also equal.
            boolean changed = newResult.size() != thisScan.iVCombinations.size();
            thisScan.iVCombinations = newResult;
            if (changed) {
                populateResultsBox(thisScan);
            } else {
                Toast.makeText(this, R.string.refine_no_progress, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Resets the floating window that contains the result and input dialogue.
     */
    private void resetInfoDialogue() {
        inputBox.setVisibility(View.VISIBLE);
        extendedEvolutionSpinner.setSelection(-1);
        resultsBox.setVisibility(View.GONE);
        allPossibilitiesBox.setVisibility(View.GONE);
    }

    /**
     * Reset service state so that a new pokemon info can be requested.
     */
    private void resetPokeflyStateMachine() {
        receivedInfo = false;
        infoShownSent = false;
    }

    /**
     * Goes back a section.
     */
    //TODO: Needs better implementation
    @OnClick(R.id.btnBackInfo)
    public void backToIvForm() {
        if (allPossibilitiesBox.getVisibility() == View.VISIBLE) {
            allPossibilitiesBox.setVisibility(View.GONE);
            resultsBox.setVisibility(View.VISIBLE);
        } else {
            allPossibilitiesBox.setVisibility(View.GONE);
            inputBox.setVisibility(View.VISIBLE);
            resultsBox.setVisibility(View.GONE);

            initialButtonsLayout.setVisibility(View.VISIBLE);
            onCheckButtonsLayout.setVisibility(View.GONE);
        }
        moveOverlayUpOrDownToMatchAppraisalBox();
        enableOrDisablePokeSpamBoxBasedOnSettings();
    }

    private void enableOrDisablePokeSpamBoxBasedOnSettings() {
        //enable/disable visibility based on PokeSpam enabled or not
        if (GoIVSettings.getInstance(getApplicationContext()).isPokeSpamEnabled()) {
            pokeSpamDialogInputContentBox.setVisibility(View.VISIBLE);
        } else {
            pokeSpamDialogInputContentBox.setVisibility(View.GONE);
        }
    }

    /**
     * showInfoLayout
     * Shows the info layout once a scan is complete. Allows the user to change any data and then
     * shows the final results.
     */
    private void showInfoLayout() {
        if (!infoShownReceived) {

            infoShownReceived = true;
            PokemonNameCorrector.PokeDist possiblePoke = corrector.getPossiblePokemon(pokemonName, candyName);
            initialButtonsLayout.setVisibility(View.VISIBLE);
            onCheckButtonsLayout.setVisibility(View.GONE);

            // set color based on similarity
            if (possiblePoke.dist == 0) {
                pokeInputSpinner.setBackgroundColor(Color.parseColor("#ddffdd"));
            } else if (possiblePoke.dist < 2) {
                pokeInputSpinner.setBackgroundColor(Color.parseColor("#ffffcc"));
            } else {
                pokeInputSpinner.setBackgroundColor(Color.parseColor("#ffcccc"));
            }

            resetToSpinner(); //always have the input as spinner as default

            autoCompleteTextView1.setText("");
            pokeInputSpinnerAdapter.updatePokemonList(
                    pokeInfoCalculator.getEvolutionLine(pokeInfoCalculator.get(possiblePoke.pokemonId)));
            int selection = pokeInputSpinnerAdapter.getPosition(pokeInfoCalculator.get(possiblePoke.pokemonId));
            pokeInputSpinner.setSelection(selection);

            pokemonHPEdit.setText(optionalIntToString(pokemonHP));
            pokemonCPEdit.setText(optionalIntToString(pokemonCP));
            pokemonCandyEdit.setText(optionalIntToString(pokemonCandy));

            showInfoLayoutArcPointer();
            setVisibility(inputAppraisalExpandBox, appraisalBox, false, false);
            positionHandler.setVisibility(appraisalBox.getVisibility());
            moveOverlayUpOrDownToMatchAppraisalBox(); //move the overlay to correct position regarding appraisal box
            adjustArcPointerBar(estimatedPokemonLevel);

            if (batterySaver) {
                infoShownReceived = false;
            }

            if (!GoIVSettings.getInstance(getBaseContext()).shouldShouldConfirmationDialogs()) {
                checkIv();
            }
        }
        enableOrDisablePokeSpamBoxBasedOnSettings();
    }

    private <T> String optionalIntToString(Optional<T> src) {
        return src.transform(new Function<T, String>() {
            @Override public String apply(T input) {
                return input.toString();
            }
        }).or("");
    }

    private void initOcr() {
        String extdir = getExternalFilesDir(null).toString();
        if (!new File(extdir + "/tessdata/eng.traineddata").exists()) {
            CopyUtils.copyAssetFolder(getAssets(), "tessdata", extdir + "/tessdata");
        }

        ocr = OcrHelper.init(extdir, displayMetrics.widthPixels, displayMetrics.heightPixels,
                pokeInfoCalculator.get(28).name,
                pokeInfoCalculator.get(31).name,
                GoIVSettings.getInstance(this).isPokeSpamEnabled());
    }


    /**
     * scanPokemon
     * Performs OCR on an image of a pokemon and sends the pulled info to PokeFly to display.
     *
     * @param pokemonImage   The image of the pokemon
     * @param screenShotPath The screenshot path if it is a file, used to delete once checked
     */
    private void scanPokemon(Bitmap pokemonImage, @NonNull Optional<String> screenShotPath) {
        //WARNING: this method *must* always send an intent at the end, no matter what, to avoid the application
        // hanging.
        Intent info = Pokefly.createNoInfoIntent();
        try {
            ScanResult res = ocr.scanPokemon(pokemonImage, trainerLevel);
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
    private void takeScreenshot() {
        Bitmap bmp = screen.grabScreen();
        if (bmp == null) {
            return;
        }
        scanPokemon(bmp, Optional.<String>absent());
        bmp.recycle();
    }

    /**
     * A picture was shared and needs to be processed. Process it and initiate UI.
     * IV Button was pressed, take screenshot and send back pokemon info.
     */
    private final BroadcastReceiver processBitmap = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bitmap bitmap = (Bitmap) intent.getParcelableExtra(KEY_BITMAP);
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
            scanPokemon(bitmap, screenShotPath);
            bitmap.recycle();
        }
    };

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
                        KEY_SEND_INFO_HP) && intent.hasExtra(KEY_SEND_INFO_LEVEL)) {
                    receivedInfo = true;

                    pokemonName = intent.getStringExtra(KEY_SEND_INFO_NAME);
                    candyName = intent.getStringExtra(KEY_SEND_INFO_CANDY);


                    @SuppressWarnings("unchecked") Optional<String> lScreenShotFile =
                            (Optional<String>) intent.getSerializableExtra(KEY_SEND_SCREENSHOT_FILE);
                    @SuppressWarnings("unchecked") Optional<Integer> lPokemonCP =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_CP);
                    @SuppressWarnings("unchecked") Optional<Integer> lPokemonHP =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_HP);
                    pokemonCP = lPokemonCP;
                    pokemonHP = lPokemonHP;
                    screenShotPath = lScreenShotFile;

                    @SuppressWarnings("unchecked") Optional<Integer> lcandyAmount =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_CANDY_AMOUNT);
                    pokemonCandy = lcandyAmount;

                    estimatedPokemonLevel = intent.getDoubleExtra(KEY_SEND_INFO_LEVEL, estimatedPokemonLevel);
                    if (estimatedPokemonLevel < 1.0) {
                        estimatedPokemonLevel = 1.0;
                    }

                    showInfoLayout();
                } else {
                    resetPokeflyStateMachine();
                }
            }
        }
    };

    /**
     * setIVButtonDisplay
     * Receiver called from MainActivity. Tells Pokefly to either show the IV Button (if on poke) or
     * hide the IV Button.
     */
    private void setIVButtonDisplay(boolean show) {
        if (show && !ivButtonShown && !infoShownSent) {
            windowManager.addView(ivButton, ivButtonParams);
            ivButtonShown = true;
        } else if (!show) {
            if (ivButtonShown) {
                windowManager.removeView(ivButton);
                ivButtonShown = false;
            }
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}

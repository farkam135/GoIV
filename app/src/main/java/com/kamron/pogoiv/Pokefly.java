package com.kamron.pogoiv;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.kamron.pogoiv.clipboard.ClipboardTokenHandler;
import com.kamron.pogoiv.logic.CPRange;
import com.kamron.pogoiv.logic.Data;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.PokeSpam;
import com.kamron.pogoiv.logic.Pokemon;
import com.kamron.pogoiv.logic.PokemonNameCorrector;
import com.kamron.pogoiv.logic.PokemonShareHandler;
import com.kamron.pogoiv.logic.ScanContainer;
import com.kamron.pogoiv.logic.ScanResult;
import com.kamron.pogoiv.logic.UpgradeCost;
import com.kamron.pogoiv.pokeflycomponents.GoIVNotificationManager;
import com.kamron.pogoiv.pokeflycomponents.IVPopupButton;
import com.kamron.pogoiv.pokeflycomponents.IVPreviewPrinter;
import com.kamron.pogoiv.pokeflycomponents.ScreenWatcher;
import com.kamron.pogoiv.widgets.IVResultsAdapter;
import com.kamron.pogoiv.widgets.PokemonSpinnerAdapter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Locale;

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
    private static final String ACTION_SEND_INFO = "com.kamron.pogoiv.ACTION_SEND_INFO";
    public static final String ACTION_STOP = "com.kamron.pogoiv.ACTION_STOP";

    private static final String KEY_TRAINER_LEVEL = "key_trainer_level";
    private static final String KEY_STATUS_BAR_HEIGHT = "key_status_bar_height";
    private static final String KEY_BATTERY_SAVER = "key_battery_saver";

    private static final String KEY_SEND_INFO_NAME = "key_send_info_name";
    private static final String KEY_SEND_INFO_TYPE = "key_send_info_type";
    private static final String KEY_SEND_INFO_CANDY = "key_send_info_candy";
    private static final String KEY_SEND_INFO_HP = "key_send_info_hp";
    private static final String KEY_SEND_INFO_CP = "key_send_info_cp";
    private static final String KEY_SEND_INFO_LEVEL = "key_send_info_level";
    private static final String KEY_SEND_SCREENSHOT_FILE = "key_send_screenshot_file";
    private static final String KEY_SEND_INFO_CANDY_AMOUNT = "key_send_info_candy_amount";
    private static final String KEY_SEND_UPGRADE_CANDY_COST = "key_send_upgrade_candy_cost";
    private static final String KEY_SEND_UNIQUE_ID = "key_send_unique_id";

    private static final String ACTION_PROCESS_BITMAP = "com.kamron.pogoiv.PROCESS_BITMAP";
    private static final String KEY_BITMAP = "bitmap";
    private static final String KEY_SCREENSHOT_FILE = "ss-file";

    private static final String PREF_USER_CORRECTIONS = "com.kamron.pogoiv.USER_CORRECTIONS";


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
    private GoIVSettings settings;


    private boolean infoShownSent = false;
    private boolean infoShownReceived = false;

    //Pokefly components
    private ScreenWatcher screenWatcher;
    private IVPopupButton ivButton;
    private ClipboardTokenHandler clipboardTokenHandler;
    private GoIVNotificationManager goIVNotificationManager;
    private IVPreviewPrinter ivPreviewPrinter;

    private ImageView arcPointer;
    private LinearLayout infoLayout;


    private PokeInfoCalculator pokeInfoCalculator;

    private AutoAppraisal autoAppraisal;

    //results pokemon picker auto complete
    @BindView(R.id.autoCompleteTextView1)
    AutoCompleteTextView autoCompleteTextView1;

    @BindView(R.id.pokePickerToggleSpinnerVsInput)
    Button pokePickerToggleSpinnerVsInput;

    @BindView(R.id.shareWithStorimod)
    ImageButton shareWithStorimod;

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
    @BindView(R.id.appraisalIVRangeGroup)
    RadioGroup appraisalIVRangeGroup;

    @BindView(R.id.appraisalIVRange4)
    RadioButton appraisalIVRange4;
    @BindView(R.id.appraisalIVRange3)
    RadioButton appraisalIVRange3;
    @BindView(R.id.appraisalIVRange2)
    RadioButton appraisalIVRange2;
    @BindView(R.id.appraisalIVRange1)
    RadioButton appraisalIVRange1;

    @BindView(R.id.attDefStaLayout)
    LinearLayout attDefStaLayout;
    @BindView(R.id.attCheckbox)
    CheckBox attCheckbox;
    @BindView(R.id.defCheckbox)
    CheckBox defCheckbox;
    @BindView(R.id.staCheckbox)
    CheckBox staCheckbox;

    @BindView(R.id.appraisalStatsGroup)
    RadioGroup appraisalStatsGroup;

    @BindView(R.id.appraisalStat4)
    RadioButton appraisalStat4;
    @BindView(R.id.appraisalStat3)
    RadioButton appraisalStat3;
    @BindView(R.id.appraisalStat2)
    RadioButton appraisalStat2;
    @BindView(R.id.appraisalStat1)
    RadioButton appraisalStat1;


    @BindView(R.id.positionHandler)
    ImageView positionHandler;

    private String pokemonName;
    private String pokemonType;
    private String candyName;
    private Optional<Integer> pokemonCandy = Optional.absent();
    private Optional<Integer> pokemonCP = Optional.absent();
    private Optional<Integer> pokemonHP = Optional.absent();
    private Optional<Integer> candyUpgradeCost = Optional.absent();
    private String pokemonUniqueID = "";
    private double estimatedPokemonLevel = 1.0;
    private @NonNull Optional<String> screenShotPath = Optional.absent();


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
        intent.putExtra(KEY_SEND_INFO_TYPE, scanResult.getPokemonType());
        intent.putExtra(KEY_SEND_INFO_CANDY, scanResult.getCandyName());
        intent.putExtra(KEY_SEND_INFO_HP, scanResult.getPokemonHP());
        intent.putExtra(KEY_SEND_INFO_CP, scanResult.getPokemonCP());
        intent.putExtra(KEY_SEND_INFO_LEVEL, scanResult.getEstimatedPokemonLevel());
        intent.putExtra(KEY_SEND_SCREENSHOT_FILE, filePath);
        intent.putExtra(KEY_SEND_INFO_CANDY_AMOUNT, scanResult.getPokemonCandyAmount());
        intent.putExtra(KEY_SEND_UPGRADE_CANDY_COST, scanResult.getUpgradeCandyCost());
        intent.putExtra(KEY_SEND_UNIQUE_ID, scanResult.getPokemonUniqueID());
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

    private String[] getPokemonNamesArray() {
        if (getResources().getBoolean(R.bool.use_default_pokemonsname_as_ocrstring)) {
            //If flag ON, force to use English strings as pokemon name for OCR.
            Resources res = getResources();
            Configuration conf = res.getConfiguration();
            Locale def = getResources().getConfiguration().locale;//Keep original locale
            conf.setLocale(new Locale("en"));
            res.updateConfiguration(conf, null);
            String[] rtn = res.getStringArray(R.array.pokemon);
            conf.setLocale(def);//Restore to original locale
            res.updateConfiguration(conf, null);
            return rtn;
        }
        return getResources().getStringArray(R.array.pokemon);
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

    public boolean isBatterySaver() {
        return batterySaver;
    }

    private String[] getPokemonDisplayNamesArray() {
        if (settings.isShowTranslatedPokemonName()) {
            //If pref ON, use translated strings as pokemon name.
            return getResources().getStringArray(R.array.pokemon);
        }
        //Otherwise, use default locale's pokemon name.
        return getPokemonNamesArray();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        running = true;

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_UPDATE_UI));

        settings = GoIVSettings.getInstance(this);
        pokeInfoCalculator = PokeInfoCalculator.getInstance(
                getPokemonNamesArray(),
                getPokemonDisplayNamesArray(),
                getResources().getIntArray(R.array.attack),
                getResources().getIntArray(R.array.defense),
                getResources().getIntArray(R.array.stamina),
                getResources().getIntArray(R.array.devolutionNumber),
                getResources().getIntArray(R.array.evolutionCandyCost),
                getResources().getIntArray(R.array.candyNames));
        displayMetrics = this.getResources().getDisplayMetrics();
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
        goIVNotificationManager = new GoIVNotificationManager(this);
        ivPreviewPrinter = new IVPreviewPrinter(this);
        clipboardTokenHandler = new ClipboardTokenHandler(this);

        if (ACTION_STOP.equals(intent.getAction())) {
            if (android.os.Build.VERSION.SDK_INT >= 24) {
                stopForeground(STOP_FOREGROUND_DETACH);
            }
            stopSelf();
            goIVNotificationManager.showPausedNotification();
        } else if (intent.hasExtra(KEY_TRAINER_LEVEL)) {
            trainerLevel = intent.getIntExtra(KEY_TRAINER_LEVEL, 1);
            statusBarHeight = intent.getIntExtra(KEY_STATUS_BAR_HEIGHT, 0);
            batterySaver = intent.getBooleanExtra(KEY_BATTERY_SAVER, false);
            createFlyingComponents();
            /* Assumes MainActivity initialized ScreenGrabber before starting this service. */
            if (!batterySaver) {
                screen = ScreenGrabber.getInstance();
                autoAppraisal = new AutoAppraisal(screen, ocr, this, attDefStaLayout,
                        attCheckbox, defCheckbox, staCheckbox,
                        appraisalIVRangeGroup, appraisalStatsGroup);
                screenWatcher = new ScreenWatcher(this, appraisalBox, autoAppraisal);
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

    /**
     * Creates the infolayout, ivbutton, arcpointer and arc adjuster.
     */
    private void createFlyingComponents() {
        createInfoLayout();
        ivButton = new IVPopupButton(this);
        createArcPointer();
        createArcAdjuster();
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
    public @ColorInt int getColorC(@ColorRes int id) {
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
            double originalWindowY;
            double initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        originalWindowY = newParams.y;
                        initialTouchY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        newParams.y = (int) (originalWindowY + (event.getRawY() - initialTouchY));
                        windowManager.updateViewLayout(infoLayout, newParams);
                        break;

                    case MotionEvent.ACTION_UP:
                        if (newParams.y != originalWindowY) {
                            saveWindowPosition(newParams.y);
                        } else {
                            Toast.makeText(Pokefly.this, R.string.position_handler_toast, Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
                return true;
            }
        });
    }

    /**
     * Saves the current Info Window location to shared preferences.
     *
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
     * Changes the text in the appraisal checkboxes depending on what team the player is on.
     */
    private void populateTeamAppraisalSpinners() {

        //Load the correct phrases from the text resources depending on what team is stored in app settings
        if (settings.playerTeam() == 0) { //mystic
            appraisalIVRange4.setText(R.string.mv4);
            appraisalIVRange3.setText(R.string.mv3);
            appraisalIVRange2.setText(R.string.mv2);
            appraisalIVRange1.setText(R.string.mv1);

            appraisalStat1.setText(R.string.ms1);
            appraisalStat2.setText(R.string.ms2);
            appraisalStat3.setText(R.string.ms3);
            appraisalStat4.setText(R.string.ms4);

        } else if (settings.playerTeam() == 1) { //valor

            appraisalIVRange4.setText(R.string.vv4);
            appraisalIVRange3.setText(R.string.vv3);
            appraisalIVRange2.setText(R.string.vv2);
            appraisalIVRange1.setText(R.string.vv1);

            appraisalStat1.setText(R.string.vs1);
            appraisalStat2.setText(R.string.vs2);
            appraisalStat3.setText(R.string.vs3);
            appraisalStat4.setText(R.string.vs4);
        } else { //instinct

            appraisalIVRange4.setText(R.string.iv4);
            appraisalIVRange3.setText(R.string.iv3);
            appraisalIVRange2.setText(R.string.iv2);
            appraisalIVRange1.setText(R.string.iv1);

            appraisalStat1.setText(R.string.is1);
            appraisalStat2.setText(R.string.is2);
            appraisalStat3.setText(R.string.is3);
            appraisalStat4.setText(R.string.is4);
        }

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

    @OnClick({R.id.shareWithStorimod})
    /**
     * Creates an intent to share the result of the pokemon scan, and closes the overlay.
     */
    public void shareScannedPokemonInformation() {
        PokemonShareHandler communicator = new PokemonShareHandler();
        communicator.spreadResultIntent(this, ScanContainer.scanContainer.currScan, pokemonUniqueID);
        cancelInfoDialog();
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
            newParams.y = sharedPref.getInt(APPRAISAL_WINDOW_POSITION, 0);
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


        IVScanResult ivScanResult = pokeInfoCalculator.getIVPossibilities(pokemon, estimatedPokemonLevel,
                pokemonHP.get(), pokemonCP.get());

        refineByAvailableAppraisalInfo(ivScanResult);

        //Dont run clipboard logic if scan failed - some tokens might crash the program.
        if (ivScanResult.iVCombinations.size() > 0) {
            addClipboardInfoIfSettingOn(ivScanResult);
        }
        populateResultsBox(ivScanResult);
        boolean enableCompare = ScanContainer.scanContainer.prevScan != null;
        exResCompare.setEnabled(enableCompare);
        exResCompare.setTextColor(getColorC(enableCompare ? R.color.colorPrimary : R.color.unimportantText));

        moveOverlay(false); //we dont want overlay to stay on top if user had appraisal box
        closeKeyboard();
        transitionOverlayViewFromInputToResults();
    }


    /**
     * Closes the android keyboard... But this method only works if focus is on a direct child of infolayout.
     * <p>
     * Why the fuck does android not have a good standard method for this.
     */
    private void closeKeyboard() {

        //Get a list of all views inside the infoLayout
        ArrayList<View> views = new ArrayList<>();
        for (int i = 0; i < infoLayout.getChildCount(); i++) {
            views.add(infoLayout.getChildAt(i));
        }

        //Tell each view inside infoLayout to close the keyboard if they currently have focus.
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        for (View view : views) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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
            } else {
                // reset spinner selecction to avoid a crash
                extendedEvolutionSpinner.setSelection(-1);
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
            if (settings.shouldDeleteScreenshots()) {
                screenShotHelper.deleteScreenShot(screenShotPath.get());
            }
        }
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

        ivScanResult.refineByAppraisalPercentageRange(getSelectedAppraiseIVRangeValue());

        ivScanResult.refineByAppraisalIVRange(getSelectedAppraiseStatRangeValue());

    }

    /**
     * Returns which value the user has selected related to the appraisal stat range.
     *
     * @return a number corresponding to which appraisalstat is selected.
     */
    private int getSelectedAppraiseStatRangeValue() {
        if (appraisalStat1.isChecked()) {
            return 1;
        }
        if (appraisalStat2.isChecked()) {
            return 2;
        }
        if (appraisalStat3.isChecked()) {
            return 3;
        }
        if (appraisalStat4.isChecked()) {
            return 4;
        }
        return 0;
    }

    /**
     * Returns which value the user has selected related to the appraisal iv % range.
     *
     * @return a number corresponding to which appraisalrange is selected.
     */
    private int getSelectedAppraiseIVRangeValue() {
        if (appraisalIVRange1.isChecked()) {
            return 1;
        }
        if (appraisalIVRange2.isChecked()) {
            return 2;
        }
        if (appraisalIVRange3.isChecked()) {
            return 3;
        }
        if (appraisalIVRange4.isChecked()) {
            return 4;
        }
        return 0;
    }




    /**
     * Adds the iv range of the pokemon to the clipboard if the clipboard setting is on.
     */
    public void addClipboardInfoIfSettingOn(IVScanResult ivScanResult) {
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



        String clipResult = "";
        IVScanResult singleIVScanResult = new IVScanResult(ivScanResult.pokemon, ivScanResult.estimatedPokemonLevel,
                ivScanResult.scannedCP);
        singleIVScanResult.addIVCombination(ivCombination.att, ivCombination.def, ivCombination.sta);
        clipResult = clipboardTokenHandler.getResults(singleIVScanResult, pokeInfoCalculator, true);


        Toast toast = Toast.makeText(this, String.format(getString(R.string.clipboard_copy_toast), clipResult),
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        ClipData clip = ClipData.newPlainText(clipResult, clipResult);
        clipboard.setPrimaryClip(clip);

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

        populateAdvancedInformation(ivScanResult);
        populatePrevScanNarrowing();
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
        resultsPokemonName.setText(ivScanResult.pokemon.toString());
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

        resultsCombinations.setText(
                String.format(getString(R.string.possible_iv_combinations), ivScanResult.iVCombinations.size()));


        populateAllIvPossibilities(ivScanResult);

    }

    /**
     * Adds all options in the all iv possibilities list.
     */
    private void populateAllIvPossibilities(IVScanResult ivScanResult) {
        IVResultsAdapter ivResults = new IVResultsAdapter(ivScanResult, this);
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
        CPRange cpRange = pokeInfoCalculator.getCpRangeAtLevel(selectedPokemon,
                ivScanResult.getCombinationLowIVs(), ivScanResult.getCombinationHighIVs(), 40);
        double maxCP = pokeInfoCalculator.getCpRangeAtLevel(selectedPokemon,
                IVCombination.MAX, IVCombination.MAX, 40).high;
        double perfection = (100.0 * cpRange.getFloatingAvg()) / maxCP;
        int difference = (int) (cpRange.getFloatingAvg() - maxCP);
        DecimalFormat df = new DecimalFormat("#.#");
        String sign = "";
        if (difference >= 0) {
            sign = "+";
        }
        String differenceString = "(" + sign + difference + ")";
        String perfectionString = df.format(perfection) + "% " + differenceString;
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
        if (settings.isPokeSpamEnabled()
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
                ivScanResult.getCombinationLowIVs(), ivScanResult.getCombinationHighIVs(), selectedLevel);
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

        resetAppraisalCheckBoxes();

        resetPokeflyStateMachine();
        resetInfoDialogue();
        if (!batterySaver) {
            autoAppraisal.reset();
            ivButton.setShown(true, infoShownSent);
        }
    }

    /**
     * toggles all the appraisal boxes to false.
     */
    private void resetAppraisalCheckBoxes() {

        attCheckbox.setChecked(false);
        defCheckbox.setChecked(false);
        staCheckbox.setChecked(false);

        appraisalIVRangeGroup.clearCheck();

        appraisalStatsGroup.clearCheck();
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
                addClipboardInfoIfSettingOn(thisScan);
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

        //Below code handles resetting appraisal box, and then expanding it if user has that setting enabled.
        if (appraisalBox.getVisibility() == View.VISIBLE) {
            toggleAppraisalBox();
        }

    }

    /**
     * Opens input appraisal expand box if setting for defaulting to expansion is on.
     */
    private void openAppraisalBoxIfSettingOn() {
        if (settings.shouldAutoOpenExpandedAppraise()) {
            setVisibility(inputAppraisalExpandBox, appraisalBox, true, true);
            positionHandler.setVisibility(appraisalBox.getVisibility());
            moveOverlayUpOrDownToMatchAppraisalBox();
        }
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
        showCandyTextBoxBasedOnSettings();
    }


    /**
     * showCandyTextBoxBasedOnSettings
     * Shows candy text box if pokespam is enabled
     * Will set the Text Edit box to use next action or done if its the last text box.
     */
    private void showCandyTextBoxBasedOnSettings() {
        //enable/disable visibility based on PokeSpam enabled or not
        if (settings.isPokeSpamEnabled()) {
            pokeSpamDialogInputContentBox.setVisibility(View.VISIBLE);
            pokemonHPEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            pokeSpamDialogInputContentBox.setVisibility(View.GONE);
            pokemonHPEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
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
            PokemonNameCorrector.PokeDist possiblePoke = new PokemonNameCorrector(PokeInfoCalculator.getInstance())
                    .getPossiblePokemon(pokemonName, candyName, candyUpgradeCost, pokemonType);
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
                    pokeInfoCalculator.getEvolutionLine(possiblePoke.pokemon));
            int selection = pokeInputSpinnerAdapter.getPosition(possiblePoke.pokemon);
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

            if (!settings.shouldShouldConfirmationDialogs()) {
                checkIv();
            }
        }
        showCandyTextBoxBasedOnSettings();
        openAppraisalBoxIfSettingOn();
    }

    private <T> String optionalIntToString(Optional<T> src) {
        return src.transform(new Function<T, String>() {
            @Override public String apply(T input) {
                return input.toString();
            }
        }).or("");
    }

    private void initOcr() {
        File externalFilesDir = getExternalFilesDir(null);
        if (externalFilesDir == null) {
            externalFilesDir = getFilesDir();
        }
        String extdir = externalFilesDir.toString();
        if (!new File(extdir + "/tessdata/eng.traineddata").exists()) {
            CopyUtils.copyAssetFolder(getAssets(), "tessdata", extdir + "/tessdata");
        }

        ocr = OcrHelper.init(extdir, displayMetrics.widthPixels, displayMetrics.heightPixels,
                pokeInfoCalculator.get(28).name,
                pokeInfoCalculator.get(31).name,
                settings);
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
    public void takeScreenshot() {
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
                    pokemonType = intent.getStringExtra(KEY_SEND_INFO_TYPE);
                    candyName = intent.getStringExtra(KEY_SEND_INFO_CANDY);


                    @SuppressWarnings("unchecked") Optional<String> lScreenShotFile =
                            (Optional<String>) intent.getSerializableExtra(KEY_SEND_SCREENSHOT_FILE);
                    @SuppressWarnings("unchecked") Optional<Integer> lPokemonCP =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_CP);
                    @SuppressWarnings("unchecked") Optional<Integer> lPokemonHP =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_HP);
                    @SuppressWarnings("unchecked") Optional<Integer> lCandyAmount =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_INFO_CANDY_AMOUNT);
                    @SuppressWarnings("unchecked") Optional<Integer> lCandyUpgradeCost =
                            (Optional<Integer>) intent.getSerializableExtra(KEY_SEND_UPGRADE_CANDY_COST);
                    @SuppressWarnings("unchecked") String lUniqueID =
                            (String) intent.getSerializableExtra(KEY_SEND_UNIQUE_ID);

                    screenShotPath = lScreenShotFile;
                    pokemonCP = lPokemonCP;
                    pokemonHP = lPokemonHP;
                    pokemonCandy = lCandyAmount;
                    candyUpgradeCost = lCandyUpgradeCost;
                    pokemonUniqueID = lUniqueID;

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


}

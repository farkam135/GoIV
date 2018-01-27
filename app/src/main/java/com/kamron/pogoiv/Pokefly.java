package com.kamron.pogoiv;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.FragmentManager;
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
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.kamron.pogoiv.Fragments.ResultFragment;
import com.kamron.pogoiv.clipboardlogic.ClipboardTokenHandler;
import com.kamron.pogoiv.pokeflycomponents.AutoAppraisal;
import com.kamron.pogoiv.pokeflycomponents.GoIVNotificationManager;
import com.kamron.pogoiv.pokeflycomponents.IVPopupButton;
import com.kamron.pogoiv.pokeflycomponents.IVPreviewPrinter;
import com.kamron.pogoiv.pokeflycomponents.ScreenWatcher;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.OcrHelper;
import com.kamron.pogoiv.scanlogic.CPRange;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.PokeSpam;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.PokemonNameCorrector;
import com.kamron.pogoiv.scanlogic.PokemonShareHandler;
import com.kamron.pogoiv.scanlogic.ScanContainer;
import com.kamron.pogoiv.scanlogic.ScanResult;
import com.kamron.pogoiv.scanlogic.UpgradeCost;
import com.kamron.pogoiv.utils.CopyUtils;
import com.kamron.pogoiv.utils.GuiUtil;
import com.kamron.pogoiv.utils.LevelRange;
import com.kamron.pogoiv.widgets.PokemonSpinnerAdapter;
import com.kamron.pogoiv.widgets.recyclerviews.adapters.IVResultsAdapter;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apptik.widget.MultiSlider;

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
    public static final String ACTION_STOP = "com.kamron.pogoiv.ACTION_STOP";

    private static final String KEY_TRAINER_LEVEL = "key_trainer_level";
    private static final String KEY_STATUS_BAR_HEIGHT = "key_status_bar_height";
    private static final String KEY_BATTERY_SAVER = "key_battery_saver";

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
    public GoIVSettings settings;
    private ResultFragment resultFragment;


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
    ImageButton pokePickerToggleSpinnerVsInput;


    private PokemonSpinnerAdapter pokeInputSpinnerAdapter;
    @BindView(R.id.spnPokemonName)
    Spinner pokeInputSpinner;



    @BindView(R.id.etCp)
    EditText pokemonCPEdit;
    @BindView(R.id.etHp)
    EditText pokemonHPEdit;
    @BindView(R.id.etCandy)
    EditText pokemonCandyEdit;
    @BindView(R.id.sbArcAdjust)
    SeekBar arcAdjustBar;
    @BindView(R.id.levelIndicator)
    TextView levelIndicator;
    @BindView(R.id.llButtonsInitial)
    LinearLayout initialButtonsLayout;
    @BindView(R.id.llButtonsOnCheck)
    LinearLayout onCheckButtonsLayout;


    // Layouts
    @BindView(R.id.inputBox)
    LinearLayout inputBox;
    @BindView(R.id.resultsBox)
    public LinearLayout resultsBox;
    @BindView(R.id.allPossibilitiesBox)
    public LinearLayout allPossibilitiesBox;

    @BindView(R.id.appraisalBox)
    LinearLayout appraisalBox;


    @BindView(R.id.inputAppraisalExpandBox)
    TextView inputAppraisalExpandBox;
    @BindView(R.id.eggRaidSwitch)
    Switch eggRaidSwitch;

    @BindView(R.id.defaultInputPart)
    LinearLayout defaultInputPart;


    //PokeSpam
    @BindView(R.id.llPokeSpamDialogInputContentBox)
    LinearLayout pokeSpamDialogInputContentBox;



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
    private Pokemon.Gender pokemonGender = Pokemon.Gender.N;
    public Optional<Integer> pokemonCandy = Optional.absent();
    private Optional<Integer> pokemonCP = Optional.absent();
    private Optional<Integer> pokemonHP = Optional.absent();
    private Optional<Integer> candyUpgradeCost = Optional.absent();
    public String pokemonUniqueID = "";
    public LevelRange estimatedPokemonLevelRange = new LevelRange(1.0);
    private @NonNull Optional<String> screenShotPath = Optional.absent();


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

    public boolean isBatterySaver() {
        return batterySaver;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        running = true;

        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(ACTION_UPDATE_UI));

        settings = GoIVSettings.getInstance(this);
        pokeInfoCalculator = PokeInfoCalculator.getInstance(settings, getResources());
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
                autoAppraisal = new AutoAppraisal(screen, this, attDefStaLayout,
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
        Drawable dot = ContextCompat.getDrawable(this, R.drawable.dot);
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
    private void setArcPointer(double pokeLevel) {

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
     * Creates the arc adjuster used to move the arc pointer in the scan screen.
     */
    private void createArcAdjuster() {
        //arcAdjustBar.setMax(Data.trainerLevelToMaxPokeLevelIndex(trainerLevel));
        arcAdjustBar.setMax(Data.trainerLevelToMaxPokeLevelIndex(40));

        arcAdjustBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                estimatedPokemonLevelRange = new LevelRange(Data.levelIdxToLevel(progress));
                setArcPointer(estimatedPokemonLevelRange.min);
                levelIndicator.setText(String.valueOf(estimatedPokemonLevelRange.toString()));
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
        LayoutInflater inflater = LayoutInflater.from(this);
        infoLayout = (LinearLayout) inflater.inflate(R.layout.dialog_info_window, null);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        ButterKnife.bind(this, infoLayout);

        createInputLayout();
        createResultLayout();


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

        resultFragment = ResultFragment.getInstance();
        resultFragment.initConnections(this);

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
            arrowDrawable = ContextCompat.getDrawable(this, R.drawable.arrow_expand);
        } else {
            boxVisibility = View.GONE;
            arrowDrawable = ContextCompat.getDrawable(this, R.drawable.arrow_collapse);
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



    @OnClick({R.id.inputAppraisalExpandBox})
    /**
     * Method called when user presses the text to expand the appraisal box on the input screen, also collapses the
     * default view, since only either the appraisal or the default view is visible
     */
    public void toggleAppraisalBox() {
        toggleVisibility(inputAppraisalExpandBox, appraisalBox, true);
        setInputBoxToMatchAppraisalBox();
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
        arcAdjustBar.setProgress(Data.maxPokeLevelToIndex(estimatedPokemonLevel));
    }

    @OnClick(R.id.btnDecrementLevel)
    public void decrementLevel() {
        estimatedPokemonLevelRange.dec();
        adjustArcPointerBar(estimatedPokemonLevelRange.min);
    }

    @OnClick(R.id.btnIncrementLevel)
    public void incrementLevel() {
        estimatedPokemonLevelRange.inc();
        adjustArcPointerBar(estimatedPokemonLevelRange.min);
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


        IVScanResult ivScanResult = pokeInfoCalculator.getIVPossibilities(pokemon, estimatedPokemonLevelRange,
                pokemonHP.get(), pokemonCP.get(), pokemonGender);

        refineByAvailableAppraisalInfo(ivScanResult);
        refineByEggRaidInformation(ivScanResult);

        //Dont run clipboard logic if scan failed - some tokens might crash the program.
        if (ivScanResult.iVCombinations.size() > 0) {
            addClipboardInfoIfSettingOn(ivScanResult);
        }
        resultFragment.populateFragmentInfos(ivScanResult);;

        moveOverlay(false); //we dont want overlay to stay on top if user had appraisal box
        closeKeyboard();
        transitionOverlayViewFromInputToResults();
    }

    /**
     * Deletes impossible combinations from the ivScanResult that comes from the knowledge that a pokemon was aquired
     * from an egg or a raid. Pokemon from eggs and raids cannot have stats lower than 10.
     *
     * @param ivScanResult The ivscanresult object to remove the impossible combinations from.
     */
    private void refineByEggRaidInformation(IVScanResult ivScanResult) {
        if (eggRaidSwitch.isChecked()) {
            ivScanResult.refineByEggRaid();
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
                resultFragment.resetEstimateSpinner();
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
     * Initialises the autocompletetextview which allows people to search for pokemon names.
     */
    private void initializePokemonAutoCompleteTextView() {
        String[] pokeList = getResources().getStringArray(R.array.pokemon);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.autocomplete_pokemon_list_item,
                pokeList);
        autoCompleteTextView1.setAdapter(adapter);
        autoCompleteTextView1.setThreshold(1);
    }


    @OnClick({R.id.btnCancelInfo, R.id.btnCloseInfo})
    /**
     * resets the info dialogue to its default state
     */
    public void cancelInfoDialog() {
        hideInfoLayoutArcPointer();

        resetAppraisalCheckBoxes();
        eggRaidSwitch.setChecked(false); //reset egg/raid checkbox

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
     * Resets the floating window that contains the result and input dialogue.
     */
    private void resetInfoDialogue() {
        inputBox.setVisibility(View.VISIBLE);
        resultFragment.resetEstimateSpinner();
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
     * If input is open, appraisal should be closed, and vice versa. This method shows the appraisal box if the input
     * is hidden, or hides the appraisal box if the input is visible.
     */
    private void setInputBoxToMatchAppraisalBox() {
        defaultInputPart.setVisibility(appraisalBox.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
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
            adjustArcPointerBar(estimatedPokemonLevelRange.min);

            if (batterySaver) {
                infoShownReceived = false;
            }

            if (!settings.shouldShouldConfirmationDialogs()) {
                checkIv();
            }
        }
        showCandyTextBoxBasedOnSettings();
        openAppraisalBoxIfSettingOn();
        setInputBoxToMatchAppraisalBox();
    }

    private <T> String optionalIntToString(Optional<T> src) {
        return src.transform(new Function<T, String>() {
            @Override
            public String apply(T input) {
                return input.toString();
            }
        }).or("");
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

        ocr = OcrHelper.init(extDir, pokeInfoCalculator, settings);
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

                    showInfoLayout();
                } else {
                    resetPokeflyStateMachine();
                }
            }
        }
    };
}

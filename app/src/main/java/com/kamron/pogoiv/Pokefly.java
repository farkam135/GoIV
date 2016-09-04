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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.LruCache;
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

import com.kamron.pogoiv.logic.CPRange;
import com.kamron.pogoiv.logic.Data;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;
import com.kamron.pogoiv.logic.ScanContainer;
import com.kamron.pogoiv.logic.ScanResult;
import com.kamron.pogoiv.logic.UpgradeCost;
import com.kamron.pogoiv.widgets.IVResultsAdapter;
import com.kamron.pogoiv.widgets.PokemonSpinnerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apptik.widget.MultiSlider;

/**
 * Created by Kamron on 7/25/2016.
 */

public class Pokefly extends Service {

    private static final String ACTION_SEND_INFO = "com.kamron.pogoiv.ACTION_SEND_INFO";

    private static final String KEY_TRAINER_LEVEL = "key_trainer_level";
    private static final String KEY_STATUS_BAR_HEIGHT = "key_status_bar_height";
    private static final String KEY_BATTERY_SAVER = "key_battery_saver";
    private static final String KEY_SCREENSHOT_URI = "key_screenshot_uri";

    private static final String KEY_SEND_INFO_NAME = "key_send_info_name";
    private static final String KEY_SEND_INFO_CANDY = "key_send_info_candy";
    private static final String KEY_SEND_INFO_HP = "key_send_info_hp";
    private static final String KEY_SEND_INFO_CP = "key_send_info_cp";
    private static final String KEY_SEND_INFO_LEVEL = "key_send_info_level";
    private static final String KEY_SEND_SCREENSHOT_DIR = "key_send_screenshot_dir";

    private static final String ACTION_PROCESS_BITMAP = "com.kamron.pogoiv.PROCESS_BITMAP";
    private static final String KEY_BITMAP = "bitmap";
    private static final String KEY_SS_FILE = "ss-file";

    private static final String PREF_USER_CORRECTIONS = "com.kamron.pogoiv.USER_CORRECTIONS";

    private static final int NOTIFICATION_REQ_CODE = 8959;

    private int trainerLevel = -1;
    private boolean batterySaver = false;
    private Uri screenshotUri;
    private String screenshotDir;

    private boolean receivedInfo = false;

    private WindowManager windowManager;
    private DisplayMetrics displayMetrics;
    ClipboardManager clipboard;
    private SharedPreferences sharedPref;
    private ScreenGrabber screen;
    private OCRHelper ocr;

    private Timer timer;
    private int areaX1;
    private int areaY1;
    private int areaX2;
    private int areaY2;


    private boolean infoShownSent = false;
    private boolean infoShownReceived = false;
    private boolean IVButtonShown = false;

    private ImageView ivButton;
    private ImageView arcPointer;
    private LinearLayout infoLayout;

    private PokeInfoCalculator pokeCalculator = null;

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

    // Refine by appraisal
    @BindView(R.id.attCheckbox)
    CheckBox attCheckbox;
    @BindView(R.id.defCheckbox)
    CheckBox defCheckbox;
    @BindView(R.id.staCheckbox)
    CheckBox staCheckbox;


    private String pokemonName;
    private String candyName;
    private int pokemonCP;
    private int pokemonHP;
    private double estimatedPokemonLevel = 1.0;

    private HashMap<String, String> userCorrections;
    /* We don't want memory usage to get out of hand for stuff that can be computed. */
    private LruCache<String, Pair<String, Integer>> cachedCorrections;

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
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            PixelFormat.TRANSPARENT);

    private final WindowManager.LayoutParams ivButtonParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    public static Intent createIntent(Activity activity, int trainerLevel, int statusBarHeight, boolean batterySaver,
                                      String screenshotDir, Uri screenshotUri) {
        Intent intent = new Intent(activity, Pokefly.class);
        intent.putExtra(KEY_TRAINER_LEVEL, trainerLevel);
        intent.putExtra(KEY_STATUS_BAR_HEIGHT, statusBarHeight);
        intent.putExtra(KEY_BATTERY_SAVER, batterySaver);
        if (!screenshotDir.isEmpty()) {
            intent.putExtra(KEY_SCREENSHOT_URI, screenshotUri.toString());
        }
        return intent;
    }

    public static Intent createNoInfoIntent() {
        return new Intent(ACTION_SEND_INFO);
    }

    public static void populateInfoIntent(Intent intent, ScanResult scanResult, String filePath) {
        intent.putExtra(KEY_SEND_INFO_NAME, scanResult.getPokemonName());
        intent.putExtra(KEY_SEND_INFO_CANDY, scanResult.getCandyName());
        intent.putExtra(KEY_SEND_INFO_HP, scanResult.getPokemonHP());
        intent.putExtra(KEY_SEND_INFO_CP, scanResult.getPokemonCP());
        intent.putExtra(KEY_SEND_INFO_LEVEL, scanResult.getEstimatedPokemonLevel());
        if (!filePath.isEmpty()) {
            intent.putExtra(KEY_SEND_SCREENSHOT_DIR, filePath);
        }
    }

    public static Intent createProcessBitmapIntent(Bitmap bitmap, String file) {
        Intent intent = new Intent(ACTION_PROCESS_BITMAP);
        intent.putExtra(KEY_BITMAP, bitmap);
        intent.putExtra(KEY_SS_FILE, file);
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
        displayMetrics = this.getResources().getDisplayMetrics();
        initOCR();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        LocalBroadcastManager.getInstance(this).registerReceiver(displayInfo, new IntentFilter(ACTION_SEND_INFO));
        LocalBroadcastManager.getInstance(this).registerReceiver(processBitmap,
                new IntentFilter(ACTION_PROCESS_BITMAP));
        pokeCalculator = PokeInfoCalculator.getInstance(
                getResources().getStringArray(R.array.Pokemon),
                getResources().getIntArray(R.array.attack),
                getResources().getIntArray(R.array.defense),
                getResources().getIntArray(R.array.stamina),
                getResources().getIntArray(R.array.DevolutionNumber),
                getResources().getIntArray(R.array.evolutionCandyCost));
        sharedPref = getSharedPreferences(PREF_USER_CORRECTIONS, Context.MODE_PRIVATE);
        userCorrections = new HashMap<>(pokeCalculator.getPokedex().size());
        loadUserCorrectionsFromPrefs();
        userCorrections.put("Sparky", pokeCalculator.get(132).name);
        userCorrections.put("Rainer", pokeCalculator.get(132).name);
        userCorrections.put("Pyro", pokeCalculator.get(132).name);
        cachedCorrections = new LruCache<>(pokeCalculator.getPokedex().size() * 2);
    }

    @SuppressWarnings("unchecked")
    private void loadUserCorrectionsFromPrefs() {
        userCorrections.putAll((Map<String, String>) sharedPref.getAll());
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(KEY_TRAINER_LEVEL)) {
            trainerLevel = intent.getIntExtra(KEY_TRAINER_LEVEL, 1);
            statusBarHeight = intent.getIntExtra(KEY_STATUS_BAR_HEIGHT, 0);
            batterySaver = intent.getBooleanExtra(KEY_BATTERY_SAVER, false);
            if (intent.hasExtra(KEY_SCREENSHOT_URI)) {
                screenshotUri = Uri.parse(intent.getStringExtra(KEY_SCREENSHOT_URI));
            }
            makeNotification(this);
            createInfoLayout();
            createIVButton();
            createArcPointer();
            createArcAdjuster();
            /* Assumes MainActivity initialized ScreenGrabber before starting this service. */
            if (!batterySaver) {
                screen = ScreenGrabber.getInstance();
                startPeriodicScreenScan();
            }
        }

        return START_STICKY;
    }

    private void startPeriodicScreenScan() {
        areaX1 = Math.round(displayMetrics.widthPixels / 24);  // these values used to get "white" left of "power up"
        areaY1 = (int) Math.round(displayMetrics.heightPixels / 1.24271845);
        areaX2 = (int) Math.round(
                displayMetrics.widthPixels / 1.15942029);  // these values used to get greenish color in transfer button
        areaY2 = (int) Math.round(displayMetrics.heightPixels / 1.11062907);
        final Handler handler = new Handler();
        timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        scanPokemonScreen();
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 750);
    }

    /**
     * scanPokemonScreen
     * Scans the device screen to check area1 for the white and area2 for the transfer button.
     * If both exist then the user is on the pokemon screen.
     */
    private void scanPokemonScreen() {
        Bitmap bmp = screen.grabScreen();
        if (bmp == null) {
            return;
        }

        if (bmp.getHeight() > bmp.getWidth()) {
            boolean shouldShow = bmp.getPixel(areaX1, areaY1) == Color.rgb(250, 250, 250) &&
                    bmp.getPixel(areaX2, areaY2) == Color.rgb(28, 135, 150);
            setIVButtonDisplay(shouldShow);
        }
        bmp.recycle();
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
        if (!batterySaver) {
            timer.cancel();
        }

        super.onDestroy();
        setIVButtonDisplay(false);
        hideInfoLayoutArcPointer();
        stopForeground(true);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(displayInfo);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(processBitmap);

        ocr.exit();
        //Now ocr contains an invalid instance hence let's clear it.
        ocr = null;
    }

    /**
     * makeNotification
     * Creates the GoIV notification
     */
    private void makeNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                NOTIFICATION_REQ_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(String.format(getString(R.string.notification_title), trainerLevel))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent);
        Notification n = builder.build();

        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        startForeground(NOTIFICATION_REQ_CODE, n);
    }

    /**
     * createArcPointer
     * Creates the arc pointer view and sets all the variables required to accurately overlay
     * pokemon go's arc pointer
     */
    private void createArcPointer() {
        arcParams.gravity = Gravity.TOP | Gravity.START;
        arcPointer = new ImageView(this);
        arcPointer.setImageResource(R.drawable.dot);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pointerHeight = getDrawable(R.drawable.dot).getIntrinsicHeight() / 2;
            pointerWidth = getDrawable(R.drawable.dot).getIntrinsicWidth() / 2;
        } else {
            pointerHeight = getResources().getDrawable(R.drawable.dot).getIntrinsicHeight() / 2;
            pointerWidth = getResources().getDrawable(R.drawable.dot).getIntrinsicWidth() / 2;
        }
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
     * createArcAdjuster
     * Creates the arc adjuster used to move the arc pointer in the scan screen
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
     * createIVButton
     * Creates the IV Button view
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
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        setIVButtonDisplay(false);
                        takeScreenshot();
                        receivedInfo = false;
                        infoShownSent = true;
                        infoShownReceived = false;
                        break;
                }
                return false;
            }
        });
    }

    /**
     * createInfoLayout
     * creates the info layout which contains all the scanned data views and allows for correction.
     */
    private void createInfoLayout() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        infoLayout = (LinearLayout) inflater.inflate(R.layout.dialog_info_window, null);
        layoutParams.gravity = Gravity.CENTER | Gravity.BOTTOM;
        ButterKnife.bind(this, infoLayout);

        pokeInputSpinnerAdapter = new PokemonSpinnerAdapter(this, R.layout.spinner_pokemon, new ArrayList<Pokemon>());
        pokeInputSpinner.setAdapter(pokeInputSpinnerAdapter);

        initializePokemonAutoCompleteTextView();

        extendedEvolutionSpinnerAdapter = new PokemonSpinnerAdapter(this, R.layout.spinner_evolution,
                new ArrayList<Pokemon>());
        extendedEvolutionSpinner.setAdapter(extendedEvolutionSpinnerAdapter);

        // Setting up Recyclerview for further use.
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvResults.hasFixedSize();

        rvResults.setLayoutManager(layoutManager);
        rvResults.setItemAnimator(new DefaultItemAnimator());

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

    public void resetToSpinner() {
        autoCompleteTextView1.setVisibility(View.GONE);
        pokeInputSpinner.setVisibility(View.VISIBLE);
    }

    private void toggleVisibility(TextView expanderText, LinearLayout expandedBox) {
        int boxVisibility;
        Drawable arrowDrawable;
        if (expandedBox.getVisibility() == View.VISIBLE) {
            boxVisibility = View.GONE;
            arrowDrawable = getResources().getDrawable(R.drawable.arrow_collapse);
        } else {
            boxVisibility = View.VISIBLE;
            arrowDrawable = getResources().getDrawable(R.drawable.arrow_expand);
        }
        expanderText.setCompoundDrawablesWithIntrinsicBounds(null, null, arrowDrawable, null);
        Animator arrowAnimator = ObjectAnimator.ofInt(arrowDrawable, "level", 0, 10000).setDuration(100);
        arrowAnimator.start();
        expandedBox.setVisibility(boxVisibility);
    }


    @OnClick({R.id.resultsMoreInformationText})
    public void toggleMoreResultsBox() {
        toggleVisibility(resultsMoreInformationText, expandedResultsBox);
    }

    @OnClick({R.id.inputAppraisalExpandBox})
    public void toggleAppraisalBox() {
        toggleVisibility(inputAppraisalExpandBox, appraisalBox);
    }

    public void adjustArcPointerBar(double estimatedPokemonLevel) {
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

    @OnClick(R.id.btnDecrementLevelExpanded)
    public void decrementLevelExpanded() {
        expandedLevelSeekbar.setProgress(expandedLevelSeekbar.getProgress() - 1);
        populateAdvancedInformation(ScanContainer.scanContainer.currScan);
    }

    @OnClick(R.id.btnCheckIv)
    public void checkIv() {

        // Check for valid parameters before attempting to do anything else.
        try {
            pokemonHP = Integer.parseInt(pokemonHPEdit.getText().toString());
            pokemonCP = Integer.parseInt(pokemonCPEdit.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.missing_inputs, Toast.LENGTH_SHORT).show();
            return;
        }

        if (batterySaver && !screenshotDir.isEmpty()) {
            if (GoIVSettings.getInstance(getBaseContext()).shouldDeleteScreenshots()) {
                getContentResolver().delete(screenshotUri, MediaStore.Files.FileColumns.DATA + "=?",
                        new String[]{screenshotDir});
            }
        }


        //below picks a pokemon from either the pokemon spinner or the user text input
        Pokemon pokemon;
        if (pokeInputSpinner.getVisibility() == View.VISIBLE) { //user picked pokemon from spinner
            String selectedPokemon = pokeInputSpinner.getSelectedItem().toString();
            pokemon = pokeCalculator.get(selectedPokemon);
        } else { //user typed manually
            String userInput = autoCompleteTextView1.getText().toString();
            pokemon = pokeCalculator.get(userInput);
            if (pokemon == null) { //no such pokemon was found, show error toast and abort showing results
                Toast.makeText(this, userInput + getString(R.string.wrongPokemonNameInput), Toast.LENGTH_SHORT).show();
                return;
            }

        }


        /* TODO: Should we set a size limit on that and throw away LRU entries? */
        /* TODO: Move this into an event listener that triggers when the user
         * actually changes the selection. */
        if (!pokemonName.equals(pokemon.name) && pokeCalculator.get(pokemonName) == null) {
            userCorrections.put(pokemonName, pokemon.name);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putString(pokemonName, pokemon.name);
            edit.apply();
        }
        IVScanResult ivScanResult = pokeCalculator.getIVPossibilities(pokemon, estimatedPokemonLevel, pokemonHP,
                pokemonCP);

        if (attCheckbox.isChecked() || defCheckbox.isChecked() || staCheckbox.isChecked()) {
            ivScanResult.refineByHighest(attCheckbox.isChecked(), defCheckbox.isChecked(), staCheckbox.isChecked());
        }

        // If no possible combinations, inform the user and abort.
        if (!ivScanResult.tooManyPossibilities && ivScanResult.getCount() == 0) {
            Toast.makeText(this, R.string.ivtext_no_possibilities, Toast.LENGTH_SHORT).show();
            return;
        }

        addToRangeToClipboardIfSettingOn(ivScanResult);
        populateResultsBox(ivScanResult);
        boolean enableCompare = ScanContainer.scanContainer.prevScan != null;
        exResCompare.setEnabled(enableCompare);
        exResCompare.setTextColor(
                getResources().getColor(enableCompare ? R.color.colorPrimary : R.color.unimportantText));
        resultsBox.setVisibility(View.VISIBLE);
        inputBox.setVisibility(View.GONE);

        initialButtonsLayout.setVisibility(View.GONE);
        onCheckButtonsLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Adds the iv range of the pokemon to the clipboard if the clipboard setting is on
     */
    private void addToRangeToClipboardIfSettingOn(IVScanResult ivScanResult) {
        if (GoIVSettings.getInstance(getApplicationContext()).shouldCopyToClipboard()) {
            if (!ivScanResult.tooManyPossibilities) {
                String clipText = ivScanResult.getLowestIVCombination().percentPerfect + "-" +
                        ivScanResult.getHighestIVCombination().percentPerfect;
                ClipData clip = ClipData.newPlainText(clipText, clipText);
                clipboard.setPrimaryClip(clip);
            }
        }

    }

    /**
     * Initialises the autocompletetextview which allows people to search for pokemon names
     */
    private void initializePokemonAutoCompleteTextView() {
        String[] pokeList = getResources().getStringArray(R.array.Pokemon);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.autocomplete_pokemon_list_item,
                pokeList);
        autoCompleteTextView1.setAdapter(adapter);
        autoCompleteTextView1.setThreshold(1);
    }

    /**
     * Sets all the information in the result box
     */
    private void populateResultsBox(IVScanResult ivScanResult) {
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
     * Adjusts expandedLevelSeekbar and expandedLevelSeekbar thumbs
     *
     * expandedLevelSeekbar is a single thumb seekbar
     * Seekbar should be max at possible Pokemon level at trainer level 40.
     * Thumb should be placed at current Pokemon level
     *
     * expandedLevelSeekbarBackground is a double thumb seekbar
     * Seekbar should be max at possible Pokemon at trainer level 40
     * Thumb 1 should be marked as an orange marker and placed at the max possible Pokemon level at the current
     * trainer level
     * Thumb 2 should be invisible and placed at the max
     */
    private void adjustSeekbarsThumbs() {
        expandedLevelSeekbar.setMax(levelToSeekbarProgress(40));
        expandedLevelSeekbar.setProgress(levelToSeekbarProgress(estimatedPokemonLevel));

        expandedLevelSeekbarBackground.setMax(levelToSeekbarProgress(40));
        expandedLevelSeekbarBackground.getThumb(0).setThumb(getResources().getDrawable(R.drawable
                .orange_seekbar_thumb_marker));
        expandedLevelSeekbarBackground.getThumb(0).setValue(
                levelToSeekbarProgress(Data.trainerLevelToMaxPokeLevel(trainerLevel)));
        expandedLevelSeekbarBackground.getThumb(1).setInvisibleThumb(true);
        expandedLevelSeekbarBackground.getThumb(1).setValue(levelToSeekbarProgress(40));
    }

    /**
     * Shows the "refine by leveling up" part if he previous pokemon could be an upgraded version
     */
    private void populatePrevScanNarrowing() {
        if (ScanContainer.scanContainer.canLastScanBePoweredUpPreviousScan()) {
            refine_by_last_scan.setVisibility(View.VISIBLE);
            exResPrevScan.setText(String.format(getString(R.string.last_scan),
                    ScanContainer.scanContainer.getPrevScanName()));
        } else {
            refine_by_last_scan.setVisibility(View.GONE);
        }

    }

    /**
     * shows the name and level of the pokemon in the results dialog
     */
    private void populateResultsHeader(IVScanResult ivScanResult) {
        resultsPokemonName.setText(ivScanResult.pokemon.name);
        resultsPokemonLevel.setText(getString(R.string.level) + " " + ivScanResult.estimatedPokemonLevel);
    }

    /**
     * populates the reuslt screen with the layout as if its multiple results
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
     * adds all options in the all iv possibilities list
     */
    private void populateAllIvPossibilities(IVScanResult ivScanResult) {
        IVResultsAdapter ivResults = new IVResultsAdapter(ivScanResult, this);
        rvResults.setAdapter(ivResults);
    }

    /**
     * populates the result screen with the layout as if it's a single result
     */
    private void populateSingleIVMatch(IVScanResult ivScanResult) {
        llMaxIV.setVisibility(View.GONE);
        llMinIV.setVisibility(View.GONE);
        tvAvgIV.setText("IV");
        resultsAttack.setText(String.valueOf(ivScanResult.iVCombinations.get(0).att));
        resultsDefense.setText(String.valueOf(ivScanResult.iVCombinations.get(0).def));
        resultsHP.setText(String.valueOf(ivScanResult.iVCombinations.get(0).sta));

        GUIUtil.setTextColorbyPercentage(resultsAttack,
                (int) Math.round(ivScanResult.iVCombinations.get(0).att * 100.0 / 15));
        GUIUtil.setTextColorbyPercentage(resultsDefense,
                (int) Math.round(ivScanResult.iVCombinations.get(0).def * 100.0 / 15));
        GUIUtil.setTextColorbyPercentage(resultsHP,
                (int) Math.round(ivScanResult.iVCombinations.get(0).sta * 100.0 / 15));

        llSingleMatch.setVisibility(View.VISIBLE);
        llMultipleIVMatches.setVisibility(View.GONE);
    }

    private int getSeekbarOffset() {
        return (int) (2 * estimatedPokemonLevel);
    }

    private double seekbarProgressToLevel(int progress) {
        return (progress + getSeekbarOffset()) /
                2.0;  //seekbar only supports integers, so the seekbar works between 2 and 80.
    }

    /**
     * @param level a valid pokemon level (hence <= 40).
     * @return a seekbar progress index.
     */
    private int levelToSeekbarProgress(double level) {
        return (int) (2 * level - getSeekbarOffset());
    }

    /**
     * sets the growth estimate text boxes to correpond to the
     * pokemon evolution and level set by the user
     */
    public void populateAdvancedInformation(IVScanResult ivScanResult) {
        double goalLevel = seekbarProgressToLevel(expandedLevelSeekbar.getProgress());
        int intSelectedPokemon =
                extendedEvolutionSpinner.getSelectedItemPosition(); //which pokemon is selected in the spinner
        ArrayList<Pokemon> evolutionLine = pokeCalculator.getEvolutionLine(ivScanResult.pokemon);

        Pokemon selectedPokemon;
        if (intSelectedPokemon == -1) {
            selectedPokemon = ivScanResult.pokemon;//if initialising list, act as if scanned pokemon is marked
            for (int i = 0; i < evolutionLine.size(); i++) {
                if (evolutionLine.get(i).number == selectedPokemon.number) {
                    intSelectedPokemon = i;
                    extendedEvolutionSpinner.setSelection(intSelectedPokemon);
                    break;
                }
            }
        } else {
            if (evolutionLine.size() > intSelectedPokemon) {
                selectedPokemon = evolutionLine.get(intSelectedPokemon);
            } else {
                selectedPokemon = evolutionLine.get(0);
            }

        }

        extendedEvolutionSpinner.setEnabled(extendedEvolutionSpinner.getCount() > 1);

        CPRange expectedRange = pokeCalculator.getCpRangeAtLevel(selectedPokemon,
                ivScanResult.lowAttack, ivScanResult.lowDefense, ivScanResult.lowStamina,
                ivScanResult.highAttack, ivScanResult.highDefense, ivScanResult.highStamina, goalLevel);
        int realCP = ivScanResult.scannedCP;
        int expectedAverage = (expectedRange.high + expectedRange.low) / 2;
        String exResultCPStr = String.valueOf(expectedAverage);

        int diffCP = expectedAverage - realCP;
        if (diffCP >= 0) {
            exResultCPStr += " (+" + diffCP + ")";
        } else {
            exResultCPStr += " (" + diffCP + ")";
        }
        exResultCP.setText(exResultCPStr);

        UpgradeCost cost = pokeCalculator.getUpgradeCost(goalLevel, estimatedPokemonLevel);
        int evolutionCandyCost = pokeCalculator.getCandyCostForEvolution(ivScanResult.pokemon, selectedPokemon);
        String candyCostText = cost.candy + evolutionCandyCost + "";
        exResCandy.setText(candyCostText);
        exResStardust.setText(String.valueOf(cost.dust));

        extendedEvolutionSpinnerAdapter.updatePokemonList(evolutionLine);
        exResLevel.setText(String.valueOf(goalLevel));

        // If goalLevel exeeds trainer capabilities then show text in orange
        if (goalLevel > Data.trainerLevelToMaxPokeLevel(trainerLevel)) {
            exResLevel.setTextColor(getResources().getColor(R.color.orange));
        } else {
            exResLevel.setTextColor(getResources().getColor(R.color.importantText));
        }
    }


    /**
     * fixes the three boxes that show iv range color and text
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
        GUIUtil.setTextColorbyPercentage(resultsMinPercentage, low);
        GUIUtil.setTextColorbyPercentage(resultsAvePercentage, ave);
        GUIUtil.setTextColorbyPercentage(resultsMaxPercentage, high);


        if (ivScanResult.iVCombinations.size() > 0) {
            resultsMinPercentage.setText(low + "%");
            resultsAvePercentage.setText(ave + "%");
            resultsMaxPercentage.setText(high + "%");
        } else {
            resultsMinPercentage.setText("?%");
            resultsAvePercentage.setText("?%");
            resultsMaxPercentage.setText("?%");
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

        resetPokeflyStateMachine();
        resetInfoDialogue();
        if (!batterySaver) {
            setIVButtonDisplay(true);
        }
    }

    /**
     * Displays the all possibilities dialog
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
            if (changed)
                populateResultsBox(thisScan);
            else
                Toast.makeText(this, R.string.refine_no_progress, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * resets the floating window that contains the result and input dialogue
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
        Intent resetIntent = MainActivity.createResetScreenshotIntent();
        LocalBroadcastManager.getInstance(Pokefly.this).sendBroadcast(resetIntent);
    }

    /**
     * Goes back a section
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
    }

    /**
     * showInfoLayout
     * Shows the info layout once a scan is complete. Allows the user to change any data and then
     * shows the final results.
     */
    private void showInfoLayout() {
        if (!infoShownReceived) {

            infoShownReceived = true;
            int[] possiblePoke = getPossiblePokemon(pokemonName, candyName);
            initialButtonsLayout.setVisibility(View.VISIBLE);
            onCheckButtonsLayout.setVisibility(View.GONE);

            // set color based on similarity
            if (possiblePoke[1] == 0) {
                pokeInputSpinner.setBackgroundColor(Color.parseColor("#ddffdd"));
            } else if (possiblePoke[1] < 2) {
                pokeInputSpinner.setBackgroundColor(Color.parseColor("#ffffcc"));
            } else {
                pokeInputSpinner.setBackgroundColor(Color.parseColor("#ffcccc"));
            }

            resetToSpinner();
            autoCompleteTextView1.setText("");
            pokeInputSpinnerAdapter.updatePokemonList(
                    pokeCalculator.getEvolutionLine(pokeCalculator.get(possiblePoke[0])));
            int selection = pokeInputSpinnerAdapter.getPosition(pokeCalculator.get(possiblePoke[0]));
            pokeInputSpinner.setSelection(selection);

            pokemonHPEdit.setText(String.valueOf(pokemonHP));
            pokemonCPEdit.setText(String.valueOf(pokemonCP));

            showInfoLayoutArcPointer();
            adjustArcPointerBar(estimatedPokemonLevel);

            if (batterySaver) {
                infoShownReceived = false;
            }

            if (!GoIVSettings.getInstance(getBaseContext()).shouldShouldConfirmationDialogs()) {
                checkIv();
            }
        }
    }

    /**
     * @return the likely pokemon number against the char sequence as well as the similarity
     */
    private int[] getPossiblePokemon(String poketext, String candytext) {
        int poketextDist = 0;
        int bestCandyMatch = Integer.MAX_VALUE;
        Pokemon p;

        /* If the user previous corrected this text, go with that. */
        if (userCorrections.containsKey(poketext)) {
            poketext = userCorrections.get(poketext);
        }

        /* If we already did similarity search for this, go with the cached value. */
        Pair<String, Integer> cached = cachedCorrections.get(poketext);
        if (cached != null) {
            poketext = cached.first;
            poketextDist = cached.second;
        }

        /* If the pokemon name was a perfect match, we are done. */
        p = pokeCalculator.get(poketext);
        if (p != null) {
            return new int[]{p.number, poketextDist};
        }

        /* If not, we limit the Pokemon search by candy name first since fewer valid candy names
         * options should mean fewer false matches. */
        p = pokeCalculator.get(candytext);

        /* If we can't find perfect candy match, do a distance/similarity based match */
        if (p == null) {
            for (Pokemon trypoke : pokeCalculator.getPokedex()) {
                /* Candy names won't match evolutions */
                if (trypoke.devoNumber != -1) {
                    continue;
                }

                int dist = trypoke.getDistanceCaseInsensitive(candytext);
                if (dist < bestCandyMatch) {
                    p = trypoke;
                    bestCandyMatch = dist;
                }
            }
        } else {
            bestCandyMatch = 0;
        }

        /* Search through all the pokemon with the same candy name and pick the one with the best
         * match to the pokemon name (not the candy name) */
        ArrayList<Pokemon> candylist = new ArrayList<>();
        candylist.add(p);
        candylist.addAll(p.evolutions);
        /* If the base pokemon has only one evolution, they we consider another level of evolution */
        if (p.evolutions.size() == 1) {
            candylist.addAll(p.evolutions.get(0).evolutions);
        }

        int bestMatch = Integer.MAX_VALUE;
        for (Pokemon candyp : candylist) {
            int dist = candyp.getDistance(poketext);
            if (dist < bestMatch) {
                p = candyp;
                bestMatch = dist;
            }
        }

        /* Adding the candy distance and the pokemon name distance gives a better idea of how much
         * guess is going on. */
        int dist = bestCandyMatch + bestMatch;

        /* Cache this correction. We don't really need to save this across launches. */
        cachedCorrections.put(poketext, new Pair<>(p.name, dist));

        return new int[]{p.number, dist};
    }

    private void initOCR() {
        String extdir = getExternalFilesDir(null).toString();
        if (!new File(extdir + "/tessdata/eng.traineddata").exists()) {
            CopyUtils.copyAssetFolder(getAssets(), "tessdata", extdir + "/tessdata");
        }

        ocr = OCRHelper.init(extdir, displayMetrics.widthPixels, displayMetrics.heightPixels,
                getResources().getString(R.string.pokemon029),
                getResources().getString(R.string.pokemon032));
    }


    /**
     * scanPokemon
     * Performs OCR on an image of a pokemon and sends the pulled info to PokeFly to display.
     *
     * @param pokemonImage The image of the pokemon
     * @param filePath     The screenshot path if it is a file, used to delete once checked
     */
    private void scanPokemon(Bitmap pokemonImage, String filePath) {
        //WARNING: this method *must* always send an intent at the end, no matter what, to avoid the application
        // hanging.
        Intent info = Pokefly.createNoInfoIntent();
        try {
            ScanResult res = ocr.scanPokemon(pokemonImage, trainerLevel);
            if (res.isFailed()) {
                Toast.makeText(Pokefly.this, getString(R.string.scan_pokemon_failed), Toast.LENGTH_SHORT).show();
            }
            Pokefly.populateInfoIntent(info, res, filePath);
        } finally {
            LocalBroadcastManager.getInstance(Pokefly.this).sendBroadcast(info);
        }
    }

    /**
     * takeScreenshot
     * Called by intent from pokefly, captures the screen and runs it through scanPokemon
     */
    private void takeScreenshot() {
        Bitmap bmp = screen.grabScreen();
        if (bmp == null) {
            return;
        }
        scanPokemon(bmp, "");
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
            String ss_file = intent.getStringExtra(KEY_SS_FILE);
            if (ss_file == null) {
                ss_file = "";
            }
            if (bitmap == null) {
                return;
            }
            scanPokemon(bitmap, ss_file);
            bitmap.recycle();
        }
    };

    /**
     * displayInfo
     * Receiver called once MainActivity's scan is complete, sets all pokemon info and shows the
     * info layout.
     */
    private BroadcastReceiver displayInfo = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!receivedInfo) {
                if (intent.hasExtra(KEY_SEND_INFO_NAME) && intent.hasExtra(KEY_SEND_INFO_CP) && intent.hasExtra(
                        KEY_SEND_INFO_HP) && intent.hasExtra(KEY_SEND_INFO_LEVEL)) {
                    receivedInfo = true;
                    pokemonName = intent.getStringExtra(KEY_SEND_INFO_NAME);
                    candyName = intent.getStringExtra(KEY_SEND_INFO_CANDY);
                    pokemonCP = intent.getIntExtra(KEY_SEND_INFO_CP, 0);
                    pokemonHP = intent.getIntExtra(KEY_SEND_INFO_HP, 0);
                    estimatedPokemonLevel = intent.getDoubleExtra(KEY_SEND_INFO_LEVEL, estimatedPokemonLevel);
                    if (estimatedPokemonLevel < 1.0) {
                        estimatedPokemonLevel = 1.0;
                    }
                    if (intent.hasExtra(KEY_SEND_SCREENSHOT_DIR)) {
                        screenshotDir = intent.getStringExtra(KEY_SEND_SCREENSHOT_DIR);
                    } else {
                        screenshotDir = "";
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
        if (show && !IVButtonShown && !infoShownSent) {
            windowManager.addView(ivButton, ivButtonParams);
            IVButtonShown = true;
        } else if (!show) {
            if (IVButtonShown) {
                windowManager.removeView(ivButton);
                IVButtonShown = false;
            }
        }
    }

    private int dpToPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}

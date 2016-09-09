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
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
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
import com.kamron.pogoiv.logic.PokemonNameCorrector;
import com.kamron.pogoiv.logic.ScanContainer;
import com.kamron.pogoiv.logic.ScanResult;
import com.kamron.pogoiv.logic.UpgradeCost;
import com.kamron.pogoiv.widgets.IVResultsAdapter;
import com.kamron.pogoiv.widgets.PokemonSpinnerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apptik.widget.MultiSlider;

/**
 * Currently, the central service in Pokemon Go, dealing with everything except
 * the initial activity.
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
    private ClipboardManager clipboard;
    private SharedPreferences sharedPref;
    private ScreenGrabber screen;
    private OcrHelper ocr;

    private Timer timer;
    private int areaX1;
    private int areaY1;
    private int areaX2;
    private int areaY2;


    private boolean infoShownSent = false;
    private boolean infoShownReceived = false;
    private boolean ivButtonShown = false;

    private ImageView ivButton;
    private ImageView arcPointer;
    private LinearLayout infoLayout;

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
        initOcr();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        LocalBroadcastManager.getInstance(this).registerReceiver(displayInfo, new IntentFilter(ACTION_SEND_INFO));
        LocalBroadcastManager.getInstance(this).registerReceiver(processBitmap,
                new IntentFilter(ACTION_PROCESS_BITMAP));
        pokeInfoCalculator = PokeInfoCalculator.getInstance(
                getResources().getStringArray(R.array.Pokemon),
                getResources().getIntArray(R.array.attack),
                getResources().getIntArray(R.array.defense),
                getResources().getIntArray(R.array.stamina),
                getResources().getIntArray(R.array.DevolutionNumber),
                getResources().getIntArray(R.array.evolutionCandyCost));
        sharedPref = getSharedPreferences(PREF_USER_CORRECTIONS, Context.MODE_PRIVATE);
        corrector = initCorrectorFromPrefs(pokeInfoCalculator, sharedPref);
    }

    @SuppressWarnings("unchecked")
    private static PokemonNameCorrector initCorrectorFromPrefs(PokeInfoCalculator pokeInfoCalculator,
                                                               SharedPreferences sharedPref) {
        return new PokemonNameCorrector(pokeInfoCalculator, (Map<String, String>) sharedPref.getAll());
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
            boolean shouldShow = bmp.getPixel(areaX1, areaY1) == Color.rgb(250, 250, 250)
                    && bmp.getPixel(areaX2, areaY2) == Color.rgb(28, 135, 150);
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
     * Creates the GoIV notification.
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

    private void toggleVisibility(TextView expanderText, LinearLayout expandedBox) {
        int boxVisibility;
        Drawable arrowDrawable;
        if (expandedBox.getVisibility() == View.VISIBLE) {
            boxVisibility = View.GONE;
            arrowDrawable = getDrawableC(R.drawable.arrow_collapse);
        } else {
            boxVisibility = View.VISIBLE;
            arrowDrawable = getDrawableC(R.drawable.arrow_expand);
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
    /**
     * Method called when user presses the text to expand the appraisal box on the input screen
     */
    public void toggleAppraisalBox() {
        toggleVisibility(inputAppraisalExpandBox, appraisalBox);
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
        } else {
            newParams.gravity = Gravity.BOTTOM;
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

        moveOverlay(appraisalBox.getVisibility() == View.VISIBLE);
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
            pokemonHP = Integer.parseInt(pokemonHPEdit.getText().toString());
            pokemonCP = Integer.parseInt(pokemonCPEdit.getText().toString());
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    @OnClick(R.id.btnCheckIv)
    /**
     * Method called when user presses "check iv" in the input screen, which takes the user to the result screen
     */
    public void checkIv() {
        //warn user and stop calculation if scan/input failed/is wrong
        if (!parseNumericInputs()) {
            Toast.makeText(this, R.string.missing_inputs, Toast.LENGTH_SHORT).show();
            return;
        }
        deleteScreenshotIfIShould();

        Pokemon pokemon = interpretWhichPokemonUserInput();
        if (pokemon == null) {
            return;
        }

        rememberUserInputForPokemonNameIfNewNickname(pokemon);

        IVScanResult ivScanResult = pokeInfoCalculator.getIVPossibilities(pokemon, estimatedPokemonLevel, pokemonHP,
                pokemonCP);

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
            String selectedPokemon = pokeInputSpinner.getSelectedItem().toString();
            pokemon = pokeInfoCalculator.get(selectedPokemon);
        } else { //user typed manually
            String userInput = autoCompleteTextView1.getText().toString();
            pokemon = pokeInfoCalculator.get(userInput);
            if (pokemon == null) { //no such pokemon was found, show error toast and abort showing results
                Toast.makeText(this, userInput + getString(R.string.wrongPokemonNameInput), Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        return pokemon;
    }

    /**
     * Checks if the app is in battery saver mode, and if the user hasnt set the setting to avoid deleting
     * screenshot, and then deletes the screenshot.
     */
    private void deleteScreenshotIfIShould() {
        if (batterySaver && !screenshotDir.isEmpty()) {
            if (GoIVSettings.getInstance(getBaseContext()).shouldDeleteScreenshots()) {
                getContentResolver().delete(screenshotUri, MediaStore.Files.FileColumns.DATA + "=?",
                        new String[]{screenshotDir});
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
        String[] pokeList = getResources().getStringArray(R.array.Pokemon);
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
     * Adjusts expandedLevelSeekbar and expandedLevelSeekbar thumbs
     * <p/>
     * expandedLevelSeekbar is a single thumb seekbar
     * Seekbar should be max at possible Pokemon level at trainer level 40.
     * Thumb should be placed at current Pokemon level
     * <p/>
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
        expandedLevelSeekbarBackground.getThumb(0).setThumb(getDrawableC(R.drawable
                .orange_seekbar_thumb_marker));
        expandedLevelSeekbarBackground.getThumb(0).setValue(
                levelToSeekbarProgress(Data.trainerLevelToMaxPokeLevel(trainerLevel)));
        expandedLevelSeekbarBackground.getThumb(1).setInvisibleThumb(true);
        expandedLevelSeekbarBackground.getThumb(1).setValue(levelToSeekbarProgress(40));
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
        double goalLevel = seekbarProgressToLevel(expandedLevelSeekbar.getProgress());
        int intSelectedPokemon =
                extendedEvolutionSpinner.getSelectedItemPosition(); //which pokemon is selected in the spinner
        ArrayList<Pokemon> evolutionLine = pokeInfoCalculator.getEvolutionLine(ivScanResult.pokemon);

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

        CPRange expectedRange = pokeInfoCalculator.getCpRangeAtLevel(selectedPokemon,
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

        UpgradeCost cost = pokeInfoCalculator.getUpgradeCost(goalLevel, estimatedPokemonLevel);
        int evolutionCandyCost = pokeInfoCalculator.getCandyCostForEvolution(ivScanResult.pokemon, selectedPokemon);
        String candyCostText = cost.candy + evolutionCandyCost + "";
        exResCandy.setText(candyCostText);
        exResStardust.setText(String.valueOf(cost.dust));

        extendedEvolutionSpinnerAdapter.updatePokemonList(evolutionLine);
        exResLevel.setText(String.valueOf(goalLevel));

        // If goalLevel exeeds trainer capabilities then show text in orange
        if (goalLevel > Data.trainerLevelToMaxPokeLevel(trainerLevel)) {
            exResLevel.setTextColor(getColorC(R.color.orange));
        } else {
            exResLevel.setTextColor(getColorC(R.color.importantText));
        }
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
        Intent resetIntent = MainActivity.createResetScreenshotIntent();
        LocalBroadcastManager.getInstance(Pokefly.this).sendBroadcast(resetIntent);
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

            pokemonHPEdit.setText(String.valueOf(pokemonHP));
            pokemonCPEdit.setText(String.valueOf(pokemonCP));

            showInfoLayoutArcPointer();
            moveOverlayUpOrDownToMatchAppraisalBox(); //move the overlay to correct position regarding appraisal box
            adjustArcPointerBar(estimatedPokemonLevel);

            if (batterySaver) {
                infoShownReceived = false;
            }

            if (!GoIVSettings.getInstance(getBaseContext()).shouldShouldConfirmationDialogs()) {
                checkIv();
            }
        }
    }

    private void initOcr() {
        String extdir = getExternalFilesDir(null).toString();
        if (!new File(extdir + "/tessdata/eng.traineddata").exists()) {
            CopyUtils.copyAssetFolder(getAssets(), "tessdata", extdir + "/tessdata");
        }

        ocr = OcrHelper.init(extdir, displayMetrics.widthPixels, displayMetrics.heightPixels,
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
     * Called by intent from pokefly, captures the screen and runs it through scanPokemon.
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

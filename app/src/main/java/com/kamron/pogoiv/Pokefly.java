package com.kamron.pogoiv;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.LruCache;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import timber.log.Timber;

/**
 * Created by Kamron on 7/25/2016.
 */

public class Pokefly extends Service {

    private static final String ACTION_SEND_INFO = "action_send_info";

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

    private static final String ACTION_PROCESS_BITMAP = "process-bitmap";
    private static final String KEY_BITMAP = "bitmap";
    private static final String KEY_SS_FILE = "ss-file";

    private static final String PREF_USER_CORRECTIONS = "com.kamron.pogoiv.USER_CORRECTIONS";


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

    private ImageView IVButton;
    private ImageView arcPointer;
    private LinearLayout infoLayout;

    private PokeInfoCalculator pokeCalculator = null;

    @BindView(R.id.tvSeeAllPossibilities)
    TextView seeAllPossibilities;
    @BindView(R.id.spnPokemonName)
    Spinner pokemonList;
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

    // Result data
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
    @BindView(R.id.resultsMoreInformationArrow)
    TextView resultsMoreInformationArrow;
    @BindView(R.id.resultsMoreInformationText)
    TextView resultsMoreInformationText;
    @BindView(R.id.expandedLevelSeekbar)
    SeekBar expandedLevelSeekbar;
    @BindView(R.id.extendedEvolutionSpinner)
    Spinner extendedEvolutionSpinner;
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


    @BindView(R.id.allPosAtt)
    LinearLayout allPosAtt;
    @BindView(R.id.allPosDef)
    LinearLayout allPosDef;
    @BindView(R.id.allPosSta)
    LinearLayout allPosSta;
    @BindView(R.id.allPosPercent)
    LinearLayout allPosPercent;

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
    private LruCache<String, String> cachedCorrections;

    private PokemonSpinnerAdapter pokeAdapter;
    private PokemonSpinnerAdapter pokeEvolutionAdapter;

    private final WindowManager.LayoutParams arcParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT);
    private int arcInitialY = 0;
    private int radius = 0;
    private int pointerHeight = 0;
    private int pointerWidth = 0;
    private int statusBarHeight = 0;
    private int arcCenter = 0;

    private final WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN,
            PixelFormat.TRANSPARENT);

    private final WindowManager.LayoutParams IVButonParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);

    public static Intent createIntent(Activity activity, int trainerLevel, int statusBarHeight, boolean batterySaver, String screenshotDir, Uri screenshotUri) {
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

    public static void populateInfoIntent(Intent intent, String pokemonName, String candyName, int pokemonHP, int pokemonCP, double estimatedPokemonLevel, String filePath) {
        intent.putExtra(KEY_SEND_INFO_NAME, pokemonName);
        intent.putExtra(KEY_SEND_INFO_CANDY, candyName);
        intent.putExtra(KEY_SEND_INFO_HP, pokemonHP);
        intent.putExtra(KEY_SEND_INFO_CP, pokemonCP);
        intent.putExtra(KEY_SEND_INFO_LEVEL, estimatedPokemonLevel);
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
        //Display disp = windowManager.getDefaultDisplay();
        //disp.getRealMetrics(displayMetrics);
        //System.out.println("New Device:" + displayMetrics.widthPixels + "," + displayMetrics.heightPixels + "," + displayMetrics.densityDpi + "," + displayMetrics.density);

        LocalBroadcastManager.getInstance(this).registerReceiver(displayInfo, new IntentFilter(ACTION_SEND_INFO));
        LocalBroadcastManager.getInstance(this).registerReceiver(processBitmap, new IntentFilter(ACTION_PROCESS_BITMAP));
        pokeCalculator = new PokeInfoCalculator(
                getResources().getStringArray(R.array.Pokemon),
                getResources().getIntArray(R.array.attack),
                getResources().getIntArray(R.array.defense),
                getResources().getIntArray(R.array.stamina),
                getResources().getIntArray(R.array.DevolutionNumber),
                getResources().getIntArray(R.array.evolutionCandyCost));
        sharedPref = getSharedPreferences(PREF_USER_CORRECTIONS, Context.MODE_PRIVATE);
        userCorrections = new HashMap<>(pokeCalculator.pokedex.size());
        userCorrections.putAll((Map<String, String>) sharedPref.getAll());
        userCorrections.put("Sparky", pokeCalculator.get(132).name);
        userCorrections.put("Rainer", pokeCalculator.get(132).name);
        userCorrections.put("Pyro", pokeCalculator.get(132).name);
        cachedCorrections = new LruCache<>(pokeCalculator.pokedex.size() * 2);
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
            makeNotification(Pokefly.this);
            createInfoLayout();
            createIVButton();
            createArcPointer();
            createArcAdjuster();
            /* Assumes MainActivity initialized ScreenGrabber before starting this service. */
            if (!batterySaver) {
                screen = ScreenGrabber.init(null, null, null);
                startPeriodicScreenScan();
            }
        }

        return START_STICKY;
    }

    private void startPeriodicScreenScan() {
        areaX1 = Math.round(displayMetrics.widthPixels / 24);  // these values used to get "white" left of "power up"
        areaY1 = (int) Math.round(displayMetrics.heightPixels / 1.24271845);
        areaX2 = (int) Math.round(displayMetrics.widthPixels / 1.15942029);  // these values used to get greenish color in transfer button
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
            boolean shouldShow = bmp.getPixel(areaX1, areaY1) == Color.rgb(250, 250, 250) && bmp.getPixel(areaX2, areaY2) == Color.rgb(28, 135, 150);
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
    }

    /**
     * makeNotification
     * Creates the GoIV notification
     *
     * @param context
     */
    private void makeNotification(Context context) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context,
                8959, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context)
                .setContentTitle(String.format(getString(R.string.notification_title), trainerLevel))
                .setContentText(getString(R.string.notification_text))
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent);
        Notification n = builder.build();

        n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        startForeground(8959, n);
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

        arcCenter = (int) ((displayMetrics.widthPixels * 0.5) - pointerWidth);
        arcInitialY = (int) Math.floor(displayMetrics.heightPixels / 2.803943) - pointerHeight - statusBarHeight; // 913 - pointerHeight - statusBarHeight; //(int)Math.round(displayMetrics.heightPixels / 6.0952381) * -1; //dpToPx(113) * -1; //(int)Math.round(displayMetrics.heightPixels / 6.0952381) * -1; //-420;
        if (displayMetrics.heightPixels == 2392) {
            arcInitialY--;
        } else if (displayMetrics.heightPixels == 1920) {
            arcInitialY++;
        }

        radius = (int) Math.round(displayMetrics.heightPixels / 4.3760683); //dpToPx(157); //(int)Math.round(displayMetrics.heightPixels / 4.37606838); //(int)Math.round(displayMetrics.widthPixels / 2.46153846); //585;
        if (displayMetrics.heightPixels == 1776 || displayMetrics.heightPixels == 960) {
            radius++;
        }
    }


    /**
     * setArcPointer
     * Sets the arc pointer to the specified degree.
     *
     * @param pokeLevel The pokemon level to set the arc pointer to.
     */
    private void setArcPointer(double pokeLevel) {
//        if (angleInDegrees > 1.0 && trainerLevel < 30) {
//            angleInDegrees -= 0.5;
//        }
//        else if(trainerLevel >= 30){
//            angleInDegrees += 0.5;
//        }
//
//        double angleInRadians = (angleInDegrees + 180) * Math.PI / 180.0;
        int index = Data.levelToLevelIdx(pokeLevel);
        arcParams.x = Data.arcX[index] - pointerWidth; //(int) (arcCenter + (radius * Math.cos(angleInRadians)));
        arcParams.y = Data.arcY[index] - pointerHeight - statusBarHeight; //(int) (arcInitialY + (radius * Math.sin(angleInRadians)));
        //System.out.println("Pointer X: "  + arcParams.x);
        //System.out.println("Pointer Y: "  + arcParams.y);
        //System.out.println(arcParams.x + "," + arcParams.y);
        windowManager.updateViewLayout(arcPointer, arcParams);
    }

    /**
     * createArcAdjuster
     * Creates the arc adjuster used to move the arc pointer in the scan screen
     */
    private void createArcAdjuster() {
        arcAdjustBar.setMax(Math.min(trainerLevel * 2 + 1, 79));

        arcAdjustBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                estimatedPokemonLevel = 1 + (progress * 0.5);
                setArcPointer(estimatedPokemonLevel);
                //setArcPointer((Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / Data.CpM[trainerLevel * 2 - 2]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        //windowManager.addView(arcAdjustBar,arcParams);
    }

    /**
     * createIVButton
     * Creates the IV Button view
     */
    private void createIVButton() {
        IVButton = new ImageView(this);
        IVButton.setImageResource(R.drawable.button);

        IVButonParams.gravity = Gravity.BOTTOM | Gravity.START;
        IVButonParams.x = dpToPx(20); //(int)Math.round(displayMetrics.widthPixels / 20.5714286);
        IVButonParams.y = dpToPx(15); //(int)Math.round(displayMetrics.heightPixels / 38.5714286);

        IVButton.setOnTouchListener(new View.OnTouchListener() {
            //private WindowManager.LayoutParams paramsF = IVButonParams;
            //private int initialX;
            //private int initialY;
            //private float initialTouchX;
            //private float initialTouchY;

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

        //windowManager.addView(IVButton, IVButonParams);
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

        pokeAdapter = new PokemonSpinnerAdapter(this, R.layout.spinner_pokemon, pokeCalculator.pokedex);
        pokemonList.setAdapter(pokeAdapter);

        pokeEvolutionAdapter = new PokemonSpinnerAdapter(this, R.layout.spinner_evolution, new ArrayList<Pokemon>());
        extendedEvolutionSpinner.setAdapter(pokeEvolutionAdapter);

        expandedLevelSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    populateAdvancedInformation(IVScanResult.scanContainer.oneScanAgo);
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
                populateAdvancedInformation(IVScanResult.scanContainer.oneScanAgo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                populateAdvancedInformation(IVScanResult.scanContainer.oneScanAgo);
            }

        });
    }


    @OnClick({R.id.resultsMoreInformationText, R.id.resultsMoreInformationArrow})
    public void toggleMoreResultsBox() {
        if (expandedResultsBox.getVisibility() == View.VISIBLE) {
            expandedResultsBox.setVisibility(View.GONE);
            resultsMoreInformationArrow.setText("▶");
        } else {
            expandedResultsBox.setVisibility(View.VISIBLE);
            resultsMoreInformationArrow.setText("▼");
        }

    }

    @OnClick(R.id.btnDecrementLevel)
    public void decrementLevel() {
        if (estimatedPokemonLevel > 1.0) {
            estimatedPokemonLevel -= 0.5;
        }
        setArcPointer(estimatedPokemonLevel);
        //setArcPointer((Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / Data.CpM[trainerLevel * 2 - 2]);
        arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));
    }

    @OnClick(R.id.btnIncrementLevel)
    public void incrementLevel() {
        if (estimatedPokemonLevel < trainerLevel + 1.5 && estimatedPokemonLevel < 40.5) {
            estimatedPokemonLevel += 0.5;
        }
        setArcPointer(estimatedPokemonLevel);
        //setArcPointer((Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / Data.CpM[trainerLevel * 2 - 2]);
        arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));
    }

    @OnClick(R.id.btnIncrementLevelExpanded)
    public void incrementLevelExpanded() {
        expandedLevelSeekbar.setProgress(expandedLevelSeekbar.getProgress() + 1);
        populateAdvancedInformation(IVScanResult.scanContainer.oneScanAgo);
    }

    @OnClick(R.id.btnDecrementLevelExpanded)
    public void decrementLevelExpanded() {
        expandedLevelSeekbar.setProgress(expandedLevelSeekbar.getProgress() - 1);
        populateAdvancedInformation(IVScanResult.scanContainer.oneScanAgo);
    }

    @OnClick(R.id.btnCheckIv)
    public void checkIv() {

        // Check for valid parameters before attempting to do anything else.-
        try {
            pokemonHP = Integer.parseInt(pokemonHPEdit.getText().toString());
            pokemonCP = Integer.parseInt(pokemonCPEdit.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, R.string.missing_inputs, Toast.LENGTH_SHORT).show();
            return;
        }

        if (batterySaver && !screenshotDir.isEmpty()) {
            if (GoIVSettings.getInstance(getBaseContext()).shouldDeleteScreenshots()) {
                getContentResolver().delete(screenshotUri, MediaStore.Files.FileColumns.DATA + "=?", new String[]{screenshotDir});
            }
        }

        int selectedPokemon = pokemonList.getSelectedItemPosition();
        Pokemon pokemon = pokeCalculator.get(selectedPokemon);
        /* TODO: Should we set a size limit on that and throw away LRU entries? */
        /* TODO: Move this into an event listener that triggers when the user
         * actually changes the selection. */
        if (!pokemonName.equals(pokemon.name) && pokeCalculator.get(pokemonName) == null) {
            userCorrections.put(pokemonName, pokemon.name);
            SharedPreferences.Editor edit = sharedPref.edit();
            edit.putString(pokemonName, pokemon.name);
            edit.apply();
        }
        IVScanResult ivScanResult = pokeCalculator.getIVPossibilities(selectedPokemon, estimatedPokemonLevel, pokemonHP, pokemonCP);

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
        boolean enableCompare = IVScanResult.scanContainer.twoScanAgo != null;
        //@color/unimportantText
        exResCompare.setEnabled(enableCompare);
        exResCompare.setTextColor(getResources().getColor(enableCompare ? R.color.colorPrimary : R.color.unimportantText));
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
            if (GoIVSettings.getInstance(getApplicationContext()).shouldCopyToClipboard()) {
                String clipText = ivScanResult.getLowestIVCombination().percentPerfect + "-" + ivScanResult.getHighestIVCombination().percentPerfect;
                ClipData clip = ClipData.newPlainText(clipText, clipText);
                clipboard.setPrimaryClip(clip);
            }
        }

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
        adjustSeekbarForPokemon(ivScanResult);

        populateAdvancedInformation(ivScanResult);
        populatePrevScanNarrowing(ivScanResult);
    }

    /**
     * Adjusts the seekbar so minimum is pokemon current level
     *
     * @param ivScanResult
     */
    private void adjustSeekbarForPokemon(IVScanResult ivScanResult) {
        expandedLevelSeekbar.setProgress(levelToProgress(trainerLevel + 1.5f));
        expandedLevelSeekbar.setMax(levelToProgress(40));
    }

    /**
     * Shows the "refine by leveling up" part if he previous pokemon could be an upgraded version
     *
     * @param ivScanResult
     */
    private void populatePrevScanNarrowing(IVScanResult ivScanResult) {
        if (ivScanResult.canThisScanBePoweredUpPreviousScan()) {
            refine_by_last_scan.setVisibility(View.VISIBLE);
            exResPrevScan.setText(String.format(getString(R.string.last_scan), ivScanResult.getPrevScanName()));
        } else {
            refine_by_last_scan.setVisibility(View.GONE);
        }

    }

    /**
     * shows the name and level of the pokemon in the results dialog
     *
     * @param ivScanResult
     */
    private void populateResultsHeader(IVScanResult ivScanResult) {
        resultsPokemonName.setText(ivScanResult.pokemon.name);
        resultsPokemonLevel.setText(getString(R.string.level) + ": " + ivScanResult.estimatedPokemonLevel);
    }

    /**
     * populates the reuslt screen with the layout as if its multiple results
     *
     * @param ivScanResult
     */
    private void populateMultipleIVMatch(IVScanResult ivScanResult) {
        llMaxIV.setVisibility(View.VISIBLE);
        llMinIV.setVisibility(View.VISIBLE);
        llSingleMatch.setVisibility(View.GONE);
        llMultipleIVMatches.setVisibility(View.VISIBLE);
        tvAvgIV.setText("AVG");
        if (ivScanResult.tooManyPossibilities) {
            resultsCombinations.setText(getString(R.string.too_many_iv_combinations));
        } else {
            resultsCombinations.setText(String.format(getString(R.string.possible_iv_combinations), ivScanResult.iVCombinations.size()));
        }

        populateIVAllPosibilities(ivScanResult);

    }

    /**
     * adds all options in the all iv possibilities list
     *
     * @param ivScanResult
     */
    private void populateIVAllPosibilities(IVScanResult ivScanResult) {

        for (IVCombination ivItem : ivScanResult.iVCombinations) {
            addIVTextTo(allPosAtt, ivItem.att);
            addIVTextTo(allPosDef, ivItem.def);
            addIVTextTo(allPosSta, ivItem.sta);
            addPercentageToPercentageColumn(ivItem.att + ivItem.sta + ivItem.def);
        }


    }

    /**
     * adds a percent data point to the all positilities dialog
     *
     * @param allIVCombined attack + defence + stamina, max 45
     */
    private void addPercentageToPercentageColumn(int allIVCombined) {
        TextView adder = new TextView(this);
        int percent = (int) ((allIVCombined / 45f) * 100);
        adder.setText(percent + "");
        setTextColorbyPercentage(adder, percent);
        allPosPercent.addView(adder);
    }

    /**
     * method for adding an iv data to the all posibilities field, this method adds a single data point to a column
     *
     * @param column attack / defence / stamina
     * @param value  A value between 0 and 15
     */
    private void addIVTextTo(LinearLayout column, int value) {
        TextView adder = new TextView(this);
        adder.setText(value + "");
        int attackpercent = (int) ((value / 15f) * 100);
        setTextColorbyPercentage(adder, attackpercent);
        column.addView(adder);
    }


    /**
     * populates the result screen with the layout as if it's a single result
     *
     * @param ivScanResult
     */
    private void populateSingleIVMatch(IVScanResult ivScanResult) {
        llMaxIV.setVisibility(View.GONE);
        llMinIV.setVisibility(View.GONE);
        tvAvgIV.setText("IV");
        resultsAttack.setText(String.valueOf(ivScanResult.iVCombinations.get(0).att));
        resultsDefense.setText(String.valueOf(ivScanResult.iVCombinations.get(0).def));
        resultsHP.setText(String.valueOf(ivScanResult.iVCombinations.get(0).sta));

        setTextColorbyPercentage(resultsAttack, (int) Math.round(ivScanResult.iVCombinations.get(0).att * 100.0 / 15));
        setTextColorbyPercentage(resultsDefense, (int) Math.round(ivScanResult.iVCombinations.get(0).def * 100.0 / 15));
        setTextColorbyPercentage(resultsHP, (int) Math.round(ivScanResult.iVCombinations.get(0).sta * 100.0 / 15));

        llSingleMatch.setVisibility(View.VISIBLE);
        llMultipleIVMatches.setVisibility(View.GONE);
    }

    private int getSeekbarOffset() {
        return (int) (2 * estimatedPokemonLevel);
    }

    private float seekbarProgressToLevel(int progress) {
        return (progress + getSeekbarOffset()) / 2.0f;  //seekbar only supports integers, so the seekbar works between 2 and 80.
    }

    private int levelToProgress(float level) {
        return Math.min((int) (level * 2), 80) - getSeekbarOffset();
    }

    /**
     * sets the growth estimate text boxes to correpond to the
     * pokemon evolution and level set by the user
     */
    public void populateAdvancedInformation(IVScanResult ivScanResult) {
        float goalLevel = seekbarProgressToLevel(expandedLevelSeekbar.getProgress());
        int intSelectedPokemon = extendedEvolutionSpinner.getSelectedItemPosition(); //which pokemon is selected in the spinner
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
            selectedPokemon = evolutionLine.get(intSelectedPokemon);
        }


        CPRange expectedRange = pokeCalculator.getCpRangeAtLevel(selectedPokemon, ivScanResult.lowAttack, ivScanResult.lowDefense, ivScanResult.lowStamina, ivScanResult.highAttack, ivScanResult.highDefense, ivScanResult.highStamina, goalLevel);
        CPRange realRange = pokeCalculator.getCpRangeAtLevel(ivScanResult.pokemon, ivScanResult.lowAttack, ivScanResult.lowDefense, ivScanResult.lowStamina, ivScanResult.highAttack, ivScanResult.highDefense, ivScanResult.highStamina, estimatedPokemonLevel);
        int expectedAverage = (expectedRange.high + expectedRange.low) / 2;
        exResultCP.setText(String.valueOf(expectedAverage) + " (+" + (expectedAverage - realRange.high) + ")");

        UpgradeCost cost = pokeCalculator.getUpgradeCost(goalLevel, estimatedPokemonLevel);
        int evolutionCandyCost = pokeCalculator.getCandyCostForEvolution(ivScanResult.pokemon, selectedPokemon);
        String candyCostText = cost.candy + evolutionCandyCost + "";
        exResCandy.setText(candyCostText);
        exResStardust.setText(String.valueOf(cost.dust));

        pokeEvolutionAdapter.updatePokemonList(evolutionLine);
        exResLevel.setText(String.valueOf(goalLevel));

        // If goalLevel exeeds trainer capabilities then show text in orange
        if (goalLevel > trainerLevel + 1.5) {
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
        setTextColorbyPercentage(resultsMinPercentage, low);
        setTextColorbyPercentage(resultsAvePercentage, ave);
        setTextColorbyPercentage(resultsMaxPercentage, high);


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

    /**
     * sets the text color to red if below 80, and green if above
     *
     * @param text  the text that changes color
     * @param value the value that is checked if its above 80
     */
    private void setTextColorbyPercentage(TextView text, int value) {
        if (value >= 80) {
            text.setTextColor(Color.parseColor("#088A08")); //dark green
        } else if (value >= 60) {
            text.setTextColor(Color.parseColor("#DBA901"));//brownish orange
        } else {
            text.setTextColor(Color.parseColor("#8A0808")); //dark red
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

        //clear the all possibilities dialog
        allPosAtt.removeAllViews();
        allPosDef.removeAllViews();
        allPosSta.removeAllViews();
        allPosPercent.removeAllViews();

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
        IVScanResult thisScan = IVScanResult.scanContainer.oneScanAgo;
        IVScanResult prevScan = IVScanResult.scanContainer.twoScanAgo;
        if (prevScan != null) {
            ArrayList<IVCombination> newResult = thisScan.getLatestIVIntersection();
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
                pokemonList.setBackgroundColor(Color.parseColor("#ddffdd"));
            } else if (possiblePoke[1] < 2) {
                pokemonList.setBackgroundColor(Color.parseColor("#ffffcc"));
            } else {
                pokemonList.setBackgroundColor(Color.parseColor("#ffcccc"));
            }

            pokemonList.setSelection(possiblePoke[0]);
            pokemonHPEdit.setText(String.valueOf(pokemonHP));
            pokemonCPEdit.setText(String.valueOf(pokemonCP));

            showInfoLayoutArcPointer();
            setArcPointer(estimatedPokemonLevel);
            //setArcPointer((Data.CpM[(int) (estimatedPokemonLevel * 2 - 2)] - 0.094) * 202.037116 / Data.CpM[trainerLevel * 2 - 2]);
            arcAdjustBar.setProgress((int) ((estimatedPokemonLevel - 1) * 2));

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
        int pokeNumber = 0;
        int bestMatch = 100;
        int bestCandyMatch = 100;
        Pokemon p;

        /* If the user previous corrected this text, go with that. */
        if (userCorrections.containsKey(poketext)) {
            poketext = userCorrections.get(poketext);
        }

        /* If we already did similarity search for this, go with the cached value. */
        String cached = cachedCorrections.get(poketext);
        if (cached != null) {
            poketext = cached;
        }

        /* If the pokemon name was a perfect match, we are done. */
        p = pokeCalculator.get(poketext);
        if (p != null) {
            int[] result = {p.number, 0};
            return result;
        }

        /* If not, we limit the Pokemon search by candy name first since fewer valid candy names
         * options should mean fewer false matches. */
        p = pokeCalculator.get(candytext);

        /* If we can't find perfect candy match, do a distance/similarity based match */
        if (p == null) {
            for (Pokemon trypoke : pokeCalculator.pokedex) {
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

        bestMatch = 100;
        for (Pokemon candyp : candylist) {
            int dist = candyp.getDistance(poketext);
            if (dist < bestMatch) {
                p = candyp;
                bestMatch = dist;
            }
        }

        /* Cache this correction. We don't really need to save this across launches. */
        cachedCorrections.put(poketext, p.name);

        /* Adding the candy distance and the pokemon name distance gives a better idea of how much
         * guess is going on. */
        int[] result = {p.number, bestCandyMatch + bestMatch};
        return result;
    }

    private void initOCR() {
        String extdir = getExternalFilesDir(null).toString();
        if (!new File(extdir + "/tessdata/eng.traineddata").exists()) {
            copyAssetFolder(getAssets(), "tessdata", extdir + "/tessdata");
        }

        ocr = OCRHelper.init(extdir, displayMetrics.widthPixels, displayMetrics.heightPixels);
        ocr.nidoFemale = getResources().getString(R.string.pokemon029);
        ocr.nidoMale = getResources().getString(R.string.pokemon032);
    }



    /**
     * scanPokemon
     * Performs OCR on an image of a pokemon and sends the pulled info to PokeFly to display.
     *
     * @param pokemonImage The image of the pokemon
     * @param filePath     The screenshot path if it is a file, used to delete once checked
     */
    private void scanPokemon(Bitmap pokemonImage, String filePath) {
        //WARNING: this method *must* always send an intent at the end, no matter what, to avoid the application hanging.
        Intent info = Pokefly.createNoInfoIntent();
        if (ocr == null) {
            Toast.makeText(Pokefly.this, "Screen analysis module not initialized", Toast.LENGTH_LONG).show();
        } else {
            try {
                ocr.scanPokemon(pokemonImage, trainerLevel);
                if (ocr.candyName.equals("") && ocr.pokemonHP == 10 && ocr.pokemonCP == 10) { //the default values for a failed scan, if all three fail, then probably scrolled down.
                    Toast.makeText(Pokefly.this, getString(R.string.scan_pokemon_failed), Toast.LENGTH_SHORT).show();
                }
                Pokefly.populateInfoIntent(info, ocr.pokemonName, ocr.candyName, ocr.pokemonHP, ocr.pokemonCP, ocr.estimatedPokemonLevel, filePath);
            } finally {
                LocalBroadcastManager.getInstance(Pokefly.this).sendBroadcast(info);
            }
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
                receivedInfo = true;
                if (intent.hasExtra(KEY_SEND_INFO_NAME) && intent.hasExtra(KEY_SEND_INFO_CP) && intent.hasExtra(KEY_SEND_INFO_HP) && intent.hasExtra(KEY_SEND_INFO_LEVEL)) {
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
            windowManager.addView(IVButton, IVButonParams);
            IVButtonShown = true;
        } else if (!show) {
            if (IVButtonShown) {
                windowManager.removeView(IVButton);
                IVButtonShown = false;
            }
        }
    };

    private int dpToPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private static boolean copyAssetFolder(AssetManager assetManager, String fromAssetPath, String toPath) {

        String[] files = new String[0];

        try {
            files = assetManager.list(fromAssetPath);
        } catch (IOException exception) {
            Timber.e("Exception thrown in copyAssetFolder()");
            Timber.e(exception);
        }
        new File(toPath).mkdirs();
        boolean res = true;
        for (String file : files)
            if (file.contains(".")) {
                res &= copyAsset(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
            } else {
                res &= copyAssetFolder(assetManager, fromAssetPath + "/" + file, toPath + "/" + file);
            }
        return res;

    }

    private static boolean copyAsset(AssetManager assetManager, String fromAssetPath, String toPath) {
        try {
            InputStream in = assetManager.open(fromAssetPath);
            new File(toPath).createNewFile();
            OutputStream out = new FileOutputStream(toPath);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
            return true;
        } catch (IOException exception) {
            Timber.e("Exception thrown in copyAsset()");
            Timber.e(exception);
            return false;
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}

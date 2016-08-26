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
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kamron on 7/25/2016.
 */

public class Pokefly extends Service {

    private static final String ACTION_DISPLAY_IV_BUTTON = "action_display_iv_button";
    private static final String ACTION_SEND_INFO = "action_send_info";

    private static final String KEY_TRAINER_LEVEL = "key_trainer_level";
    private static final String KEY_STATUS_BAR_HEIGHT = "key_status_bar_height";
    private static final String KEY_BATTERY_SAVER = "key_battery_saver";
    private static final String KEY_SCREENSHOT_URI = "key_screenshot_uri";

    private static final String KEY_DISPLAY_IV_BUTTON_SHOW = "key_send_info_show";

    private static final String KEY_SEND_INFO_NAME = "key_send_info_name";
    private static final String KEY_SEND_INFO_CANDY = "key_send_info_candy";
    private static final String KEY_SEND_INFO_HP = "key_send_info_hp";
    private static final String KEY_SEND_INFO_CP = "key_send_info_cp";
    private static final String KEY_SEND_INFO_LEVEL = "key_send_info_level";
    private static final String KEY_SEND_SCREENSHOT_DIR = "key_send_screenshot_dir";

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

    private boolean infoShownSent = false;
    private boolean infoShownReceived = false;
    private boolean IVButtonShown = false;

    private ImageView IVButton;
    private ImageView arcPointer;
    private LinearLayout infoLayout;

    private PokeInfoCalculator pokeCalculator = null;

    @BindView(R.id.tvIvInfo)
    TextView ivText;
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
    @BindView(R.id.resultsMoreInformationArrow)
    TextView resultsMoreInformationArrow;
    @BindView(R.id.resultsMoreInformationText)
    TextView resultsMoreInformationText;
    @BindView(R.id.expandedLevelSeekbar)
    SeekBar expandedLevelSeekbar;
    @BindView(R.id.extendedEvolutionSpinner)
    Spinner extendedEvolutionSpinner;

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

    public static Intent createIVButtonIntent(boolean shouldShow) {
        Intent intent = new Intent(ACTION_DISPLAY_IV_BUTTON);
        intent.putExtra(KEY_DISPLAY_IV_BUTTON_SHOW, shouldShow);
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        //Display disp = windowManager.getDefaultDisplay();
        //disp.getRealMetrics(displayMetrics);
        //System.out.println("New Device:" + displayMetrics.widthPixels + "," + displayMetrics.heightPixels + "," + displayMetrics.densityDpi + "," + displayMetrics.density);

        LocalBroadcastManager.getInstance(this).registerReceiver(displayInfo, new IntentFilter(ACTION_SEND_INFO));
        LocalBroadcastManager.getInstance(this).registerReceiver(setIVButtonDisplay, new IntentFilter(ACTION_DISPLAY_IV_BUTTON));
        pokeCalculator = new PokeInfoCalculator(
                getResources().getStringArray(R.array.Pokemon),
                getResources().getIntArray(R.array.attack),
                getResources().getIntArray(R.array.defense),
                getResources().getIntArray(R.array.stamina),
                getResources().getIntArray(R.array.DevolutionNumber));
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
            displayMetrics = this.getResources().getDisplayMetrics();
            createInfoLayout();
            createIVButton();
            createArcPointer();
            createArcAdjuster();
        }
        return START_STICKY;
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
        super.onDestroy();
        if (IVButton != null && IVButtonShown) windowManager.removeView(IVButton);
        hideInfoLayoutArcPointer();
        stopForeground(true);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(displayInfo);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(setIVButtonDisplay);
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
        int index = Data.convertLevelToIndex(pokeLevel);
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
                        windowManager.removeView(IVButton);
                        IVButtonShown = false;
                        Intent intent = MainActivity.createScreenshotIntent();
                        LocalBroadcastManager.getInstance(Pokefly.this).sendBroadcast(intent);
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
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                updateCostFields(IVScanResult.scanContainer.oneScanAgo);
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
                updateCostFields(IVScanResult.scanContainer.oneScanAgo);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                updateCostFields(IVScanResult.scanContainer.oneScanAgo);
            }

        });
    }


    @OnClick({R.id.resultsMoreInformationText, R.id.resultsMoreInformationArrow})
    public void toggleMoreResultsBox() {
        if (expandedResultsBox.getVisibility() == View.VISIBLE) {
            expandedResultsBox.setVisibility(View.GONE);
            resultsMoreInformationArrow.setText("► ");
        } else {
            expandedResultsBox.setVisibility(View.VISIBLE);
            resultsMoreInformationArrow.setText("▼ ");
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
        expandedLevelSeekbar.setProgress(expandedLevelSeekbar.getProgress()+1);
    }

    @OnClick(R.id.btnDecrementLevelExpanded)
    public void decrementLevelExpanded() {
        expandedLevelSeekbar.setProgress(expandedLevelSeekbar.getProgress()-1);
    }

    @OnClick(R.id.btnCheckIv)
    public void checkIv() {
        if (batterySaver && !screenshotDir.isEmpty()) {
            if (GoIVSettings.getSettings(getBaseContext()).getDeleteScreenshots())
                getContentResolver().delete(screenshotUri, MediaStore.Files.FileColumns.DATA + "=?", new String[]{screenshotDir});
        }
        pokemonHP = Integer.parseInt(pokemonHPEdit.getText().toString());
        pokemonCP = Integer.parseInt(pokemonCPEdit.getText().toString());
        initialButtonsLayout.setVisibility(View.GONE);
        onCheckButtonsLayout.setVisibility(View.VISIBLE);

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

        if (attCheckbox.isChecked() || defCheckbox.isChecked() || staCheckbox.isChecked()){
            ivScanResult.refineByHighest(attCheckbox.isChecked(), defCheckbox.isChecked(), staCheckbox.isChecked());
        }

        populateResultsBox(ivScanResult);
        resultsBox.setVisibility(View.VISIBLE);
        inputBox.setVisibility(View.GONE);

    }

    /**
     * sets the information in the results box
     */
    private void populateResultsBox(IVScanResult ivScanResult) {

        resultsPokemonName.setText(ivScanResult.pokemon.name);
        resultsCombinations.setText(String.format(getString(R.string.possible_iv_combinations), ivScanResult.iVCombinations.size()));

        //TODO: Populate ivText in a better way.
        String allIvs = "";
        for (IVCombination ivItem : ivScanResult.iVCombinations) {
            allIvs += String.format(getString(R.string.ivtext_stats), ivItem.att, ivItem.def, ivItem.sta, ivItem.percentPerfect) + "\n";
        }
        ivText.setText(allIvs);

        resultsPokemonLevel.setText(getString(R.string.level) + ": " + ivScanResult.estimatedPokemonLevel);
        setResultScreenPercentageRange(ivScanResult);

        //Preselect the maximum level we can reach currently, the user can move to higher or lower.
        expandedLevelSeekbar.setProgress(levelToProgress(trainerLevel + 1.5f));
        expandedLevelSeekbar.setMax(levelToProgress(40));
        updateCostFields(ivScanResult);
        exResPrevScan.setText(String.format(getString(R.string.last_scan),ivScanResult.getPrevScanName()));
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
    public void updateCostFields(IVScanResult ivScanResult) {
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
        int expectedAverage = (expectedRange.high + realRange.low) / 2;
        exResultCP.setText(String.valueOf(expectedAverage) + " (+" + (expectedAverage - realRange.high) + ")");

        UpgradeCost cost = pokeCalculator.getUpgradeCost(goalLevel, estimatedPokemonLevel);
        exResCandy.setText(String.valueOf(cost.candy));
        exResStardust.setText(String.valueOf(cost.dust));

        pokeEvolutionAdapter.updatePokemonList(evolutionLine);
        exResLevel.setText(String.valueOf(goalLevel));

        // If goalLevel exeeds trainer capabilities then show text in orange
        if(goalLevel>trainerLevel+1.5){
            exResLevel.setTextColor(getResources().getColor(R.color.orange));
        }else{
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
        if (!batterySaver) {
            windowManager.addView(IVButton, IVButonParams);
            IVButtonShown = true;
        }
        attCheckbox.setChecked(false);
        defCheckbox.setChecked(false);
        staCheckbox.setChecked(false);
        resetPokeflyStateMachine();
        resetInfoDialogue();
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
            thisScan.iVCombinations = newResult;
            populateResultsBox(thisScan);

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

            if (!GoIVSettings.getSettings(getBaseContext()).getShowConfirmationDialog())
                checkIv();
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

                int dist = trypoke.getDistance(candytext);
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
    private BroadcastReceiver setIVButtonDisplay = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean show = intent.getBooleanExtra(KEY_DISPLAY_IV_BUTTON_SHOW, false);
            if (show && !IVButtonShown && !infoShownSent) {
                windowManager.addView(IVButton, IVButonParams);
                IVButtonShown = true;
            } else if (!show) {
                if (IVButtonShown) {
                    windowManager.removeView(IVButton);
                    IVButtonShown = false;
                }
            }
        }
    };

    private int dpToPx(int dp) {
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}

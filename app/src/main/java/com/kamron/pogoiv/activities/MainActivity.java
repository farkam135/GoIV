package com.kamron.pogoiv.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.NpTrainerLevelPickerListener;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanPoint;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.updater.AppUpdate;
import com.kamron.pogoiv.updater.AppUpdateUtil;
import com.kamron.pogoiv.widgets.PlayerTeamAdapter;

import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_SHOW_UPDATE_DIALOG = "com.kamron.pogoiv.SHOW_UPDATE_DIALOG";
    public static final String ACTION_START_POKEFLY = "com.kamron.pogoiv.ACTION_START_POKEFLY";
    public static final String ACTION_RESTART_POKEFLY = "com.kamron.pogoiv.ACTION_RESTART_POKEFLY";
    public static final String ACTION_OPEN_SETTINGS = "com.kamron.pogoiv.ACTION_OPEN_SETTINGS";


    private static final String youtubeTutorialCalibrationUrl = "https://www.youtube.com/embed/w7dNEW1FLjQ?rel=0";

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private static final int WRITE_STORAGE_REQ_CODE = 1236;
    private static final int SCREEN_CAPTURE_REQ_CODE = 1235;

    private static final String PREF_LEVEL = "level";

    public static boolean shouldShowUpdateDialog;
    private SharedPreferences sharedPref;
    private ScreenGrabber screen;

    private DisplayMetrics displayMetrics;
    private DisplayMetrics rawDisplayMetrics;

    private boolean batterySaver;

    private int trainerLevel;

    private Button launchButton;
    private NumberPicker npTrainerLevel;

    private ScanPoint arcInit;
    private int arcRadius;
    private final BroadcastReceiver pokeflyStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateLaunchButtonText(Pokefly.isRunning());
            launchButton.setEnabled(true);
            startPokeFlyOnStop();
        }
    };

    private final BroadcastReceiver showUpdateDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            assert BuildConfig.isInternetAvailable;
            AppUpdate update = intent.getParcelableExtra("update");
            if (update.getStatus() == AppUpdate.UPDATE_AVAILABLE && shouldShowUpdateDialog && !isGoIVBeingUpdated(
                    context)) {
                AlertDialog updateDialog = AppUpdateUtil.getAppUpdateDialog(MainActivity.this, update);
                updateDialog.show();
            }
            if (!shouldShowUpdateDialog) {
                shouldShowUpdateDialog = true;
            }
        }
    };

    private boolean shouldRestartOnStopComplete;
    private boolean skipStartPogo;

    private final BroadcastReceiver restartPokeFly = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            restartPokeFly(true);
        }
    };

    private GoIVSettings settings;

    public static Intent createUpdateDialogIntent(AppUpdate update) {
        Intent updateIntent = new Intent(MainActivity.ACTION_SHOW_UPDATE_DIALOG);
        updateIntent.putExtra("update", update);
        return updateIntent;
    }

    public static boolean isGoIVBeingUpdated(Context context) {

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterByStatus(DownloadManager.STATUS_RUNNING);
        Cursor c = downloadManager.query(q);
        if (c.moveToFirst()) {
            String fileName = c.getString(c.getColumnIndex(DownloadManager.COLUMN_TITLE));
            if (fileName.equals(context.getString(R.string.notification_updating))) {
                return true;
            }
        }
        return false;
    }

    private boolean hasAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        return true;
    }

    NpTrainerLevelPickerListener npTrainerLevelPickerListenerInstance = new NpTrainerLevelPickerListener(this);

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG);
        initiateAndLoadSettings(); //Loading settings must be done before methods that use settings

        runAutoUpdateStartupChecks();
        initiateUserScreenSettings();
        initiateGui();
        warnUserFirstLaunchIfNoScreenRecording();

        registerAllBroadcastRecievers();


    }

    /**
     * Makes the help-buttons load and navigate to the tutorial youtube webview, or open the browser if using offline
     * build.
     */
    private void setupTutorialButton() {
        Button tuthelp = (Button) findViewById(R.id.recalibrationHelp);
        Button tuthelp2 = (Button) findViewById(R.id.recalibrationHelp2);
        tuthelp.setOnClickListener(new RecalibrationTutListener());
        tuthelp2.setOnClickListener(new RecalibrationTutListener());
    }

    /**
     * Loads the webview youtube video.
     */
    private void loadRecalibrationTutorialVideo() {
        String frameVideo = "<html><iframe width=\"310\" height=\"480\" src=\""
                + youtubeTutorialCalibrationUrl
                + "\" frameborder=\"0\" gesture=\"media\" allow=\"encrypted-media\" "
                + "allowfullscreen></iframe></html>";

        WebView displayYoutubeVideo = (WebView) findViewById(R.id.webview_tutorial);
        displayYoutubeVideo.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

        });
        WebSettings webSettings = displayYoutubeVideo.getSettings();
        webSettings.setJavaScriptEnabled(true);
        displayYoutubeVideo.loadData(frameVideo, "text/html", "utf-8");
    }

    /**
     * Makes the localBroadcastManager register recievers for the different accepted intents, and tells
     * the app to
     * actually do something when those intents are received.
     */

    private void registerAllBroadcastRecievers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(pokeflyStateChanged,
                new IntentFilter(Pokefly.ACTION_UPDATE_UI));
        LocalBroadcastManager.getInstance(this).registerReceiver(showUpdateDialog,
                new IntentFilter(ACTION_SHOW_UPDATE_DIALOG));
        LocalBroadcastManager.getInstance(this).registerReceiver(restartPokeFly,
                new IntentFilter(ACTION_RESTART_POKEFLY));
        initiateTeamPickerSpinner();

        runActionOnIntent(getIntent());
    }


    /**
     * Runs the initialization logic related to the user screen, taking measurements so the ocr will scan the right
     * areas.
     */
    private void initiateUserScreenSettings() {
        displayMetrics = this.getResources().getDisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        rawDisplayMetrics = new DisplayMetrics();
        Display disp = windowManager.getDefaultDisplay();
        disp.getRealMetrics(rawDisplayMetrics);
    }

    /**
     * Runs all the startup settings initialization.
     */
    private void initiateAndLoadSettings() {
        settings = GoIVSettings.getInstance(this);
        PreferenceManager.setDefaultValues(this, R.xml.settings, false);
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        trainerLevel = sharedPref.getInt(PREF_LEVEL, 1);
        batterySaver = settings.isManualScreenshotModeEnabled();
    }

    /**
     * Checks for any published updates if auto-updater settings is on and deletes previous updates.
     */
    private void runAutoUpdateStartupChecks() {
        shouldShowUpdateDialog = true;
        AppUpdateUtil.deletePreviousApkFile(MainActivity.this);
        if (settings.isAutoUpdateEnabled()) {
            AppUpdateUtil.checkForUpdate(this);
        }
    }

    /**
     * Initiates all the gui components in the mainactivity.
     */
    private void initiateGui() {
        setContentView(R.layout.activity_main);

        TextView tvVersionNumber = (TextView) findViewById(R.id.version_number);
        tvVersionNumber.setText(String.format("v%s", getVersionName()));

        setupTutorialButton();
        hideWebviewIfOfflineFlavour();
        initiateOptimizationWarning();
        initiateLevelPicker();
        initiateHelpButton();
        initiateCommunityButtons();
        initiateStartButton();
    }

    private void hideWebviewIfOfflineFlavour() {
        if (BuildConfig.FLAVOR.toLowerCase().contains("offline")) {
            findViewById(R.id.webview_tutorial).setVisibility(View.GONE);
        }
    }

    /**
     * Show the optimization-warning and its components depending on if the user hasn't a manual screen calibration
     * saved, if the calibration isn't updated and if the device has weird screen ratio.
     */
    private void initiateOptimizationWarning() {
        if (settings.hasUpToDateManualScanCalibration()) {
            findViewById(R.id.optimizationWarningLayout).setVisibility(View.GONE); // Ensure the layout isn't visible

        } else {
            findViewById(R.id.optimizationWarningLayout).setVisibility(View.VISIBLE);

            if (settings.hasManualScanCalibration()) {
                // Has outdated calibration
                findViewById(R.id.shouldRunOptimizationAgainWarning).setVisibility(View.VISIBLE);

            } else {
                // Has never calibrated
                findViewById(R.id.neverRunOptimizationWarning).setVisibility(View.VISIBLE);

                // If the screen ratio isn't standard the user must run calibration
                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getRealSize(size);
                float ratio = (float) size.x / size.y;
                float standardRatio = 9 / 16f;
                float tolerance = 1 / 400f;
                if (ratio < (standardRatio - tolerance) || ratio > (standardRatio + tolerance)) {
                    findViewById(R.id.nonStandardScreenWarning).setVisibility(View.VISIBLE);
                }
            }
        }
    }

    /**
     * Initiates the links to reddit and github.
     */
    private void initiateCommunityButtons() {
        View redditButton = findViewById(R.id.reddit);
        redditButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://www.reddit.com/r/GoIV/");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        View githubButton = findViewById(R.id.github);
        githubButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://github.com/farkam135/GoIV");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }

    private void initiateHelpButton() {
        Button helpButton = (Button) findViewById(R.id.help);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(R.string.instructions_title)
                        .setMessage(R.string.instructions_message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });

    }

    /**
     * Shows an alert warning the user about not being on android 5, and that the app is locked into screenshot mode.
     */
    private void warnUserFirstLaunchIfNoScreenRecording() {
        boolean userOnBelowAndroid5 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH;
        boolean hasNotShownAndroid5Warning = !settings.hasShownNoScreenRecWarning();
        if (hasNotShownAndroid5Warning && userOnBelowAndroid5) {
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.android_sub5_warning)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();

            settings.setHasShownScreenRecWarning();
        }
    }

    /**
     * Configures the logic for the start button.
     */
    private void initiateStartButton() {

        launchButton = findViewById(R.id.start);
        ViewCompat.setBackgroundTintList(launchButton, null);
        launchButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                runStartButtonLogic();

            }
        });
    }

    public void runStartButtonLogic() {
        if (!hasAllPermissions()) {
            getAllPermissions();
        } else if (!Pokefly.isRunning()) { //Will start goiv
            startGoIV();
        } else { //Will stop goiv
            stopGoIV();
        }
    }

    private void stopGoIV() {
        stopService(new Intent(MainActivity.this, Pokefly.class));
        if (screen != null) {
            screen.exit();
        }
    }

    private void startGoIV() {
        batterySaver = settings.isManualScreenshotModeEnabled();
        setupDisplaySizeInfo();
        trainerLevel = setupTrainerLevel();

        Data.setupArcPoints(arcInit, arcRadius, trainerLevel);

        if (batterySaver) {
            startPokeFly();
        } else {
            startScreenService();
        }
    }

    private void startPoGoIfSettingOn() {
        if (settings.shouldLaunchPokemonGo() && !skipStartPogo) {
            openPokemonGoApp();
        }
    }

    /**
     * Initiates the scrollable level picker.
     */
    private void initiateLevelPicker() {
        npTrainerLevel = (NumberPicker) findViewById(R.id.trainerLevel);
        npTrainerLevel.setMaxValue(40);
        npTrainerLevel.setMinValue(1);
        npTrainerLevel.setWrapSelectorWheel(false);
        npTrainerLevel.setValue(trainerLevel);

        npTrainerLevel.setOnScrollListener(npTrainerLevelPickerListenerInstance);
        npTrainerLevel.setOnValueChangedListener(npTrainerLevelPickerListenerInstance);
    }

    /**
     * Requests overlay and storage permissions if android version allows it.
     */
    private void getAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(
                MainActivity.this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_STORAGE_REQ_CODE);
        }
    }

    /**
     * Initiates the team picker spinner.
     */
    private void initiateTeamPickerSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.teamPickerSpinner);
        PlayerTeamAdapter adapter = new PlayerTeamAdapter(this);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                settings.setPlayerTeam(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        spinner.setSelection(settings.playerTeam());
    }

    private void setupDisplaySizeInfo() {
        arcInit = new ScanPoint((int) (displayMetrics.widthPixels * 0.5),
                (int) Math.floor(displayMetrics.heightPixels * 0.35664));
        if (displayMetrics.heightPixels == 2392 || displayMetrics.heightPixels == 800) {
            arcInit.yCoord--;
        } else if (displayMetrics.heightPixels == 1920) {
            arcInit.yCoord++;
        }

        arcRadius = (int) Math.round(displayMetrics.heightPixels * 0.2285);
        if (displayMetrics.heightPixels == 1776 || displayMetrics.heightPixels == 960
                || displayMetrics.heightPixels == 800) {
            arcRadius++;
        }
    }

    /**
     * save the trainerlevel from the numberpicker to settings and return it.
     *
     * @return the level in the number picker.
     */
    private int setupTrainerLevel() {
        // This call to clearFocus will accept whatever input the user pressed, without
        // forcing him to press the green checkmark on the keyboard.
        // Otherwise the typed value won't be read if either:
        // - the user presses Start before closing the keyboard, or
        // - the user closes the keyboard with the back button (note that does not cancel
        //   the typed text).
        npTrainerLevel.clearFocus();
        int trainerLevel = npTrainerLevel.getValue();

        sharedPref.edit().putInt(PREF_LEVEL, trainerLevel).apply();
        return trainerLevel;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
         * This intent will update the button label, but later. This matters
         * when we start Pokefly: onResume can get called right away after
         * sending that intent, when Pokefly.isRunning is still false, so an
         * immediate update will reset the label to "START" while the actual
         * meaning is "STOP".
         * The new intent created here is delivered after the intent to start
         * Pokefly (because intents are delivered in order). The ordering is not
         * really documented, but appears likely enough to work in principle,
         * and it works well enough in practice:
         * http://stackoverflow.com/a/28513424/53974. Since this is mostly a UI
         * issue, this fix should be good enough.
         */
        LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Pokefly.ACTION_UPDATE_UI));

        settings = GoIVSettings.getInstance(MainActivity.this);
    }

    private int getStatusBarHeight() {
        Rect rectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        return rectangle.top;
    }

    /**
     * Starts the PokeFly background service which contains overlay logic.
     */
    private void startPokeFly() {
        launchButton.setText(R.string.main_starting);
        launchButton.setEnabled(false);

        startPoGoIfSettingOn();
        firePokeFlyIntent();
        skipStartPogo = false;
    }


    /**
     * This method actually starts pokefly, but other thins need to be done first, such as updating the text on the
     * buttons, and starting pogo.
     */
    private void firePokeFlyIntent() {
        int statusBarHeight = getStatusBarHeight();
        Intent intent = Pokefly.createIntent(this, trainerLevel, statusBarHeight, batterySaver);
        startService(intent);
    }

    private void updateLaunchButtonText(boolean isPokeflyRunning) {
        if (!hasAllPermissions()) {
            launchButton.setText(R.string.main_permission);
        } else if (isPokeflyRunning) {
            launchButton.setText(R.string.main_stop);
        } else {
            launchButton.setText(R.string.main_start);
        }
    }

    private String getVersionName() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("Exception thrown while getting version name");
            Timber.e(e);
        }
        return "Error while getting version name";
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pokeflyStateChanged);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(showUpdateDialog);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(restartPokeFly);
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    /**
     * Called when another activity has sent a result to this activity. For example when this activity starts the
     * activity which calls for the projectionmanager, which tells this class if the screen capture has been enabled.
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            updateLaunchButtonText(false);

        } else if (requestCode == SCREEN_CAPTURE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
                MediaProjection mProjection = projectionManager.getMediaProjection(resultCode, data);
                screen = ScreenGrabber.init(mProjection, rawDisplayMetrics);

                startPokeFly();
            } else {
                updateLaunchButtonText(false);
            }
        }
    }

    /**
     * Runs a launch intent for Pokemon GO.
     */
    private void openPokemonGoApp() {
        Intent i = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
        if (i != null) {
            i.addFlags(FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == WRITE_STORAGE_REQ_CODE) {
            updateLaunchButtonText(false);
        }
    }

    /**
     * Starts the screen capture.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScreenService() {
        launchButton.setText(R.string.accept_screen_capture);
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_CAPTURE_REQ_CODE);
    }

    /**
     * We will get custom intents from notifications.
     *
     * @param intent this paramater will not be stored and will only be available here.
     */
    @Override
    protected final void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        runActionOnIntent(intent);
    }

    /**
     * Handles custom action intents action probably from notification.
     *
     * @param intent will get send the intent to check the action on.
     */
    private void runActionOnIntent(Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        } else if (ACTION_START_POKEFLY.equals(intent.getAction())) {
            if (!Pokefly.isRunning()) {
                launchButton.callOnClick();
            }
        } else if (ACTION_OPEN_SETTINGS.equals(intent.getAction())) {
            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
    }

    /**
     * Increment or decrement the trainer level.
     *
     * @param addition this parameter will check if we want to increment or decrement the trainer level.
     */
    private void incrementTrainerLevelByOne(boolean addition) {
        if (Pokefly.isRunning()) {
            int newvalue;

            if (addition && trainerLevel < npTrainerLevel.getMaxValue()) {
                newvalue = trainerLevel + 1;
            } else if (!addition && trainerLevel > npTrainerLevel.getMinValue()) {
                newvalue = trainerLevel - 1;
            } else {
                return;
            }
            npTrainerLevel.setValue(newvalue);
            restartPokeFly(false);
        }
    }

    /**
     * We want to reset pokefly settings, for now we will completely restart pokefly.
     *
     * @param skipStartPoGO this parameter will check if we want to skip restart PoGO.
     */
    private void restartPokeFly(final boolean skipStartPoGO) {
        if (Pokefly.isRunning()) {
            this.shouldRestartOnStopComplete = true;
            this.skipStartPogo = skipStartPoGO;
            launchButton.callOnClick();
        }
    }

    /**
     * We want to restart when pokefly is finally stopped, check that its not running, then call the button.
     */
    private void startPokeFlyOnStop() {
        if (!Pokefly.isRunning() && shouldRestartOnStopComplete) {
            //we are done restarting, no need to restart again
            shouldRestartOnStopComplete = false;
            launchButton.callOnClick();
        }
    }


    /**
     * An onclick class that shows the video tutorial and loads the tutorial video, and scrolls to the view, or hides
     * it if its the second time someone clicks.
     */
    private class RecalibrationTutListener implements View.OnClickListener {

        @Override public void onClick(View view) {
            if (BuildConfig.FLAVOR.toLowerCase().contains("online")) {
                final LinearLayout webLayout = findViewById(R.id.weblayout);
                WebView displayYoutubeVideo = (WebView) findViewById(R.id.webview_tutorial);

                if (webLayout.getVisibility() == View.GONE) {
                    webLayout.setVisibility(View.VISIBLE);
                    loadRecalibrationTutorialVideo();
                    final ScrollView sw = findViewById(R.id.scrollviewMain);

                    sw.post(new Runnable() {
                        @Override
                        public void run() {
                            sw.smoothScrollTo(0, webLayout.getTop());
                        }
                    });
                } else {
                    displayYoutubeVideo.stopLoading();
                    webLayout.setVisibility(View.GONE);
                }

            } else { //running offline version, we cant load the webpage inserted into the app, we need to open browser.
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(youtubeTutorialCalibrationUrl));
                startActivity(i);
            }


        }

    }
}

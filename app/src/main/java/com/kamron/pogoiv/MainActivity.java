package com.kamron.pogoiv;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.kamron.pogoiv.logic.Data;
import com.kamron.pogoiv.updater.AppUpdate;
import com.kamron.pogoiv.updater.AppUpdateUtil;
import com.kamron.pogoiv.updater.DownloadUpdateService;
import com.kamron.pogoiv.widgets.PlayerTeamAdapter;

import java.io.File;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    public static final String ACTION_SHOW_UPDATE_DIALOG = "com.kamron.pogoiv.SHOW_UPDATE_DIALOG";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private static final int WRITE_STORAGE_REQ_CODE = 1236;
    private static final int SCREEN_CAPTURE_REQ_CODE = 1235;

    private static final String PREF_LEVEL = "level";
    private static final String PREF_SCREENSHOT_DIR = "screenshotDir";
    private static final String PREF_SCREENSHOT_URI = "screenshotUri";

    private static final String ACTION_RESET_SCREENSHOT = "com.kamron.pogoiv.RESET_SCREENSHOT";
    public static boolean shouldShowUpdateDialog;
    private SharedPreferences sharedPref;
    private ScreenGrabber screen;
    private ContentObserver screenShotObserver;
    private FileObserver screenShotScanner;

    private boolean screenShotWriting = false;
    private DisplayMetrics displayMetrics;
    private DisplayMetrics rawDisplayMetrics;

    private boolean batterySaver;
    private String screenshotDir;
    private Uri screenshotUri;

    private boolean readyForNewScreenshot = true;
    /**
     * resetScreenshot
     * Used to notify a new request for screenshot can be made. Needed to prevent multiple
     * intents for some devices.
     */
    private final BroadcastReceiver resetScreenshot = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            readyForNewScreenshot = true;
        }
    };
    private boolean pokeFlyRunning = false;
    private int trainerLevel;

    private final Point arcInit = new Point();
    private int arcRadius;
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
    private GoIVSettings settings;

    public static Intent createResetScreenshotIntent() {
        return new Intent(ACTION_RESET_SCREENSHOT);
    }

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
            if (fileName.equals(DownloadUpdateService.DOWNLOAD_UPDATE_TITLE)) {
                return true;
            }
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.tag(TAG);

        settings = GoIVSettings.getInstance(this);

        shouldShowUpdateDialog = true;

        if (settings.isAutoUpdateEnabled()) {
            AppUpdateUtil.checkForUpdate(this);
        }

        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(this, R.xml.settings, false);

        TextView tvVersionNumber = (TextView) findViewById(R.id.version_number);
        tvVersionNumber.setText(getVersionName());

        TextView goIvInfo = (TextView) findViewById(R.id.goiv_info);
        goIvInfo.setMovementMethod(LinkMovementMethod.getInstance());

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        trainerLevel = sharedPref.getInt(PREF_LEVEL, 1);
        screenshotDir = sharedPref.getString(PREF_SCREENSHOT_DIR, "");
        screenshotUri = Uri.parse(sharedPref.getString(PREF_SCREENSHOT_URI, ""));
        batterySaver = settings.isManualScreenshotModeEnabled();

        final NumberPicker npTrainerLevel = (NumberPicker) findViewById(R.id.trainerLevel);
        npTrainerLevel.setMaxValue(40);
        npTrainerLevel.setMinValue(1);
        npTrainerLevel.setWrapSelectorWheel(false);
        npTrainerLevel.setValue(trainerLevel);

        Button launch = (Button) findViewById(R.id.start);
        launch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (((Button) v).getText().toString().equals(getString(R.string.main_permission))) {
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
                } else if (((Button) v).getText().toString().equals(getString(R.string.main_start))) {
                    batterySaver = settings.isManualScreenshotModeEnabled();
                    setupDisplaySizeInfo();
                    trainerLevel = setupTrainerLevel(npTrainerLevel);

                    Data.setupArcPoints(arcInit, arcRadius, trainerLevel);

                    if (batterySaver) {
                        if (!screenshotDir.isEmpty()) {
                            startScreenshotService();
                        } else {
                            getScreenshotDir();
                        }
                    } else {
                        startScreenService();
                    }
                } else if (((Button) v).getText().toString().equals(getString(R.string.main_stop))) {
                    stopService(new Intent(MainActivity.this, Pokefly.class));
                    if (screen != null) {
                        screen.exit();
                    } else if (screenShotScanner != null) {
                        screenShotScanner.stopWatching();
                        screenShotScanner = null;
                    }
                    pokeFlyRunning = false;
                    ((Button) v).setText(getString(R.string.main_start));
                }
            }
        });

        checkPermissions(launch);


        displayMetrics = this.getResources().getDisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        rawDisplayMetrics = new DisplayMetrics();
        Display disp = windowManager.getDefaultDisplay();
        disp.getRealMetrics(rawDisplayMetrics);

        LocalBroadcastManager.getInstance(this).registerReceiver(resetScreenshot,
                new IntentFilter(ACTION_RESET_SCREENSHOT));
        LocalBroadcastManager.getInstance(this).registerReceiver(showUpdateDialog,
                new IntentFilter(ACTION_SHOW_UPDATE_DIALOG));

        initiateTeamPickerSpinner();
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
        arcInit.x = (int) (displayMetrics.widthPixels * 0.5);

        arcInit.y = (int) Math.floor(displayMetrics.heightPixels / 2.803943);
        if (displayMetrics.heightPixels == 2392 || displayMetrics.heightPixels == 800) {
            arcInit.y--;
        } else if (displayMetrics.heightPixels == 1920) {
            arcInit.y++;
        }

        arcRadius = (int) Math.round(displayMetrics.heightPixels / 4.3760683);
        if (displayMetrics.heightPixels == 1776 || displayMetrics.heightPixels == 960
                || displayMetrics.heightPixels == 800) {
            arcRadius++;
        }
    }

    public int setupTrainerLevel(NumberPicker npTrainerLevel) {
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

    private void getScreenshotDir() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.battery_saver_setup)
                .setMessage(R.string.battery_saver_instructions)
                .setPositiveButton(R.string.setup, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ((Button) findViewById(R.id.start)).setText(R.string.take_screenshot);
                        screenShotObserver = new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange, Uri uri) {
                                if (readyForNewScreenshot) {
                                    if (uri.toString().contains("images")) {
                                        final String pathChange = getRealPathFromUri(MainActivity.this, uri);
                                        if (pathChange.contains("Screenshot")) {
                                            screenshotDir = pathChange.substring(0,
                                                    pathChange.lastIndexOf(File.separator));
                                            screenshotUri = uri;
                                            getContentResolver().unregisterContentObserver(screenShotObserver);
                                            sharedPref.edit().putString("screenshotDir", screenshotDir).apply();
                                            sharedPref.edit().putString("screenshotUri", uri.toString()).apply();
                                            ((Button) findViewById(R.id.start)).setText(R.string.main_start);
                                            new AlertDialog.Builder(MainActivity.this)
                                                    .setTitle(R.string.battery_saver_setup)
                                                    .setMessage(String.format(getString(R.string.screenshot_dir_found),
                                                            screenshotDir))
                                                    .setPositiveButton(R.string.done,
                                                            new DialogInterface.OnClickListener() {
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    screenShotObserver = null;
                                                                    getContentResolver().delete(screenshotUri,
                                                                            MediaStore.Files.FileColumns.DATA + "=?",
                                                                            new String[]{pathChange});
                                                                }
                                                            })
                                                    .show();
                                        }
                                    }
                                }
                                super.onChange(selfChange, uri);
                            }
                        };
                        getContentResolver().registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
                                screenShotObserver);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
        ((Button) findViewById(R.id.start)).setText(R.string.main_stop);

        int statusBarHeight = getStatusBarHeight();
        Intent intent = Pokefly.createIntent(this, trainerLevel, statusBarHeight, batterySaver, screenshotDir,
                screenshotUri);
        startService(intent);

        pokeFlyRunning = true;

        if (settings.shouldLaunchPokemonGo()) {
            openPokemonGoApp();
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

    /**
     * Checks to see if all runtime permissions are granted,
     * if not change button text to Grant Permissions.
     *
     * @param launch The start button to change the text of
     */
    private void checkPermissions(Button launch) {
        //Check Permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            launch.setText(getString(R.string.main_permission));
        } else if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            launch.setText(getString(R.string.main_permission));
        }
    }

    @Override
    public void onDestroy() {
        if (pokeFlyRunning) {
            stopService(new Intent(MainActivity.this, Pokefly.class));
            pokeFlyRunning = false;
        }
        if (screen != null) {
            screen.exit();
        }
        if (screenShotObserver != null) {
            getContentResolver().unregisterContentObserver(screenShotObserver);
        }
        if (screenShotScanner != null) {
            screenShotScanner.stopWatching();
            screenShotScanner = null;
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(resetScreenshot);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(showUpdateDialog);
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // SYSTEM_ALERT_WINDOW permission not granted...
                ((Button) findViewById(R.id.start)).setText(getString(R.string.main_permission));
            } else if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                ((Button) findViewById(R.id.start)).setText(getString(R.string.main_start));
            }
        } else if (requestCode == SCREEN_CAPTURE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
                MediaProjection mProjection = projectionManager.getMediaProjection(resultCode, data);
                screen = ScreenGrabber.init(mProjection, rawDisplayMetrics, displayMetrics);

                startPokeFly();
            } else {
                ((Button) findViewById(R.id.start)).setText(getString(R.string.main_start));
            }
        }
    }

    /**
     * Runs a launch intent for Pokemon GO.
     */
    private void openPokemonGoApp() {
        Intent i = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
        if (i != null) {
            startActivity(i);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == WRITE_STORAGE_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Settings.canDrawOverlays(this) && ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    // SYSTEM_ALERT_WINDOW permission not granted...
                    ((Button) findViewById(R.id.start)).setText(getString(R.string.main_start));
                }
            }
        }
    }

    /**
     * Starts the screen capture.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startScreenService() {
        ((Button) findViewById(R.id.start)).setText(R.string.accept_screen_capture);
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(
                Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_CAPTURE_REQ_CODE);
    }

    /**
     * Starts the screenshot service, which checks for a new screenshot to scan.
     */
    private void startScreenshotService() {
        screenShotScanner = new FileObserver(screenshotDir, FileObserver.CLOSE_NOWRITE | FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, String file) {
                if (readyForNewScreenshot && file != null) {
                    readyForNewScreenshot = false;
                    File pokemonScreenshot = new File(screenshotDir + File.separator + file);
                    String filepath = pokemonScreenshot.getAbsolutePath();
                    Bitmap bmp = BitmapFactory.decodeFile(filepath);
                    Intent newintent = Pokefly.createProcessBitmapIntent(bmp, filepath);
                    LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(newintent);
                }
            }
        };
        screenShotScanner.startWatching();
        startPokeFly();
    }

    private String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null,
                    MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}

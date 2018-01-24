package com.kamron.pogoiv.activities;

import android.Manifest;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.updater.AppUpdate;
import com.kamron.pogoiv.updater.AppUpdateUtil;
import com.kamron.pogoiv.updater.DownloadUpdateService;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TAG_FRAGMENT_CONTENT = "content";

    public static final String ACTION_SHOW_UPDATE_DIALOG = "com.kamron.pogoiv.SHOW_UPDATE_DIALOG";
    public static final String ACTION_START_POKEFLY = "com.kamron.pogoiv.ACTION_START_POKEFLY";
    public static final String ACTION_RESTART_POKEFLY = "com.kamron.pogoiv.ACTION_RESTART_POKEFLY";

    private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
    private static final int WRITE_STORAGE_REQ_CODE = 1236;
    private static final int SCREEN_CAPTURE_REQ_CODE = 1235;

    public static boolean shouldShowUpdateDialog;


    @BindView(R.id.bottomNavigation)
    BottomNavigationView bottomNavigation;


    private ScreenGrabber screen;
    private DisplayMetrics rawDisplayMetrics;
    private boolean shouldRestartOnStopComplete;
    private boolean skipStartPogo;
    private View alertBadgeView;

    private BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView
            .OnNavigationItemSelectedListener() {
        @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            return showSection(item.getItemId());
        }
    };

    private final BroadcastReceiver pokeflyStateChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateLaunchButtonText(Pokefly.isRunning(), true);
            startPokeFlyOnStop();
        }
    };

    private final BroadcastReceiver showUpdateDialog = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
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

    private final BroadcastReceiver restartPokeFly = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            restartPokeFly(true);
        }
    };


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

    boolean hasAllPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            return false;
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_DENIED) {
            return false;
        }
        return true;
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Timber.tag(TAG);

        if (savedInstanceState == null) {
            bottomNavigation.setSelectedItemId(R.id.menu_home);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content, new MainFragment(), TAG_FRAGMENT_CONTENT)
                    .commit();
        }

        bottomNavigation.setOnNavigationItemSelectedListener(navigationListener);

        runAutoUpdateStartupChecks();
        initiateUserScreenSettings();
        warnUserFirstLaunchIfNoScreenRecording();
        registerAllBroadcastReceivers();
    }

    private boolean showSection(final @IdRes int sectionId) {
        final Class<? extends Fragment> newSectionClass;
        switch (sectionId) {
            case R.id.menu_recalibrate:
                newSectionClass = RecalibrateFragment.class;
                break;
            default:
            case R.id.menu_home:
                newSectionClass = MainFragment.class;
                break;
            case R.id.menu_clipboard:
                newSectionClass = ClipboardModifierParentFragment.class;
                break;
        }

        Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_CONTENT);

        if (!currentFragment.getClass().equals(newSectionClass)) {
            // The user requested a section change

            Runnable discardedChangesRunnable = new Runnable() {
                @Override public void run() {
                    // Changes discarded, go to the selected section
                    try {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.content,
                                        newSectionClass.newInstance(),
                                        TAG_FRAGMENT_CONTENT)
                                .commitAllowingStateLoss();
                        // Remove the listener so this callback won't be fired when setSelectedItemId() is called
                        bottomNavigation.setOnNavigationItemSelectedListener(null);
                        bottomNavigation.setSelectedItemId(sectionId);
                        bottomNavigation.setOnNavigationItemSelectedListener(navigationListener);
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            };

            if (checkUnsavedClipboardBeforeLeaving(discardedChangesRunnable)) {
                // Unsaved changes: stay on the current section
                return false;
            } else {
                // No unsaved changes, go to the selected section
                try {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content,
                                    newSectionClass.newInstance(),
                                    TAG_FRAGMENT_CONTENT)
                            .commitAllowingStateLoss();
                } catch (Exception e) {
                    Timber.e(e);
                }
            }
        }
        return true;
    }

    /**
     * Makes the localBroadcastManager register recievers for the different accepted intents, and tells
     * the app to
     * actually do something when those intents are received.
     */

    private void registerAllBroadcastReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(pokeflyStateChanged,
                new IntentFilter(Pokefly.ACTION_UPDATE_UI));
        LocalBroadcastManager.getInstance(this).registerReceiver(showUpdateDialog,
                new IntentFilter(ACTION_SHOW_UPDATE_DIALOG));
        LocalBroadcastManager.getInstance(this).registerReceiver(restartPokeFly,
                new IntentFilter(ACTION_RESTART_POKEFLY));

        runActionOnIntent(getIntent());
    }

    /**
     * Runs the initialization logic related to the user screen, taking measurements so the ocr will scan the right
     * areas.
     */
    private void initiateUserScreenSettings() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        rawDisplayMetrics = new DisplayMetrics();
        //noinspection ConstantConditions
        Display display = windowManager.getDefaultDisplay();
        display.getRealMetrics(rawDisplayMetrics);
    }

    /**
     * Checks for any published updates if auto-updater settings is on and deletes previous updates.
     */
    private void runAutoUpdateStartupChecks() {
        shouldShowUpdateDialog = true;
        AppUpdateUtil.deletePreviousApkFile(MainActivity.this);
        if (GoIVSettings.getInstance(this).isAutoUpdateEnabled()) {
            AppUpdateUtil.checkForUpdate(this);
        }
    }

    /**
     * Shows an alert warning the user about not being on android 5, and that the app is locked into screenshot mode.
     */
    private void warnUserFirstLaunchIfNoScreenRecording() {
        boolean userOnBelowAndroid5 = Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT_WATCH;
        boolean hasNotShownAndroid5Warning = !GoIVSettings.getInstance(this).hasShownNoScreenRecWarning();
        if (hasNotShownAndroid5Warning && userOnBelowAndroid5) {
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.android_sub5_warning)
                    .setPositiveButton(android.R.string.ok, null)
                    .show();

            GoIVSettings.getInstance(this).setHasShownScreenRecWarning();
        }
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
        Intent stopIntent = Pokefly.createStopIntent(this);
        startService(stopIntent);
        if (screen != null) {
            screen.exit();
        }
    }

    @SuppressLint("NewApi")
    private void startGoIV() {
        boolean screenshotMode = GoIVSettings.getInstance(this).isManualScreenshotModeEnabled();
        if (screenshotMode) {
            startPokeFly();

        } else { // Start screen capture then, when ready, Pokefly will be started
            MainFragment.updateLaunchButtonText(this, R.string.accept_screen_capture, null);
            MediaProjectionManager projectionManager =
                    (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            //noinspection ConstantConditions
            startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_CAPTURE_REQ_CODE);
        }
    }

    private void startPoGoIfSettingOn() {
        if (GoIVSettings.getInstance(this).shouldLaunchPokemonGo() && !skipStartPogo) {
            openPokemonGoApp();
        }
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

        initRecalibrationAlertBadge();

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
        MainFragment.updateLaunchButtonText(this, R.string.main_starting, false);

        startPoGoIfSettingOn();

        Intent intent = Pokefly.createStartIntent(this, getStatusBarHeight());
        startService(intent);

        skipStartPogo = false;
    }

    private void updateLaunchButtonText(boolean isPokeflyRunning, @Nullable Boolean enableButton) {
        if (!hasAllPermissions()) {
            MainFragment.updateLaunchButtonText(this, R.string.main_permission, enableButton);
        } else if (isPokeflyRunning) {
            MainFragment.updateLaunchButtonText(this, R.string.main_stop, enableButton);
        } else {
            MainFragment.updateLaunchButtonText(this, R.string.main_start, enableButton);
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(pokeflyStateChanged);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(showUpdateDialog);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(restartPokeFly);
        super.onDestroy();
    }

    /**
     * Called when another activity has sent a result to this activity.
     * For example when this activity starts the activity which calls for the MediaProjectionManager, which tells this
     * class if the screen capture has been enabled.
     */
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            updateLaunchButtonText(false, null);

        } else if (requestCode == SCREEN_CAPTURE_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(
                        Context.MEDIA_PROJECTION_SERVICE);
                //noinspection ConstantConditions
                MediaProjection mProjection = projectionManager.getMediaProjection(resultCode, data);
                screen = ScreenGrabber.init(mProjection, rawDisplayMetrics);

                startPokeFly();
            } else {
                updateLaunchButtonText(false, null);
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
            updateLaunchButtonText(false, null);
        }
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
        if (intent != null) {
            if (ACTION_START_POKEFLY.equals(intent.getAction()) && !Pokefly.isRunning()) {
                runStartButtonLogic();
            }
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
            runStartButtonLogic();
        }
    }

    /**
     * We want to restart when pokefly is finally stopped, check that its not running, then call the button.
     */
    private void startPokeFlyOnStop() {
        if (!Pokefly.isRunning() && shouldRestartOnStopComplete) {
            //we are done restarting, no need to restart again
            shouldRestartOnStopComplete = false;
            runStartButtonLogic();
        }
    }

    @Override
    public void onBackPressed() {
        Runnable onDiscardChangesRunnable = new Runnable() {
            @Override public void run() {
                // User discarded changes
                MainActivity.super.onBackPressed();
            }
        };
        if (!checkUnsavedClipboardBeforeLeaving(onDiscardChangesRunnable)) {
            // No unsaved changes
            super.onBackPressed();
        }
    }

    @SuppressLint("RestrictedApi")
    private void initRecalibrationAlertBadge() {
        if (GoIVSettings.getInstance(this).hasUpToDateManualScanCalibration()) {
            // Calibration is up-to-date
            if (alertBadgeView != null && alertBadgeView.getParent() != null) {
                // Remove badge view
                ((ViewGroup) alertBadgeView.getParent()).removeView(alertBadgeView);
            }

        } else {
            // Calibration is outdated: add an alert badge to recalibrate section bottom menu icon
            if (alertBadgeView == null || alertBadgeView.getParent() == null) {
                // Alert badge view is not attached to the layout, add it
                BottomNavigationMenuView bottomNavigationMenuView =
                        (BottomNavigationMenuView) bottomNavigation.getChildAt(0);
                for (int i = 0; i < bottomNavigationMenuView.getChildCount(); i++) {
                    View itemView = bottomNavigationMenuView.getChildAt(i);
                    if (itemView instanceof BottomNavigationItemView) {
                        int itemViewId = ((BottomNavigationItemView) itemView).getItemData().getItemId();
                        if (R.id.menu_recalibrate == itemViewId) {
                            if (alertBadgeView == null) {
                                alertBadgeView = LayoutInflater.from(this)
                                        .inflate(R.layout.alert_badge, (BottomNavigationItemView) itemView, false);
                            }
                            ((BottomNavigationItemView) itemView).addView(alertBadgeView);
                        }
                    }
                }
            }
            // Animate the exclamation mark to draw user attention
            startWobbleAnimator(alertBadgeView.findViewById(R.id.exclamationMark));
        }
    }

    private void startWobbleAnimator(@NonNull View targetView) {
        Keyframe rkf0 = Keyframe.ofFloat(0f, 0f);
        Keyframe rkf1 = Keyframe.ofFloat(.25f, 10f);
        Keyframe rkf2 = Keyframe.ofFloat(.75f, -10f);
        Keyframe rkf3 = Keyframe.ofFloat(1f, 0f);
        PropertyValuesHolder rpvh = PropertyValuesHolder.ofKeyframe(View.ROTATION, rkf0, rkf1, rkf2, rkf3);
        ObjectAnimator rotate = ObjectAnimator.ofPropertyValuesHolder(targetView, rpvh);
        rotate.setRepeatMode(ObjectAnimator.RESTART);
        rotate.setRepeatCount(9);
        rotate.setDuration(500);

        float dX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics());
        Keyframe tkf0 = Keyframe.ofFloat(0f, 0);
        Keyframe tkf1 = Keyframe.ofFloat(.25f, dX);
        Keyframe tkf2 = Keyframe.ofFloat(.75f, -dX);
        Keyframe tkf3 = Keyframe.ofFloat(1f, 0f);
        PropertyValuesHolder tpvh = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X, tkf0, tkf1, tkf2, tkf3);
        ObjectAnimator translate = ObjectAnimator.ofPropertyValuesHolder(targetView, tpvh);
        translate.setRepeatMode(ObjectAnimator.RESTART);
        translate.setRepeatCount(9);
        translate.setDuration(500);

        AnimatorSet set = new AnimatorSet();
        set.setStartDelay(1000);
        set.playTogether(rotate, translate);
        set.start();
    }

    /**
     * Check if there are unsaved clipboard changes.
     * @param onDiscardChangesRunnable This will be run on the main thread ig the user decides to discard the changes
     * @return true if there are unsaved changes
     */
    private boolean checkUnsavedClipboardBeforeLeaving(final Runnable onDiscardChangesRunnable) {
        boolean unsavedChanges = false;
        for (Fragment pf : getSupportFragmentManager().getFragments()) {
            if (pf instanceof ClipboardModifierParentFragment) {
                for (Fragment cf : pf.getChildFragmentManager().getFragments()) {
                    if (cf instanceof ClipboardModifierChildFragment) {
                        unsavedChanges |= ((ClipboardModifierChildFragment) cf).hasUnsavedChanges();
                    }
                }
            }
        }
        if (unsavedChanges) {
            new AlertDialog.Builder(this)
                    .setTitle(android.R.string.dialog_alert_title)
                    .setMessage(R.string.discard_unsaved_changes)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {
                            runOnUiThread(onDiscardChangesRunnable);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        }
        return unsavedChanges;
    }

}

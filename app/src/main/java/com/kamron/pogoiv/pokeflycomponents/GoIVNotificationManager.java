package com.kamron.pogoiv.pokeflycomponents;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.ScreenShotHelper;
import com.kamron.pogoiv.activities.MainActivity;
import com.kamron.pogoiv.activities.OcrCalibrationResultActivity;

/**
 * Created by johan on 2017-07-06.
 * <p>
 * An object which can display a running or paused notification for goiv
 */

public class GoIVNotificationManager {

    private static final int NOTIFICATION_REQ_CODE = 8959;

    private static final String NOTIFICATION_CHANNEL_ID = "8959";

    private static Pokefly pokefly;

    private static final String ACTION_RECALIBRATE_SCANAREA = "com.kamron.pogoiv.ACTION_RECALIBRATE_SCANAREA";

    public GoIVNotificationManager(Pokefly pokefly) {
        GoIVNotificationManager.pokefly = pokefly;
    }


    /**
     * Show a paused notification in the system notification tray.
     */
    public void showPausedNotification() {
        // Prepare views
        RemoteViews contentView =
                new RemoteViews(pokefly.getPackageName(), R.layout.notification_pokefly_paused);
        RemoteViews contentBigView =
                new RemoteViews(pokefly.getPackageName(), R.layout.notification_pokefly_paused_expanded);

        // Open app action
        Intent openAppIntent = new Intent(pokefly, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                pokefly, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.root, openAppPendingIntent);
        contentBigView.setOnClickPendingIntent(R.id.root, openAppPendingIntent);

        // Open settings action
        Intent startSettingAppIntent = new Intent(pokefly, MainActivity.class)
                .setAction(MainActivity.ACTION_OPEN_SETTINGS);
        PendingIntent startSettingsPendingIntent = PendingIntent.getActivity(
                pokefly, 0, startSettingAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.settings, startSettingsPendingIntent);
        contentBigView.setOnClickPendingIntent(R.id.settings, startSettingsPendingIntent);

        // Start pokefly action
        Intent startServiceIntent = new Intent(pokefly, MainActivity.class)
                .setAction(MainActivity.ACTION_START_POKEFLY);
        PendingIntent startServicePendingIntent = PendingIntent.getActivity(
                pokefly, 0, startServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.start, startServicePendingIntent);
        contentBigView.setOnClickPendingIntent(R.id.start, startServicePendingIntent);

        // Build notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(pokefly, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(pokefly.getString(R.string.notification_title_goiv_stopped))
                .setContentText(pokefly.getString(R.string.notification_title_tap_to_open))
                .setColor(pokefly.getColorC(R.color.colorAccent))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setContent(contentView)
                .setCustomBigContentView(contentBigView)
                .setOngoing(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification
                    .setCustomContentView(contentView)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        }

        NotificationManager notificationManager =
                (NotificationManager) pokefly.getSystemService(Context.NOTIFICATION_SERVICE);

        initNotificationChannel(notificationManager);

        notificationManager.notify(NOTIFICATION_REQ_CODE, notification.build());
    }

    /**
     * Show a running notification in the system notification tray.
     */
    public void showRunningNotification() {
        // Prepare views
        RemoteViews contentView =
                new RemoteViews(pokefly.getPackageName(), R.layout.notification_pokefly_started);
        RemoteViews contentBigView =
                new RemoteViews(pokefly.getPackageName(), R.layout.notification_pokefly_started_expanded);
        contentView.setTextViewText(R.id.notification_title,
                pokefly.getString(R.string.notification_title_short, pokefly.getTrainerLevel()));
        contentBigView.setTextViewText(R.id.notification_title,
                pokefly.getString(R.string.notification_title, pokefly.getTrainerLevel()));

        // Open app action
        Intent openAppIntent = new Intent(pokefly, MainActivity.class);
        PendingIntent openAppPendingIntent = PendingIntent.getActivity(
                pokefly, 0, openAppIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.root, openAppPendingIntent);
        contentBigView.setOnClickPendingIntent(R.id.root, openAppPendingIntent);

        // Recalibrate action
        Intent recalibrateScreenScanningIntent = new Intent(pokefly, NotificationActionService.class)
                .setAction(ACTION_RECALIBRATE_SCANAREA);
        PendingIntent recalibrateScreenScanningPendingIntent = PendingIntent.getService(
                pokefly, 0, recalibrateScreenScanningIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.recalibrate, recalibrateScreenScanningPendingIntent);
        contentBigView.setOnClickPendingIntent(R.id.recalibrate, recalibrateScreenScanningPendingIntent);

        // Stop service action
        Intent stopServiceIntent = new Intent(pokefly, Pokefly.class)
                .setAction(Pokefly.ACTION_STOP);
        PendingIntent stopServicePendingIntent = PendingIntent.getService(
                pokefly, 0, stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.pause, stopServicePendingIntent);
        contentBigView.setOnClickPendingIntent(R.id.pause, stopServicePendingIntent);

        // Build notification
        NotificationCompat.Builder notification = new NotificationCompat.Builder(pokefly, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon_play)
                .setContentTitle(pokefly.getString(R.string.notification_title, pokefly.getTrainerLevel()))
                .setContentText(pokefly.getString(R.string.notification_title_tap_to_open))
                .setColor(pokefly.getColorC(R.color.colorPrimary))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setContent(contentView)
                .setCustomBigContentView(contentBigView)
                .setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            notification
                    .setCustomContentView(contentView)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle());
        }

        NotificationManager notificationManager =
                (NotificationManager) pokefly.getSystemService(Context.NOTIFICATION_SERVICE);

        initNotificationChannel(notificationManager);

        pokefly.startForeground(NOTIFICATION_REQ_CODE, notification.build());
    }

    private void initNotificationChannel(NotificationManager notificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && notificationManager.getNotificationChannel
                (NOTIFICATION_CHANNEL_ID) == null) {
            // Create notification channel
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "GoIV", NotificationManager.IMPORTANCE_DEFAULT);

            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * The class which receives the intent to recalibrate the scan area.
     */
    public static class NotificationActionService extends IntentService {
        public NotificationActionService() {
            super(NotificationActionService.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            String action = intent.getAction();
            if (ACTION_RECALIBRATE_SCANAREA.equals(action)) {
                Handler mainThreadHandler = new Handler(Looper.getMainLooper());

                if (GoIVSettings.getInstance(this).isManualScreenshotModeEnabled()) {
                    // Close the notification shade
                    sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
                    // Tell the user that the next screenshot will be used to recalibrate GoIV
                    ScreenShotHelper.sShouldRecalibrateWithNextScreenshot = true;
                    mainThreadHandler.post(new Runnable() {
                        @Override public void run() {
                            Toast.makeText(NotificationActionService.this,
                                    R.string.ocr_calibration_screenshot_mode, Toast.LENGTH_LONG).show();
                        }
                    });

                } else { // Start calibration!
                    // Close the notification shade so we can screenshot pogo
                    sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

                    mainThreadHandler.post(new Runnable() {
                        @Override public void run() {
                            if (pokefly != null && pokefly.getIvButton() != null) {
                                pokefly.getIvButton().setShown(false, false); // Hide IV button: it might interfere
                            }
                        }
                    });
                    mainThreadHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (ScreenGrabber.getInstance() == null) {
                                return; // Don't recalibrate when screen watching isn't running!!!
                            }

                            OcrCalibrationResultActivity.startCalibration(NotificationActionService.this,
                                    ScreenGrabber.getInstance().grabScreen());
                        }
                    }, 2000);
                }
            }
        }
    }
}
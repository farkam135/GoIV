package com.kamron.pogoiv.pokeflycomponents;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.activities.MainActivity;
import com.kamron.pogoiv.activities.OcrCalibrationResultActivity;

/**
 * Created by johan on 2017-07-06.
 * <p>
 * An object which can display a running or paused notification for goiv
 */

public class GoIVNotificationManager {

    private static final int NOTIFICATION_REQ_CODE = 8959;

    private static Pokefly pokefly;

    public static final String ACTION_RECALIBRATE_SCANAREA = "com.kamron.pogoiv.ACTION_RECALIBRATE_SCANAREA";

    public GoIVNotificationManager(Pokefly pokefly) {
        this.pokefly = pokefly;
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
        Notification notification = new NotificationCompat.Builder(pokefly)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(pokefly.getString(R.string.notification_title_goiv_stopped))
                .setContentText(pokefly.getString(R.string.notification_title_tap_to_open))
                .setColor(pokefly.getColorC(R.color.colorAccent))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setContent(contentView)
                .setCustomBigContentView(contentBigView)
                .setOngoing(false)
                .build();

        NotificationManager notificationManager =
                (NotificationManager) pokefly.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_REQ_CODE, notification);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent recalibrateScreenScanningIntent = new Intent(pokefly, NotificationActionService.class)
                    .setAction(ACTION_RECALIBRATE_SCANAREA);
            PendingIntent recalibrateScreenScanningPendingIntent = PendingIntent.getService(
                    pokefly, 0, recalibrateScreenScanningIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            contentView.setOnClickPendingIntent(R.id.recalibrate, recalibrateScreenScanningPendingIntent);
            contentBigView.setOnClickPendingIntent(R.id.recalibrate, recalibrateScreenScanningPendingIntent);
        } else {
            contentView.setViewVisibility(R.id.recalibrate, View.GONE);
            contentBigView.setViewVisibility(R.id.recalibrate, View.GONE);
        }

        // Stop service action
        Intent stopServiceIntent = new Intent(pokefly, Pokefly.class)
                .setAction(Pokefly.ACTION_STOP);
        PendingIntent stopServicePendingIntent = PendingIntent.getService(
                pokefly, 0, stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        contentView.setOnClickPendingIntent(R.id.pause, stopServicePendingIntent);
        contentBigView.setOnClickPendingIntent(R.id.pause, stopServicePendingIntent);

        // Build notification
        Notification notification = new NotificationCompat.Builder(pokefly)
                .setSmallIcon(R.drawable.notification_icon_play)
                .setContentTitle(pokefly.getString(R.string.notification_title, pokefly.getTrainerLevel()))
                .setContentText(pokefly.getString(R.string.notification_title_tap_to_open))
                .setColor(pokefly.getColorC(R.color.colorPrimary))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .setContent(contentView)
                .setCustomBigContentView(contentBigView)
                .setOngoing(true)
                .build();

        pokefly.startForeground(NOTIFICATION_REQ_CODE, notification);
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

                Handler handler = new Handler(Looper.getMainLooper());
                Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                pokefly.sendBroadcast(closeIntent); //closes the notification window so we can screenshot pogo

                handler.post(new Runnable() {
                    @Override public void run() {
                        pokefly.getIvButton().setShown(false, false); // Hide IV button: it might interfere
                    }
                });

                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Retry trice
                        for (int i = 0; i < 3; i++) {
                            OcrCalibrationResultActivity.sCalibrationImage = ScreenGrabber.getInstance().grabScreen();
                            if (OcrCalibrationResultActivity.sCalibrationImage != null) {
                                Intent showResultIntent = new Intent(pokefly, OcrCalibrationResultActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(showResultIntent);
                                break; // Stop retries
                            }
                        }
                    }
                }, 2000);
            }
        }
    }
}

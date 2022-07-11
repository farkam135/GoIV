package com.kamron.pogoiv;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.Log;

import com.kamron.pogoiv.activities.OcrCalibrationResultActivity;

import java.io.File;
import java.util.Calendar;

import timber.log.Timber;


public class ScreenShotHelper {

    private static ScreenShotHelper instance = null;
    public static boolean sShouldRecalibrateWithNextScreenshot = false;

    private ContentObserver mediaObserver;
    private ContentResolver contentResolver;

    private String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = contentResolver.query(contentUri, proj, null, null,
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

    private ScreenShotHelper(final Context context) {
        this.contentResolver = context.getContentResolver();
        mediaObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange, Uri uri) {
                super.onChange(selfChange, uri);
                if (!uri.toString().contains("images")) {
                    return;
                }

                final String pathChange = getRealPathFromUri(uri);
                if (!pathChange.contains("Screenshot")) {
                    return;
                }

                /* Ignore random events related to opening old screenshots by making
                 * sure the file was created within the past 10 seconds.
                 */
                long now = Calendar.getInstance().getTimeInMillis();
                long filetime = new File(pathChange).lastModified();
                if (now - filetime > 10000) {
                    return;
                }

                Bitmap bitmap = null;
                try {
                    bitmap = BitmapFactory.decodeFile(pathChange);
                } catch (Exception e) {
                    Timber.log(Log.ERROR, e);
                } finally {
                    if (bitmap != null) {
                        if (sShouldRecalibrateWithNextScreenshot) {
                            // Use the screenshot to recalibrate GoIV
                            OcrCalibrationResultActivity.startCalibration(context, bitmap, 0, 0);
                            sShouldRecalibrateWithNextScreenshot = false;
                            if (GoIVSettings.getInstance(context).shouldDeleteScreenshots()) {
                                deleteScreenShot(pathChange);
                            }
                        } else {
                            // Scan 'mon info
                            Intent newintent = Pokefly.createProcessBitmapIntent(bitmap, pathChange);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(newintent);
                        }
                    }
                }
            }
        };
        contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true,
                mediaObserver);
    }

    public static ScreenShotHelper start(final Context context) {
        if (instance == null) {
            instance = new ScreenShotHelper(context);
        }
        return instance;
    }

    public void deleteScreenShot(String filePath) {
        contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Files.FileColumns.DATA + "=?",
                new String[]{filePath});

    }

    public void stop() {
        contentResolver.unregisterContentObserver(mediaObserver);
        contentResolver = null;
        mediaObserver = null;
        instance = null;
    }
}

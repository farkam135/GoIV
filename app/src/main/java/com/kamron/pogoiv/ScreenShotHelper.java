package com.kamron.pogoiv;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.FileObserver;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;

/**
 * Created by Sarav on 9/9/2016.
 */
public class ScreenShotHelper {

    private static ScreenShotHelper instance = null;
    private FileObserver screenShotScanner;

    private ScreenShotHelper(final Context context, final String screenShotDir) {
        screenShotScanner = new FileObserver(screenShotDir, FileObserver.CLOSE_NOWRITE | FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, String file) {
                if (file != null) {
                    File pokemonScreenshot = new File(screenShotDir + File.separator + file);
                    String filepath = pokemonScreenshot.getAbsolutePath();
                    Bitmap bmp = BitmapFactory.decodeFile(filepath);
                    Intent newintent = Pokefly.createProcessBitmapIntent(bmp, filepath);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(newintent);
                }
            }
        };
        screenShotScanner.startWatching();
    }

    public static ScreenShotHelper start(final Context context, final String screenShotDir) {
        if (instance == null) {
            instance = new ScreenShotHelper(context, screenShotDir);
        }
        return instance;
    }

    public void stop() {
        screenShotScanner.stopWatching();
        instance = null;
    }
}

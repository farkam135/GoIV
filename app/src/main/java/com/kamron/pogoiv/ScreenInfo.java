package com.kamron.pogoiv;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import lombok.Getter;

/**
 * Created by pgiarrusso on 1/10/2016.
 */

public class ScreenInfo {
    @Getter
    private final Point arcInit = new Point();
    @Getter
    private int arcRadius;
    @Getter
    private final DisplayMetrics displayMetrics;
    @Getter
    private final DisplayMetrics rawDisplayMetrics;

    private static ScreenInfo singletonInstance;

    public static void init(Activity ctx) {
        singletonInstance = new ScreenInfo(ctx);
    }

    public static ScreenInfo getInstance() {
        return singletonInstance;
    }

    private ScreenInfo(Activity ctx) {
        this.displayMetrics = ctx.getResources().getDisplayMetrics();

        WindowManager windowManager = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        this.rawDisplayMetrics = new DisplayMetrics();
        Display disp = windowManager.getDefaultDisplay();
        disp.getRealMetrics(rawDisplayMetrics);

        setupDisplaySizeInfo(displayMetrics);
    }

    private void setupDisplaySizeInfo(DisplayMetrics displayMetrics) {
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
}

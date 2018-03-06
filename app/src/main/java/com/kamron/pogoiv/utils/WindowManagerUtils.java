package com.kamron.pogoiv.utils;


import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public final class WindowManagerUtils {

    private WindowManagerUtils() {
    }

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // Navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // Navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // Navigation bar is not present
        return new Point();
    }

    public static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //noinspection ConstantConditions
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        //noinspection ConstantConditions
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        return size;
    }

}

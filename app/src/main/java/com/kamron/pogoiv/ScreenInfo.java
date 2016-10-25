package com.kamron.pogoiv;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.kamron.pogoiv.logic.Data;

import lombok.Getter;

/**
 * Created by pgiarrusso on 1/10/2016.
 */

public class ScreenInfo {
    private int[] arcX;
    private int[] arcY;

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

    /**
     * setupArcPoints
     * Sets up the x,y coordinates of the arc using the trainer level, stores it in Data.arcX/arcY
     */
    public void setupArcPoints(int trainerLevel) {
        /*
         * Pokemon levels go from 1 to trainerLevel + 1.5, in increments of 0.5.
         * Here we use levelIdx for levels that are doubled and shifted by - 2; after this adjustment,
         * the level can be used to index CpM, arcX and arcY.
         */
        int maxPokeLevelIdx = Data.trainerLevelToMaxPokeLevelIdx(trainerLevel);
        arcX = new int[maxPokeLevelIdx + 1]; //We access entries [0..maxPokeLevelIdx], hence + 1.
        arcY = new int[maxPokeLevelIdx + 1];

        double baseCpM = Data.getLevelIdxCpM(0);
        //TODO: debug this formula when we get to the end of CpM (that is, levels 39/40).
        double maxPokeCpMDelta = Data.getLevelIdxCpM(Math.min(maxPokeLevelIdx + 1, Data.getCpMLength() - 1)) - baseCpM;

        //pokeLevelIdx <= maxPokeLevelIdx ensures we never overflow CpM/arc/arcY.
        for (int pokeLevelIdx = 0; pokeLevelIdx <= maxPokeLevelIdx; pokeLevelIdx++) {
            double pokeCurrCpMDelta = Data.getLevelIdxCpM(pokeLevelIdx) - baseCpM;
            double arcRatio = pokeCurrCpMDelta / maxPokeCpMDelta;
            double angleInRadians = (arcRatio + 1) * Math.PI;

            arcX[pokeLevelIdx] = (int) (arcInit.x + (arcRadius * Math.cos(angleInRadians)));
            arcY[pokeLevelIdx] = (int) (arcInit.y + (arcRadius * Math.sin(angleInRadians)));
        }
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

    public Point getArcPoint(int levelIdx) {
        return new Point(arcX[levelIdx], arcY[levelIdx]);
    }
}

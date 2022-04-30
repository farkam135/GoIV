package com.kamron.pogoiv.pokeflycomponents;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import androidx.annotation.ColorInt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.pokeflycomponents.fractions.AppraisalFraction;
import com.kamron.pogoiv.utils.fractions.FractionManager;

import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.SCREEN_INFO_CARD_WHITE_HEX;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.SCREEN_INFO_CARD_WHITE_PIXEL;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.SCREEN_INFO_FAB_GREEN_HEX;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.SCREEN_INFO_FAB_GREEN_PIXEL;

/**
 * A class which checks the screen every time the user pressses the screen, and calls to the ivbutton and
 * quickivpreview when the user is on the pokemon screen.
 * Created by johan on 2017-07-06.
 */

public class ScreenWatcher {

    private static final int SCREEN_SCAN_INITIAL_DELAY_MS = 1000; // just to check if we left the screen
    private static final int SCREEN_SCAN_DELAY_MS = 700;
    private static final int SCREEN_SCAN_RETRIES = 4;

    private LinearLayout touchView;
    private WindowManager.LayoutParams touchViewParams;
    private Point[] area = new Point[2];
    private Integer[] areaColor = new Integer[2];

    private Handler screenScanHandler;
    private ScreenScan screenScanRunnable;
    private int screenScanRetries;

    private AppraisalManager appraisalManager;

    private Pokefly pokefly;
    private DisplayMetrics displayMetrics;
    private FractionManager fractionManager;

    public ScreenWatcher(Pokefly pokefly, FractionManager fractionManager, AppraisalManager appraisalManager) {
        this.pokefly = pokefly;
        this.displayMetrics = pokefly.getResources().getDisplayMetrics();
        this.fractionManager = fractionManager;
        this.appraisalManager = appraisalManager;

        initMarkerPixels();
    }

    /**
     * Initiates which pixels should be scanned during a screen scan to determine if the user is on the pokemon screen.
     */
    private void initMarkerPixels() {

        GoIVSettings set = GoIVSettings.getInstance(pokefly);
        if (set.hasManualScanCalibration()) {
            try {
                String wpps = set.getCalibrationValue(SCREEN_INFO_CARD_WHITE_PIXEL);
                int area0x = Integer.parseInt(wpps.split(",")[0]);
                int area0y = Integer.parseInt(wpps.split(",")[1]);

                String gpmp = set.getCalibrationValue(SCREEN_INFO_FAB_GREEN_PIXEL);
                int area1x = Integer.parseInt(gpmp.split(",")[0]);
                int area1y = Integer.parseInt(gpmp.split(",")[1]);

                area[0] = new Point(area0x, area0y);
                area[1] = new Point(area1x, area1y);

                areaColor[0] = Color.parseColor(set.getCalibrationValue(SCREEN_INFO_CARD_WHITE_HEX));
                areaColor[1] = Color.parseColor(set.getCalibrationValue(SCREEN_INFO_FAB_GREEN_HEX));
            } catch (Exception e) {
                Log.e("ScreenWatcher", e.getMessage());
            }
        }

        if (area[0] == null || area[1] == null) {
            area[0] = new Point(                // these values used to get "white" left of "power up"
                    (int) Math.round(displayMetrics.widthPixels * 0.041667),
                    (int) Math.round(displayMetrics.heightPixels * 0.8046875));
            area[1] = new Point(                // these values used to get greenish color in transfer button
                    (int) Math.round(displayMetrics.widthPixels * 0.862445),
                    (int) Math.round(displayMetrics.heightPixels * 0.9004));
        }
    }

    /**
     * Scan a screen every time a user presses the screen, and trigger the quickIvPreview and IVButton to show if
     * he's on a pokemon screen.
     */
    @SuppressLint("RtlHardcoded")
    public void watchScreen() {
        screenScanHandler = new Handler();
        screenScanRunnable = new ScreenScan();

        touchView = new LinearLayout(pokefly);
        //noinspection deprecation
        touchViewParams = new WindowManager.LayoutParams(
                1,
                1,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSPARENT);
        touchViewParams.gravity = Gravity.LEFT | Gravity.TOP;

        touchView.setOnTouchListener(new GoIVOnTouchEventLogic());
        WindowManager windowManager = (WindowManager) pokefly.getSystemService(Pokefly.WINDOW_SERVICE);
        windowManager.addView(touchView, touchViewParams);
    }

    /**
     * isUserOnPokemonScreen
     * Scans the device screen to check area[0] for the white and area[1] for the transfer button.
     * If both exist then the user is on the pokemon screen.
     */
    private boolean isUserOnPokemonScreen() {
        if (ScreenGrabber.getInstance() != null) {
            @ColorInt int[] pixels = ScreenGrabber.getInstance().grabPixels(area);
            if (pixels != null) {
                if (areaColor[0] != null) {
                    return pixels[0] == areaColor[0] && pixels[1] == areaColor[1];
                } else {
                    return (pixels[0] == Color.rgb(250, 250, 250) || pixels[0] == Color.rgb(249, 249, 249))
                            && pixels[1] == Color.rgb(28, 135, 150);
                }
            }
        }
        return false;
    }

    /**
     * Used to cancel any pending screen scan (and consequent quick IV preview scan) when the user requested to Pokefly
     * to take a screenshot and scan the Pokémon (due to a press on the IVPopupButton).
     */
    public void cancelPendingScreenScan() {
        if (screenScanHandler != null) {
            screenScanHandler.removeCallbacks(screenScanRunnable);
        }
    }

    /**
     * The running method which is called initially by the touchevent, and which calls itself like an echo a couple
     * of times just to retry - in case the user is running a particularly fast / slow phone.
     */
    private class ScreenScan implements Runnable {
        @Override public void run() {
            if (screenScanRetries > 0) {
                if (isUserOnPokemonScreen()) {
                    screenScanRetries = 0; //skip further retries.
                    pokefly.getIvButton().setShown(true, pokefly.getInfoShownSent());
                    pokefly.getIvPreviewPrinter().printIVPreview(pokefly.getIvButton());

                } else {
                    screenScanRetries--;
                    screenScanHandler.postDelayed(screenScanRunnable, SCREEN_SCAN_DELAY_MS);
                    pokefly.getIvButton().setShown(false, pokefly.getInfoShownSent());
                }
            }
        }
    }

    /**
     * The logic which should run every time goiv is running and the user presses the screen.
     */
    private class GoIVOnTouchEventLogic implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // Let's check first to see if the user is performing an Appraisal
            if (!GoIVSettings.getInstance(pokefly).isManualScreenshotModeEnabled()
                    && fractionManager.currentFractionIsInstanceOf(AppraisalFraction.class)) {
                // Let appraisalManager know that the user has touched the PokemonGo app while the
                // appraisalBox was Visible.  This is our indication that the user has started a Pogo appraisal
                appraisalManager.screenTouched();
            } else {
                // Not appraising, let's check to see if they're looking at a pokemon screen.
                // The postDelayed will wait SCREEN_SCAN_DELAY_MS after the user touches the screen before
                // performing a scan of the screen to detect the pixels associated with a Pokemon screen.
                if (screenScanHandler != null) { // Ensure this is not executed after unwatchScreen()
                    screenScanHandler.removeCallbacks(screenScanRunnable);
                    screenScanHandler.postDelayed(screenScanRunnable, SCREEN_SCAN_INITIAL_DELAY_MS);
                    screenScanRetries = SCREEN_SCAN_RETRIES;
                    pokefly.getIvButton().outsideScreenClicked();
                }
            }
            return false;
        }
    }

    /**
     * Undoes the effects of watchScreen.
     */
    public void unwatchScreen() {
        WindowManager windowManager = (WindowManager) pokefly.getSystemService(Pokefly.WINDOW_SERVICE);
        windowManager.removeView(touchView);
        touchViewParams = null;
        touchView = null;
        screenScanHandler.removeCallbacks(screenScanRunnable);
        screenScanRunnable = null;
        screenScanHandler = null;
    }
}

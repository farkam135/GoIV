package com.kamron.pogoiv.pokeflycomponents;

import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.OcrHelper;
import com.kamron.pogoiv.scanlogic.IVCombination;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Johan on 2016-12-01.
 * A class to handle automatic scanning of appraisal information.
 */
public class AppraisalManager {

    private ScreenScan autoScreenScanner;
    private Handler handler = new Handler();
    private Pokefly pokefly;

    private ScreenGrabber screenGrabber;

    private ArrayList<OnAppraisalEventListener> eventListeners = new ArrayList<>();

    private static final int SCANRETRIES = 5; // max num of retries if appraisal text doesn't match
    private static final int RETRYDELAY = 200; // ms delay between retry scans
    private int numTouches = 0;
    private boolean autoAppraisalDone = false;

    public int attack = 0;
    public boolean attackValid = false;
    public int defense = 0;
    public boolean defenseValid = false;
    public int stamina = 0;
    public boolean staminaValid = false;

    /**
     * Instantiate the appraisal logic handler. If screenGrabber is not null, enables auto appraisal.
     * @param screenGrabber The helper class that gets the screenshots from the MediaProjection API
     */
    public AppraisalManager(@Nullable ScreenGrabber screenGrabber, @NonNull Pokefly pokefly) {
        this.screenGrabber = screenGrabber;
        GoIVSettings settings = GoIVSettings.getInstance(pokefly);
        this.autoScreenScanner = new ScreenScan(settings.getAutoAppraisalScanDelay());
        this.pokefly = pokefly;
    }

    public void addOnAppraisalEventListener(OnAppraisalEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void removeOnAppraisalEventListener(OnAppraisalEventListener eventListener) {
        eventListeners.remove(eventListener);
    }

    public void screenTouched() {
        numTouches++;

        // First touch is usually the Pokemon Go menu button in the bottom right of the Pokemon screen.
        // Although, it's entirely possible for the user to touch the area below (or above) GoIV an unlimited number
        // of times without actually ever starting the appraisal process

        if ((numTouches > 2) && (!autoAppraisalDone)) {
            // Signal to the user that we're now looking for the first appraisal phase.
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.highlightActiveUserInterface();
            }
            // Scan Appraisal text after the configured delay.
            autoScreenScanner.post();
        }
    }

    /**
     * Resets any necessary variables to their default states for the next Appraisal process.
     */
    public void reset() {
        // Delete values
        attack = 0;
        attackValid = false;
        defense = 0;
        defenseValid = false;
        stamina = 0;
        staminaValid = false;

        // Reset state
        numTouches = 0;
        autoAppraisalDone = false;
    }

    private void addStatScanResult(@Nullable IVCombination combination) {
        if (combination != null) {
            attack = combination.att;
            attackValid = true;
            defense = combination.def;
            defenseValid = true;
            stamina = combination.sta;
            staminaValid = true;
            autoAppraisalDone = true;
        }
        // Refresh the selection
        for (OnAppraisalEventListener eventListener : eventListeners) {
            eventListener.refreshSelection();
        }
    }


    /**
     * The task which looks at the bottom of the screen, and adds any info it finds.  This method then calls
     * addStatScanResult which performs the work of matching phrases to determine what should be checked.
     */
    private class ScreenScan implements Runnable {

        private static final int STAT_COUNT = 3;
        private static final int ALLOWED_DISTANCE = 6;
        private static final int COLOR_WHITE = 0xffFFFFFF;
        private static final int COLOR_GRAY = 0xffE2E2E2;
        private static final int COLOR_ORANGE = 0xffEE9219;
        private static final int COLOR_RED = 0xffE18079;

        private static final float OFFSET_SPEECH_TOP = 0.16f;  // 0.22f if the text has always at least two rows

        private int barStart;
        private float stepWidth;
        private int barLength;
        private int[] barCenter;

        // If this is null => initialize all variables on first scan, otherwise only do an update
        private int[][] barData;
        private int retries = 0;

        private int initialDelay;

        ScreenScan(int initialDelay) {
            this.initialDelay = initialDelay;
        }

        void post() {
            barData = null;
            retries = 0;
            handler.removeCallbacks(this);
            handler.postDelayed(this, initialDelay);
        }

        private void initScreen(Bitmap screen) {
            // Find top border of the speech bubble (scan to the left of the text to be sure that there is only white)
            int offset = screen.getHeight() - pokefly.getCurrentNavigationBarHeight() - (int) (
                    screen.getWidth() * OFFSET_SPEECH_TOP);
            int x = (int) (screen.getWidth() * 0.04f);
            int color;
            do {
                color = screen.getPixel(x, offset);
                offset -= 2;  // it is probably save to not scan EVERY pixel
            }
            while (color == COLOR_WHITE);

            Timber.d("Appraisal speech bubble top: %d", offset);

            this.barLength = (int) (screen.getWidth() * 0.33f);
            // Prevent rounding errors, especially as it gets multiplied by 15 again later
            this.stepWidth = screen.getWidth() / 15.0f * 0.33f;
            this.barStart = (int) (screen.getWidth() * 0.136f);

            // We are at the top of the speech bubble, we can now just use the relative values from here
            offset -= (int) (screen.getWidth() * 0.06f); // Offset should be now just below the first bar
            this.barCenter = new int[STAT_COUNT];
            for (int i = STAT_COUNT - 1; i >= 0; i--) {
                boolean inside = false;
                int bottom = -1;
                do {
                    color = screen.getPixel((int) (this.barStart + this.stepWidth), offset);
                    if (!inside && color != COLOR_WHITE) {
                        bottom = offset;
                        inside = true;
                    }
                    offset -= 1;
                }
                while ((color == COLOR_WHITE) ^ inside);
                // offset = top y of bar, bottom = bottom y of bar
                this.barCenter[i] = offset + (bottom - offset) / 2;
                offset -= (int) (screen.getWidth() * 0.06f);
                Timber.d("Appraisal stat bar #%d: x=%d, y=%d", i, barStart, this.barCenter[i]);
            }

            this.barData = new int[3][this.barLength];
        }

        @Override
        public void run() {
            Bitmap screen = screenGrabber.grabScreen();
            if (screen != null) {
                if (barData == null) {
                    initScreen(screen);
                }

                boolean change = false;
                for (int i = this.barData.length - 1; i >= 0; i--) {
                    int[] buffer = new int[this.barLength];
                    screen.getPixels(buffer, 0, this.barLength, this.barStart, this.barCenter[i], this.barLength, 1);
                    if (!Arrays.equals(buffer, this.barData[i])) {
                        Timber.d("Appraisal stat bar #%d has changed", i);
                        change = true;
                        this.barData[i] = buffer;
                    }
                }

                if (change && retries < SCANRETRIES) {
                    retries++;
                    handler.postDelayed(this, RETRYDELAY);
                } else {
                    int[] width = new int[this.barData.length];
                    // It scans them from the bottom to the top, so invert it either here to below in the creation
                    for (int i = 0; i < this.barData.length; i++) {
                        int color;
                        // The "do {} while" below loop increments it at least once
                        width[i] = -1;
                        do {
                            width[i]++;
                            color = this.barData[i][(int) (width[i] * this.stepWidth)];
                        }
                        while (OcrHelper.isInColorRange(color , COLOR_ORANGE, ALLOWED_DISTANCE));

                        if (OcrHelper.isInColorRange(color , COLOR_RED, ALLOWED_DISTANCE)) {
                            width[i] = 15;
                        } else if (!OcrHelper.isInColorRange(color, COLOR_GRAY, ALLOWED_DISTANCE)) {
                            Timber.d("Invalid scan on bar #%d (color %08x)", i, color);
                            width = null;
                            break;
                        }
                    }
                    if (width == null) {
                        addStatScanResult(null);
                    } else {
                        addStatScanResult(new IVCombination(width[0], width[1], width[2]));
                    }
                }
            }
        }
    }

    public interface OnAppraisalEventListener {
        void refreshSelection();

        void highlightActiveUserInterface();
    }

}

package com.kamron.pogoiv.pokeflycomponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.OcrHelper;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Johan on 2016-12-01.
 * A class to handle automatic scanning of appraisal information.
 */
public class AppraisalManager {

    private ScreenScan autoScreenScanner = new ScreenScan();
    private Handler handler = new Handler();
    private GoIVSettings settings;

    private ScreenGrabber screenGrabber;

    private ArrayList<OnAppraisalEventListener> eventListeners = new ArrayList<>();

    private static final int SCANRETRIES = 3; // max num of retries if appraisal text doesn't match
    private static final int RETRYDELAY = 50; // ms delay between retry scans
    private int numTouches = 0;
    private int numRetries = 0;
    private boolean autoAppraisalDone = false;

    public IVSumRange appraisalIVSumRange = IVSumRange.UNKNOWN;
    public HashSet<HighestStat> highestStats = new HashSet<>();
    public IVValueRange appraisalHighestStatValueRange = IVValueRange.UNKNOWN;
    public HashSet<StatModifier> statModifiers = new HashSet<>();

    //Appraisal phrases
    private String highest_stat_att;
    private String highest_stat_def;
    private String highest_stat_hp;
    private String ivrange1_phrase1;
    private String ivrange1_phrase2;
    private String ivrange2_phrase1;
    private String ivrange2_phrase2;
    private String ivrange3_phrase1;
    private String ivrange3_phrase2;
    private String ivrange4_phrase1;
    private String ivrange4_phrase2;
    private String statsrange1_phrase1;
    private String statsrange1_phrase2;
    private String statsrange2_phrase1;
    private String statsrange2_phrase2;
    private String statsrange3_phrase1;
    private String statsrange3_phrase2;
    private String statsrange4_phrase1;
    private String statsrange4_phrase2;


    /**
     * Instantiate the appraisal logic handler. If screenGrabber is not null, enables auto appraisal.
     * @param screenGrabber The helper class that gets the screenshots from the MediaProjection API
     */
    public AppraisalManager(@Nullable ScreenGrabber screenGrabber, @NonNull Context context) {
        this.screenGrabber = screenGrabber;
        settings = GoIVSettings.getInstance(context);
        getAppraisalPhrases(context);
    }

    public void addOnAppraisalEventListener(OnAppraisalEventListener eventListener) {
        eventListeners.add(eventListener);
    }

    public void removeOnAppraisalEventListener(OnAppraisalEventListener eventListener) {
        eventListeners.remove(eventListener);
    }

    private void getAppraisalPhrases(@NonNull Context context) {
        highest_stat_att = context.getString(R.string.highest_stat_att);
        highest_stat_def = context.getString(R.string.highest_stat_def);
        highest_stat_hp = context.getString(R.string.highest_stat_hp);

        if (settings.playerTeam() == 0) {
            ivrange1_phrase1 = context.getString(R.string.mystic_percentage1_phrase1);
            ivrange1_phrase2 = context.getString(R.string.mystic_percentage1_phrase2);
            ivrange2_phrase1 = context.getString(R.string.mystic_percentage2_phrase1);
            ivrange2_phrase2 = context.getString(R.string.mystic_percentage2_phrase2);
            ivrange3_phrase1 = context.getString(R.string.mystic_percentage3_phrase1);
            ivrange3_phrase2 = context.getString(R.string.mystic_percentage3_phrase2);
            ivrange4_phrase1 = context.getString(R.string.mystic_percentage4_phrase1);
            ivrange4_phrase2 = context.getString(R.string.mystic_percentage4_phrase2);
            statsrange1_phrase1 = context.getString(R.string.mystic_ivrange1_phrase1);
            statsrange1_phrase2 = context.getString(R.string.mystic_ivrange1_phrase2);
            statsrange2_phrase1 = context.getString(R.string.mystic_ivrange2_phrase1);
            statsrange2_phrase2 = context.getString(R.string.mystic_ivrange2_phrase2);
            statsrange3_phrase1 = context.getString(R.string.mystic_ivrange3_phrase1);
            statsrange3_phrase2 = context.getString(R.string.mystic_ivrange3_phrase2);
            statsrange4_phrase1 = context.getString(R.string.mystic_ivrange4_phrase1);
            statsrange4_phrase2 = context.getString(R.string.mystic_ivrange4_phrase2);
        } else if (settings.playerTeam() == 1) {
            ivrange1_phrase1 = context.getString(R.string.valor_percentage1_phrase1);
            ivrange1_phrase2 = context.getString(R.string.valor_percentage1_phrase2);
            ivrange2_phrase1 = context.getString(R.string.valor_percentage2_phrase1);
            ivrange2_phrase2 = context.getString(R.string.valor_percentage2_phrase2);
            ivrange3_phrase1 = context.getString(R.string.valor_percentage3_phrase1);
            ivrange3_phrase2 = context.getString(R.string.valor_percentage3_phrase2);
            ivrange4_phrase1 = context.getString(R.string.valor_percentage4_phrase1);
            ivrange4_phrase2 = context.getString(R.string.valor_percentage4_phrase2);
            statsrange1_phrase1 = context.getString(R.string.valor_ivrange1_phrase1);
            statsrange1_phrase2 = context.getString(R.string.valor_ivrange1_phrase2);
            statsrange2_phrase1 = context.getString(R.string.valor_ivrange2_phrase1);
            statsrange2_phrase2 = context.getString(R.string.valor_ivrange2_phrase2);
            statsrange3_phrase1 = context.getString(R.string.valor_ivrange3_phrase1);
            statsrange3_phrase2 = context.getString(R.string.valor_ivrange3_phrase2);
            statsrange4_phrase1 = context.getString(R.string.valor_ivrange4_phrase1);
            statsrange4_phrase2 = context.getString(R.string.valor_ivrange4_phrase2);
        } else {
            ivrange1_phrase1 = context.getString(R.string.instinct_percentage1_phrase1);
            ivrange1_phrase2 = context.getString(R.string.instinct_percentage1_phrase2);
            ivrange2_phrase1 = context.getString(R.string.instinct_percentage2_phrase1);
            ivrange2_phrase2 = context.getString(R.string.instinct_percentage2_phrase2);
            ivrange3_phrase1 = context.getString(R.string.instinct_percentage3_phrase1);
            ivrange3_phrase2 = context.getString(R.string.instinct_percentage3_phrase2);
            ivrange4_phrase1 = context.getString(R.string.instinct_percentage4_phrase1);
            ivrange4_phrase2 = context.getString(R.string.instinct_percentage4_phrase2);
            statsrange1_phrase1 = context.getString(R.string.instinct_ivrange1_phrase1);
            statsrange1_phrase2 = context.getString(R.string.instinct_ivrange1_phrase2);
            statsrange2_phrase1 = context.getString(R.string.instinct_ivrange2_phrase1);
            statsrange2_phrase2 = context.getString(R.string.instinct_ivrange2_phrase2);
            statsrange3_phrase1 = context.getString(R.string.instinct_ivrange3_phrase1);
            statsrange3_phrase2 = context.getString(R.string.instinct_ivrange3_phrase2);
            statsrange4_phrase1 = context.getString(R.string.instinct_ivrange4_phrase1);
            statsrange4_phrase2 = context.getString(R.string.instinct_ivrange4_phrase2);
        }
    }

    public void screenTouched() {
        numTouches++;
        numRetries = 0;

        // First touch is usually the Pokemon Go menu button in the bottom right of the Pokemon screen.
        // Although, it's entirely possible for the user to touch the area below (or above) GoIV an unlimited number
        // of times without actually ever starting the appraisal process

        if (numTouches == 2) { // Second touch is usually the "Appraise" menu item.
            // Signal to the user that we're now looking for the first appraisal phase.
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.highlightActiveUserInterface();
            }
        } else if ((numTouches > 2) && (!autoAppraisalDone)) {
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.highlightActiveUserInterface();
            }
            // Scan Appraisal text after the configured delay.
            scanAppraisalText(settings.getAutoAppraisalScanDelay());
        } else if (autoAppraisalDone) {
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.resetActivatedUserInterface();
            }
        }
    }

    /**
     * Common method for setting a .postDelayed handler to scan the appraisal text after the specified milliseconds.
     * This allows for the same function to be used upon initial scan as well as the follow-up retry scans while
     * waiting for the appraisal text animation to finish.
     *
     * @param delay_millis The number of milliseconds that the scan will be delayed before firing off.
     */
    private void scanAppraisalText(int delay_millis) {
        handler.removeCallbacks(autoScreenScanner);
        handler.postDelayed(autoScreenScanner, delay_millis);
    }

    /**
     * Resets any necessary variables to their default states for the next Appraisal process.
     */
    public void reset() {
        // Delete values
        appraisalIVSumRange = IVSumRange.UNKNOWN;
        highestStats.clear();
        appraisalHighestStatValueRange = IVValueRange.UNKNOWN;
        statModifiers.clear();

        // Reset state
        numTouches = 0;
        numRetries = 0;
        autoAppraisalDone = false;

        for (OnAppraisalEventListener eventListener : eventListeners) {
            eventListener.resetActivatedUserInterface();
        }
    }

    /**
     * Alters the state of the AppraisalManager instance to reflect the added information of the appraise text.
     *
     * @param appraiseText Text such as "...pokemon is breathtaking..."
     * @param hash the hash of the bitmap used by ocr
     */
    private void addInfoFromAppraiseText(String appraiseText, String hash) {
        boolean match = false;

        if (appraisalIVSumRange == IVSumRange.UNKNOWN) {
            // Only if none of the IVRange checkboxes have been checked.
            // See if appraiseText matches any of the IVRange strings
            match = setIVRangeWith(appraiseText);
        }
        if (!match && appraisalIVSumRange != IVSumRange.UNKNOWN) {
            // Only if IVRange is done and have not matched yet.
            // See if appraiseText matches any of the Highest Stats strings
            match = setHighestStatsWith(appraiseText);
        }
        if (!match) { // Lastly, check if the appraiseText matches any of the Stat phrases
            match = setStatsRangeWith(appraiseText);
        }
        if (!match && numRetries < SCANRETRIES) { // If nothing matched and we have not yet reached maximum # of retries
            numRetries++;
            // Nothing matched, so this phrase should be thrown away.
            OcrHelper.removeEntryFromAppraisalCache(settings, hash);
            // Let's schedule another scan to see if animation has finished.
            scanAppraisalText(RETRYDELAY);
        } else if (!match) { // Nothing matched and we've ran out of retry attempts.
            // Nothing matched, so this phrase should be thrown away.
            OcrHelper.removeEntryFromAppraisalCache(settings, hash);
        }
    }

    /**
     * Selects the appropriate appraisalStatsGroup Checkbox depending on which phrase is matched.
     *
     * @param appraiseText the text to interpret.
     * @return boolean returns true if the appraiseText matched a configured phrase.
     */
    private boolean setStatsRangeWith(String appraiseText) {
        if (appraiseText.toLowerCase().contains(statsrange1_phrase1)
                || (appraiseText.toLowerCase().contains(statsrange1_phrase2))) {
            for (OnAppraisalEventListener eventListener: eventListeners) {
                eventListener.selectIVValueRange(IVValueRange.RANGE_15);
                eventListener.highlightActiveUserInterface();
            }
            autoAppraisalDone = true;
            return true;
        }
        if (appraiseText.toLowerCase().contains(statsrange2_phrase1)
                || (appraiseText.toLowerCase().contains(statsrange2_phrase2))) {
            for (OnAppraisalEventListener eventListener: eventListeners) {
                eventListener.selectIVValueRange(IVValueRange.RANGE_13_14);
                eventListener.highlightActiveUserInterface();
            }
            autoAppraisalDone = true;
            return true;
        }
        if (appraiseText.toLowerCase().contains(statsrange3_phrase1)
                || (appraiseText.toLowerCase().contains(statsrange3_phrase2))) {
            for (OnAppraisalEventListener eventListener: eventListeners) {
                eventListener.selectIVValueRange(IVValueRange.RANGE_8_12);
                eventListener.highlightActiveUserInterface();
            }
            autoAppraisalDone = true;
            return true;
        }
        if (appraiseText.toLowerCase().contains(statsrange4_phrase1)
                || (appraiseText.toLowerCase().contains(statsrange4_phrase2))) {
            for (OnAppraisalEventListener eventListener: eventListeners) {
                eventListener.selectIVValueRange(IVValueRange.RANGE_0_7);
                eventListener.highlightActiveUserInterface();
            }
            autoAppraisalDone = true;
            return true;
        }
        return false;
    }

    /**
     * Sets each of the highest stats as found within the appraisal phrases given.
     *
     * @param appraiseText the text to interpret.
     * @return boolean returns true if one of the highest stats phrase was matched.
     */
    private boolean setHighestStatsWith(String appraiseText) {
        if (appraiseText.toLowerCase().contains(highest_stat_att)) {
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.selectHighestStat(HighestStat.ATK);
            }
            return true;
        }
        if (appraiseText.toLowerCase().contains(highest_stat_def)) {
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.selectHighestStat(HighestStat.DEF);
            }
            return true;
        }
        if (appraiseText.toLowerCase().contains(highest_stat_hp)) {
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.selectHighestStat(HighestStat.STA);
            }
            return true;
        }
        return false;
    }

    /**
     * Selects the appropriate appraisalIVRangeGroup Checkbox depending on which phrase is matched.
     *
     * @param appraiseText the text to interpret.
     * @return boolean returns true if the appraiseText matched a configured phrase.
     */
    private boolean setIVRangeWith(String appraiseText) {
        if (appraiseText.toLowerCase().contains(ivrange1_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange1_phrase2))) {
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.selectIVSumRange(IVSumRange.RANGE_37_45);
            }
            return true;
        }
        if (appraiseText.toLowerCase().contains(ivrange2_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange2_phrase2))) {
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.selectIVSumRange(IVSumRange.RANGE_30_36);
            }
            return true;
        }
        if (appraiseText.toLowerCase().contains(ivrange3_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange3_phrase2))) {
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.selectIVSumRange(IVSumRange.RANGE_23_29);
            }
            return true;
        }
        if (appraiseText.toLowerCase().contains(ivrange4_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange4_phrase2))) {
            for (OnAppraisalEventListener eventListener : eventListeners) {
                eventListener.selectIVSumRange(IVSumRange.RANGE_0_22);
            }
            return true;
        }
        return false;
    }


    /**
     * The task which looks at the bottom of the screen, and adds any info it finds.  This method then calls
     * addInfoFromAppraiseText which performs the work of matching phrases to determine what should be checked.
     */
    private class ScreenScan implements Runnable {
        @Override
        public void run() {
            Bitmap screen = screenGrabber.grabScreen();
            if (screen != null) {
                String appraiseText = OcrHelper.getAppraisalText(settings, screen);
                String hash = appraiseText.substring(0, appraiseText.indexOf("#"));
                String text = appraiseText.substring(appraiseText.indexOf("#") + 1);
                addInfoFromAppraiseText(text, hash);
            }
        }
    }

    public enum IVSumRange {
        UNKNOWN(0, 45),
        RANGE_0_22(0, 22),
        RANGE_23_29(23, 29),
        RANGE_30_36(30, 36),
        RANGE_37_45(37, 45);

        public float minSum;
        public float maxSum;

        IVSumRange(int minSum, float maxSum) {
            this.minSum = minSum;
            this.maxSum = maxSum;
        }
    }

    public enum HighestStat {
        ATK,
        DEF,
        STA
    }

    public enum IVValueRange {
        UNKNOWN(0, 15),
        RANGE_0_7(0, 7),
        RANGE_8_12(8, 12),
        RANGE_13_14(13, 14),
        RANGE_15(15, 15);

        public int minValue;
        public int maxValue;

        IVValueRange(int minValue, int maxValue) {
            this.minValue = minValue;
            this.maxValue = maxValue;
        }
    }

    public enum StatModifier {
        EGG_OR_RAID(10),
        WEATHER_BOOST(4);

        public int minStat;

        StatModifier(int minStat) {
            this.minStat = minStat;
        }
    }

    public interface OnAppraisalEventListener {
        void selectIVSumRange(IVSumRange range);

        void selectHighestStat(HighestStat stat);

        void selectIVValueRange(IVValueRange range);

        void highlightActiveUserInterface();

        void resetActivatedUserInterface();
    }

}

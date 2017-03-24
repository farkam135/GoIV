package com.kamron.pogoiv;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

/**
 * Created by Johan on 2016-12-01.
 * A class to handle automatic scanning of appraisal information.
 */
public class AutoAppraisal {

    ScreenScan screenScanner = new ScreenScan(); //The runnable that keeps scanning the screen
    Handler handler = new Handler();
    private GoIVSettings settings;

    private OcrHelper ocr;
    private ScreenGrabber screenGrabber;
    Context context;

    private static final int SCANRETRIES = 3; // max num of retries if appraisal text doesn't match
    private static final int RETRYDELAY = 50; // ms delay between retry scans
    private int numTouches = 0;
    private int numRetries = 0;
    private int scanDelay;
    private boolean autoAppraisalDone = false;

    //UI elements in pokefly to modify.
    CheckBox attCheckbox;
    CheckBox defCheckbox;
    CheckBox staCheckbox;

    RadioGroup appraisalIVRangeGroup;
    RadioGroup appraisalStatsGroup;
    LinearLayout attDefStaLayout;

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


    public AutoAppraisal(ScreenGrabber screenGrabber, OcrHelper ocr, Context context, LinearLayout attDefStaLayout,
                         CheckBox attCheckbox, CheckBox defCheckbox, CheckBox staCheckbox,
                         RadioGroup appraisalIVRangeGroup, RadioGroup appraisalStatsGroup) {
        this.ocr = ocr;
        this.context = context;
        this.screenGrabber = screenGrabber;
        this.attDefStaLayout = attDefStaLayout;
        this.attCheckbox = attCheckbox;
        this.defCheckbox = defCheckbox;
        this.staCheckbox = staCheckbox;
        this.appraisalIVRangeGroup = appraisalIVRangeGroup;
        this.appraisalStatsGroup = appraisalStatsGroup;
        settings = GoIVSettings.getInstance(context);
        getAppraisalPhrases();
    }

    private void getAppraisalPhrases() {
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
            highlightActiveCheckboxGroup();
        } else if ((numTouches > 2) && (!autoAppraisalDone)) {
            // pickup possible changes of the setting
            scanDelay = settings.getAutoAppraisalScanDelay();
            highlightActiveCheckboxGroup();
            // Scan Appraisal text after the configured delay.
            scanAppraisalText(scanDelay);
        } else if (autoAppraisalDone) {
            resetBackgroundHighlights();
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
        handler.removeCallbacks(screenScanner);
        handler.postDelayed(screenScanner, delay_millis);
    }

    /**
     * Sets the background for the appropriate checkbox group depending on where we are at in the appraisal process.
     */
    private void highlightActiveCheckboxGroup() {
        resetBackgroundHighlights();
        if (!isIVRangeGroupDone()) {
            appraisalIVRangeGroup.setBackgroundResource(R.drawable.highlight_rectangle);
        } else if (isIVRangeGroupDone() && !isStatsGroupDone()) {
            attDefStaLayout.setBackgroundResource(R.drawable.highlight_rectangle);
        } else {
            appraisalStatsGroup.setBackgroundResource(R.drawable.highlight_rectangle);
        }
    }

    /**
     * Disables any background highlight that had previously been set.  This method is used as a quick way to remove
     * all backgrounds prior to setting again or when the auto appraisal process has completed.
     */
    private void resetBackgroundHighlights() {
        appraisalIVRangeGroup.setBackground(null);
        attDefStaLayout.setBackground(null);
        appraisalStatsGroup.setBackground(null);
    }

    /**
     * Resets any necessary variables to their default states for the next Appraisal process.
     */
    public void reset() {
        numTouches = 0;
        numRetries = 0;
        autoAppraisalDone = false;
        resetBackgroundHighlights();
    }

    /**
     * Alters the state of the AutoAppraisal instance to reflect the added information of the appraise text.
     *
     * @param appraiseText Text such as "...pokemon is breathtaking..."
     * @param hash the hash of the bitmap used by ocr
     */
    private void addInfoFromAppraiseText(String appraiseText, String hash) {
        boolean match = false;

        if (!isIVRangeGroupDone()) { // Only if none of the IVRange checkboxes have been checked.
            // See if appraiseText matches any of the IVRange strings
            match = setIVRangeWith(appraiseText);
        }
        if (!match && isIVRangeGroupDone()) { // Only if IVRange is done and have not matched yet.
            // See if appraiseText matches any of the Highest Stats strings
            match = setHighestStatsWith(appraiseText);
        }
        if (!match) { // Lastly, check if the appraiseText matches any of the Stat phrases
            match = setStatsRangeWith(appraiseText);
        }
        if (!match && numRetries < SCANRETRIES) { // If nothing matched and we have not yet reached maximum # of retries
            numRetries++;
            // Nothing matched, so this phrase should be thrown away.
            ocr.removeEntryFromApprisalCache(hash);
            // Let's schedule another scan to see if animation has finished.
            scanAppraisalText(RETRYDELAY);
        } else if (!match) { // Nothing matched and we've ran out of retry attempts.
            // Nothing matched, so this phrase should be thrown away.
            ocr.removeEntryFromApprisalCache(hash);
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
            appraisalStatsGroup.check(R.id.appraisalStat1);
            highlightActiveCheckboxGroup();
            autoAppraisalDone = true;
            return true;
        }
        if (appraiseText.toLowerCase().contains(statsrange2_phrase1)
                || (appraiseText.toLowerCase().contains(statsrange2_phrase2))) {
            appraisalStatsGroup.check(R.id.appraisalStat2);
            highlightActiveCheckboxGroup();
            autoAppraisalDone = true;
            return true;
        }
        if (appraiseText.toLowerCase().contains(statsrange3_phrase1)
                || (appraiseText.toLowerCase().contains(statsrange3_phrase2))) {
            appraisalStatsGroup.check(R.id.appraisalStat3);
            highlightActiveCheckboxGroup();
            autoAppraisalDone = true;
            return true;
        }
        if (appraiseText.toLowerCase().contains(statsrange4_phrase1)
                || (appraiseText.toLowerCase().contains(statsrange4_phrase2))) {
            appraisalStatsGroup.check(R.id.appraisalStat4);
            highlightActiveCheckboxGroup();
            autoAppraisalDone = true;
            return true;
        }
        return false;
    }

    /**
     * Sets each of the highest stats as found within the appraisal phrases given
     *
     * @param appraiseText the text to interpret.
     * @return boolean returns true if one of the highest stats phrase was matched.
     */
    private boolean setHighestStatsWith(String appraiseText) {
        if (appraiseText.toLowerCase().contains(highest_stat_att)) {
            attCheckbox.setChecked(true);
            return true;
        }
        if (appraiseText.toLowerCase().contains(highest_stat_def)) {
            defCheckbox.setChecked(true);
            return true;
        }
        if (appraiseText.toLowerCase().contains(highest_stat_hp)) {
            staCheckbox.setChecked(true);
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
            appraisalIVRangeGroup.check(R.id.appraisalIVRange1);
            return true;
        }
        if (appraiseText.toLowerCase().contains(ivrange2_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange2_phrase2))) {
            appraisalIVRangeGroup.check(R.id.appraisalIVRange2);
            return true;
        }
        if (appraiseText.toLowerCase().contains(ivrange3_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange3_phrase2))) {
            appraisalIVRangeGroup.check(R.id.appraisalIVRange3);
            return true;
        }
        if (appraiseText.toLowerCase().contains(ivrange4_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange4_phrase2))) {
            appraisalIVRangeGroup.check(R.id.appraisalIVRange4);
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
            String appraiseText = ocr.getAppraisalText(screen);
            String hash = appraiseText.substring(0, appraiseText.indexOf("#"));
            String text = appraiseText.substring(appraiseText.indexOf("#") + 1);
            addInfoFromAppraiseText(text, hash);
        }
    }

    /**
     * Return whether any of the checkboxes for appraisalStatsGroup are selected.
     *
     * @return true if any checkbox is selected.
     */
    private boolean isStatsGroupDone() {
        return appraisalStatsGroup.getCheckedRadioButtonId() != -1;
    }

    /**
     * Return whether any of the checkboxes for appraisalIVRangeGroup are selected.
     *
     * @return true if any checkbox is selected.
     */
    private boolean isIVRangeGroupDone() {
        return appraisalIVRangeGroup.getCheckedRadioButtonId() != -1;
    }

}

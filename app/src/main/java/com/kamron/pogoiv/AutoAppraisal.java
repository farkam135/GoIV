package com.kamron.pogoiv;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.RadioGroup;

/**
 * Created by Johan on 2016-12-01.
 * A class to handle automatic scanning of appraisal information.
 */
public class AutoAppraisal {

    private static final int SCANDELAY = 800; //in milliseconds

    ScreenScan screenScanner = new ScreenScan(); //The runnable that keeps scanning the screen
    Handler handler = new Handler();
    private GoIVSettings settings;

    private OcrHelper ocr;
    private ScreenGrabber screenGrabber;
    Context context;

    private int numTouches = 0;

    //UI elements in pokefly to modify.
    CheckBox attCheckbox;
    CheckBox defCheckbox;
    CheckBox staCheckbox;

    RadioGroup appraisalRangeGroup;
    RadioGroup appraisalStatGroup;

    //Appraisal phrases
    private String highest_stat_att;
    private String highest_stat_def;
    private String highest_stat_hp;
    private String percentage1_phrase1;
    private String percentage1_phrase2;
    private String percentage2_phrase1;
    private String percentage2_phrase2;
    private String percentage3_phrase1;
    private String percentage3_phrase2;
    private String percentage4_phrase1;
    private String percentage4_phrase2;
    private String ivrange1_phrase1;
    private String ivrange1_phrase2;
    private String ivrange2_phrase1;
    private String ivrange2_phrase2;
    private String ivrange3_phrase1;
    private String ivrange3_phrase2;
    private String ivrange4_phrase1;
    private String ivrange4_phrase2;


    public AutoAppraisal(ScreenGrabber screenGrabber, OcrHelper ocr, Context context,
                         CheckBox attCheckbox, CheckBox defCheckbox, CheckBox staCheckbox,
                         RadioGroup appraisalRangeGroup, RadioGroup appraisalStatGroup) {
        this.ocr = ocr;
        this.context = context;
        this.screenGrabber = screenGrabber;
        this.attCheckbox = attCheckbox;
        this.defCheckbox = defCheckbox;
        this.staCheckbox = staCheckbox;
        this.appraisalRangeGroup = appraisalRangeGroup;
        this.appraisalStatGroup = appraisalStatGroup;
        settings = GoIVSettings.getInstance(context);
        getAppraisalPhrases();
    }

    private void getAppraisalPhrases() {
        highest_stat_att = context.getString(R.string.highest_stat_att);
        highest_stat_def = context.getString(R.string.highest_stat_def);
        highest_stat_hp = context.getString(R.string.highest_stat_hp);

        if (settings.playerTeam() == 0) {
            percentage1_phrase1 = context.getString(R.string.mystic_percentage1_phrase1);
            percentage1_phrase2 = context.getString(R.string.mystic_percentage1_phrase2);
            percentage2_phrase1 = context.getString(R.string.mystic_percentage2_phrase1);
            percentage2_phrase2 = context.getString(R.string.mystic_percentage2_phrase2);
            percentage3_phrase1 = context.getString(R.string.mystic_percentage3_phrase1);
            percentage3_phrase2 = context.getString(R.string.mystic_percentage3_phrase2);
            percentage4_phrase1 = context.getString(R.string.mystic_percentage4_phrase1);
            percentage4_phrase2 = context.getString(R.string.mystic_percentage4_phrase2);
            ivrange1_phrase1 = context.getString(R.string.mystic_ivrange1_phrase1);
            ivrange1_phrase2 = context.getString(R.string.mystic_ivrange1_phrase2);
            ivrange2_phrase1 = context.getString(R.string.mystic_ivrange2_phrase1);
            ivrange2_phrase2 = context.getString(R.string.mystic_ivrange2_phrase2);
            ivrange3_phrase1 = context.getString(R.string.mystic_ivrange3_phrase1);
            ivrange3_phrase2 = context.getString(R.string.mystic_ivrange3_phrase2);
            ivrange4_phrase1 = context.getString(R.string.mystic_ivrange4_phrase1);
            ivrange4_phrase2 = context.getString(R.string.mystic_ivrange4_phrase2);
        } else if (settings.playerTeam() == 1) {
            percentage1_phrase1 = context.getString(R.string.valor_percentage1_phrase1);
            percentage1_phrase2 = context.getString(R.string.valor_percentage1_phrase2);
            percentage2_phrase1 = context.getString(R.string.valor_percentage2_phrase1);
            percentage2_phrase2 = context.getString(R.string.valor_percentage2_phrase2);
            percentage3_phrase1 = context.getString(R.string.valor_percentage3_phrase1);
            percentage3_phrase2 = context.getString(R.string.valor_percentage3_phrase2);
            percentage4_phrase1 = context.getString(R.string.valor_percentage4_phrase1);
            percentage4_phrase2 = context.getString(R.string.valor_percentage4_phrase2);
            ivrange1_phrase1 = context.getString(R.string.valor_ivrange1_phrase1);
            ivrange1_phrase2 = context.getString(R.string.valor_ivrange1_phrase2);
            ivrange2_phrase1 = context.getString(R.string.valor_ivrange2_phrase1);
            ivrange2_phrase2 = context.getString(R.string.valor_ivrange2_phrase2);
            ivrange3_phrase1 = context.getString(R.string.valor_ivrange3_phrase1);
            ivrange3_phrase2 = context.getString(R.string.valor_ivrange3_phrase2);
            ivrange4_phrase1 = context.getString(R.string.valor_ivrange4_phrase1);
            ivrange4_phrase2 = context.getString(R.string.valor_ivrange4_phrase2);
        } else {
            percentage1_phrase1 = context.getString(R.string.instinct_percentage1_phrase1);
            percentage1_phrase2 = context.getString(R.string.instinct_percentage1_phrase2);
            percentage2_phrase1 = context.getString(R.string.instinct_percentage2_phrase1);
            percentage2_phrase2 = context.getString(R.string.instinct_percentage2_phrase2);
            percentage3_phrase1 = context.getString(R.string.instinct_percentage3_phrase1);
            percentage3_phrase2 = context.getString(R.string.instinct_percentage3_phrase2);
            percentage4_phrase1 = context.getString(R.string.instinct_percentage4_phrase1);
            percentage4_phrase2 = context.getString(R.string.instinct_percentage4_phrase2);
            ivrange1_phrase1 = context.getString(R.string.instinct_ivrange1_phrase1);
            ivrange1_phrase2 = context.getString(R.string.instinct_ivrange1_phrase2);
            ivrange2_phrase1 = context.getString(R.string.instinct_ivrange2_phrase1);
            ivrange2_phrase2 = context.getString(R.string.instinct_ivrange2_phrase2);
            ivrange3_phrase1 = context.getString(R.string.instinct_ivrange3_phrase1);
            ivrange3_phrase2 = context.getString(R.string.instinct_ivrange3_phrase2);
            ivrange4_phrase1 = context.getString(R.string.instinct_ivrange4_phrase1);
            ivrange4_phrase2 = context.getString(R.string.instinct_ivrange4_phrase2);
        }
    }

    public void screenTouched() {
        numTouches++;

        if (numTouches > 2) {
            handler.removeCallbacks(screenScanner);
            handler.postDelayed(screenScanner, SCANDELAY);
        }
    }

    public void reset() {
        numTouches = 0;
    }

    /**
     * Alters the state of the AutoAppraisal instance to reflect the added information of the appraise text.
     *
     * @param appraiseText Text such as "...pokemon is breathtaking..."
     * @param hash the hash of the bitmap used by ocr
     */
    private void addInfoFromAppraiseText(String appraiseText, String hash) {
        boolean match = false;
        if (appraisalRangeGroup.getCheckedRadioButtonId() == -1) {
            match = setIVRangeWith(appraiseText);
        }
        if (!match) {
            match = setHighestStatsWith(appraiseText);
        }
        if (!match) {
            match = setStatRangeWith(appraiseText);
        }
        if (!match) {
            ocr.removeEntryFromApprisalCache(hash);
        }
    }

    private boolean setStatRangeWith(String appraiseText) {

        if (appraiseText.toLowerCase().contains(ivrange1_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange1_phrase2))) {
            appraisalStatGroup.check(R.id.appraisalStat1);
            return true;
        }
        if (appraiseText.toLowerCase().contains(ivrange2_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange2_phrase2))) {
            appraisalStatGroup.check(R.id.appraisalStat2);
            return true;
        }
        if (appraiseText.toLowerCase().contains(ivrange3_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange3_phrase2))) {
            appraisalStatGroup.check(R.id.appraisalStat3);
            return true;
        }
        if (appraiseText.toLowerCase().contains(ivrange4_phrase1)
                || (appraiseText.toLowerCase().contains(ivrange4_phrase2))) {
            appraisalStatGroup.check(R.id.appraisalStat4);
            return true;
        }
        return false;
    }

    /**
     * sets the highest stats by interpreting the appraiseText, and sets the autoAppraisal state to the next looking
     * for if found.
     *
     * @param appraiseText the text to interpret.
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
     * sets the ivRangePhrase by interpreting the appraiseText.
     *
     * @param appraiseText the text to interpret.
     */
    private boolean setIVRangeWith(String appraiseText) {
        if (appraiseText.toLowerCase().contains(percentage1_phrase1)
                || (appraiseText.toLowerCase().contains(percentage1_phrase2))) {
            appraisalRangeGroup.check(R.id.appraisalRange1);
            return true;
        }
        if (appraiseText.toLowerCase().contains(percentage2_phrase1)
                || (appraiseText.toLowerCase().contains(percentage2_phrase2))) {
            appraisalRangeGroup.check(R.id.appraisalRange2);
            return true;
        }
        if (appraiseText.toLowerCase().contains(percentage3_phrase1)
                || (appraiseText.toLowerCase().contains(percentage3_phrase2))) {
            appraisalRangeGroup.check(R.id.appraisalRange3);
            return true;
        }
        if (appraiseText.toLowerCase().contains(percentage4_phrase1)
                || (appraiseText.toLowerCase().contains(percentage4_phrase2))) {
            appraisalRangeGroup.check(R.id.appraisalRange4);
            return true;
        }
        return false;
    }

    /**
     * The task which looks at the bottom of the screen, and adds any info it finds.
     */
    private class ScreenScan implements Runnable {
        @Override
        public void run() {
            if (isNotDone()) {
                Bitmap screen = screenGrabber.grabScreen();
                String appraiseText = ocr.getAppraisalText(screen);
                String hash = appraiseText.substring(0, appraiseText.indexOf("#"));
                String text = appraiseText.substring(appraiseText.indexOf("#") + 1);
                addInfoFromAppraiseText(text, hash);
            }
        }
    }

    /**
     * Checks if all data has been filled for an appraisal evaluation
     *
     * @return true if there's still information left to get.
     */
    private boolean isNotDone() {
        //Since stamina range is set last, if non has been set we are not done.
        return appraisalStatGroup.getCheckedRadioButtonId() == -1;
    }

}

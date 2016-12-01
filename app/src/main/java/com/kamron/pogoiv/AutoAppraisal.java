package com.kamron.pogoiv;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.CheckBox;
import android.widget.Spinner;

import java.util.Observable;

/**
 * Created by Johan on 2016-12-01.
 * <p>
 * Todo
 */

public class AutoAppraisal{

    private static final int SCANINTERVAL = 250; //in milliseconds
    private int scansLeft= 100; //max scanning time is scaniterval * scansLeft - the appraisal will quit when scans is 0

    ScreenScan screenScanner = new ScreenScan(); //The runnable that keeps scanning the screen
    Handler handler = new Handler();

    private OcrHelper ocr;
    private ScreenGrabber screenGrabber;

    //UI elements in pokefly to modify.
    CheckBox attCheckbox;
    CheckBox defCheckbox;
    CheckBox staCheckbox;
    Spinner appraisalPercentageRange;
    Spinner appraisalIvRange;


    public AutoAppraisal(ScreenGrabber screenGrabber, OcrHelper ocr, Context context, CheckBox attCheckbox,
                         CheckBox defCheckbox, CheckBox staCheckbox, Spinner appraisalPercentageRange,
                         Spinner appraisalIvRange) {
        this.ocr = ocr;
        this.screenGrabber = screenGrabber;
        this.attCheckbox = attCheckbox;
        this.defCheckbox = defCheckbox;
        this.staCheckbox = staCheckbox;
        this.appraisalPercentageRange = appraisalPercentageRange;
        this.appraisalIvRange = appraisalIvRange;
    }

    public void start(boolean run) {
        if (run) {
            handler.postDelayed(screenScanner, SCANINTERVAL);
        }
    }



    /**
     * Alters the state of the AutoAppraisal instance to reflect the added information of the appraise text.
     *
     * @param appraiseText Text such as "...pokemon is breathtaking..."
     */
    private void addInfoFromAppraiseText(String appraiseText) {
        setIVRangeWith(appraiseText);
        setHighestStatsWith(appraiseText);
        setStatRangeWith(appraiseText);

    }

    private void setStatRangeWith(String appraiseText) {

        if (appraiseText.toLowerCase().contains("calculations") || (appraiseText.contains("incredible"))) {
            appraisalIvRange.setSelection(1);
        }
        if (appraiseText.toLowerCase().contains("impressed") || (appraiseText.contains("stats"))) {
            appraisalIvRange.setSelection(2);
        }
        if (appraiseText.toLowerCase().contains("noticeably") || (appraiseText.contains("trending"))) {
            appraisalIvRange.setSelection(3);
        }
        if (appraiseText.toLowerCase().contains("norm") || (appraiseText.contains("opinion"))) {
            appraisalIvRange.setSelection(4);
        }
    }

    /**
     * sets the highest stats by interpreting the appraiseText, and sets the autoAppraisal state to the next looking
     * for if found.
     *
     * @param appraiseText the text to interpret.
     */
    private void setHighestStatsWith(String appraiseText) {
        if (appraiseText.toLowerCase().contains("attack")) {
            attCheckbox.setChecked(true);
        }
        if (appraiseText.toLowerCase().contains("defense")) {
            defCheckbox.setChecked(true);
        }
        if (appraiseText.toLowerCase().contains("hp")) {
            staCheckbox.setChecked(true);
        }
    }

    /**
     * sets the ivRangePhrase by interpreting the appraiseText.
     *
     * @param appraiseText the text to interpret.
     */
    private void setIVRangeWith(String appraiseText) {
        if (appraiseText.contains("wonder") || (appraiseText.contains("breathtaking"))) {
            appraisalPercentageRange.setSelection(1);
        }
        if (appraiseText.contains("caught") || (appraiseText.contains("attention"))) {
            appraisalPercentageRange.setSelection(2);
        }
        if (appraiseText.contains("above") || (appraiseText.contains("average"))) {
            appraisalPercentageRange.setSelection(3);
        }
        if (appraiseText.contains("likely") || (appraiseText.contains("headway"))) {
            appraisalPercentageRange.setSelection(4);
        }

    }

    /**
     * The repeated task which looks at the bottom of the screen, and adds any info it finds.
     * The method calls itself until its called itself scansLeft times or it doesnt look like its on the appraisal
     * screen anymore.
     */
    private class ScreenScan implements Runnable {
        @Override
        public void run() {
            Bitmap screen = screenGrabber.grabScreen();
            String appraiseText = ocr.getAppraisalText(screen);
            addInfoFromAppraiseText(appraiseText);
            if (scansLeft > 0 && isNotDone()) {
                handler.postDelayed(this, SCANINTERVAL);
                scansLeft--;
            }

        }
    }

    /**
     * Checks if all data has been filled for an appraisal evaluation
     * @return true if there's still information left to get.
     */
    private boolean isNotDone() {
        //Since stamina range is set last, if the selected item is still 0, none has been selected yet.
        return appraisalIvRange.getSelectedItemPosition() == 0;
    }

}

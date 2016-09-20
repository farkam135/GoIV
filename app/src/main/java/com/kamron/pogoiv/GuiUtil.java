package com.kamron.pogoiv;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by OskO on 30/08/16.
 */
public class GuiUtil {

    private static final String RED = "#B10000";
    private static final String ORANGE = "#F1A900";
    private static final String GREEN = "#00A000";
    private static final String BLUE = "#0099EF";

    /**
     * Sets the text color based on IV to match the in-game appraisal system.
     *
     * @param text  the text that changes color
     * @param value the value that is checked (from 0 - 15)
     */
    public static void setTextColorByIV(TextView text, int value) {
        if (value == 15) {
            text.setTextColor(Color.parseColor(BLUE));
        } else if (value >= 13) {
            text.setTextColor(Color.parseColor(GREEN));
        } else if (value >= 8) {
            text.setTextColor(Color.parseColor(ORANGE));
        } else {
            text.setTextColor(Color.parseColor(RED));
        }
    }

    /**
     * Sets the text color based on total IV percentage to match the in-game appraisal system.
     *
     * @param text  the text that changes color
     * @param value the value that is checked
     */
    public static void setTextColorByPercentage(TextView text, int value) {
        if (value > 81) { // between 80 and 82.2
            text.setTextColor(Color.parseColor(BLUE));
        } else if (value > 65) { // between 64.4 and 66.7
            text.setTextColor(Color.parseColor(GREEN));
        } else if (value > 50) { // between 48.9 and 51.1
            text.setTextColor(Color.parseColor(ORANGE));
        } else {
            text.setTextColor(Color.parseColor(RED));
        }
    }

    /**
     * Converts dp units to pixels.
     *
     * @param dp      the dp ammount you want to return as pixels
     * @param context the context
     */
    public static int dpToPixels(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }
}

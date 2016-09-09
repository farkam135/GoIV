package com.kamron.pogoiv;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.TextView;

/**
 * Created by OskO on 30/08/16.
 */
public class GuiUtil {

    /**
     * Sets the text color to red if below 80, and green if above.
     *
     * @param text  the text that changes color
     * @param value the value that is checked if its above 80
     */
    public static void setTextColorbyPercentage(TextView text, int value) {
        if (value >= 80) {
            text.setTextColor(Color.parseColor("#088A08")); //dark green
        } else if (value >= 60) {
            text.setTextColor(Color.parseColor("#DBA901")); //brownish orange
        } else {
            text.setTextColor(Color.parseColor("#8A0808")); //dark red
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

package com.kamron.pogoiv;

import android.graphics.Color;
import android.widget.TextView;

/**
 * Created by OskO on 30/08/16.
 */
public class GUIUtil {

    /**
     * Sets the text color to red if below 80, and green if above
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
}

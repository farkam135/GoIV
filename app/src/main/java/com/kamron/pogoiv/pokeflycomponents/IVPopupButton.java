package com.kamron.pogoiv.pokeflycomponents;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.logic.IVScanResult;

/**
 * Created by johan on 2017-07-06.
 * <p>
 * The class representing the floating button View in the bottom left corner of the pokemon screen which brings up the
 * full input modification screen
 */

public class IVPopupButton extends android.support.v7.widget.AppCompatButton {

    private Pokefly pokefly;
    private WindowManager windowManager;
    private boolean showing = false;

    private final WindowManager.LayoutParams ivButtonParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);


    /**
     * Create an instance of the IVPopupButton.
     *
     * @param context the pokefly context
     */
    public IVPopupButton(Context context) {
        super(context);
        pokefly = (Pokefly) context;
        windowManager = (WindowManager) pokefly.getSystemService(pokefly.WINDOW_SERVICE);
        resetButtonLook();

        setOnTouchListener(new OnIVClick());
    }

    /**
     * Hides or shows the IV button
     *
     * @param shouldShow    true if the button should attempt to show itself
     * @param infoShownSent the boolean in pokefly that indiates if the state is already in sent state.
     */
    public void setShown(boolean shouldShow, boolean infoShownSent) {
        if (shouldShow && !showing && !infoShownSent) {
            showSelf();

        } else if (!shouldShow) {
            if (showing) {
                removeSelf();
            }
        }
    }

    /**
     * Shows the ivbutton.
     */
    private void showSelf() {
        resetButtonLook();
        windowManager.addView(this, getLayoutParams());
        showing = true;
    }

    /**
     * resets the visual look of the button to its default state.
     */
    private void resetButtonLook() {
        setBackgroundResource(R.drawable.iv_button);
        setWidth(dpToPx(60));
        setText("");
        ivButtonParams.gravity = Gravity.BOTTOM | Gravity.START;
        ivButtonParams.x = dpToPx(16);
        ivButtonParams.y = dpToPx(14);
        setLayoutParams(ivButtonParams);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * Modifies the look of the IVPopupButton to represent a quick representation of an iv scan.
     *
     * @param ivrs what data to base the look on.
     */
    public void showQuickIVPreviewLook(IVScanResult ivrs) {
        setTextAlignment(TEXT_ALIGNMENT_CENTER);

        int low = ivrs.getLowestIVCombination().percentPerfect;
        int high = ivrs.getHighestIVCombination().percentPerfect;
        if (ivrs.getCount() == 1 || high == low) { // display something like "IV: 98%"
            setText(ivrs.pokemon.name + "\nIV: " + low + "%");
        } else { // display something like "IV: 55 - 87%"
            setText(ivrs.pokemon.name + "\nIV: " + low + " - " + high + "%");
        }

        setBackgroundGradient(ivrs);
        setTextColorFromIVs(ivrs);


    }

    private void setTextColorFromIVs(IVScanResult ivrs) {

        //setTextColor(ResourcesCompat.getColor(getResources(), R.color.iv_text_color, null));

        int blue = ResourcesCompat.getColor(getResources(), R.color.t_blue, null);
        int green = ResourcesCompat.getColor(getResources(), R.color.t_green, null);
        int yellow = ResourcesCompat.getColor(getResources(), R.color.t_yellow, null);
        int red = ResourcesCompat.getColor(getResources(), R.color.t_red, null);

        int percent = ivrs.getAveragePercent();
        if (percent < 51) {
            setTextColor(red);
        } else if (percent < 66) {
            setTextColor(yellow);
        } else if (percent < 82) {
            setTextColor(green);
        } else if (percent < 101) {
            setTextColor(blue);
        }

    }

    /**
     * Picks the correct background resource to have the correct color gradient representing the IV spread.
     *
     * @param ivrs The possible IVs to adjust to.
     */
    private void setBackgroundGradient(IVScanResult ivrs) {

        int low = ivrs.getLowestIVCombination().percentPerfect;
        int high = ivrs.getHighestIVCombination().percentPerfect;

        setWidth(dpToPx(60));
        setBackgroundResource(R.drawable.preview_button_0_100);

        setGradientColor(getColorForPercentIV(low), getColorForPercentIV(high));


    }

    /**
     * Set the ivpreview button outer line color gradient.
     *
     * @param c1 integer representing the color of the left side of the gradient
     * @param c2 integer representing the color of the right side of the gradient
     */
    private void setGradientColor(int c1, int c2) {
        LayerDrawable bgDrawable = (LayerDrawable) getBackground();
        GradientDrawable inner = (GradientDrawable) bgDrawable.findDrawableByLayerId(R.id.iv_preview_ring_gradient);
        if (inner != null) {
            inner.setColors(new int[]{c1, c2});
        }

    }

    /**
     * Get the color that should be shown on the ivpopupbutton for a certain iv percentage.
     *
     * @param percent an integer between 0 and 100 representing the iv %
     * @return an integer representing a color
     */
    private int getColorForPercentIV(int percent) {
        if (percent < 51) {
            return ResourcesCompat.getColor(getResources(), R.color.p_red, null);
        } else if (percent < 66) {
            return ResourcesCompat.getColor(getResources(), R.color.p_yellow, null);
        } else if (percent < 82) {
            return ResourcesCompat.getColor(getResources(), R.color.p_green, null);
        } else {
            return ResourcesCompat.getColor(getResources(), R.color.p_blue, null);
        }
    }

    /**
     * Resets the look of the IvPopupButton if the iv button is visible, because user clicking outside GoIV ui might
     * navigate away from the screen and showing old IV preview would be missgiving.
     */
    public void outsideScreenClicked() {
        setText("...");
        setGradientColor(0, 0); //makes gradient invisible
    }

    /**
     * The class which defines what happens when the IVPopupButton is clicked.
     */
    private class OnIVClick implements OnTouchListener {

        @Override public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                removeSelf();
                pokefly.takeScreenshot();
                pokefly.setIVButtonClickedStates();
            }
            return false;
        }
    }

    /**
     * Removes the button from its parent container.
     */
    private void removeSelf() {
        windowManager.removeView(this);
        showing = false;
    }


    private int dpToPx(int dp) {
        return Math.round(dp * (getContext().getResources().getDisplayMetrics().density));
    }

}

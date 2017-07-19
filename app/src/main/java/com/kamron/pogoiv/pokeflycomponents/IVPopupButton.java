package com.kamron.pogoiv.pokeflycomponents;

import android.content.Context;
import android.graphics.PixelFormat;
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
        setBackgroundResource(R.drawable.iv_button);
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
    public void resetButtonLook() {
        setBackgroundResource(R.drawable.iv_button);
        setWidth(dpToPx(60));
        setText("");
        ivButtonParams.gravity = Gravity.BOTTOM | Gravity.START;
        ivButtonParams.x = dpToPx(16);
        ivButtonParams.y = dpToPx(14);
        setLayoutParams(ivButtonParams);

        //setTypeface(null, Typeface.BOLD);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * Modifies the look of the IVPopupButton to represent a quick representation of an iv scan.
     *
     * @param ivrs what data to base the look on.
     */
    public void showQuickIVPreviewLook(IVScanResult ivrs) {
        //setBackgroundResource(R.drawable.preview_button);
        //setBackgroundResource(R.mipmap.iv_preview_1);
        setTextAlignment(TEXT_ALIGNMENT_CENTER);

        int low = ivrs.getLowestIVCombination().percentPerfect;
        int high = ivrs.getHighestIVCombination().percentPerfect;
        if (ivrs.getCount() == 1 || high == low) { // display something like "IV: 98%"
            setText(ivrs.pokemon.name + "\nIV: " + low + "%");
        } else { // display something like "IV: 55 - 87%"
            setText(ivrs.pokemon.name + "\nIV: " + low + " - " + high + "%");
        }

        setBackgroundResource(R.drawable.iv_button_background);
        setWidth(dpToPx(120));
        setBackgroundGradient(ivrs);
        setTextColorFromIVs(ivrs);


    }

    private void setTextColorFromIVs(IVScanResult ivrs) {
        int blue = ResourcesCompat.getColor(getResources(), R.color.p_text_blue, null);
        int green = ResourcesCompat.getColor(getResources(), R.color.p_text_green, null);
        int yellow = ResourcesCompat.getColor(getResources(), R.color.p_text_yellow, null);
        int orange = ResourcesCompat.getColor(getResources(), R.color.p_text_orange, null);
        int red = ResourcesCompat.getColor(getResources(), R.color.p_text_red, null);

        int percent = ivrs.getAveragePercent();
        if (percent < 51) {
            setTextColor(red);
        } else if (percent < 66) {
            setTextColor(orange);
        } else if (percent < 82) {
            setTextColor(yellow);
        } else if (percent < 101) {
            setTextColor(green);
        }
    }

    /**
     * Picks the correct background resource to have the correct color gradient representing the IV spread.
     *
     * @param ivrs The possible IVs to adjust to.
     */
    private void setBackgroundGradient(IVScanResult ivrs) {

        int background = ResourcesCompat.getColor(getResources(), R.color.p_background, null);
        int green = ResourcesCompat.getColor(getResources(), R.color.p_green, null);
        int yellow = ResourcesCompat.getColor(getResources(), R.color.p_yellow, null);
        int orange = ResourcesCompat.getColor(getResources(), R.color.p_orange, null);
        int red = ResourcesCompat.getColor(getResources(), R.color.p_red, null);

        int low = ivrs.getLowestIVCombination().percentPerfect;
        int high = ivrs.getHighestIVCombination().percentPerfect;

//        if (low < 51 && high < 51) {
//            setBackgroundResource(R.drawable.preview_button_0_50);
//        } else if (low < 51 && high < 66) {
//            setBackgroundResource(R.drawable.preview_button_0_65);
//        } else if (low < 51 && high < 82) {
//            setBackgroundResource(R.drawable.preview_button_0_81);
//        } else if (low < 51 && high < 101) {
//            setBackgroundResource(R.drawable.preview_button_0_100);
//        } else if (low < 66 && high < 66) {
//            setBackgroundResource(R.drawable.preview_button_51_65);
//        } else if (low < 66 && high < 82) {
//            setBackgroundResource(R.drawable.preview_button_51_81);
//        } else if (low < 66 && high < 101) {
//            setBackgroundResource(R.drawable.preview_button_51_100);
//        } else if (low < 82 && high < 82) {
//            setBackgroundResource(R.drawable.preview_button_66_81);
//        } else if (low < 82 && high < 101) {
//            setBackgroundResource(R.drawable.preview_button_66_100);
//        } else if (low < 101 && high < 101) {
//            setBackgroundResource(R.drawable.preview_button_82_100);
//        }

//        View minIndicatorView = findViewById(R.id.ivPreviewMinIndicator);
//        int[] colors = {red, background};
//
//        GradientDrawable gradientDrawable = new GradientDrawable(
//                GradientDrawable.Orientation.LEFT_RIGHT, colors);
//
//        minIndicatorView.setBackground(gradientDrawable);
    }

    /**
     * Resets the look of the IvPopupButton if the iv button is visible, because user clicking outside GoIV ui might
     * navigate away from the screen and showing old IV preview would be missgiving.
     */
    public void outsideScreenClicked() {
        setBackgroundResource(R.drawable.iv_button_background);
        setWidth(dpToPx(60));
        setText("...");
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

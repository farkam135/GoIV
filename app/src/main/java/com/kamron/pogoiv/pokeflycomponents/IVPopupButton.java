package com.kamron.pogoiv.pokeflycomponents;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.ScanResult;

import static com.kamron.pogoiv.pokeflycomponents.GoIVNotificationManager.ACTION_RECALIBRATE_SCANAREA;

/**
 * Created by johan on 2017-07-06.
 * <p>
 * The class representing the floating button View in the bottom left corner of the pokemon screen which brings up the
 * full input modification screen
 */

@SuppressLint("ViewConstructor")
public class IVPopupButton extends androidx.appcompat.widget.AppCompatButton {

    private final Pokefly pokefly;
    private boolean shouldRecalibrate = false;

    public static final WindowManager.LayoutParams layoutParams;

    static {
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                        ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                        : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        layoutParams.gravity = Gravity.BOTTOM | Gravity.START;
    }

    /**
     * Create an instance of the IVPopupButton.
     *
     * @param pokefly The pokefly service that created this button
     */
    public IVPopupButton(Pokefly pokefly) {
        super(pokefly);
        this.pokefly = pokefly;

        // Init button position
        layoutParams.x = dpToPx(16);
        layoutParams.y = dpToPx(14);

        // Start hidden
        setVisibility(GONE);

        resetButtonLook();
        setOnTouchListener(new OnIVClick());
    }

    /**
     * Hides or shows the IV button.
     *
     * @param shouldShow    true if the button should attempt to show itself
     * @param infoShownSent the boolean in pokefly that indiates if the state is already in sent state.
     */
    public void setShown(boolean shouldShow, boolean infoShownSent) {
        if (shouldShow && getVisibility() != VISIBLE && !infoShownSent) {
            resetButtonLook();
            setVisibility(VISIBLE);

        } else if (!shouldShow) {
            if (getVisibility() == VISIBLE) {
                setVisibility(GONE);
            }
        }
    }

    /**
     * resets the visual look of the button to its default state.
     */
    private void resetButtonLook() {
        setTextColor(Color.WHITE);
        setBackgroundResource(R.drawable.iv_button);
        setWidth(dpToPx(60));
        setText("");
        setTextAlignment(TEXT_ALIGNMENT_CENTER);
        setGravity(Gravity.CENTER_VERTICAL);
    }

    /**
     * Modifies the look of the IVPopupButton to represent a quick representation of an iv scan.
     *
     * @param scanResult what data to base the look on.
     */
    public void showQuickIVPreviewLook(@NonNull ScanResult scanResult) {
        shouldRecalibrate = false;

        IVCombination lowest = scanResult.getLowestIVCombination();
        IVCombination highest = scanResult.getHighestIVCombination();

        if (lowest != null && highest != null) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
            int low = lowest.percentPerfect;
            int high = highest.percentPerfect;

            final StringBuilder text = new StringBuilder();
            String pokemonName = scanResult.pokemon.name;
            if (pokemonName.contains(" - ")) { // check including form name
                pokemonName = pokemonName.replace(" - ", "\n");
            }
            if (scanResult.getIVCombinationsCount() == 1 || high == low) { // Display something like "IV: 98%"
                text.append(getContext().getString(
                        R.string.iv_button_exact_result_preview_format, pokemonName, low));
            } else { // Display something like "IV: 55 - 87%"
                text.append(getContext().getString(
                        R.string.iv_button_range_result_preview_format, pokemonName, low, high));
            }
            if (scanResult.levelRange.min != scanResult.levelRange.max) {
                text.append("*");
            }
            setText(text);

            setBackgroundGradient(scanResult);
            setTextColorFromIVs(scanResult);

            copyToClipboardIfSettingOn(scanResult);
        }
    }

    private void copyToClipboardIfSettingOn(ScanResult scanResult) {

        if (GoIVSettings.getInstance(pokefly).shouldFastCopyToClipboard()){

            pokefly.addClipboardInfoIfSettingOn(scanResult);
        }
    }

    private void setTextColorFromIVs(ScanResult scanResult) {

        //setTextColor(ResourcesCompat.getColor(getResources(), R.color.iv_text_color, null));

        int blue = ResourcesCompat.getColor(getResources(), R.color.t_blue, null);
        int green = ResourcesCompat.getColor(getResources(), R.color.t_green, null);
        int yellow = ResourcesCompat.getColor(getResources(), R.color.t_yellow, null);
        int red = ResourcesCompat.getColor(getResources(), R.color.t_red, null);

        int percent = scanResult.getIVPercentAvg();
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
     * @param scanResult The possible IVs to adjust to.
     */
    private void setBackgroundGradient(@NonNull ScanResult scanResult) {
        IVCombination lowest = scanResult.getLowestIVCombination();
        IVCombination highest = scanResult.getHighestIVCombination();

        if (lowest != null && highest != null) {
            int low = lowest.percentPerfect;
            int high = highest.percentPerfect;

            setWidth(dpToPx(60));
            setBackgroundResource(R.drawable.preview_button_0_100);

            setGradientColor(getColorForPercentIV(low), getColorForPercentIV(high));
        }
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
        setBackgroundResource(R.drawable.preview_button_0_100);

        int black = ResourcesCompat.getColor(getResources(), R.color.p_loading, null);
        setGradientColor(black, black);
    }

    public void showError() {
        resetButtonLook();

        setBackgroundResource(R.drawable.preview_button_0_100);
        int black = ResourcesCompat.getColor(getResources(), R.color.p_error, null);
        setGradientColor(black, black);

        GoIVSettings settings = GoIVSettings.getInstance(pokefly);
        if (settings.hasManualScanCalibration() && settings.hasUpToDateManualScanCalibration()) {
            setText("?");
        } else {
            shouldRecalibrate = true;
            setText(R.string.button_recalibrate);
        }
    }

    /**
     * The class which defines what happens when the IVPopupButton is clicked.
     */
    private class OnIVClick implements OnTouchListener {

        @Override public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (shouldRecalibrate) {
                    shouldRecalibrate = false;
                    pokefly.startService(new Intent(pokefly, GoIVNotificationManager.NotificationActionService.class)
                            .setAction(ACTION_RECALIBRATE_SCANAREA));
                } else {
                    setVisibility(GONE);
                    pokefly.requestScan();
                    pokefly.setIVButtonClickedStates();
                }
                return true;
            }
            return false;
        }
    }

    private static int dpToPx(int dp) {
        return Math.round(dp * Resources.getSystem().getDisplayMetrics().density);
    }

}

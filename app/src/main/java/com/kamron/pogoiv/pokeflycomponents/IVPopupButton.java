package com.kamron.pogoiv.pokeflycomponents;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;

/**
 * Created by johan on 2017-07-06.
 * <p>
 * The class representing the floating button View in the bottom left corner of the pokemon screen which brings up the
 * full input modification screen
 */

public class IVPopupButton extends ImageView {

    private Pokefly pokefly;
    private WindowManager windowManager;

    private final WindowManager.LayoutParams ivButtonParams = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);


    public boolean isShown() {
        return getParent() != null;
    }

    /**
     * Create an instance of the IVPopupButton
     *
     * @param context the pokefly context
     */
    public IVPopupButton(Context context) {
        super(context);
        setLayoutParams(ivButtonParams);
        pokefly = (Pokefly) context;
        windowManager = (WindowManager) pokefly.getSystemService(pokefly.WINDOW_SERVICE);
        setImageResource(R.drawable.button);
        ivButtonParams.gravity = Gravity.BOTTOM | Gravity.START;
        ivButtonParams.x = dpToPx(20);
        ivButtonParams.y = dpToPx(15);

        setOnTouchListener(new OnIVClick());
    }

    /**
     * Hides or shows the IV button
     *
     * @param shouldShow    true if the button should attempt to show itself
     * @param infoShownSent the boolean in pokefly that indiates if the state is already in sent state.
     */
    public void setShown(boolean shouldShow, boolean infoShownSent) {
        Log.d("infoShownSent", "infoShowSentValue:" + infoShownSent);
        if (shouldShow && !isShown() && !infoShownSent) {
            windowManager.addView(this, getLayoutParams());
        } else if (!shouldShow) {
            if (isShown()) {
                removeSelf();
            }
        }
    }

    /**
     * The class which defines what happens when the IVPopupButton is clicked
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
     * Removes the button from its parent container
     */
    private void removeSelf() {
        windowManager.removeView(this);
    }


    private int dpToPx(int dp) {
        return Math.round(dp * (getContext().getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}

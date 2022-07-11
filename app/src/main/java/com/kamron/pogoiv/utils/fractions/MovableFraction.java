package com.kamron.pogoiv.utils.fractions;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.kamron.pogoiv.R;


public abstract class MovableFraction extends Fraction implements View.OnTouchListener {

    private SharedPreferences sharedPrefs;
    private int originalWindowY;
    private int initialTouchY;


    public MovableFraction(SharedPreferences sharedPrefs) {
        this.sharedPrefs = sharedPrefs;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (fractionManager == null) {
            return false;
        }

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                originalWindowY = fractionManager.getCurrentFloatingViewVerticalOffset();
                initialTouchY = (int) motionEvent.getRawY();
                return true;

            case MotionEvent.ACTION_MOVE:
                int touchVerticalDiff = (int) (motionEvent.getRawY() - initialTouchY);
                int offset;
                if (getAnchor() == Anchor.BOTTOM) {
                    offset = originalWindowY - touchVerticalDiff;
                } else {
                    offset = originalWindowY + touchVerticalDiff;
                }
                fractionManager.updateFloatingViewVerticalOffset(offset);
                return true;

            case MotionEvent.ACTION_UP:
                if (fractionManager.getCurrentFloatingViewVerticalOffset() != originalWindowY) {
                    saveVerticalOffset(fractionManager.getCurrentFloatingViewVerticalOffset());
                } else {
                    Toast.makeText(view.getContext(), R.string.position_handler_toast, Toast.LENGTH_SHORT).show();
                }
                return true;

            default:
                return false;
        }
    }

    @Override
    public final int getVerticalOffset(@NonNull DisplayMetrics displayMetrics) {
        String key = getVerticalOffsetSharedPreferencesKey();
        if (key == null) {
            return getDefaultVerticalOffset(displayMetrics);
        }
        return sharedPrefs.getInt(key, getDefaultVerticalOffset(displayMetrics));
    }

    public abstract int getDefaultVerticalOffset(DisplayMetrics displayMetrics);

    private void saveVerticalOffset(int offset) {
        String key = getVerticalOffsetSharedPreferencesKey();
        if (key == null) {
            return;
        }
        sharedPrefs.edit()
                .putInt(key, offset)
                .apply();
    }

    /**
     * Return the key to save/retrieve the offset for this Fraction from the SharedPreferences.
     * If null the offset won't be saved/restored.
     * @return SharedPreference key
     */
    protected abstract @Nullable String getVerticalOffsetSharedPreferencesKey();

}

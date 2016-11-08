package com.kamron.pogoiv;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.NumberPicker;

/**
 * This handler will handle changes to NpTrainerLevelPickerListener and handles if we restart on complete and if we
 * skip restarting Pogo.
 * Created by NightMadness on 11/5/2016.
 */

class NpTrainerLevelPickerListener implements NumberPicker.OnScrollListener, NumberPicker.OnValueChangeListener {
    private int scrollState = 0;
    private final Handler handler = new Handler();
    private final int delayTime = 500;
    private Context context;


    public NpTrainerLevelPickerListener(Context context) {
        this.context = context;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Pokefly.isRunning()) {
                LocalBroadcastManager.getInstance(context)
                        .sendBroadcast(new Intent(MainActivity.ACTION_RESTART_POKEFLY));
            }
        }
    };

    @Override
    public void onScrollStateChange(NumberPicker view, int scrollState) {
        this.scrollState = scrollState;
        if (scrollState == SCROLL_STATE_IDLE) {
            update();
        } else {
            //we are scrolling (or flinging) so we don't need to run update
            //just in case we had any pending posts in queue.
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onValueChange(final NumberPicker picker, final int oldVal, final int newVal) {
        if (scrollState == SCROLL_STATE_IDLE) {
            update();
        }
    }

    private void update() {
        handler.removeCallbacks(runnable);
        handler.postDelayed(runnable, delayTime);
    }

}

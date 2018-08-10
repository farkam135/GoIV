package com.kamron.pogoiv.activities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

/**
 * Created by Johan on 2018-08-06.
 * A simple view that has a half-circle on it. Made for showing the arc-indicator in the manual recalibration activity.
 */

class ArcView extends View {
    public ArcView(Context context) {
        super(context);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(Math.min(getWidth() / 100, 2));
        p.setColor(Color.GREEN);


        int radius = getWidth() / 2;
        int xCoord = getWidth() / 2;
        int yCoord = getHeight();

        RectF oval = new RectF(xCoord - radius, yCoord - radius,
                xCoord + radius, yCoord + radius);
        canvas.drawArc(oval, 180, 180, false, p);

    }
}

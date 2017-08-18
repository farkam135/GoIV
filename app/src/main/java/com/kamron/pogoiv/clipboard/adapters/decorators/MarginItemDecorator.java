package com.kamron.pogoiv.clipboard.adapters.decorators;

import android.content.res.Resources;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;


public class MarginItemDecorator extends RecyclerView.ItemDecoration {

    private final Rect margins;

    public MarginItemDecorator(int leftDp, int topDp, int rightDp, int bottomDp) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        margins = new Rect(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDp, dm),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topDp, dm),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDp, dm),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomDp, dm));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margins);
    }

}

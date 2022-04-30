package com.kamron.pogoiv.widgets.recyclerviews.decorators;

import android.content.res.Resources;
import android.graphics.Rect;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import static android.view.View.LAYOUT_DIRECTION_LTR;
import static android.view.View.LAYOUT_DIRECTION_RTL;


public class MarginItemDecorator extends RecyclerView.ItemDecoration {

    private final Rect margins;

    public MarginItemDecorator(int leftDp, int topDp, int rightDp, int bottomDp) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        this.margins = new Rect(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDp, dm),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, topDp, dm),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDp, dm),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bottomDp, dm));
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(margins);

        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
            LinearLayoutManager llm = ((LinearLayoutManager) parent.getLayoutManager());
            int position = parent.getChildAdapterPosition(view);
            if (position == 0) {
                // This is the first item of a LinearLayoutManager
                if (llm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    if (llm.getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                        outRect.left = 0;
                    } else if (llm.getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                        outRect.right = 0;
                    }
                } else if (llm.getLayoutDirection() == LinearLayoutManager.VERTICAL) {
                    outRect.top = 0;
                }
            }
            if (position == parent.getAdapter().getItemCount() - 1) {
                // This is the last item of a LinearLayoutManager
                if (llm.getOrientation() == LinearLayoutManager.HORIZONTAL) {
                    if (llm.getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                        outRect.right = 0;
                    } else if (llm.getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                        outRect.left = 0;
                    }
                } else if (llm.getLayoutDirection() == LinearLayoutManager.VERTICAL) {
                    outRect.bottom = 0;
                }
            }
        }

        if (parent.getLayoutManager() instanceof GridLayoutManager) {
            GridLayoutManager glm = ((GridLayoutManager) parent.getLayoutManager());
            int position = parent.getChildAdapterPosition(view);
            int spanCount = glm.getSpanCount();
            int spanIndex = glm.getSpanSizeLookup().getSpanIndex(position, spanCount);
            int spanSize = glm.getSpanSizeLookup().getSpanSize(position);
            // Check column position
            if (spanIndex % spanCount == 0) {
                // This element is on the first column
                if (glm.getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                    outRect.left = 0;
                } else if (glm.getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                    outRect.right = 0;
                }
            }
            if (spanIndex % spanCount + spanSize == spanCount) {
                // This element is on the last column
                if (glm.getLayoutDirection() == LAYOUT_DIRECTION_LTR) {
                    outRect.right = 0;
                } else if (glm.getLayoutDirection() == LAYOUT_DIRECTION_RTL) {
                    outRect.left = 0;
                }
            }
        }
    }

}

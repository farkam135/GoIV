package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by Johan on 2018-02-19.
 * A class which decices how every cell in the moveset table should look based on the data.
 */

public class PowerTableDataAdapter extends TableDataAdapter<MovesetData> {
    private int isSelectedColor;

    public PowerTableDataAdapter(Context context,
                                 MovesetData[] data) {
        super(context, data);
    }

    @Override public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        MovesetData move = getRowData(rowIndex);
        View renderedView = null;

        TextView tv = new TextView(getContext());
        if (columnIndex == 0) {
            tv.setTextColor(getMoveColor(move.isQuickIsLegacy()));
            tv.setText(move.getQuick());
            tv.setBackgroundColor(getIsSelectedColor(move.isScannedQuick()));
        } else if (columnIndex == 1) {
            tv.setTextColor(getMoveColor(move.isChargeIsLegacy()));
            tv.setText(move.getCharge());
            tv.setBackgroundColor(getIsSelectedColor(move.isScannedCharge()));
        } else if (columnIndex == 2) {
            tv.setTextColor(getPowerColor(move.getAtkScore()));
            tv.setText(move.getAtkScore() + "");
        } else if (columnIndex == 3) {
            tv.setTextColor(getPowerColor(move.getDefScore()));
            tv.setText(move.getDefScore() + "");
        }

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        tv.setMaxLines(1);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        renderedView = tv;
        return renderedView;
    }

    private int getIsSelectedColor(boolean scanned) {
        if (scanned) {
            return Color.parseColor("#dbdcdd");
        } else{

            return Color.parseColor("#fafafa");
        }
    }

    private int getMoveColor(boolean legacy) {
        if (legacy) {
            return Color.parseColor("#a3a3a3");
        } else{

            return Color.parseColor("#282828");
        }
    }

    private int getPowerColor(double atkScore) {
        if (atkScore > 10) {
            return Color.parseColor("#0d47a1");
        }
        if (atkScore > 8) {
            return Color.parseColor("#2e7d32");
        }
        if (atkScore > 6) {
            return Color.parseColor("#f9a825");
        }
        return Color.parseColor("#d84315");


    }

}

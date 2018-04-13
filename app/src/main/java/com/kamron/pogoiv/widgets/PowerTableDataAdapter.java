package com.kamron.pogoiv.widgets;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.MovesetData;

import java.text.DecimalFormat;

import de.codecrafters.tableview.TableDataAdapter;

/**
 * Created by Johan on 2018-02-19.
 * A class which decices how every cell in the moveset table should look based on the data.
 */

public class PowerTableDataAdapter extends TableDataAdapter<MovesetData> {

    private DecimalFormat scoreFormat = new DecimalFormat("0.00");
    public MovesetData scannedMoveset;


    public PowerTableDataAdapter(Pokefly pokefly, MovesetData[] data) {
        super(pokefly, data);
        selectScannedMoveset(pokefly, data);
    }

    @Override public View getCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        MovesetData move = getRowData(rowIndex);
        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources()
                .getDisplayMetrics());

        TextView tv = new TextView(getContext());

        if (columnIndex == 0) { // Quick move cell in table
            tv.setTextColor(getMoveColor(move.isQuickIsLegacy()));
            tv.setText(move.getQuick());
            if (move.equals(scannedMoveset)) {
                tv.setTypeface(null, Typeface.BOLD);
            }

        } else if (columnIndex == 1) { //Charge move in table
            tv.setTextColor(getMoveColor(move.isChargeIsLegacy()));
            tv.setText(move.getCharge());
            if (move.equals(scannedMoveset)) {
                tv.setTypeface(null, Typeface.BOLD);
            }

        } else if (columnIndex == 2) { // Attack score in table
            tv.setTextColor(getPowerColor(move.getAtkScore()));
            tv.setText(scoreFormat.format(move.getAtkScore()));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);

        } else if (columnIndex == 3) { // Defence score in table
            tv.setTextColor(getPowerColor(move.getDefScore()));
            tv.setText(scoreFormat.format(move.getDefScore()));
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }

        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);


        tv.setPadding((int) (dp * 2), (int) (dp * 4), (int) (dp * 2), (int) (dp * 4));
        tv.setSingleLine(true);
        tv.setEllipsize(TextUtils.TruncateAt.END);

        /*
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins((int) (dp * 2), (int) (dp * 2), (int) (dp * 2), (int) (dp * 2));
        tv.setLayoutParams(lp);
*/

        return tv;
    }

    private void selectScannedMoveset(@NonNull Pokefly pokefly, @Nullable MovesetData[] data) {
        if (data == null) {
            return;
        }

        int bestDistance = Integer.MAX_VALUE;
        for (MovesetData moveset : data) {
            int quickDistance =
                    Data.levenshteinDistance(pokefly.movesetQuick.toLowerCase(), moveset.getQuick().toLowerCase());
            int chargeDistance =
                    Data.levenshteinDistance(pokefly.movesetCharge.toLowerCase(), moveset.getCharge().toLowerCase());
            int combinedDistance = (quickDistance + 1) * (chargeDistance + 1);
            if (combinedDistance < bestDistance) {
                scannedMoveset = moveset;
                bestDistance = combinedDistance;
            }
        }
    }

    private int getIsSelectedColor(boolean scanned) {
        if (scanned) {
            return Color.parseColor("#edfcef");
        } else {
            return Color.parseColor("#ffffff");
        }
    }

    private int getMoveColor(boolean legacy) {
        if (legacy) {
            return Color.parseColor("#a3a3a3");
        } else {
            return Color.parseColor("#282828");
        }
    }

    private int getPowerColor(double atkScore) {
        if (atkScore > 0.95) {
            return Color.parseColor("#4c8fdb");
        }
        if (atkScore > 0.85) {
            return Color.parseColor("#8eed94");
        }
        if (atkScore > 0.7) {
            return Color.parseColor("#f9a825");
        }
        return Color.parseColor("#d84315");
    }

}

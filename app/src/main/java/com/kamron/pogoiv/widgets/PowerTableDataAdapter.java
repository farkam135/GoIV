package com.kamron.pogoiv.widgets;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kamron.pogoiv.scanlogic.MovesetData;

import java.text.DecimalFormat;

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
        float dp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getContext().getResources()
                .getDisplayMetrics());
        View renderedView = null;

        TextView tv = new TextView(getContext());
        if (columnIndex == 0) {

            tv.setTextColor(getMoveColor(move.isQuickIsLegacy()));
            //substring to remove the "_fast", replace to switch _ with spaces.
            String quickText = move.getQuick().substring(0, move.getQuick().length() - 5).replace("_", " ");
            quickText = properCase(quickText);
            tv.setText(quickText);
            //tv.setBackgroundColor(getIsSelectedColor(isScannedQuick(move)));
        } else if (columnIndex == 1) {
            tv.setTextColor(getMoveColor(move.isChargeIsLegacy()));
            String chargeText = move.getCharge().replace("_", " ");
            chargeText = properCase(chargeText);
            tv.setText(chargeText);
            //tv.setBackgroundColor(getIsSelectedColor(isScannedCharge(move)));
        } else if (columnIndex == 2) {
            //tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setTextColor(getPowerColor(move.getAtkScore()));
            tv.setText(translateScore(move.getAtkScore()));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else if (columnIndex == 3) {
            //tv.setTextColor(Color.parseColor("#ffffff"));
            tv.setTextColor(getPowerColor(move.getDefScore()));
            tv.setText(translateScore(move.getDefScore()));
            tv.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
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

        renderedView = tv;
        return renderedView;
    }

    private String translateScore(double defScore) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(defScore);
    }

    private boolean isScannedCharge(MovesetData move) {
        //todo check if the moveset charge attack is the same as the one in the ivScanresult.
        return false;
    }

    private boolean isScannedQuick(MovesetData move) {
        //todo check if the moveset quickattack is the same as the one in the ivScanresult.
        return false;
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

    /**
     * Make the first letter uppercase, and the rest lowercase. Taken from:
     * https://stackoverflow.com/questions/2375649/converting-to-upper-and-lower-case-in-java.
     *
     * @param inputVal The text to change the upper/lower case of.
     */
    String properCase(String inputVal) {
        // Empty strings should be returned as-is.

        if (inputVal.length() == 0) {
            return "";
        }

        // Strings with only one character uppercased.

        if (inputVal.length() == 1) {
            return inputVal.toUpperCase();
        }

        // Otherwise uppercase first letter, lowercase the rest.

        return inputVal.substring(0, 1).toUpperCase()
                + inputVal.substring(1).toLowerCase();
    }
}

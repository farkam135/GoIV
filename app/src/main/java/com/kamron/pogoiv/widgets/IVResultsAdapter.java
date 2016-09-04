package com.kamron.pogoiv.widgets;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamron.pogoiv.GUIUtil;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;

/**
 * Created by OskO on 29/08/16.
 */
public class IVResultsAdapter extends RecyclerView.Adapter<IVResultsAdapter.ResultsViewHolder> {
    private final LayoutInflater layoutInflater;
    private final IVScanResult mDataSet;

    public IVResultsAdapter(IVScanResult ivScanResult, Context context) {
        layoutInflater = LayoutInflater.from(context);
        mDataSet = ivScanResult;
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iv_results_item, parent, false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder holder, int position) {
        IVCombination currentSet = mDataSet.iVCombinations.get(position);
        holder.resultAttack.setText(String.valueOf(currentSet.att));
        holder.resultDefense.setText(String.valueOf(currentSet.def));
        holder.resultHP.setText(String.valueOf(currentSet.sta));
        holder.resultPercentage.setText(String.valueOf(currentSet.percentPerfect));

        GUIUtil.setTextColorbyPercentage(holder.resultAttack, (int) Math.round(currentSet.att * 100.0 / 15));
        GUIUtil.setTextColorbyPercentage(holder.resultDefense, (int) Math.round(currentSet.def * 100.0 / 15));
        GUIUtil.setTextColorbyPercentage(holder.resultHP, (int) Math.round(currentSet.sta * 100.0 / 15));
        GUIUtil.setTextColorbyPercentage(holder.resultPercentage, currentSet.percentPerfect);

        if (position % 2 != 0) {
            holder.llRvResult.setBackgroundColor(Color.parseColor("#EFEFEF"));
        } else {
            holder.llRvResult.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.iVCombinations.size();
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder {
        final TextView resultAttack, resultDefense, resultHP, resultPercentage;
        final LinearLayout llRvResult;

        public ResultsViewHolder(View itemView) {
            super(itemView);

            resultAttack = (TextView) itemView.findViewById(R.id.resultAttack);
            resultDefense = (TextView) itemView.findViewById(R.id.resultDefense);
            resultHP = (TextView) itemView.findViewById(R.id.resultHP);
            resultPercentage = (TextView) itemView.findViewById(R.id.resultPercentage);
            llRvResult = (LinearLayout) itemView.findViewById(R.id.llRvResult);
        }
    }
}
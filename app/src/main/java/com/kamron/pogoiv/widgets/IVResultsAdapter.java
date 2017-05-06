package com.kamron.pogoiv.widgets;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamron.pogoiv.GuiUtil;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.logic.IVCombination;
import com.kamron.pogoiv.logic.IVScanResult;

/**
 * Created by OskO on 29/08/16.
 */
public class IVResultsAdapter extends RecyclerView.Adapter<IVResultsAdapter.ResultsViewHolder> {
    private final IVScanResult dataSet;
    private Pokefly pokefly;

    public IVResultsAdapter(IVScanResult ivScanResult, Pokefly pokefly) {
        dataSet = ivScanResult;
        this.pokefly = pokefly;
    }

    @Override
    public ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.iv_results_item, parent, false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder holder, int position) {
        IVCombination currentSet = dataSet.iVCombinations.get(position);
        holder.resultAttack.setText(String.valueOf(currentSet.att));
        holder.resultDefense.setText(String.valueOf(currentSet.def));
        holder.resultHP.setText(String.valueOf(currentSet.sta));
        holder.resultPercentage.setText(String.valueOf(currentSet.percentPerfect));

        GuiUtil.setTextColorByIV(holder.resultAttack, currentSet.att);
        GuiUtil.setTextColorByIV(holder.resultDefense, currentSet.def);
        GuiUtil.setTextColorByIV(holder.resultHP, currentSet.sta);
        GuiUtil.setTextColorByPercentage(holder.resultPercentage, currentSet.percentPerfect);

        if (position % 2 != 0) {
            holder.llRvResult.setBackgroundColor(Color.parseColor("#EFEFEF"));
        } else {
            holder.llRvResult.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.iVCombinations.size();
    }

    class ResultsViewHolder extends RecyclerView.ViewHolder {
        final TextView resultAttack;
        final TextView resultDefense;
        final TextView resultHP;
        final TextView resultPercentage;
        final LinearLayout llRvResult;

        public ResultsViewHolder(View itemView) {
            super(itemView);

            resultAttack = (TextView) itemView.findViewById(R.id.resultAttack);
            resultDefense = (TextView) itemView.findViewById(R.id.resultDefense);
            resultHP = (TextView) itemView.findViewById(R.id.resultHP);
            resultPercentage = (TextView) itemView.findViewById(R.id.resultPercentage);
            llRvResult = (LinearLayout) itemView.findViewById(R.id.llRvResult);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    pokefly.addSpecificClipboard(dataSet, new IVCombination(
                            Integer.parseInt(resultAttack.getText().toString()),
                            Integer.parseInt(resultDefense.getText().toString()),
                            Integer.parseInt(resultHP.getText().toString())
                    ));
                }
            });
        }

    }


}
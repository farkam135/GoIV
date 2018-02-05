package com.kamron.pogoiv.pokeflycomponents.fractions;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.utils.fractions.Fraction;
import com.kamron.pogoiv.widgets.recyclerviews.adapters.IVResultsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;


public class IVCombinationsFraction extends Fraction {

    @BindView(R.id.rvResults)
    RecyclerView rvResults;


    private Context context;
    private Pokefly pokefly;
    private IVScanResult ivScanResult;


    public IVCombinationsFraction(@NonNull Pokefly pokefly, @NonNull IVScanResult ivScanResult) {
        this.context = pokefly;
        this.pokefly = pokefly;
        this.ivScanResult = ivScanResult;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.dialog_all_possibilities;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        // All IV combinations RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvResults.setLayoutManager(layoutManager);
        rvResults.setHasFixedSize(true);
        ivScanResult.sortCombinations();
        IVResultsAdapter ivResults = new IVResultsAdapter(ivScanResult, pokefly);
        rvResults.setAdapter(ivResults);
    }

    @Override public void onDestroy() {
        // Nothing to do
    }

}


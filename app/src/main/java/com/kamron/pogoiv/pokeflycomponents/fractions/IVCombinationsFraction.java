package com.kamron.pogoiv.pokeflycomponents.fractions;


import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.utils.fractions.Fraction;
import com.kamron.pogoiv.widgets.recyclerviews.adapters.IVResultsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class IVCombinationsFraction extends Fraction {

    @BindView(R.id.rvResults)
    RecyclerView rvResults;


    private Context context;
    private Pokefly pokefly;


    public IVCombinationsFraction(@NonNull Pokefly pokefly) {
        this.context = pokefly;
        this.pokefly = pokefly;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fraction_iv_combinations;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        // All IV combinations RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        rvResults.setLayoutManager(layoutManager);
        rvResults.setHasFixedSize(true);
        Pokefly.scanResult.sortIVCombinations();
        rvResults.setAdapter(new IVResultsAdapter(Pokefly.scanResult, pokefly));
    }

    @Override public void onDestroy() {
        // Nothing to do
    }

    @Override
    public Anchor getAnchor() {
        return Anchor.BOTTOM;
    }

    @Override
    public int getVerticalOffset(@NonNull DisplayMetrics displayMetrics) {
        return 0;
    }

    @OnClick(R.id.btnBack)
    void onBack() {
        pokefly.navigateToIVResultFraction();
    }

    @OnClick(R.id.btnClose)
    void onClose() {
        pokefly.closeInfoDialog();
    }

}


package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.utils.GuiUtil;
import com.kamron.pogoiv.utils.fractions.Fraction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class IVResultFraction extends Fraction {

    @BindView(R.id.tvSeeAllPossibilities)
    TextView seeAllPossibilities;

    @BindView(R.id.llMaxIV)
    LinearLayout llMaxIV;
    @BindView(R.id.llMinIV)
    LinearLayout llMinIV;
    @BindView(R.id.llMultipleIVMatches)
    LinearLayout llMultipleIVMatches;
    @BindView(R.id.llSingleMatch)
    LinearLayout llSingleMatch;
    @BindView(R.id.tvAvgIV)
    TextView tvAvgIV;
    @BindView(R.id.resultsCombinations)
    TextView resultsCombinations;
    @BindView(R.id.correctCPLevel)
    TextView correctCPorLevel;
    @BindView(R.id.resultsPokemonName)
    TextView resultsPokemonName;
    @BindView(R.id.resultsAttack)
    TextView resultsAttack;
    @BindView(R.id.resultsDefense)
    TextView resultsDefense;
    @BindView(R.id.resultsHP)
    TextView resultsHP;
    @BindView(R.id.resultsPokemonLevel)
    TextView resultsPokemonLevel;

    @BindView(R.id.resultsMinPercentage)
    TextView resultsMinPercentage;
    @BindView(R.id.resultsAvePercentage)
    TextView resultsAvePercentage;
    @BindView(R.id.resultsMaxPercentage)
    TextView resultsMaxPercentage;


    private Context context;
    private Pokefly pokefly;
    private IVScanResult ivScanResult;


    public IVResultFraction(@NonNull Pokefly pokefly, @NonNull IVScanResult ivScanResult) {
        this.context = pokefly;
        this.pokefly = pokefly;
        this.ivScanResult = ivScanResult;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fragment_ivresult;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        // Show IV information
        ivScanResult.sortCombinations();
        populateResultsHeader();

        if (ivScanResult.getCount() == 0) {
            populateNotIVMatch();
        } else if (ivScanResult.getCount() == 1) {
            populateSingleIVMatch();
        } else { // More than a match
            populateMultipleIVMatch();
        }
        setResultScreenPercentageRange(); //color codes the result
    }

    @Override public void onDestroy() {
        // Nothing to do
    }

    /**
     * Populates the result screen with the layout as if its multiple results.
     */
    private void populateMultipleIVMatch() {
        llMaxIV.setVisibility(View.VISIBLE);
        llMinIV.setVisibility(View.VISIBLE);
        llSingleMatch.setVisibility(View.GONE);
        llMultipleIVMatches.setVisibility(View.VISIBLE);
        tvAvgIV.setText(context.getString(R.string.avg));

        resultsCombinations.setText(
                context.getString(R.string.possible_iv_combinations, ivScanResult.iVCombinations.size()));

        seeAllPossibilities.setVisibility(View.VISIBLE);
        correctCPorLevel.setVisibility(View.GONE);
    }


    /**
     * Displays the all possibilities dialog.
     */
    @OnClick(R.id.tvSeeAllPossibilities)
    public void displayAllPossibilities() {
        pokefly.navigateToIVCombinationsFraction();
    }

    /**
     * Shows the name and level of the pokemon in the results dialog.
     */
    private void populateResultsHeader() {
        resultsPokemonName.setText(ivScanResult.pokemon.toString());
        resultsPokemonLevel.setText(
                context.getString(R.string.level_num, ivScanResult.estimatedPokemonLevel.toString()));
    }


    /**
     * Populates the result screen with error warning.
     */
    private void populateNotIVMatch() {
        llMaxIV.setVisibility(View.VISIBLE);
        llMinIV.setVisibility(View.VISIBLE);
        llSingleMatch.setVisibility(View.GONE);
        llMultipleIVMatches.setVisibility(View.VISIBLE);
        tvAvgIV.setText(context.getString(R.string.avg));

        resultsCombinations.setText(
                context.getString(R.string.possible_iv_combinations, ivScanResult.iVCombinations.size()));

        seeAllPossibilities.setVisibility(View.GONE);
        correctCPorLevel.setVisibility(View.VISIBLE);
    }


    /**
     * Populates the result screen with the layout as if it's a single result.
     */
    private void populateSingleIVMatch() {
        llMaxIV.setVisibility(View.GONE);
        llMinIV.setVisibility(View.GONE);
        tvAvgIV.setText(context.getString(R.string.iv));
        resultsAttack.setText(String.valueOf(ivScanResult.iVCombinations.get(0).att));
        resultsDefense.setText(String.valueOf(ivScanResult.iVCombinations.get(0).def));
        resultsHP.setText(String.valueOf(ivScanResult.iVCombinations.get(0).sta));

        GuiUtil.setTextColorByIV(resultsAttack, ivScanResult.iVCombinations.get(0).att);
        GuiUtil.setTextColorByIV(resultsDefense, ivScanResult.iVCombinations.get(0).def);
        GuiUtil.setTextColorByIV(resultsHP, ivScanResult.iVCombinations.get(0).sta);

        llSingleMatch.setVisibility(View.VISIBLE);
        llMultipleIVMatches.setVisibility(View.GONE);
        seeAllPossibilities.setVisibility(View.VISIBLE);
        correctCPorLevel.setVisibility(View.GONE);
    }


    /**
     * Fixes the three boxes that show iv range color and text.
     */
    private void setResultScreenPercentageRange() {
        int low = 0;
        int ave = 0;
        int high = 0;
        if (ivScanResult.iVCombinations.size() > 0) {
            low = ivScanResult.getLowestIVCombination().percentPerfect;
            ave = ivScanResult.getAveragePercent();
            high = ivScanResult.getHighestIVCombination().percentPerfect;
        }
        GuiUtil.setTextColorByPercentage(resultsMinPercentage, low);
        GuiUtil.setTextColorByPercentage(resultsAvePercentage, ave);
        GuiUtil.setTextColorByPercentage(resultsMaxPercentage, high);


        if (ivScanResult.iVCombinations.size() > 0) {
            resultsMinPercentage.setText(context.getString(R.string.percent, low));
            resultsAvePercentage.setText(context.getString(R.string.percent, ave));
            resultsMaxPercentage.setText(context.getString(R.string.percent, high));
        } else {
            String unknown_percent = context.getString(R.string.unknown_percent);
            resultsMinPercentage.setText(unknown_percent);
            resultsAvePercentage.setText(unknown_percent);
            resultsMaxPercentage.setText(unknown_percent);
        }
    }

}


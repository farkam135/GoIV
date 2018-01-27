package com.kamron.pogoiv.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.IVScanResult;
import com.kamron.pogoiv.utils.GuiUtil;
import com.kamron.pogoiv.widgets.recyclerviews.adapters.IVResultsAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class IVResultFragment extends Fragment {

    @BindView(R.id.tvSeeAllPossibilities)
    TextView seeAllPossibilities;

    @BindView(R.id.llMaxIV)
    LinearLayout llMaxIV;
    @BindView(R.id.llMinIV)
    LinearLayout llMinIV;
    @BindView(R.id.llMultipleIVMatches)
    LinearLayout llMultipleIVMatches;
    @BindView(R.id.refine_by_last_scan)
    LinearLayout refine_by_last_scan;
    @BindView(R.id.llSingleMatch)
    LinearLayout llSingleMatch;
    @BindView(R.id.tvAvgIV)
    TextView tvAvgIV;
    @BindView(R.id.resultsCombinations)
    TextView resultsCombinations;
    @BindView(R.id.rvResults)
    RecyclerView rvResults;
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


    Pokefly pokefly;

    public IVResultFragment() {
        createAllIvLayout();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_ivresult, container, false);
        ButterKnife.bind(this, thisView);
        return thisView;
    }

    /**
     * Populates the result screen with the layout as if its multiple results.
     */
    private void populateMultipleIVMatch(IVScanResult ivScanResult) {
        llMaxIV.setVisibility(View.VISIBLE);
        llMinIV.setVisibility(View.VISIBLE);
        llSingleMatch.setVisibility(View.GONE);
        llMultipleIVMatches.setVisibility(View.VISIBLE);
        tvAvgIV.setText(getString(R.string.avg));

        resultsCombinations.setText(
                String.format(getString(R.string.possible_iv_combinations), ivScanResult.iVCombinations.size()));


        populateAllIvPossibilities(ivScanResult);
        seeAllPossibilities.setVisibility(View.VISIBLE);
        correctCPorLevel.setVisibility(View.GONE);
    }

    /**
     * Adds all options in the all iv possibilities list.
     */
    private void populateAllIvPossibilities(IVScanResult ivScanResult) {
        IVResultsAdapter ivResults = new IVResultsAdapter(ivScanResult, pokefly);
        rvResults.setAdapter(ivResults);
    }

    /**
     * Creates and initializes the components in the "screen" in he floating dialog that shows all possible iv
     * combinations.
     */
    private void createAllIvLayout() {
        // Setting up Recyclerview for further use.
        LinearLayoutManager layoutManager = new LinearLayoutManager(pokefly);
        rvResults.hasFixedSize();

        rvResults.setLayoutManager(layoutManager);
        rvResults.setItemAnimator(new DefaultItemAnimator());

    }


    /**
     * Displays the all possibilities dialog.
     */
    @OnClick(R.id.tvSeeAllPossibilities)
    public void displayAllPossibilities() {
        pokefly.resultsBox.setVisibility(View.GONE);
        pokefly.allPossibilitiesBox.setVisibility(View.VISIBLE);
    }


    /**
     * Sets all the information in the result box.
     */
    public void populateResultsBox(IVScanResult ivScanResult) {
        ivScanResult.sortCombinations();
        populateResultsHeader(ivScanResult);


        if (ivScanResult.getCount() == 0) {
            populateNotIVMatch(ivScanResult);
        } else if (ivScanResult.getCount() == 1) {
            populateSingleIVMatch(ivScanResult);
        } else { // More than a match
            populateMultipleIVMatch(ivScanResult);
        }
        setResultScreenPercentageRange(ivScanResult); //color codes the result


    }

    /**
     * Shows the name and level of the pokemon in the results dialog.
     */
    private void populateResultsHeader(IVScanResult ivScanResult) {
        resultsPokemonName.setText(ivScanResult.pokemon.toString());
        resultsPokemonLevel.setText(getString(R.string.level_num, ivScanResult.estimatedPokemonLevel.toString()));
    }


    /**
     * Populates the result screen with error warning.
     */
    private void populateNotIVMatch(IVScanResult ivScanResult) {
        llMaxIV.setVisibility(View.VISIBLE);
        llMinIV.setVisibility(View.VISIBLE);
        llSingleMatch.setVisibility(View.GONE);
        llMultipleIVMatches.setVisibility(View.VISIBLE);
        tvAvgIV.setText(getString(R.string.avg));

        resultsCombinations.setText(
                String.format(getString(R.string.possible_iv_combinations), ivScanResult.iVCombinations.size()));

        seeAllPossibilities.setVisibility(View.GONE);
        correctCPorLevel.setVisibility(View.VISIBLE);
    }


    /**
     * Populates the result screen with the layout as if it's a single result.
     */
    private void populateSingleIVMatch(IVScanResult ivScanResult) {
        llMaxIV.setVisibility(View.GONE);
        llMinIV.setVisibility(View.GONE);
        tvAvgIV.setText(getString(R.string.iv));
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
     *
     * @param ivScanResult the scan result used to populate the TextViews
     */
    private void setResultScreenPercentageRange(IVScanResult ivScanResult) {
        int low = 0;
        int ave = 0;
        int high = 0;
        if (ivScanResult.iVCombinations.size() != 0) {
            low = ivScanResult.getLowestIVCombination().percentPerfect;
            ave = ivScanResult.getAveragePercent();
            high = ivScanResult.getHighestIVCombination().percentPerfect;
        }
        GuiUtil.setTextColorByPercentage(resultsMinPercentage, low);
        GuiUtil.setTextColorByPercentage(resultsAvePercentage, ave);
        GuiUtil.setTextColorByPercentage(resultsMaxPercentage, high);


        if (ivScanResult.iVCombinations.size() > 0) {
            resultsMinPercentage.setText(getString(R.string.percent, low));
            resultsAvePercentage.setText(getString(R.string.percent, ave));
            resultsMaxPercentage.setText(getString(R.string.percent, high));
        } else {
            String unknown_percent = getString(R.string.unknown_percent);
            resultsMinPercentage.setText(unknown_percent);
            resultsAvePercentage.setText(unknown_percent);
            resultsMaxPercentage.setText(unknown_percent);
        }
    }

}


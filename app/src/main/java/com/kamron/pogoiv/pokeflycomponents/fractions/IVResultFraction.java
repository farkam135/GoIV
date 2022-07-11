package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.PokemonShareHandler;
import com.kamron.pogoiv.utils.GUIColorFromPokeType;
import com.kamron.pogoiv.utils.GuiUtil;
import com.kamron.pogoiv.utils.ReactiveColorListener;
import com.kamron.pogoiv.utils.fractions.Fraction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class IVResultFraction extends Fraction implements ReactiveColorListener {

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


    @BindView(R.id.ivResultsHeader)
    LinearLayout ivResultsHeader;
    @BindView(R.id.powerUpButton)
    Button powerUpButton;
    @BindView(R.id.ivButton)
    Button ivButton;
    @BindView(R.id.movesetButton)
    Button movesetButton;

    @BindView(R.id.baseStatsResults)
    TextView baseStatsResults;


    private Context context;
    private Pokefly pokefly;


    public IVResultFraction(@NonNull Pokefly pokefly) {
        this.context = pokefly;
        this.pokefly = pokefly;
    }


    @Override
    public int getLayoutResId() {
        return R.layout.fraction_iv_result;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        // Show IV information
        Pokefly.scanResult.sortIVCombinations();
        populateResultsHeader();

        if (Pokefly.scanResult.getIVCombinationsCount() == 0) {
            populateNotIVMatch();
        } else if (Pokefly.scanResult.getIVCombinationsCount() == 1) {
            populateSingleIVMatch();
        } else { // More than a match
            populateMultipleIVMatch();
        }
        setResultScreenPercentageRange(); //color codes the result
        GUIColorFromPokeType.getInstance().setListenTo(this);
        updateGuiColors();
        setBasePokemonStatsText();
    }

    private void setBasePokemonStatsText() {
        int att = Pokefly.scanResult.pokemon.baseAttack;
        int def = Pokefly.scanResult.pokemon.baseDefense;
        int sta = Pokefly.scanResult.pokemon.baseStamina;
        baseStatsResults.setText("Base stats: Att - " + att + " Def - " + def + " Sta - " + sta);
    }


    @Override public void onDestroy() {
        // Nothing to do
        GUIColorFromPokeType.getInstance().removeListener(this);
    }

    @Override
    public Anchor getAnchor() {
        return Anchor.BOTTOM;
    }

    @Override
    public int getVerticalOffset(@NonNull DisplayMetrics displayMetrics) {
        return 0;
    }

    /**
     * Displays the all possibilities dialog.
     */
    @OnClick(R.id.tvSeeAllPossibilities)
    public void displayAllPossibilities() {
        pokefly.navigateToIVCombinationsFraction();
    }

    @OnClick(R.id.powerUpButton)
    void onPowerUp() {
        pokefly.navigateToPowerUpFraction();
    }

    @OnClick(R.id.movesetButton)
    void onMoveset() {
        pokefly.navigateToMovesetFraction();
    }

    @OnClick(R.id.btnBack)
    void onBack() {
        pokefly.navigateToPreferredStartFraction();
    }

    @OnClick(R.id.btnClose)
    void onClose() {
        pokefly.closeInfoDialog();
    }

    /**
     * Shows the name and level of the pokemon in the results dialog.
     */
    private void populateResultsHeader() {
        resultsPokemonName.setText(Pokefly.scanResult.pokemon.toString());
        resultsPokemonLevel.setText(
                context.getString(R.string.level_num, Pokefly.scanResult.levelRange.toString()));
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
                context.getString(R.string.possible_iv_combinations, Pokefly.scanResult.getIVCombinationsCount()));

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
        resultsAttack.setText(String.valueOf(Pokefly.scanResult.getIVCombinationAt(0).att));
        resultsDefense.setText(String.valueOf(Pokefly.scanResult.getIVCombinationAt(0).def));
        resultsHP.setText(String.valueOf(Pokefly.scanResult.getIVCombinationAt(0).sta));

        GuiUtil.setTextColorByIV(resultsAttack, Pokefly.scanResult.getIVCombinationAt(0).att);
        GuiUtil.setTextColorByIV(resultsDefense, Pokefly.scanResult.getIVCombinationAt(0).def);
        GuiUtil.setTextColorByIV(resultsHP, Pokefly.scanResult.getIVCombinationAt(0).sta);

        llSingleMatch.setVisibility(View.VISIBLE);
        int possibleCombinationsCount = Pokefly.scanResult.getIVCombinations().size();
        if (possibleCombinationsCount > 1) {
            // We are showing a single match since the user selected one combination but there are
            // more. Let the user see their count and press "see all" to select another combination.
            llMultipleIVMatches.setVisibility(View.VISIBLE);
            resultsCombinations.setText(
                    context.getString(R.string.possible_iv_combinations, possibleCombinationsCount));
            seeAllPossibilities.setVisibility(View.VISIBLE);
        } else {
            llMultipleIVMatches.setVisibility(View.GONE);
        }
        correctCPorLevel.setVisibility(View.GONE);
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
                context.getString(R.string.possible_iv_combinations, Pokefly.scanResult.getIVCombinationsCount()));

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
        if (Pokefly.scanResult.getIVCombinationsCount() > 0) {
            low = Pokefly.scanResult.getLowestIVCombination().percentPerfect;
            ave = Pokefly.scanResult.getIVPercentAvg();
            high = Pokefly.scanResult.getHighestIVCombination().percentPerfect;
        }
        GuiUtil.setTextColorByPercentage(resultsMinPercentage, low);
        GuiUtil.setTextColorByPercentage(resultsAvePercentage, ave);
        GuiUtil.setTextColorByPercentage(resultsMaxPercentage, high);


        if (Pokefly.scanResult.getIVCombinationsCount() > 0) {
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

    /**
     * Creates an intent to share the result of the pokemon scan, and closes the overlay.
     */
    @OnClick({R.id.shareWithOtherApp})
    void shareScannedPokemonInformation() {
        PokemonShareHandler communicator = new PokemonShareHandler();
        communicator.spreadResultIntent(pokefly);
        pokefly.closeInfoDialog();
    }

    @Override public void updateGuiColors() {
        int c = GUIColorFromPokeType.getInstance().getColor();
        powerUpButton.setBackgroundColor(c);
        movesetButton.setBackgroundColor(c);
        ivResultsHeader.setBackgroundColor(c);
    }
}


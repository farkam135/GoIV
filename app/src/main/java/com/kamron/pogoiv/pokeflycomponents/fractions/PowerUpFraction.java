package com.kamron.pogoiv.pokeflycomponents.fractions;


import android.content.Context;
import android.content.res.ColorStateList;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.CPRange;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.PokeSpam;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.ScanResult;
import com.kamron.pogoiv.scanlogic.UpgradeCost;
import com.kamron.pogoiv.utils.GUIColorFromPokeType;
import com.kamron.pogoiv.utils.ReactiveColorListener;
import com.kamron.pogoiv.utils.fractions.Fraction;
import com.kamron.pogoiv.widgets.PokemonSpinnerAdapter;

import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.apptik.widget.MultiSlider;


/**
 * A simple {@link Fragment} subclass.
 */
public class PowerUpFraction extends Fraction implements ReactiveColorListener {

    @BindView(R.id.expandedLevelSeekbar)
    SeekBar expandedLevelSeekbar;
    @BindView(R.id.exResLevel)
    TextView exResLevel;
    @BindView(R.id.exResultCP)
    TextView exResultCP;
    @BindView(R.id.exResultCPPlus)
    TextView exResultCPPlus;
    @BindView(R.id.extendedEvolutionSpinner)
    Spinner extendedEvolutionSpinner;
    @BindView(R.id.exResultHP)
    TextView exResultHP;
    @BindView(R.id.exResultHPPlus)
    TextView exResultHPPlus;
    @BindView(R.id.exResultPercentPerfection)
    TextView exResultPercentPerfection;
    @BindView(R.id.exResStardust)
    TextView exResStardust;
    @BindView(R.id.exResPokeSpam)
    TextView exResPokeSpam;
    @BindView(R.id.expandedLevelSeekbarBackground)
    MultiSlider expandedLevelSeekbarBackground;

    @BindView(R.id.llPokeSpam)
    LinearLayout pokeSpamView;
    @BindView(R.id.exResCandy)
    TextView exResCandy;
    @BindView(R.id.exResXlCandy)
    TextView exResXlCandy;



    @BindView(R.id.powerupHeader)
    LinearLayout powerupHeader;
    @BindView(R.id.powerUpButton)
    Button powerUpButton;
    @BindView(R.id.ivButton)
    Button ivButton;
    @BindView(R.id.movesetButton)
    Button movesetButton;

    private Context context;
    private Pokefly pokefly;
    private PokemonSpinnerAdapter extendedEvolutionSpinnerAdapter;
    private ColorStateList exResLevelDefaultColor;


    public PowerUpFraction(@NonNull Pokefly pokefly) {
        this.context = pokefly;
        this.pokefly = pokefly;
    }

    @Override public int getLayoutResId() {
        return R.layout.fraction_power_up;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        exResLevelDefaultColor = exResLevel.getTextColors();

        createExtendedResultLevelSeekbar();
        createExtendedResultEvolutionSpinner();
        adjustSeekbarsThumbs();
        populateAdvancedInformation();

        updateGuiColors();
        GUIColorFromPokeType.getInstance().setListenTo(this);
    }



    @Override public void onDestroy() {
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

    @OnClick(R.id.ivButton)
    void onIV() {
        pokefly.navigateToIVResultFraction();
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
     * Sets the growth estimate text boxes to correspond to the
     * pokemon evolution and level set by the user.
     */
    public void populateAdvancedInformation() {
        double selectedLevel = seekbarProgressToLevel(expandedLevelSeekbar.getProgress());
        Pokemon selectedPokemon = initPokemonSpinnerIfNeeded(Pokefly.scanResult.pokemon);

        setEstimateCpTextBox(Pokefly.scanResult, selectedLevel, selectedPokemon);
        setEstimateHPTextBox(Pokefly.scanResult, selectedLevel, selectedPokemon);
        setPokemonPerfectionPercentageText(Pokefly.scanResult, selectedLevel, selectedPokemon);
        setEstimateCostTextboxes(Pokefly.scanResult, selectedLevel, selectedPokemon, Pokefly.scanResult.isLucky);
        exResLevel.setText(String.valueOf(selectedLevel));
        setEstimateLevelTextColor(selectedLevel);

        setAndCalculatePokeSpamText(Pokefly.scanResult);
    }

    /**
     * Initialize the pokemon spinner in the evolution and powerup box in the result window, and return picked pokemon.
     * <p/>
     * The method will populate the spinner with the correct pokemon evolution line, and disable the spinner if there's
     * the evolution line contains only one pokemon. The method will also select by default either the evolution of
     * the scanned pokemon (if there is one) or the pokemon itself.
     * <p/>
     * This method only does anything if it detects that the spinner was not previously initialized.
     *
     * @param scannedPokemon the pokemon to use for selecting a good default, if init is performed
     */
    private Pokemon initPokemonSpinnerIfNeeded(Pokemon scannedPokemon) {
        ArrayList<Pokemon> evolutionLine = PokeInfoCalculator.getInstance().getEvolutionLine(scannedPokemon);
        extendedEvolutionSpinnerAdapter.updatePokemonList(evolutionLine);

        int spinnerSelectionIdx = extendedEvolutionSpinner.getSelectedItemPosition();

        if (spinnerSelectionIdx == -1) {
            if (!scannedPokemon.getEvolutions().isEmpty()) {
                scannedPokemon = scannedPokemon.getEvolutions().get(0);
            }
            // This happens at the beginning or after changing the pokemon list.
            //if initialising list, act as if scanned pokemon is marked
            for (int i = 0; i < evolutionLine.size(); i++) {
                if (evolutionLine.get(i).toString() == scannedPokemon.toString()) {
                    spinnerSelectionIdx = i;
                    break;
                }
            }
            //Invariant: evolutionLine.get(spinnerSelectionIdx).number == scannedPokemon.number., hence
            //evolutionLine.get(spinnerSelectionIdx) == scannedPokemon.
            extendedEvolutionSpinner.setSelection(spinnerSelectionIdx);
            extendedEvolutionSpinner.setEnabled(evolutionLine.size() > 1);
        }
        return extendedEvolutionSpinnerAdapter.getItem(spinnerSelectionIdx);
    }

    /**
     * Sets the "expected cp textview" to (+x) or (-y) in the powerup and evolution estimate box depending on what's
     * appropriate.
     *
     * @param scanResult    the ivscanresult of the current pokemon
     * @param selectedLevel   The goal level the pokemon in ivScanresult pokemon should reach
     * @param selectedPokemon The goal pokemon evolution he ivScanresult pokemon should reach
     */
    private void setEstimateCpTextBox(ScanResult scanResult, double selectedLevel, Pokemon selectedPokemon) {
        CPRange expectedRange = PokeInfoCalculator.getInstance().getCpRangeAtLevel(selectedPokemon,
                scanResult.getCombinationLowIVs(), scanResult.getCombinationHighIVs(), selectedLevel);
        int realCP = scanResult.cp;
        int expectedAverage = expectedRange.getAvg();

        exResultCP.setText(String.valueOf(expectedAverage));

        String exResultCPStrPlus = "";
        int diffCP = expectedAverage - realCP;
        if (diffCP >= 0) {
            exResultCPStrPlus += " (+" + diffCP + ")";
        } else {
            exResultCPStrPlus += " (" + diffCP + ")";
        }
        exResultCPPlus.setText(exResultCPStrPlus);
    }

    /**
     * Sets the "expected HP  textview" to the estimat HP in the powerup and evolution estimate box.
     *
     * @param scanResult  the ivscanresult of the current pokemon
     * @param selectedLevel The goal level the pokemon in ivScanresult pokemon should reach
     */
    private void setEstimateHPTextBox(ScanResult scanResult, double selectedLevel, Pokemon selectedPokemon) {
        int newHP = PokeInfoCalculator.getInstance().getHPAtLevel(scanResult, selectedLevel, selectedPokemon);

        exResultHP.setText(String.valueOf(newHP));

        int oldHP = PokeInfoCalculator.getInstance().getHPAtLevel(
                scanResult, Pokefly.scanResult.levelRange.min, scanResult.pokemon);
        int hpDiff = newHP - oldHP;
        String sign = (hpDiff >= 0) ? "+" : ""; //add plus in front if positive.
        String hpTextPlus = " (" + sign + hpDiff + ")";
        exResultHPPlus.setText(hpTextPlus);
    }

    /**
     * Sets the pokemon perfection % text in the powerup and evolution results box.
     *
     * @param scanResult    The object containing the ivs to base current pokemon on.
     * @param selectedLevel   Which level the prediction should me made for.
     * @param selectedPokemon The pokemon to compare selected iv with max iv to.
     */
    private void setPokemonPerfectionPercentageText(ScanResult scanResult,
                                                    double selectedLevel, Pokemon selectedPokemon) {
        CPRange cpRange = PokeInfoCalculator.getInstance().getCpRangeAtLevel(selectedPokemon,
                scanResult.getCombinationLowIVs(), scanResult.getCombinationHighIVs(),
                selectedLevel);
        double maxCP = PokeInfoCalculator.getInstance().getCpRangeAtLevel(selectedPokemon,
                IVCombination.MAX, IVCombination.MAX, selectedLevel).high;
        double perfection = (100.0 * cpRange.getFloatingAvg()) / maxCP;
        int difference = (int) (cpRange.getFloatingAvg() - maxCP);
        DecimalFormat df = new DecimalFormat("#.#");
        String sign = "";
        if (difference >= 0) {
            sign = "+";
        }
        String differenceString = "(" + sign + difference + ")";
        String perfectionString = df.format(perfection) + "% " + differenceString;
        exResultPercentPerfection.setText(perfectionString);
    }

    /**
     * Sets the candy cost and stardust cost textfields in the powerup and evolution estimate box. The textviews are
     * populated with the cost in dust and candy required to go from the pokemon in ivscanresult to the desired
     * selecterdLevel and selectedPokemon.
     *
     * @param scanResult    The pokemon to base the estimate on.
     * @param selectedLevel   The level the pokemon needs to reach.
     * @param selectedPokemon The target pokemon. (example, ivScan pokemon can be weedle, selected can be beedrill.)
     * @param isLucky         Whether the pokemon is lucky, and costs half the normal amount of dust.
     */
    private void setEstimateCostTextboxes(ScanResult scanResult, double selectedLevel, Pokemon selectedPokemon,
                                          boolean isLucky) {
        UpgradeCost cost = PokeInfoCalculator.getInstance()
                .getUpgradeCost(selectedLevel, Pokefly.scanResult.levelRange.min, isLucky);
        int evolutionCandyCost = PokeInfoCalculator.getInstance()
                .getCandyCostForEvolution(scanResult.pokemon, selectedPokemon);
        String candyCostText = cost.candy + evolutionCandyCost + "";
        exResCandy.setText(candyCostText);
        String candyXlCostText = Integer.toString(cost.candyXl);
        exResXlCandy.setText(candyXlCostText);
        DecimalFormat formater = new DecimalFormat();
        exResStardust.setText(formater.format(cost.dust));
    }

    /**
     * Sets the text color of the level next to the slider in the estimate box to normal or orange depending on if
     * the user can level up the pokemon that high with his current trainer level. For example, if the user has
     * trainer level 20, then his pokemon can reach a max level of 22 - so any goalLevel above 22 would become
     * orange.
     *
     * @param selectedLevel The level to reach.
     */
    private void setEstimateLevelTextColor(double selectedLevel) {
        // If selectedLevel exceeds trainer capabilities then show text in orange
        if (selectedLevel > Data.trainerLevelToMaxPokeLevel(pokefly.getTrainerLevel())) {
            exResLevel.setTextColor(ContextCompat.getColor(pokefly, R.color.orange));
        } else {
            exResLevel.setTextColor(exResLevelDefaultColor);
        }
    }


    /**
     * setAndCalculatePokeSpamText sets pokespamtext and makes it visible.
     *
     * @param scanResult ScanResult object that contains the scan results, mainly needed to get candEvolutionCost
     *                     variable
     */
    private void setAndCalculatePokeSpamText(ScanResult scanResult) {
        if (GoIVSettings.getInstance(pokefly).isPokeSpamEnabled()
                && scanResult.pokemon != null) {
            if (scanResult.pokemon.candyEvolutionCost < 0) {
                exResPokeSpam.setText(context.getString(R.string.pokespam_not_available));
                pokeSpamView.setVisibility(View.VISIBLE);
                return;
            }

            PokeSpam pokeSpamCalculator = new PokeSpam(
                    Pokefly.scanData.getPokemonCandyAmount().or(0),
                    scanResult.pokemon.candyEvolutionCost);

            // number for total evolvable
            int totEvol = pokeSpamCalculator.getTotalEvolvable();
            // number for rows of evolvables
            int evolRow = pokeSpamCalculator.getEvolveRows();
            // number for evolvables in extra row (not complete row)
            int evolExtra = pokeSpamCalculator.getEvolveExtra();

            String text;

            if (totEvol < PokeSpam.HOW_MANY_POKEMON_WE_HAVE_PER_ROW) {
                text = String.valueOf(totEvol);
            } else if (evolExtra == 0) {
                text = context.getString(R.string.pokespam_formatted_message2, totEvol, evolRow);
            } else {
                text = context.getString(R.string.pokespam_formatted_message, totEvol, evolRow, evolExtra);
            }
            exResPokeSpam.setText(text);
            pokeSpamView.setVisibility(View.VISIBLE);
        } else {
            exResPokeSpam.setText("");
            pokeSpamView.setVisibility(View.GONE);
        }
    }

    /**
     * Creates and initializes the level seekbarr in the evolution and powerup prediction section in the results
     * screen.
     */
    private void createExtendedResultLevelSeekbar() {
        expandedLevelSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser) {
                    populateAdvancedInformation();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    /**
     * Creates and initializes the evolution spinner in the evolution and powerup prediction section in the results
     * screen.
     */
    private void createExtendedResultEvolutionSpinner() {
        //The evolution picker for seeing estimates of how much cp and cost a pokemon will have at a different evolution
        extendedEvolutionSpinnerAdapter = new PokemonSpinnerAdapter(pokefly, R.layout.spinner_pokemon,
                new ArrayList<Pokemon>());
        extendedEvolutionSpinner.setAdapter(extendedEvolutionSpinnerAdapter);

        extendedEvolutionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                populateAdvancedInformation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                populateAdvancedInformation();
            }

        });

    }

    /**
     * Adjusts expandedLevelSeekbar and expandedLevelSeekbar thumbs.
     * expandedLevelSeekbar - Adjustable single thumb seekbar to allow users to check for more Pokemon stats at
     * different Pokemon level
     * expandedLevelSeekbarBackground - Static double thumb seekbar as background to identify area of Pokemon stats
     * above Pokemon level at current trainer level
     */
    private void adjustSeekbarsThumbs() {
        // Set Seekbar max value to max Pokemon level at trainer level 40
        expandedLevelSeekbar.setMax(levelToSeekbarProgress(Data.MAXIMUM_POKEMON_LEVEL));

        // Set Thumb value to current Pokemon level
        expandedLevelSeekbar.setProgress(
                levelToSeekbarProgress(Pokefly.scanResult.levelRange.min));

        // Set Seekbar Background max value to max Pokemon level at trainer level 40
        expandedLevelSeekbarBackground.setMax(levelToSeekbarProgress(Data.MAXIMUM_POKEMON_LEVEL));

        // Set Thumb 1 drawable to an orange marker and value at the max possible Pokemon level at the current
        // trainer level
        expandedLevelSeekbarBackground.getThumb(0).setThumb(
                ContextCompat.getDrawable(pokefly, R.drawable.orange_seekbar_thumb_marker));
        expandedLevelSeekbarBackground.getThumb(0).setValue(
                levelToSeekbarProgress(Data.trainerLevelToMaxPokeLevel(pokefly.getTrainerLevel())));

        // Set Thumb 2 to invisible and value at max Pokemon level at trainer level 40
        expandedLevelSeekbarBackground.getThumb(1).setInvisibleThumb(true);
        expandedLevelSeekbarBackground.getThumb(1).setValue(levelToSeekbarProgress(Data.MAXIMUM_POKEMON_LEVEL));

        // Set empty on touch listener to prevent changing values of Thumb 1
        expandedLevelSeekbarBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }


    /**
     * Calculate the seekbar progress from a pokemon level.
     *
     * @param level a valid pokemon level (hence <= 40).
     * @return a seekbar progress index.
     */
    private int levelToSeekbarProgress(double level) {
        return (int) (2 * level - getSeekbarOffset());
    }

    @OnClick(R.id.exResultPercentPerfection)
    public void explainCPPercentageComparedToMaxIV() {
        Toast.makeText(pokefly.getApplicationContext(), R.string.perfection_explainer, Toast.LENGTH_LONG).show();
    }


    @OnClick(R.id.btnIncrementLevelExpanded)
    public void incrementLevelExpanded() {
        expandedLevelSeekbar.setProgress(expandedLevelSeekbar.getProgress() + 1);
        populateAdvancedInformation();
    }


    @OnClick(R.id.btnDecrementLevelExpanded)
    public void decrementLevelExpanded() {
        expandedLevelSeekbar.setProgress(expandedLevelSeekbar.getProgress() - 1);
        populateAdvancedInformation();
    }

    private int getSeekbarOffset() {
        return (int) (2 * Pokefly.scanResult.levelRange.min);
    }

    private double seekbarProgressToLevel(int progress) {
        return (progress + getSeekbarOffset()) / 2.0;
        //seekbar only supports integers, so the seekbar works between 2 and 80.
    }

    @Override public void updateGuiColors() {
        int c = GUIColorFromPokeType.getInstance().getColor();
        ivButton.setBackgroundColor(c);
        movesetButton.setBackgroundColor(c);
        powerupHeader.setBackgroundColor(c);
    }
}

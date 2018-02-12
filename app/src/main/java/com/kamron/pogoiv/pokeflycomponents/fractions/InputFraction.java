package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.PokemonNameCorrector;
import com.kamron.pogoiv.utils.LevelRange;
import com.kamron.pogoiv.utils.fractions.Fraction;
import com.kamron.pogoiv.widgets.PokemonSpinnerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.hoang8f.android.segmented.SegmentedGroup;


public class InputFraction extends Fraction {

    @BindView(R.id.navigationButtons)
    SegmentedGroup navigationButtons;

    private PokemonSpinnerAdapter pokeInputAdapter;
    @BindView(R.id.spnPokemonName)
    Spinner pokeInputSpinner;

    //results pokemon picker auto complete
    @BindView(R.id.autoCompleteTextView1)
    AutoCompleteTextView autoCompleteTextView1;

    @BindView(R.id.pokePickerToggleSpinnerVsInput)
    ImageButton pokePickerToggleSpinnerVsInput;

    @BindView(R.id.etCp)
    EditText pokemonCPEdit;
    @BindView(R.id.etHp)
    EditText pokemonHPEdit;
    @BindView(R.id.etCandy)
    EditText pokemonCandyEdit;
    @BindView(R.id.sbArcAdjust)
    SeekBar arcAdjustBar;
    @BindView(R.id.levelIndicator)
    TextView levelIndicator;

    //PokeSpam
    @BindView(R.id.llPokeSpamDialogInputContentBox)
    LinearLayout pokeSpamDialogInputContentBox;


    private Pokefly pokefly;
    private PokeInfoCalculator pokeInfoCalculator;

    private String pokemonName;
    private String pokemonType;
    private String candyName;
    private Pokemon.Gender pokemonGender;
    private Optional<Integer> pokemonCandy;
    private Optional<Integer> pokemonCP;
    private Optional<Integer> pokemonHP;
    private Optional<Integer> candyUpgradeCost;


    public InputFraction(@NonNull Pokefly pokefly, @NonNull PokeInfoCalculator pokeInfoCalculator,
                         String pokemonName, String pokemonType, String candyName, Pokemon.Gender pokemonGender,
                         Optional<Integer> pokemonCandy, Optional<Integer> pokemonCP, Optional<Integer> pokemonHP,
                         Optional<Integer> candyUpgradeCost) {
        this.pokefly = pokefly;
        this.pokeInfoCalculator = pokeInfoCalculator;
        this.pokemonName = pokemonName;
        this.pokemonType = pokemonType;
        this.candyName = candyName;
        this.pokemonGender = pokemonGender;
        this.pokemonCandy = pokemonCandy;
        this.pokemonCP = pokemonCP;
        this.pokemonHP = pokemonHP;
        this.candyUpgradeCost = candyUpgradeCost;
    }

    @Override public int getLayoutResId() {
        return R.layout.fraction_input;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        // Don't let the user reach appraisal screen if is manual screenshot mode
        navigationButtons.setVisibility(pokefly.startedInManualScreenshotMode ? View.GONE : View.VISIBLE);

        // Initialize pokemon species spinner
        pokeInputAdapter = new PokemonSpinnerAdapter(pokefly, R.layout.spinner_pokemon, new ArrayList<Pokemon>());
        pokeInputSpinner.setAdapter(pokeInputAdapter);

        // Fix arc when level is changed
        createArcAdjuster();

        // Setup manual pokemon species input
        initializePokemonAutoCompleteTextView();

        // Guess the species
        PokemonNameCorrector.PokeDist possiblePoke = new PokemonNameCorrector(PokeInfoCalculator.getInstance())
                .getPossiblePokemon(pokemonName, candyName, candyUpgradeCost, pokemonType);

        // set color based on similarity
        if (possiblePoke.dist == 0) {
            pokeInputSpinner.setBackgroundColor(Color.parseColor("#ddffdd"));
        } else if (possiblePoke.dist < 2) {
            pokeInputSpinner.setBackgroundColor(Color.parseColor("#ffffcc"));
        } else {
            pokeInputSpinner.setBackgroundColor(Color.parseColor("#ffcccc"));
        }

        resetToSpinner(); //always have the input as spinner as default

        autoCompleteTextView1.setText("");
        pokeInputAdapter.updatePokemonList(
                pokeInfoCalculator.getEvolutionLine(possiblePoke.pokemon));
        int selection = pokeInputAdapter.getPosition(possiblePoke.pokemon);
        pokeInputSpinner.setSelection(selection);

        pokemonHPEdit.setText(optionalIntToString(pokemonHP));
        pokemonCPEdit.setText(optionalIntToString(pokemonCP));
        pokemonCandyEdit.setText(optionalIntToString(pokemonCandy));

        adjustArcPointerBar(pokefly.estimatedPokemonLevelRange.min);

        if (!GoIVSettings.getInstance(pokefly).shouldShouldConfirmationDialogs()) {
            checkIv();
        }
        showCandyTextBoxBasedOnSettings();
    }

    @Override
    public void onDestroy() {
        saveToPokefly();
    }

    private void saveToPokefly() {
        try {
            pokefly.pokemonHP = Optional.of(Integer.parseInt(pokemonHPEdit.getText().toString()));
        } catch (NumberFormatException e) {
            pokefly.pokemonHP = Optional.absent();
        }
        try {
            pokefly.pokemonCP = Optional.of(Integer.parseInt(pokemonCPEdit.getText().toString()));
        } catch (NumberFormatException e) {
            pokefly.pokemonCP = Optional.absent();
        }
        try {
            pokefly.pokemonCandy = Optional.of(Integer.parseInt(pokemonCandyEdit.getText().toString()));
        } catch (NumberFormatException e) {
            pokefly.pokemonCandy = Optional.absent();
        }
        Pokemon pokemon = interpretWhichPokemonUserInput();
        if (pokemon != null) {
            pokefly.pokemon = Optional.of(pokemon);
        } else {
            pokefly.pokemon = Optional.absent();
        }
    }

    /**
     * In the input screen, switches between the two methods the user has of picking pokemon - a dropdown list, or
     * typing.
     */
    @OnClick({R.id.pokePickerToggleSpinnerVsInput})
    public void toggleSpinnerVsInput() {
        if (autoCompleteTextView1.getVisibility() == View.GONE) {
            autoCompleteTextView1.setVisibility(View.VISIBLE);
            autoCompleteTextView1.requestFocus();
            pokeInputSpinner.setVisibility(View.GONE);
        } else {
            resetToSpinner();
        }
    }

    private void resetToSpinner() {
        autoCompleteTextView1.setVisibility(View.GONE);
        pokeInputSpinner.setVisibility(View.VISIBLE);
    }

    private void adjustArcPointerBar(double estimatedPokemonLevel) {
        pokefly.setArcPointer(estimatedPokemonLevel);
        arcAdjustBar.setProgress(Data.maxPokeLevelToIndex(estimatedPokemonLevel));
    }

    @OnClick(R.id.btnDecrementLevel)
    public void decrementLevel() {
        pokefly.estimatedPokemonLevelRange.dec();
        adjustArcPointerBar(pokefly.estimatedPokemonLevelRange.min);
    }

    @OnClick(R.id.btnIncrementLevel)
    public void incrementLevel() {
        pokefly.estimatedPokemonLevelRange.inc();
        adjustArcPointerBar(pokefly.estimatedPokemonLevelRange.min);
    }

    /**
     * Creates the arc adjuster used to move the arc pointer in the scan screen.
     */
    private void createArcAdjuster() {
        //arcAdjustBar.setMax(Data.trainerLevelToMaxPokeLevelIndex(trainerLevel));
        arcAdjustBar.setMax(Data.trainerLevelToMaxPokeLevelIndex(40));

        arcAdjustBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pokefly.estimatedPokemonLevelRange = new LevelRange(Data.levelIdxToLevel(progress));
                pokefly.setArcPointer(pokefly.estimatedPokemonLevelRange.min);
                levelIndicator.setText(String.valueOf(pokefly.estimatedPokemonLevelRange.toString()));
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
     * Initialises the autocompletetextview which allows people to search for pokemon names.
     */
    private void initializePokemonAutoCompleteTextView() {
        String[] pokeList = pokefly.getResources().getStringArray(R.array.pokemon);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(pokefly, R.layout.autocomplete_pokemon_list_item, pokeList);
        autoCompleteTextView1.setAdapter(adapter);
        autoCompleteTextView1.setThreshold(1);
    }

    /**
     * showCandyTextBoxBasedOnSettings
     * Shows candy text box if pokespam is enabled
     * Will set the Text Edit box to use next action or done if its the last text box.
     */
    private void showCandyTextBoxBasedOnSettings() {
        //enable/disable visibility based on PokeSpam enabled or not
        if (GoIVSettings.getInstance(pokefly).isPokeSpamEnabled()) {
            pokeSpamDialogInputContentBox.setVisibility(View.VISIBLE);
            pokemonHPEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            pokeSpamDialogInputContentBox.setVisibility(View.GONE);
            pokemonHPEdit.setImeOptions(EditorInfo.IME_ACTION_DONE);
        }
    }

    private <T> String optionalIntToString(Optional<T> src) {
        return src.transform(new Function<T, String>() {
            @Override
            public String apply(T input) {
                return input.toString();
            }
        }).or("");
    }

    /**
     * Checks whether the user input a pokemon using the spinner or the text input on the input screen
     * null if no correct input was provided (user typed non-existant pokemon or spinner error)
     * If user typed in incorrect pokemon, a toast will be displayed.
     *
     * @return The pokemon the user selected/typed or null if user put wrong input
     */
    private Pokemon interpretWhichPokemonUserInput() {
        //below picks a pokemon from either the pokemon spinner or the user text input
        Pokemon pokemon;
        if (pokeInputSpinner.getVisibility() == View.VISIBLE) { //user picked pokemon from spinner
            //This could be pokemon = pokeInputSpinner.getSelectedItem(); if they didn't give it type Object.
            pokemon = pokeInputAdapter.getItem(pokeInputSpinner.getSelectedItemPosition());
        } else { //user typed manually
            String userInput = autoCompleteTextView1.getText().toString();
            pokemon = pokeInfoCalculator.get(userInput);
            if (pokemon == null) { //no such pokemon was found, show error toast and abort showing results
                Toast.makeText(pokefly, userInput + pokefly.getString(R.string.wrong_pokemon_name_input),
                        Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        return pokemon;
    }

    /**
     * Method called when user presses "check iv" in the input screen, which takes the user to the result screen.
     */
    @OnClick(R.id.btnCheckIv)
    void checkIv() {
        saveToPokefly();
        pokefly.computeIv();
    }

    @OnClick(R.id.appraisalButton)
    void onAppraisal() {
        pokefly.navigateToAppraisalFraction();
    }

    @OnClick(R.id.btnClose)
    void onClose() {
        pokefly.closeInfoDialog();
    }

}

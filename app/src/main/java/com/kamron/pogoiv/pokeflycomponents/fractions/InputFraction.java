package com.kamron.pogoiv.pokeflycomponents.fractions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.legacy.widget.Space;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.scanlogic.IVCombination;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.Pokemon;
import com.kamron.pogoiv.scanlogic.PokemonBase;
import com.kamron.pogoiv.scanlogic.PokemonNameCorrector;
import com.kamron.pogoiv.scanlogic.ScanResult;
import com.kamron.pogoiv.utils.GUIColorFromPokeType;
import com.kamron.pogoiv.utils.LevelRange;
import com.kamron.pogoiv.utils.ReactiveColorListener;
import com.kamron.pogoiv.utils.fractions.Fraction;
import com.kamron.pogoiv.widgets.PokemonSpinnerAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import timber.log.Timber;


public class InputFraction extends Fraction implements ReactiveColorListener {

    private PokemonSpinnerAdapter pokeInputAdapter;
    @BindView(R.id.spnPokemonName)
    Spinner pokeInputSpinner;

    //results pokemon picker auto complete
    @BindView(R.id.autoCompleteTextView1)
    AutoCompleteTextView autoCompleteTextView1;

    @BindView(R.id.pokePickerToggleSpinnerVsInput)
    ImageView pokePickerToggleSpinnerVsInput;

    @BindView(R.id.inputHeaderBG)
    LinearLayout inputHeader;

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


    @BindView(R.id.btnCheckIv)
    Button btnCheckIv;

    @BindView(R.id.appraisalButton)
    Button appraisalButton;

    //PokeSpam
    @BindView(R.id.llPokeSpamSpace)
    Space llPokeSpamSpace;
    @BindView(R.id.llPokeSpamDialogInputContentBox)
    LinearLayout pokeSpamDialogInputContentBox;


    private Pokefly pokefly;
    private PokeInfoCalculator pokeInfoCalculator;

    //since the fragment calls onchanged, ontextchanged etc methods on fragment creation, the
    //fragment will update and calculate the pokemon several times when the ui is created.
    //To prevent this, this boolean stops any calculation, until its set to true.
    private boolean isInitiated = false;

    public InputFraction(@NonNull Pokefly pokefly) {
        this.pokefly = pokefly;
        this.pokeInfoCalculator = PokeInfoCalculator.getInstance();


    }

    @Override public int getLayoutResId() {
        return R.layout.fraction_input;
    }

    @Override public void onCreate(@NonNull View rootView) {
        ButterKnife.bind(this, rootView);

        // Initialize pokemon species spinner
        pokeInputAdapter = new PokemonSpinnerAdapter(pokefly, R.layout.spinner_pokemon, new ArrayList<Pokemon>());
        pokeInputSpinner.setAdapter(pokeInputAdapter);

        pokeInputSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                updateIVInputFractionPreview();
            }

            @Override public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // Fix arc when level is changed
        createArcAdjuster();

        // Setup manual pokemon species input
        initializePokemonAutoCompleteTextView();

        PokemonNameCorrector.PokeDist possiblePoke;
        // If not-null, then the Pokemon has been manually set, so we shouldn't guess it.
        if (Pokefly.scanData.getPokemon() != null) {
            possiblePoke = new PokemonNameCorrector.PokeDist(Pokefly.scanData.getPokemon(), 0);
        } else {
            // Guess the species
            possiblePoke = PokemonNameCorrector.getInstance(pokefly).getPossiblePokemon(Pokefly.scanData);
        }

        // set color based on similarity
        if (possiblePoke.dist == 0) {
            pokeInputSpinner.setBackgroundColor(Color.parseColor("#FFF9F9F9"));
        } else if (possiblePoke.dist < 2) {
            pokeInputSpinner.setBackgroundColor(Color.parseColor("#ffffdd"));
        } else {
            pokeInputSpinner.setBackgroundColor(Color.parseColor("#ffdddd"));
        }

        resetToSpinner(); //always have the input as spinner as default

        autoCompleteTextView1.setText("");
        pokeInputAdapter.updatePokemonList(
                pokeInfoCalculator.getEvolutionForms(possiblePoke.pokemon));
        int selection = pokeInputAdapter.getPosition(possiblePoke.pokemon);
        pokeInputSpinner.setSelection(selection);

        pokemonHPEdit.setText(optionalIntToString(Pokefly.scanData.getPokemonHP()));
        pokemonCPEdit.setText(optionalIntToString(Pokefly.scanData.getPokemonCP()));
        pokemonCandyEdit.setText(optionalIntToString(Pokefly.scanData.getPokemonCandyAmount()));

        adjustArcPointerBar(Pokefly.scanData.getEstimatedPokemonLevel().min);

        showCandyTextBoxBasedOnSettings();

        GUIColorFromPokeType.getInstance().setListenTo(this);
        updateGuiColors();
        isInitiated = true;
        updateIVInputFractionPreview();
    }



    @Override
    public void onDestroy() {
        saveToPokefly();
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

    private void saveToPokefly() {
        if (isInitiated){
            final String hp = pokemonHPEdit.getText().toString();
            if (!Strings.isNullOrEmpty(hp)) {
                try {
                    Pokefly.scanData.setPokemonHP(Integer.parseInt(hp));
                } catch (NumberFormatException e) {
                    Timber.d(e);
                }
            }
            final String cp = pokemonCPEdit.getText().toString();
            if (!Strings.isNullOrEmpty(cp)) {
                try {
                    Pokefly.scanData.setPokemonCP(Integer.parseInt(cp));
                } catch (NumberFormatException e) {
                    Timber.d(e);
                }
            }
            final String candies = pokemonCandyEdit.getText().toString();
            if (!Strings.isNullOrEmpty(candies)) {
                try {
                    Pokefly.scanData.setPokemonCandyAmount(Integer.parseInt(candies));
                } catch (NumberFormatException e) {
                    Timber.d(e);
                }
            }
            Pokemon pokemon = interpretWhichPokemonUserInput();
            if (pokemon != null) {
              Pokefly.scanData.setPokemon(pokemon);


            }
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


            Bitmap icon = BitmapFactory.decodeResource(pokefly.getResources(),
                    R.drawable.toggleselectwrite);
            pokePickerToggleSpinnerVsInput.setImageBitmap(icon);
            autoCompleteTextView1.requestFocus();
            pokeInputSpinner.setVisibility(View.GONE);
        } else {
            resetToSpinner();
            Bitmap icon = BitmapFactory.decodeResource(pokefly.getResources(),
                    R.drawable.toggleselectmenu);
            pokePickerToggleSpinnerVsInput.setImageBitmap(icon);
        }

        updateGuiColors();
    }


    private void resetToSpinner() {
        autoCompleteTextView1.setVisibility(View.GONE);
        pokeInputSpinner.setVisibility(View.VISIBLE);
    }

    private void adjustArcPointerBar(double estimatedPokemonLevel) {
        pokefly.setArcPointer(estimatedPokemonLevel);
        arcAdjustBar.setProgress(Data.levelToLevelIdx(estimatedPokemonLevel));
        updateIVInputFractionPreview();
    }

    @OnTextChanged({R.id.etCp, R.id.etHp, R.id.etCandy, R.id.autoCompleteTextView1})
    public void updateIVFractionSpinnerDueToTextChange() {
        updateIVInputFractionPreview();
    }

    /**
     * Update the text on the 'next' button to indicate quick IV overview
     */
    private void updateIVInputFractionPreview() {
        if(isInitiated){
            saveToPokefly();

            updateIVPreview(pokefly, btnCheckIv);
        }
    }

    public static void updateIVPreview(Pokefly pokefly, Button btnCheckIv) {
        ScanResult scanResult = null;
        try {
            scanResult = pokefly.computeIVWithoutUIChange();
        } catch (IllegalStateException e) {
            //Couldn't compute a valid scanresult. This is most likely due to missing HP / CP values
        }

        // If it couldn't compute a scan result it should behave the same as if there are no valid
        // IV combinations.
        int possibleIVs = scanResult == null ? 0 : scanResult.getIVCombinations().size();
        //btnCheckIv.setEnabled(possibleIVs != 0);
        if (possibleIVs == 0) {
            btnCheckIv.setText("? | More info");
        } else {
            if (possibleIVs == 1) {
                IVCombination result = scanResult.getIVCombinations().get(0);
                btnCheckIv.setText(result.percentPerfect + "% (" + result.att + ":" + result.def + ":" + result.sta + ") | More info");
            } else if (scanResult.getLowestIVCombination().percentPerfect == scanResult
                    .getHighestIVCombination().percentPerfect) {
                btnCheckIv.setText(scanResult.getLowestIVCombination().percentPerfect + "% | More info");
            } else {
                btnCheckIv.setText(scanResult.getLowestIVCombination().percentPerfect + "% - " + scanResult
                        .getHighestIVCombination().percentPerfect + "% | More info");
            }
        }
    }

    @OnClick(R.id.btnDecrementLevel)
    public void decrementLevel() {
        if (Pokefly.scanData.getEstimatedPokemonLevel().min > Data.MINIMUM_POKEMON_LEVEL) {
            Pokefly.scanData.getEstimatedPokemonLevel().dec();
            adjustArcPointerBar(Pokefly.scanData.getEstimatedPokemonLevel().min);
        }
    }

    @OnClick(R.id.btnIncrementLevel)
    public void incrementLevel() {
        if (Data.levelToLevelIdx(Pokefly.scanData.getEstimatedPokemonLevel().min) < arcAdjustBar.getMax()) {
            Pokefly.scanData.getEstimatedPokemonLevel().inc();
            adjustArcPointerBar(Pokefly.scanData.getEstimatedPokemonLevel().min);
        }
    }

    /**
     * Creates the arc adjuster used to move the arc pointer in the scan screen.
     */
    private void createArcAdjuster() {
        // The max seek bar value will be the maximum wild pokemon level or the trainer max capture level if higher
        arcAdjustBar.setMax(Math.max(Data.levelToLevelIdx(Data.MAXIMUM_WILD_POKEMON_LEVEL),
                Data.levelToLevelIdx(Data.trainerLevelToMaxPokeLevel(pokefly.getTrainerLevel()))));

        arcAdjustBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    Pokefly.scanData.setEstimatedPokemonLevelRange(new LevelRange(Data.levelIdxToLevel(progress)));
                }
                pokefly.setArcPointer(Pokefly.scanData.getEstimatedPokemonLevel().min);
                levelIndicator.setText(Pokefly.scanData.getEstimatedPokemonLevel().toString());

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                btnCheckIv.setText("...");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                updateIVInputFractionPreview();
            }
        });
    }

    /**
     * Initialises the autocompletetextview which allows people to search for pokemon names.
     */
    private void initializePokemonAutoCompleteTextView() {
        String[] pokeList = pokeInfoCalculator.getPokemonNamesWithFormArray();
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
            llPokeSpamSpace.setVisibility(View.VISIBLE);
            pokeSpamDialogInputContentBox.setVisibility(View.VISIBLE);
            pokemonHPEdit.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        } else {
            llPokeSpamSpace.setVisibility(View.GONE);
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
        Pokemon pokemon = null;
        if (pokeInputSpinner.getVisibility() == View.VISIBLE) { //user picked pokemon from spinner
            //This could be pokemon = pokeInputSpinner.getSelectedItem(); if they didn't give it type Object.
            pokemon = pokeInputAdapter.getItem(pokeInputSpinner.getSelectedItemPosition());
        } else { //user typed manually
            String userInput = autoCompleteTextView1.getText().toString();
            int lowestDist = Integer.MAX_VALUE;
            for (PokemonBase poke : pokeInfoCalculator.getPokedex()) {
                int dist = Data.levenshteinDistance(poke.name, userInput);
                if (dist < lowestDist) {
                    lowestDist = dist;
                    pokemon = poke.forms.get(0);
                }
                // Even though the above might've used forms[0], iterate over all forms as the form's name might have
                // a better distance. There might be a case where forms[1] beats poke.name but not forms[0].
                for (Pokemon form : poke.forms) {
                    dist = Data.levenshteinDistance(form.name, userInput);
                    if (dist < lowestDist) {
                        lowestDist = dist;
                        pokemon = form;
                    }
                }
            }
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

    @Override public void updateGuiColors() {
        //int c = Color.parseColor("#47253C");
        int c = GUIColorFromPokeType.getInstance().getColor();
        inputHeader.setBackgroundColor(c);
        appraisalButton.setBackgroundColor(c);
        pokemonCPEdit.setTextColor(c);
        pokemonHPEdit.setTextColor(c);
        pokemonCandyEdit.setTextColor(c);
        btnCheckIv.setBackgroundColor(c);

        PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
        Drawable d = pokePickerToggleSpinnerVsInput.getDrawable();
        d.setColorFilter(c,mMode);

    }
}

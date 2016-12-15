package com.kamron.pogoiv.clipboard;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.tokens.SeparatorToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ClipboardModifierActivity extends AppCompatActivity {

    private ClipboardTokenHandler cth;
    private TextView clipboardMaxLength;
    private TextView multipleText;
    private TextView singleText;
    private TextView clipboardDescription;
    private LinearLayout clipboardShowcase;
    private Switch clipboardMaxEvolutionVariant;
    private Switch singleResSwitch;
    private LinearLayout clipTokenEditor;
    private EditText customSeperator;
    private LinearLayout singleMultiLayout;

    private ArrayList<ClipboardTokenButton> tokenButtons = new ArrayList<>();
    private ClipboardToken selectedToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.clipboard_activity_title);
        setContentView(R.layout.activity_clipboard_modifier);
        initiateInstanceVariables();
        setSingleCheckboxShownDependingOnSetting(); //must be done after initiateInstanceVariables
        updateFields();
        fillTokenList(clipboardMaxEvolutionVariant.isChecked());
    }

    /**
     * Hides the checkfox for choosing to edit the single or multiple clipboard results.
     */
    private void setSingleCheckboxShownDependingOnSetting() {
        GoIVSettings settings = GoIVSettings.getInstance(this);
        if (settings.shouldCopyToClipboardSingle()) {
            singleMultiLayout.setVisibility(View.VISIBLE);
        } else {
            singleMultiLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Initiates instance variables.
     */
    private void initiateInstanceVariables() {
        cth = new ClipboardTokenHandler(this);
        multipleText = (TextView) findViewById(R.id.multipleText);
        singleText = (TextView) findViewById(R.id.singleText);
        clipboardMaxLength = (TextView) findViewById(R.id.clipboardMaxLength);
        clipboardDescription = (TextView) findViewById(R.id.clipboardDescription);
        clipboardShowcase = (LinearLayout) findViewById(R.id.clipboardShowcase);
        clipTokenEditor = (LinearLayout) findViewById(R.id.clipTokenEditor);
        clipboardMaxEvolutionVariant = (Switch) findViewById(R.id.clipboardMaxEvolutionVariant);
        singleResSwitch = (Switch) findViewById(R.id.singleResCheckbox);
        customSeperator = (EditText) findViewById(R.id.customSeperator);
        singleMultiLayout = (LinearLayout) findViewById(R.id.singleMultiLayout);
    }

    /**
     * Removes, and re-adds markers for each token used, which when clicked removes the token from user settings.
     */
    private void updateEditField() {
        clipTokenEditor.removeAllViews();
        int index = 0;
        String separatorTokenStringName = new SeparatorToken("").getCategory(this);

        for (ClipboardToken clipboardToken : getCurrentlyModifyingList()) {

            TextView tokenEditingBox = new TextView(this);
            if (clipboardToken.getCategory(this).equals(separatorTokenStringName)) {
                tokenEditingBox.setText(clipboardToken.getPreview() + " ❌");
            } else {
                tokenEditingBox.setText(clipboardToken.getTokenName(this) + "\n" + clipboardToken.getPreview() + " ❌");
            }
            tokenEditingBox.setPadding(0, 0, 0, 0);
            tokenEditingBox.setBackgroundColor(Color.rgb(44, 57, 128)); //dark purplish blue
            tokenEditingBox.setTextColor(Color.WHITE);

            TextView divider = new TextView(this);
            divider.setText("     ");

            final int finalI = index;
            tokenEditingBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cth.removeToken(finalI, singleResSwitch.isChecked());
                    updateFields();
                }
            });
            index++;

            clipTokenEditor.addView(divider);
            clipTokenEditor.addView(tokenEditingBox);
        }
    }

    /**
     * Get a list of tokens, the default list if user is currently modifying the default list, or the single result
     * user token list if that checkbox is marked.
     *
     * @return The users token setting for either single or multiple results
     */
    private List<ClipboardToken> getCurrentlyModifyingList() {
        return cth.getTokens(singleResSwitch.isChecked());
    }

    /**
     * Toggles showing the evolved variants of tokens in the list.
     *
     * @param v needed for xml onclick.
     */
    public void toggleEvolvedVariant(View v) {
        fillTokenList(clipboardMaxEvolutionVariant.isChecked());
    }

    /**
     * Populate the token picker gridview with all possible tokens in their respective groups.
     */
    private void fillTokenList(boolean evolvedVariant) {
        ArrayList<ClipboardToken> possibleTokens = ClipboardTokenCollection.getSamples();
        clipboardShowcase.removeAllViews();

        HashMap<String, GridLayout> groups = new HashMap<>();
        //Create empty category gridlayotu holders for each category
        for (ClipboardToken tok : possibleTokens) {
            String category = tok.getCategory(this);
            if (!groups.containsKey(category)) {
                GridLayout layout = new GridLayout(this);
                layout.setColumnCount(3);
                groups.put(category, layout);
                TextView categoryTitle = new TextView(this);
                categoryTitle.setText(category);
                clipboardShowcase.addView(new TextView(this)); //simple way of getting padding
                clipboardShowcase.addView(categoryTitle);
                clipboardShowcase.addView(layout);
            }
        }

        //populate the categories
        for (ClipboardToken token : possibleTokens) {
            //Add the token if it matches the selected evolution variant, or if it doesnt change on ev variant.
            if (evolvedVariant == token.maxEv || !token.changesOnEvolutionMax()) {
                ClipboardTokenButton btnTag = new ClipboardTokenButton(this, token, cth);
                tokenButtons.add(btnTag);
                //add button to the layout
                groups.get(token.getCategory(this)).addView(btnTag);
            }

        }
        unColorallButtons();

    }


    /**
     * Select a token to show its description.
     *
     * @param token Which token to show.
     */
    public void selectToken(ClipboardToken token) {
        selectedToken = token;
        updateClipboardDescription();
    }

    /**
     * Updates the description, the preview window, the highlighted single/multi text,  and the editor window.
     */
    private void updateFields() {
        highlightSingleOrMultiText();
        updateClipboardDescription();
        updateLengthIndicator();
        updateEditField();

    }

    /**
     * Updates the description, the preview window and the editor window.
     *
     * @param v needed for xml to find method
     */
    public void updateFields(View v) {
        updateFields();
    }

    /**
     * Makes either the "single" or "multi" text bold to highlight to the user which is selected.
     */
    private void highlightSingleOrMultiText() {
        if (singleResSwitch.isChecked()) {
            multipleText.setTextColor(Color.LTGRAY);
            singleText.setTextColor(Color.BLACK);
        } else {
            multipleText.setTextColor(Color.BLACK);
            singleText.setTextColor(Color.LTGRAY);
        }
    }

    /**
     * Sets the clipboard description for the selected token.
     */
    private void updateClipboardDescription() {

        if (selectedToken == null) {
            clipboardDescription.setText(R.string.clipboard_no_token_selected);
        } else if (selectedToken.maxEv) {
            clipboardDescription.setText(selectedToken.getLongDescription(this)
                    + getString(R.string.clipboard_description_max_evo_token));
        } else { //selectedtoken not max ev
            clipboardDescription.setText(selectedToken.getLongDescription(this));
        }

    }

    /**
     * Add a token to the user settings.
     *
     * @param v needed for onclick xml.
     */
    public void addToken(View v) {
        if (selectedToken != null) {
            cth.addToken(selectedToken, singleResSwitch.isChecked());
            updateEditField();
            updateLengthIndicator();
        } else {
            Toast.makeText(this, R.string.clipboard_no_token_selected, Toast.LENGTH_LONG).show();
        }

    }

    /**
     * Adds a custom string token to the user settings.
     *
     * @param v needed for onclick xml
     */
    public void addCustomString(View v) {
        if (customSeperator.getText() != null && !customSeperator.getText().toString().equals("")) { //no custom string
            String inputString = customSeperator.getText().toString();
            if (inputString.contains(".")) { //invalid custom string

                Toast.makeText(this, R.string.clipboard_custom_seperator_no_dot,
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                cth.addToken(new SeparatorToken(inputString), singleResSwitch.isChecked());
                updateEditField();
                updateLengthIndicator();

                //clear text field
                customSeperator.setText("");

                //close keyboard
                InputMethodManager inputManager =
                        (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }

        } else {
            Toast.makeText(this, "Please fill in your custom separator", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Finishes and exits the activity.
     *
     * @param v needed for onclick xml.
     */
    public void saveAndExit(View v) {
        finish();
    }

    /**
     * Updates the preview length indicator.
     */
    public void updateLengthIndicator() {
        clipboardMaxLength.setText("(" + cth.getMaxLength(singleResSwitch.isChecked()) + " characters)");
    }

    /**
     * Resets all Token buttons to default unselected color.
     */
    public void unColorallButtons() {
        for (ClipboardTokenButton tokenButton : tokenButtons) {
            tokenButton.resetColor();
        }
    }
}

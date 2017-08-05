package com.kamron.pogoiv.clipboard;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
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
    private EditText customSeparator;
    private LinearLayout singleMultiLayout;

    private ArrayList<ClipboardTokenButton> tokenButtons = new ArrayList<>();
    private ClipboardToken selectedToken = null;

    private View.OnClickListener deleteTokenListener = new View.OnClickListener() {
        @Override public void onClick(View view) {
            cth.removeToken((Integer) view.getTag(), singleResSwitch.isChecked());
            updateFields();
        }
    };

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
        customSeparator = (EditText) findViewById(R.id.customSeperator);
        singleMultiLayout = (LinearLayout) findViewById(R.id.singleMultiLayout);
    }

    /**
     * Removes, and re-adds markers for each token used, which when clicked removes the token from user settings.
     */
    private void updateEditField() {
        clipTokenEditor.removeAllViews();
        String separatorTokenStringName = new SeparatorToken("").getCategory();

        Integer index = 0;
        for (ClipboardToken clipboardToken : getCurrentlyModifyingList()) {
            final View rootTokenView =
                    getLayoutInflater().inflate(R.layout.layout_token_preview, clipTokenEditor, false);

            final TextView tokenTextView = (TextView) rootTokenView.findViewById(android.R.id.text1);
            if (clipboardToken.getCategory().equals(separatorTokenStringName)) {
                tokenTextView.setText(clipboardToken.getPreview());
            } else {
                tokenTextView.setText(clipboardToken.getTokenName(this) + "\n" + clipboardToken.getPreview());
            }

            final ImageButton tokenDeleteButton = (ImageButton) rootTokenView.findViewById(R.id.btnDelete);
            tokenDeleteButton.setTag(index);
            tokenDeleteButton.setOnClickListener(deleteTokenListener);

            clipTokenEditor.addView(rootTokenView);
            index++;
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
            String category = tok.getCategory();
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
                groups.get(token.getCategory()).addView(btnTag);
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
            clipboardDescription.setText("No token selected...");
        } else if (selectedToken.maxEv) {
            clipboardDescription.setText(selectedToken.getLongDescription(this) + " This token is a max evolution "
                    + "variant, meaning that it will return a result as if your monster was already fully evolved, "
                    + "which might be more interesting in a lot of cases.");
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
        if (customSeparator.getText() != null && !customSeparator.getText().toString().equals("")) { //no custom string
            String inputString = customSeparator.getText().toString();
            if (inputString.contains(".")) { //invalid custom string

                Toast.makeText(this, "Custom separator can't contain . because the developer is lazy",
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                cth.addToken(new SeparatorToken(inputString), singleResSwitch.isChecked());
                updateEditField();
                updateLengthIndicator();

                //clear text field
                customSeparator.setText("");

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

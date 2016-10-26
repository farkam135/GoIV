package com.kamron.pogoiv.clipboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamron.pogoiv.R;

import java.util.ArrayList;
import java.util.HashMap;

public class ClipboardModifierActivity extends AppCompatActivity {

    private ClipboardTokenHandler cth;
    private TextView clipboardPreview;
    private TextView clipboardMaxLength;
    private TextView clipboardDescription;
    private LinearLayout clipboardShowcase;
    private CheckBox clipboardMaxEvolutionVariant;
    private LinearLayout clipTokenEditor;

    private ArrayList<ClipboardTokenButton> tokenButtons = new ArrayList<>();
    private ClipboardToken selectedToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard_modifier);
        initiateInstanceVariables();
        updateFields();
        fillTokenList(clipboardMaxEvolutionVariant.isChecked());

    }

    /**
     * Initiates instance variables.
     */
    private void initiateInstanceVariables() {
        cth = new ClipboardTokenHandler(this);
        clipboardMaxLength = (TextView) findViewById(R.id.clipboardMaxLength);
        clipboardPreview = (TextView) findViewById(R.id.clipboardPreview);
        clipboardDescription = (TextView) findViewById(R.id.clipboardDescription);
        clipboardShowcase = (LinearLayout) findViewById(R.id.clipboardShowcase);
        clipTokenEditor = (LinearLayout) findViewById(R.id.clipTokenEditor);
        clipboardMaxEvolutionVariant = (CheckBox) findViewById(R.id.clipboardMaxEvolutionVariant);
    }

    /**
     * Removes, and re-adds markers for each token used, which when clicked removes the token from user settings.
     */
    private void updateEditField() {
        clipTokenEditor.removeAllViews();
        int index = 0;
        for (ClipboardToken clipboardToken : cth.getTokens()) {

            TextView tokenEditingBox = new TextView(this);
            tokenEditingBox.setText(clipboardToken.getPreview() + " ❌");
            tokenEditingBox.setPadding(0, 0, 0, 0);
            tokenEditingBox.setBackgroundColor(Color.parseColor("#fadede"));

            TextView divider = new TextView(this);
            divider.setText("     ");

            final int finalI = index;
            tokenEditingBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cth.removeToken(finalI);
                    updateFields();
                }
            });
            index++;

            clipTokenEditor.addView(divider);
            clipTokenEditor.addView(tokenEditingBox);
        }
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
     * Updates the description, the preview window and the editor window.
     */
    private void updateFields() {
        updateClipboardDescription();
        updateClipPreview();
        updateEditField();

    }

    /**
     * Sets the clipboard description for the selected token.
     */
    private void updateClipboardDescription() {

        if (selectedToken == null) {
            clipboardDescription.setText(getString(R.string.clipboard_modifier_no_token));
        } else if (selectedToken.maxEv) {
            clipboardDescription.setText(selectedToken.getLongDescription(this) + " " + getString(R.string
                    .token_max_evo));
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
        cth.addToken(selectedToken);
        updateEditField();
        updateClipPreview();
    }

    /**
     * Updates the preview string and length indicator..
     */
    public void updateClipPreview() {
        clipboardPreview.setText(cth.getPreviewString());
        clipboardMaxLength.setText("(" + cth.getMaxLength() + ")");
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

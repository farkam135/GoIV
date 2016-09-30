package com.kamron.pogoiv.clipboard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamron.pogoiv.R;

import java.util.ArrayList;

public class ClipboardModifierActivity extends AppCompatActivity {

    private ClipboardTokenHandler cth;
    private TextView clipboardPreview;
    private TextView clipboardMaxLength;
    private TextView clipboardDescription;
    private GridLayout clipboardGridLayout;
    private LinearLayout clipTokenEditor;

    private ArrayList<ClipboardTokenButton> tokenButtons = new ArrayList<>();
    private ClipboardToken selectedToken = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard_modifier);
        initiateInstanceVariables();
        updateFields();
        fillTokenList();

    }

    /**
     * Initiates instance variables.
     */
    private void initiateInstanceVariables() {
        cth = new ClipboardTokenHandler(this);
        clipboardMaxLength = (TextView) findViewById(R.id.clipboardMaxLength);
        clipboardPreview = (TextView) findViewById(R.id.clipboardPreview);
        clipboardDescription = (TextView) findViewById(R.id.clipboardDescription);
        clipboardGridLayout = (GridLayout) findViewById(R.id.clipboardGridLayout);
        clipTokenEditor = (LinearLayout) findViewById(R.id.clipTokenEditor);
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
     * Populate the token picker gridview with all possible tokens.
     */
    private void fillTokenList() {
        clipboardGridLayout.removeAllViews();
        ArrayList<ClipboardToken> possibleTokens = ClipboardTokenCollection.getSamples();

        for (ClipboardToken possibleToken : possibleTokens) {
            //set the properties for button
            ClipboardTokenButton btnTag = new ClipboardTokenButton(this, possibleToken, cth);

            tokenButtons.add(btnTag);
            //add button to the layout
            clipboardGridLayout.addView(btnTag);
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
            clipboardDescription.setText("No token selected...");
        } else if (selectedToken.maxEv) {
            clipboardDescription.setText(selectedToken.getLongDescription(this) + " This token is a max evolution "
                    + "variant, meaning that it will return a result as if your Pokémon was already fully evolved, "
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

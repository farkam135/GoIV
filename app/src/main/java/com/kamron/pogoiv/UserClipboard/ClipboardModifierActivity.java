package com.kamron.pogoiv.UserClipboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.TextView;

import com.kamron.pogoiv.R;

import java.util.ArrayList;

public class ClipboardModifierActivity extends AppCompatActivity {

    private ClipboardTokenHandler cth;
    private TextView clipboardPreviewString;
    private TextView clipboardLengthOfOutput;
    private CheckBox evolutionVariantCheckbox;
    private GridLayout tokenListLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard_modifier_old);
        this.cth = new ClipboardTokenHandler(this);
        this.clipboardLengthOfOutput = (TextView) findViewById(R.id.clipboardLengthOfOutput);
        this.clipboardPreviewString = (TextView) findViewById(R.id.clipboardPreviewString);
        this.tokenListLayout = (GridLayout) findViewById(R.id.clipboard_modifier_root);
        this.evolutionVariantCheckbox = (CheckBox) findViewById(R.id.evolutionVariantCheckbox);

        updateTokenButtonListEvolutionToggle(evolutionVariantCheckbox);
        updateLabels();
    }

    /**
     * updates the labels relating to the clipboard tokens.
     */
    public void updateLabels() {
        clipboardPreviewString.setText(cth.getPreviewString());

        int maxLengthInt = cth.getMaxLength();
        String maxLength;
        if (cth.getMaxLength() > 12) {
            maxLength = String.valueOf(maxLengthInt) + " Longer than a pokemon nickname can be";
        } else {
            maxLength = String.valueOf(maxLengthInt);
        }
        clipboardLengthOfOutput.setText(maxLength);
    }

    /**
     * Clears all tokens from settings.
     *
     * @param v Required for the xml onclick to find the method
     */
    public void clearTokens(View v) {
        cth.clearTokens();
        updateLabels();
    }


    /**
     * Switched between evolution and normal variants of token list
     * @param view Required for the xml onclick to find the method
     */
    public void updateTokenButtonListEvolutionToggle(View view) {
        tokenListLayout.removeAllViews();
        boolean evolutionVariant = evolutionVariantCheckbox.isChecked();
        ArrayList<ClipboardToken> possibleTokens = ClipboardTokenCollection.getSamples();
        for (ClipboardToken possibleToken : possibleTokens) {
            if (evolutionVariant == possibleToken.maxEv){
                //set the properties for button
                ClipboardTokenButton btnTag = new ClipboardTokenButton(this, possibleToken, cth);

                //add button to the layout
                tokenListLayout.addView(btnTag);
            }
        }

    }
}

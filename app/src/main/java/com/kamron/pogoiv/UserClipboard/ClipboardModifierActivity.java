package com.kamron.pogoiv.UserClipboard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import com.kamron.pogoiv.R;

import java.util.ArrayList;

public class ClipboardModifierActivity extends AppCompatActivity {

    private ClipboardTokenHandler cth;
    private TextView clipboardPreviewString;
    private TextView clipboardLengthOfOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clipboard_modifier);
        this.cth = new ClipboardTokenHandler(this);
        this.clipboardLengthOfOutput = (TextView) findViewById(R.id.clipboardLengthOfOutput);
        this.clipboardPreviewString = (TextView) findViewById(R.id.clipboardPreviewString);
        createButtons();
        updateLabels();
    }

    /**
     * updates the labels relating to the clipboard tokens.
     */
    public void updateLabels() {
        clipboardPreviewString.setText(cth.getPreviewString());
        String maxLength = String.valueOf(cth.getMaxLength());
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
     * Adds a button for each possible token type.
     */
    private void createButtons() {
        //the layout on which you are working
        GridLayout layout = (GridLayout) findViewById(R.id.clipboard_modifier_root);
        ArrayList<ClipboardToken> possibleTokens = ClipboardTokenCollection.getSamples();
        for (ClipboardToken token : possibleTokens) {
            //set the properties for button
            ClipboardTokenButton btnTag = new ClipboardTokenButton(this, token, cth);

            //add button to the layout
            layout.addView(btnTag);
        }
    }
}

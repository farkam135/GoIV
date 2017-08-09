package com.kamron.pogoiv.clipboard;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.adapters.TokensPreviewAdapter;
import com.kamron.pogoiv.clipboard.adapters.TokensShowcaseAdapter;
import com.kamron.pogoiv.clipboard.layoutmanagers.TokenGridLayoutManager;
import com.kamron.pogoiv.clipboard.tokens.SeparatorToken;

import java.util.List;

public class ClipboardModifierActivity
        extends AppCompatActivity
        implements ClipboardToken.OnTokenSelectedListener, ClipboardToken.OnTokenDeleteListener {

    private ClipboardTokenHandler cth;
    private TextView clipboardMaxLength;
    private Spinner resultModeSpinner;
    private TextView clipboardDescription;
    private CheckBox clipboardMaxEvolutionVariant;
    private EditText customSeparator;
    private LinearLayout singleMultiLayout;

    private TokensPreviewAdapter tokenPreviewAdapter;
    private TokensShowcaseAdapter tokenShowcaseAdapter;
    private ClipboardToken selectedToken = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle(R.string.clipboard_activity_title);
        setContentView(R.layout.activity_clipboard_modifier);
        initiateInstanceVariables();
        setSingleCheckboxShownDependingOnSetting(); //must be done after initiateInstanceVariables
        updateFields();

        // Init the spinner that let the user switch from multiple result to single result edit mode.
        resultModeSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,
                new String[] {"Multiple result", "Single result"}));
        resultModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateFields();
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {
                // Nothing to do
            }
        });
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
        resultModeSpinner = (Spinner) findViewById(R.id.resultModeSpinner);
        clipboardMaxLength = (TextView) findViewById(R.id.clipboardMaxLength);
        clipboardDescription = (TextView) findViewById(R.id.clipboardDescription);
        RecyclerView tokenPreviewRecyclerView = (RecyclerView) findViewById(R.id.tokenPreviewRecyclerView);
        clipboardMaxEvolutionVariant = (CheckBox) findViewById(R.id.clipboardMaxEvolutionVariant);
        customSeparator = (EditText) findViewById(R.id.customSeperator);
        singleMultiLayout = (LinearLayout) findViewById(R.id.singleMultiLayout);
        RecyclerView tokenShowcaseRecyclerView = (RecyclerView) findViewById(R.id.tokenShowcaseRecyclerView);

        // Populate the token preview RecyclerView with all configured tokens.
        tokenPreviewAdapter = new TokensPreviewAdapter(getCurrentlyModifyingList(), this);
        tokenPreviewRecyclerView.setHasFixedSize(false);
        tokenPreviewRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        tokenPreviewRecyclerView.setAdapter(tokenPreviewAdapter);

        // Populate the token showcase RecyclerView with all possible tokens. The TokenListAdapter will put them in
        // their respective category while TokenGridLayoutManager will arrange them in a grid with category headers
        // that span the entire RecyclerView width.
        tokenShowcaseAdapter = new TokensShowcaseAdapter(this, clipboardMaxEvolutionVariant.isChecked(), this);
        tokenShowcaseRecyclerView.setHasFixedSize(false);
        tokenShowcaseRecyclerView.setLayoutManager(new TokenGridLayoutManager(this, tokenShowcaseAdapter));
        tokenShowcaseRecyclerView.setAdapter(tokenShowcaseAdapter);
    }

    private boolean isSingleResultMode() {
        return resultModeSpinner.getSelectedItemPosition() == 1;
    }

    /**
     * Get a list of tokens, the default list if user is currently modifying the default list, or the single result
     * user token list if that checkbox is marked.
     *
     * @return The users token setting for either single or multiple results
     */
    private List<ClipboardToken> getCurrentlyModifyingList() {
        return cth.getTokens(isSingleResultMode());
    }

    /**
     * Toggles showing the evolved variants of tokens in the list.
     *
     * @param v needed for xml onclick.
     */
    public void toggleEvolvedVariant(View v) {
        tokenShowcaseAdapter.setEvolvedVariant(clipboardMaxEvolutionVariant.isChecked());
    }

    /**
     * Select a token to show its description.
     *
     * @param token Which token to show.
     */
    @Override public void onTokenSelected(ClipboardToken token, int adapterPosition) {
        selectedToken = token;
        updateClipboardDescription();
        updateLengthIndicator();
    }

    @Override public void onTokenDeleted(int adapterPosition) {
        cth.removeToken(adapterPosition, isSingleResultMode());
        updateFields();
    }

    /**
     * Updates the description, the preview window, the highlighted single/multi text,  and the editor window.
     */
    private void updateFields() {
        updateClipboardDescription();
        updateLengthIndicator();
        tokenPreviewAdapter.setData(getCurrentlyModifyingList());
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
            cth.addToken(selectedToken, isSingleResultMode());
            tokenPreviewAdapter.setData(getCurrentlyModifyingList());
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
                cth.addToken(new SeparatorToken(inputString), isSingleResultMode());
                tokenPreviewAdapter.setData(getCurrentlyModifyingList());
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
        clipboardMaxLength.setText("(" + cth.getMaxLength(isSingleResultMode()) + "/12 characters)");
    }

}

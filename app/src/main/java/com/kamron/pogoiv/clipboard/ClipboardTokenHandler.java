package com.kamron.pogoiv.clipboard;

import android.content.Context;
import android.util.Log;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Johan on 2016-09-24.
 * The class which handles communication between user settings of how they want the clipboard output to be, and any
 * changes.
 */

public class ClipboardTokenHandler {

    private ArrayList<ClipboardToken> tokens = new ArrayList<>();
    private Context context;

    /**
     * Gets a peak at which tokens that exist.
     * @return
     */
    public List<ClipboardToken> getTokens(){
        return Collections.unmodifiableList(tokens);
    }

    /**
     * Create a new clipboardTokenHandler that can edit and read ClipboardToken information.
     *
     * @param context Used to get application user settings.
     */
    public ClipboardTokenHandler(Context context) {
        initializeTokensFromSettings();
        this.context = context;
    }

    /**
     * Reads the ClipboardToken settings from persistent memory, and initializes a the clipboardtokenarray.
     */
    private void initializeTokensFromSettings() {
        String storedSetting = GoIVSettings.getInstance(context).getClipboardPreference();
        Log.d("NahojjjenClippy", "ClipboardTokenHandler gets following knowledge of user settings: " + storedSetting);
        String[] tokenRepresentationArray = storedSetting.split("\\.");

        Log.d("NahojjjenClippy", "Size of split Array: " + tokenRepresentationArray.length);
        ArrayList<ClipboardToken> exampleTokens = ClipboardTokenCollection.getSamples();

        Log.d("NahojjjenClippy", "Size of clipboardtokencollection Array: " + exampleTokens.size());

        String representation;
        for (int i = 0; i < tokenRepresentationArray.length; i++) { // for all saved tokens
            representation = tokenRepresentationArray[i];
            for (ClipboardToken tokenExample : exampleTokens) { //compare it to library of known tokens
                //substring is used because the . will be removed in the split
                String tokenExampleString = tokenExample.getStringRepresentation().substring(1);
                if (tokenExampleString.equals(representation)) { //when we found what kind of token was saved
                    tokens.add(tokenExample);       //add it to the list.
                    Log.d("NahojjjenClippy", "added " + tokenExample.getClass().getSimpleName() + " to setting");
                    //Having duplicate references to the same token will never be a problem, as the user
                    //will simply get duplicate output, which is what's expected.
                }
            }
        }


    }

    /**
     * Remove the i:th token in the token list. If you have A,B,C,D and remove 2, you remove C and the resulting list
     * would be A,B,D. The D will have moved up, there wont be a null marker.
     * @param i which index to remove in the list.
     */
    public void removeToken(int i){
        tokens.remove(i);
        saveTokenChanges();
    }

    /**
     * Get a preview of how a string output with all the current tokens could look.
     *
     * @return An example output that could be produced with the current token settings
     */
    public String getPreviewString() {
        if (tokens.size() == 0) {
            return "~~~~";
        }
        String returner = "";

        for (ClipboardToken token : tokens) {
            returner += token.getPreview();
        }
        return returner;
    }

    /**
     * Get the maximum possible length of the string produced by the current token settings.
     *
     * @return An integer which represents the maximum possible size of the token inputs.
     */
    public int getMaxLength() {
        int sum = 0;

        for (ClipboardToken token : tokens) {
            sum += token.getMaxLength();
        }
        return sum;
    }

    /**
     * Add a token after all other current remembered tokens.
     *
     * @param token Which token type to add.
     */
    public void addToken(ClipboardToken token) {
        tokens.add(token);
        saveTokenChanges();
    }

    /**
     * Clears all tokens from the token list.
     */
    public void clearTokens() {
        tokens.clear();
        saveTokenChanges();
    }

    /**
     * Get the entire result from the all the Clipboard tokens in user settings
     *
     * @param ivScanResult       Used by some tokens to calculate information.
     * @param pokeInfoCalculator Used by some tokens to calculate information.
     * @return A string with all the tokens returned result on each other
     */
    public String getResults(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {
        Log.d("NahojjjenClippy", "Size of token train: "+ tokens.size());
        String returner = "";
        for (ClipboardToken token : tokens) {
            returner += token.getValue(ivScanResult, pokeInfoCalculator);
        }
        return returner;
    }

    /**
     * Saves the token changes to persistent memory.
     */
    private void saveTokenChanges() {
        GoIVSettings.getInstance(context).setClipboardPreference(tokens);
        Log.d("NahojjjenClippy", "Saved token Changes");
    }
}

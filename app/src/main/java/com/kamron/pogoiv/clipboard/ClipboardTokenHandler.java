package com.kamron.pogoiv.clipboard;

import android.content.Context;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboard.tokens.SeparatorToken;
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
    private ArrayList<ClipboardToken> tokensSingle = new ArrayList<>(); //user setting for single results
    private Context context;


    /**
     * Create a new clipboardTokenHandler that can edit and read ClipboardToken information.
     *
     * @param context Used to get application user settings.
     */
    public ClipboardTokenHandler(Context context) {

        String storedSetting = GoIVSettings.getInstance(context).getClipboardPreference();
        String storedSettingSingle = GoIVSettings.getInstance(context).getClipboardSinglePreference();
        tokens = initializeTokensFromSettings(storedSetting);
        tokensSingle = initializeTokensFromSettings(storedSettingSingle);
        this.context = context;
    }


    /**
     * Analyze an ivscan and get a string which corresponds to what the users clipboard settings are
     *
     * @param ivScanResult       Which scan result to base the string on
     * @param pokeInfoCalculator An object used to calculate the logic for the clipboardtokens
     * @return A string corresponding to the user settings which is based on the ivscan.
     */
    public String getClipboardText(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator) {

        GoIVSettings settings = GoIVSettings.getInstance(context);
        String clipResult = "";

        // has the user enabled the setting for different results and is there just a single result??
        if (settings.shouldCopyToClipboardSingle() && ivScanResult.getCount() == 1) {
            clipResult = getResults(ivScanResult, pokeInfoCalculator, true);
        } else {
            clipResult = getResults(ivScanResult, pokeInfoCalculator, false);
        }
        return clipResult;
    }


    /**
     * Gets a peak at which tokens that exist. To modify the list, call ClipboardTokenHandler add or remove methods.
     *
     * @return an unmodifiable list of the tokens currently in the user settings.
     */
    public List<ClipboardToken> getTokens(boolean single) {
        return Collections.unmodifiableList(getCorrectTokenList(single));
    }


    /**
     * Parses a string representation of a clipboard setting, and translates it into a list of ClipboardTokens.
     *
     * @param storedSetting The stored string representation of users clipboard setting
     */
    private ArrayList<ClipboardToken> initializeTokensFromSettings(String storedSetting) {
        ArrayList<ClipboardToken> saveTo = new ArrayList<>();
        String[] tokenRepresentationArray = storedSetting.split("\\.");

        ArrayList<ClipboardToken> exampleTokens = ClipboardTokenCollection.getSamples();


        String representation;
        for (int i = 0; i < tokenRepresentationArray.length; i++) { // for all saved tokens
            representation = tokenRepresentationArray[i];

            //Check for a custom user added seperator
            String seperatorClassName = new SeparatorToken("").getClass().getSimpleName();
            if (representation.contains(seperatorClassName)) {
                saveTo.add(new SeparatorToken(representation.substring(seperatorClassName.length())));
                continue;
            }

            for (ClipboardToken tokenExample : exampleTokens) {
                //compare it to library of known tokens
                //substring is used because the . will be removed in the split
                String tokenExampleString = tokenExample.getStringRepresentation().substring(1);
                if (tokenExampleString.equals(representation)) { //when we found what kind of token was saved
                    saveTo.add(tokenExample);       //add it to the list.
                    //Having duplicate references to the same token will never be a problem, as the user
                    //will simply get duplicate output, which is what's expected.
                }
            }

        }
        return saveTo;
    }

    /**
     * A method which returns either the token list for single or multiple results
     *
     * @param single true to modify/work with the settings for single IV results, false for general setting.
     * @return a list of clipboardtokens for single or multiple results.
     */
    private List<ClipboardToken> getCorrectTokenList(boolean single) {
        if (single) {
            return tokensSingle;
        }
        return tokens;
    }

    /**
     * Remove the i:th token in the token list. If you have A,B,C,D and remove 2, you remove C and the resulting list
     * would be A,B,D. The D will have moved up, there wont be a null marker.
     *
     * @param i      which index to remove in the list.
     * @param single true to modify/work with the settings for single IV results, false for general setting.
     */
    public void removeToken(int i, boolean single) {
        getCorrectTokenList(single).remove(i);
        saveTokenChanges();
    }

    /**
     * Get a preview of how a string output with all the current tokens could look.
     *
     * @param single true to modify/work with the settings for single IV results, false for general setting.
     * @return An example output that could be produced with the current token settings
     */
    public String getPreviewString(boolean single) {
        if (getCorrectTokenList(single).size() == 0) {
            return context.getString(R.string.no_clipboard_preview);
        }
        String returner = "";

        for (ClipboardToken token : getCorrectTokenList(single)) {
            returner += token.getPreview();
        }
        return returner;
    }

    /**
     * Get the maximum possible length of the string produced by the current token settings.
     *
     * @param single true to modify/work with the settings for single IV results, false for general setting.
     * @return An integer which represents the maximum possible size of the token inputs.
     */
    public int getMaxLength(boolean single) {
        int sum = 0;

        for (ClipboardToken token : getCorrectTokenList(single)) {
            sum += token.getMaxLength();
        }
        return sum;
    }

    /**
     * Add a token after all other current remembered tokens.
     *
     * @param single true to modify/work with the settings for single IV results, false for general setting.
     * @param token  Which token type to add.
     */
    public void addToken(ClipboardToken token, boolean single) {
        getCorrectTokenList(single).add(token);
        saveTokenChanges();
    }

    /**
     * Clears all tokens from the token list.
     *
     * @param single true to modify/work with the settings for single IV results, false for general setting.
     */
    public void clearTokens(boolean single) {
        getCorrectTokenList(single).clear();
        saveTokenChanges();
    }

    /**
     * Get the entire result from the all the Clipboard tokens in user settings
     *
     * @param ivScanResult       Used by some tokens to calculate information.
     * @param pokeInfoCalculator Used by some tokens to calculate information.
     * @param single             true to modify/work with the settings for single IV results, false for general setting.
     * @return A string with all the tokens returned result on each other
     */
    public String getResults(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator, boolean single) {
        String returner = "";
        for (ClipboardToken token : getCorrectTokenList(single)) {
            returner += token.getValue(ivScanResult, pokeInfoCalculator);
        }
        return returner;
    }

    /**
     * Saves the token changes to persistent memory. Saves both single and multi tokens.
     */
    private void saveTokenChanges() {
        GoIVSettings.getInstance(context).setClipboardPreference(tokens);
        GoIVSettings.getInstance(context).setClipboardSinglePreference(tokensSingle);
    }


}

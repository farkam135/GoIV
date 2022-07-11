package com.kamron.pogoiv.clipboardlogic;

import android.content.Context;
import androidx.annotation.NonNull;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.clipboardlogic.tokens.HasBeenAppraisedToken;
import com.kamron.pogoiv.clipboardlogic.tokens.PokemonNameToken;
import com.kamron.pogoiv.clipboardlogic.tokens.SeparatorToken;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.GENERAL_RESULT;
import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.PERFECT_IV_RESULT;
import static com.kamron.pogoiv.clipboardlogic.ClipboardResultMode.SINGLE_RESULT;

/**
 * Created by Johan on 2016-09-24.
 * The class which handles communication between user settings of how they want the clipboard output to be, and any
 * changes.
 */

public class ClipboardTokenHandler {

    private ListMultimap<ClipboardResultMode, ClipboardToken> tokens = ArrayListMultimap.create();
    private Context context;


    /**
     * Create a new clipboardTokenHandler that can edit and read ClipboardToken information.
     *
     * @param context Used to get application user settings.
     */
    public ClipboardTokenHandler(Context context) {

        String storedSetting = GoIVSettings.getInstance(context).getClipboardPreference();
        tokens.putAll(GENERAL_RESULT, initializeTokensFromSettings(storedSetting));
        String singleStoredSetting = GoIVSettings.getInstance(context).getClipboardSinglePreference();
        tokens.putAll(SINGLE_RESULT, initializeTokensFromSettings(singleStoredSetting));
        String maxivStoredSetting = GoIVSettings.getInstance(context).getClipboardPerfectIvPreference();
        tokens.putAll(PERFECT_IV_RESULT, initializeTokensFromSettings(maxivStoredSetting));
        this.context = context;
    }


    /**
     * Analyze an ivscan and get a string which corresponds to what the users clipboard settings are.
     *
     * @param scanResult       Which scan result to base the string on
     * @param pokeInfoCalculator An object used to calculate the logic for the clipboard tokens
     * @return A string corresponding to the user settings which is based on the ivscan.
     */
    public String getClipboardText(@NonNull ScanResult scanResult,
                                   @NonNull PokeInfoCalculator pokeInfoCalculator) {
        GoIVSettings settings = GoIVSettings.getInstance(context);
        final ClipboardResultMode resultMode;

        final boolean isSingle = scanResult.getIVCombinationsCount() == 1;
        final boolean isPerfect = isSingle
                && scanResult.getIVCombinationAt(0).percentPerfect == 100;

        // has the user enabled one or more settings for alternative formats and is one of them applicable??
        if (settings.shouldCopyToClipboardPerfectIV() && isPerfect) {
            resultMode = PERFECT_IV_RESULT;
        } else if (settings.shouldCopyToClipboardSingle() && isSingle) {
            resultMode = SINGLE_RESULT;
        } else {
            resultMode = GENERAL_RESULT;
        }

        StringBuilder returner = new StringBuilder();
        for (ClipboardToken token : getCorrectTokenList(resultMode)) {
            returner.append(token.getValue(scanResult, pokeInfoCalculator));
        }

        return returner.toString();
    }


    /**
     * Gets a peak at which tokens that exist. To modify the list, call ClipboardTokenHandler add or remove methods.
     *
     * @param resultMode The result mode to get the tokens for
     * @return An unmodifiable list of the tokens currently in the user settings.
     */
    public List<ClipboardToken> getTokens(ClipboardResultMode resultMode) {
        return Collections.unmodifiableList(getCorrectTokenList(resultMode));
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

        for (String representation : tokenRepresentationArray) { // for all saved tokens

            //Check for a custom user added inputs
            String seperatorClassName = SeparatorToken.class.getSimpleName();
            if (representation.contains(seperatorClassName)) {
                saveTo.add(new SeparatorToken(representation.substring(seperatorClassName.length())));
                continue;
            }
            String nameLengthClassName = PokemonNameToken.class.getSimpleName();
            if (representation.contains(nameLengthClassName)) {
                String nameParams = representation.substring(nameLengthClassName.length());
                String numbrOnly = nameParams.replaceAll("[^\\d]", "" );
                String textOnly = nameParams.replaceAll("\\d", "");
                boolean maxVariant = textOnly.contains("true");
                int nameLimit = Integer.parseInt(numbrOnly);
                saveTo.add(new PokemonNameToken(maxVariant, nameLimit));
                continue;
            }
            String appraisedClassName = HasBeenAppraisedToken.class.getSimpleName();
            if (representation.contains(appraisedClassName)) {
                int classLength = appraisedClassName.length();
                saveTo.add(new HasBeenAppraisedToken(true,
                        representation.substring(classLength,classLength+1),
                        representation.substring(classLength+1, classLength+2)));
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
     * A method which returns either the token list for single or multiple results.
     *
     * @param resultMode Mode to modify the settings of
     * @return A collection of clipboard tokens for single or multiple results
     */
    private List<ClipboardToken> getCorrectTokenList(ClipboardResultMode resultMode) {
        return tokens.get(resultMode);
    }

    /**
     * Remove the i:th token in the token list. If you have A,B,C,D and remove 2, you remove C and the resulting list
     * would be A,B,D. The D will have moved up, there wont be a null marker.
     *
     * @param i          Which index to remove in the list
     * @param resultMode Result mode to modify the settings for
     */
    public void removeToken(int i, ClipboardResultMode resultMode) {
        getCorrectTokenList(resultMode).remove(i);
        saveTokenChanges(resultMode);
    }

    /**
     * Get a preview of how a string output with all the current tokens could look.
     *
     * @param resultMode Result mode to modify the settings for
     * @return An example output that could be produced with the current token settings
     */
    public String getPreviewString(ClipboardResultMode resultMode) {
        if (getCorrectTokenList(resultMode).isEmpty()) {
            return context.getString(R.string.no_clipboard_preview);
        }
        StringBuilder returner = new StringBuilder();

        for (ClipboardToken token : getCorrectTokenList(resultMode)) {
            returner.append(token.getPreview());
        }
        return returner.toString();
    }

    /**
     * Clear the current remembered tokens and persist the new list.
     *
     * @param tokenList  Which token types to persist
     * @param resultMode Result mode to modify the settings for
     */
    public void setTokenList(List<ClipboardToken> tokenList, ClipboardResultMode resultMode) {
        getCorrectTokenList(resultMode).clear();
        getCorrectTokenList(resultMode).addAll(tokenList);
        saveTokenChanges(resultMode);
    }

    /**
     * Add a token after all other current remembered tokens.
     *
     * @param resultMode Result mode to modify the settings for
     * @param token      Which token type to add
     */
    public void addToken(ClipboardToken token, ClipboardResultMode resultMode) {
        getCorrectTokenList(resultMode).add(token);
        saveTokenChanges(resultMode);
    }

    /**
     * Clears all tokens from the token list.
     *
     * @param resultMode Result mode to modify the settings for
     */
    public void clearTokens(ClipboardResultMode resultMode) {
        getCorrectTokenList(resultMode).clear();
        saveTokenChanges(resultMode);
    }

    /**
     * Saves the token changes to persistent memory. Saves both single and multi tokens.
     *
     * @param resultMode Result mode to persist the settings for
     */
    private void saveTokenChanges(ClipboardResultMode resultMode) {
        String representation = tokenListToRepresentation(tokens.get(resultMode));

        switch (resultMode) {
            default:
            case GENERAL_RESULT:
                GoIVSettings.getInstance(context).setClipboardPreference(representation);
                break;
            case SINGLE_RESULT:
                GoIVSettings.getInstance(context).setClipboardSinglePreference(representation);
                break;
            case PERFECT_IV_RESULT:
                GoIVSettings.getInstance(context).setClipboardPerfectIvPreference(representation);
                break;
        }
    }

    /**
     * Utility method to check if a token list equals the saved one.
     *
     * @param tokenList  List to check.
     * @param resultMode Result mode to use to compare the settings for
     * @return true if the configuration equals.
     */
    public boolean savedConfigurationEquals(List<ClipboardToken> tokenList, ClipboardResultMode resultMode) {
        String storedSettings;
        switch (resultMode) {
            default:
            case SINGLE_RESULT:
                storedSettings = GoIVSettings.getInstance(context).getClipboardSinglePreference();
                break;
            case GENERAL_RESULT:
                storedSettings = GoIVSettings.getInstance(context).getClipboardPreference();
                break;
            case PERFECT_IV_RESULT:
                storedSettings = GoIVSettings.getInstance(context).getClipboardPerfectIvPreference();
                break;
        }

        return storedSettings.equals(tokenListToRepresentation(tokenList));
    }

    public static String tokenListToRepresentation(List<ClipboardToken> tokenList) {
        StringBuilder representation = new StringBuilder();
        for (ClipboardToken token : tokenList) {
            representation.append(token.getStringRepresentation());
        }
        return representation.toString();
    }

}

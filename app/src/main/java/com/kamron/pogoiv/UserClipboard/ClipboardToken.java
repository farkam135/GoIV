package com.kamron.pogoiv.UserClipboard;

import android.content.Context;

import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;

/**
 * Created by Johan on 2016-09-24.
 * An interface which lists the methods required for tokens that users can use to build custom clipboard snippets on
 * scan results.
 * <p>
 * If you implement a concrete version of a token, remember to add it to the ClipboardTokenCollection class. This is
 * not an ideal implementation, so if anyone's got time for a cleaner implementation, feel free to discuss it on github.
 */

public interface ClipboardToken {
    /**
     * What's the longest possible output that this token can produce?
     * For example, if the token outputs a number between 0 and 150, the maximum length would be 3.
     *
     * @return An integer representing the biggest possible size of the output.
     */
    public int getMaxLength();

    /**
     * Get the result as interpreted by this clipboardtoken
     *
     * @param ivScanResult       Information that can be used to calculate the output for the token.
     * @param pokeInfoCalculator Information that can be used to calculate the output for the token.
     * @return A string representing the value as interpreted by the clipboardToken
     */
    public String getValue(IVScanResult ivScanResult, PokeInfoCalculator pokeInfoCalculator);

    /**
     * Get a String which represents an example of normal output from this token. For example if the token returns a
     * random number between 0 and 150, "133" would be a good normal output.
     * <p>
     * The example output should give "longer" previews if different lengths are possible.
     *
     * @return An example output that the token could produce, used to preview how the token info would look.
     */
    public String getPreview();

    /**
     * Get the string representation of how this token is saved in the persistent memory.
     * The string must be unique for each token, and start with a "."
     * No period is allowed to be in the string other than the initial sign.
     *
     * @return A string representing this token as saved in persistent memory setting.
     */
    public String getStringRepresentation();

    /**
     * Get what the short name of the token is, for example "Pokemon name"
     *
     * @param context used to reach the string resources
     * @return The short name of the token which should be recognizable by an end user
     */
    public String getTokenName(Context context);

}

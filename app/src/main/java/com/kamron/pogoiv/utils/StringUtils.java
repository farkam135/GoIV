package com.kamron.pogoiv.utils;

import java.text.Normalizer;
import java.util.Locale;

public class StringUtils {

    /**
     * Returns the normalized string for simplifying characters to detect pokemons.
     *
     * Actually scanned character result quality is often not enough in some reasons like following:
     *  - device screen resolution
     *  - font properties(size, forms and ligature)
     *  - letter complexity in some locales
     *
     * This method provides normalized string to simplify characters.
     * For examples, special characters such as â, é, ば etc are replaced with their normalized forms a, e, は etc.
     * So they can be compared more simply.
     *
     */
    public static String normalize(String s) {
        s = s.toLowerCase();
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{M}]", "");

        /* append more normalizers below for each locales, if needed */
        //String lang = Locale.getDefault().getLanguage();
        //
        //if (Locale.getDefault().getLanguage().contains("ja")) {
        //  ...
        //}

        return s;
    }
}

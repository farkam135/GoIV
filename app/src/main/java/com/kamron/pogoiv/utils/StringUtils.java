package com.kamron.pogoiv.utils;

import java.text.Normalizer;
import java.util.Locale;

public class StringUtils {
/**
 * Returns the normalized string for simplifying words to detect pokemons.
 */
    public static String normalize(String s) {
        s = s.toLowerCase();
        s = Normalizer.normalize(s, Normalizer.Form.NFD);
        s = s.replaceAll("[\\p{M}]", "");

        /* append more normalizers below for each locales, if needed */
        //String lang = Locale.getDefault().getLanguage();
        //
        //if (Locale.getDefault().getLanguage().contains("ja")) {
        //}

        return s;
    }
}

package com.kamron.pogoiv.pokeflycomponents;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;
import com.kamron.pogoiv.logic.PokemonNameCorrector;
import com.kamron.pogoiv.logic.ScanResult;

/**
 * Created by johan on 2017-07-06.
 * <p>
 * An object which can show a quick indication of a pokemons IV or information based on the custom clipboard depening
 * on user setting.
 */

public class IVPreviewPrinter {

    private static final int DELAY_SCAN_MILLIS = 50;

    private Pokefly pokefly;
    private GoIVSettings settings;
    private PokeInfoCalculator pokeInfoCalculator;

    public IVPreviewPrinter(Pokefly pokefly) {
        this.pokefly = pokefly;

        settings = GoIVSettings.getInstance(pokefly);
        pokeInfoCalculator = PokeInfoCalculator.getInstance();
    }

    /**
     * Shows a toast message that displays either a short message about the pokemon currently on the screen, or the
     * users clipboard setting about the pokemon currently on the screen.
     * @param ivButton The ivButton to change the look of.
     */
    public void printIVPreview(IVPopupButton ivButton) {
        if (settings.shouldShowQuickIVPreview()) {
            Handler handler = new Handler();
            //A delayed action, because the screengrabber needs to wait and ensure there's a frame to grab - fails if
            //the delay is not long enough.
            handler.postDelayed(new QuickIVScanAttempt(ivButton), DELAY_SCAN_MILLIS);

        }
    }

    /**
     * A quick scan which will try to analyze the screen and show a quick iv preview message.
     */
    private class QuickIVScanAttempt implements Runnable {

        private IVPopupButton ivButton;

        public QuickIVScanAttempt(IVPopupButton ivButton) {
            this.ivButton = ivButton;
        }

        @Override
        public void run() {
            boolean succeeded = runQuickScan();
            if (!succeeded) {
                Toast.makeText(pokefly, "Touch screen to retry quick IV scan", Toast.LENGTH_SHORT).show();
            }
        }

        /**
         * Attempts to generate and print a quickiv message, if it fails, does nothing and returns false.
         *
         * @return true if successfully printed message, false otherwise.
         */
        private boolean runQuickScan() {
            Bitmap bmp = ScreenGrabber.getInstance().grabScreen();

            if (bmp == null) {
                return false;
            }

            ScanResult res = pokefly.getOcr().scanPokemon(bmp, pokefly.getTrainerLevel());
            if (!res.getPokemonHP().isPresent() || !res.getPokemonCP().isPresent()) {
                return false;
            }

            IVScanResult ivrs = getIVScanResults(res);
            if (ivrs.getCount() <= 0) { //unsuccessful scan
                return false;
            }

            //String toastMessage = getQuickIVMessage(ivrs);
            printClipboardIfSettingIsOn(ivrs);
            ivButton.showQuickIVPreviewLook(ivrs);

            //Toast.makeText(pokefly, toastMessage, Toast.LENGTH_SHORT).show();
            return true;

        }
    }

    /**
     * Get ivscanresults from a screen scan.
     *
     * @param res The scan result which has not been processed to an ivscanresult containing pure screen ocr data.
     * @return the processed ivscanresult
     */
    private IVScanResult getIVScanResults(ScanResult res) {
        PokemonNameCorrector corrector = new PokemonNameCorrector(pokeInfoCalculator);
        Pokemon poke = corrector.getPossiblePokemon(res.getPokemonName(), res.getCandyName(),
                res.getUpgradeCandyCost(), res.getPokemonType()).pokemon;
        IVScanResult ivrs = pokeInfoCalculator.getIVPossibilities(poke, res.getEstimatedPokemonLevel(),
                res.getPokemonHP().get(), res.getPokemonCP().get());
        return ivrs;
    }

    /**
     * Makes a system toast for the clipboard is the setting is on.
     *
     * @param ivrs The iv result to base the message on.
     * @return A string build up by the iv results.
     */
    private void printClipboardIfSettingIsOn(IVScanResult ivrs) {
        String returner;
        if (settings.shouldReplaceQuickIvPreviewWithClipboard()) {
            returner = pokefly.getClipboardTokenHandler().getClipboardText(ivrs, pokeInfoCalculator);
            Toast.makeText(pokefly, returner, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get a string which is either the default QuickIV message, or the clipboard setting depending on what the user
     * preference is.
     *
     * @param ivrs The iv result to base the message on
     * @return A string build up by the iv results
     */
    private String getQuickIVMessage(IVScanResult ivrs) {
        String returner;
        if (settings.shouldReplaceQuickIvPreviewWithClipboard()) {
            returner = pokefly.getClipboardTokenHandler().getClipboardText(ivrs, pokeInfoCalculator);
        } else {
            returner = "IV: " + ivrs.getLowestIVCombination().percentPerfect + " - "
                    + ivrs.getHighestIVCombination().percentPerfect + "%";
        }
        return returner;
    }
}

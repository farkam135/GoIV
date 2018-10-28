package com.kamron.pogoiv.pokeflycomponents;

import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.OcrHelper;
import com.kamron.pogoiv.scanlogic.PokeInfoCalculator;
import com.kamron.pogoiv.scanlogic.PokemonNameCorrector;
import com.kamron.pogoiv.scanlogic.ScanData;
import com.kamron.pogoiv.scanlogic.ScanResult;

import java.lang.ref.WeakReference;

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
    private Handler handler;
    private QuickIVScanAttempt quickIVScanAttempt;

    public IVPreviewPrinter(Pokefly pokefly) {
        this.pokefly = pokefly;

        settings = GoIVSettings.getInstance(pokefly);
        pokeInfoCalculator = PokeInfoCalculator.getInstance();
        handler = new Handler();
        quickIVScanAttempt = new QuickIVScanAttempt();
    }

    /**
     * Shows a toast message that displays either a short message about the pokemon currently on the screen, or the
     * users clipboard setting about the pokemon currently on the screen.
     *
     * @param ivButton The ivButton to change the look of.
     */
    public void printIVPreview(IVPopupButton ivButton) {
        if (settings.shouldShowQuickIVPreview()) {
            // A delayed action, because the screen grabber needs to wait and ensure there's a frame to grab - fails if
            // the delay is not long enough.
            handler.removeCallbacks(quickIVScanAttempt);
            quickIVScanAttempt.updateReferences(pokefly, this, ivButton);
            handler.postDelayed(quickIVScanAttempt, DELAY_SCAN_MILLIS);
        }
    }

    /**
     * A quick scan which will try to analyze the screen and show a quick iv preview message.
     */
    private static class QuickIVScanAttempt implements Runnable {

        private WeakReference<Pokefly> pokeflyRef;
        private WeakReference<IVPreviewPrinter> ivPreviewPrinterRef;
        private WeakReference<IVPopupButton> ivButtonRef;
        private PokemonNameCorrector pokemonNameCorrector;

        void updateReferences(Pokefly pokefly, IVPreviewPrinter ivPreviewPrinter, IVPopupButton ivButton) {
            pokeflyRef = new WeakReference<>(pokefly);
            ivPreviewPrinterRef = new WeakReference<>(ivPreviewPrinter);
            ivButtonRef = new WeakReference<>(ivButton);
            pokemonNameCorrector = PokemonNameCorrector.getInstance(pokefly);
        }

        @Override
        public void run() {
            boolean succeeded = runQuickScan();
            if (!succeeded) {
                IVPopupButton ivButton = ivButtonRef.get();
                if (ivButton != null) {
                    ivButton.showError();
                }
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

            Pokefly pokefly = pokeflyRef.get();
            if (pokefly == null) {
                return false; // This quick scan fired after Pokefly stopped
            }

            OcrHelper ocr = pokefly.getOcr();
            if (ocr == null) {
                return false; // This quick scan fired after Pokefly stopped
            }

            ScanData data = ocr.scanPokemon(GoIVSettings.getInstance(pokefly), bmp, pokefly.getTrainerLevel(), false);
            if (!data.getPokemonHP().isPresent() || !data.getPokemonCP().isPresent()) {
                return false;
            }

            IVPreviewPrinter ivPreviewPrinter = ivPreviewPrinterRef.get();
            if (ivPreviewPrinter == null) {
                return false; // The class that scheduled this runnable has been garbage collected
            }

            ScanResult scanResults = new ScanResult(pokemonNameCorrector, data);
            PokeInfoCalculator.getInstance().getIVPossibilities(scanResults);
            if (scanResults.getIVCombinationsCount() <= 0) { //unsuccessful scan
                return false;
            }

            ivPreviewPrinter.printClipboardIfSettingIsOn(scanResults);

            IVPopupButton ivButton = ivButtonRef.get();
            if (ivButton != null) {
                ivButton.showQuickIVPreviewLook(scanResults);
            }

            return true;
        }
    }

    /**
     * Makes a system toast for the clipboard is the setting is on.
     *
     * @param scanResult The iv result to base the message on.
     * @return A string build up by the iv results.
     */
    private void printClipboardIfSettingIsOn(ScanResult scanResult) {
        String returner;
        if (settings.shouldReplaceQuickIvPreviewWithClipboard()) {
            returner = pokefly.getClipboardTokenHandler().getClipboardText(scanResult, pokeInfoCalculator);
            Toast.makeText(pokefly, returner, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Get a string which is either the default QuickIV message, or the clipboard setting depending on what the user
     * preference is.
     *
     * @param scanResult The iv result to base the message on
     * @return A string build up by the iv results
     */
    private String getQuickIVMessage(ScanResult scanResult) {
        String returner;
        if (settings.shouldReplaceQuickIvPreviewWithClipboard()) {
            returner = pokefly.getClipboardTokenHandler().getClipboardText(scanResult, pokeInfoCalculator);
        } else {
            returner = "IV: " + scanResult.getLowestIVCombination().percentPerfect + " - "
                    + scanResult.getHighestIVCombination().percentPerfect + "%";
        }
        return returner;
    }
}

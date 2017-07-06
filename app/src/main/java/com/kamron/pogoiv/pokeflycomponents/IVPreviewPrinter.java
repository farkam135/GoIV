package com.kamron.pogoiv.pokeflycomponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.ScreenGrabber;
import com.kamron.pogoiv.logic.IVScanResult;
import com.kamron.pogoiv.logic.PokeInfoCalculator;
import com.kamron.pogoiv.logic.Pokemon;
import com.kamron.pogoiv.logic.PokemonNameCorrector;
import com.kamron.pogoiv.logic.ScanResult;

/**
 * Created by johan on 2017-07-06.
 */

public class IVPreviewPrinter {

    private final int DELAY_SCAN_MILLIS = 50;

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
     */
    public void printIVPreview() {
        if (settings.shouldShowQuickIVPreview()) {
            Handler handler = new Handler();
            //A delayed action, because the screengrabber needs to wait and ensure there's a frame to grab - fails if
            //the delay is not long enough.
            handler.postDelayed(new QuickIVScanAttempt(), DELAY_SCAN_MILLIS);

        }
    }

    /**
     * A quick scan which will try to analyze the screen and show a quick iv preview message
     */
    private class QuickIVScanAttempt implements Runnable {


        @Override
        public void run() {
            Bitmap bmp = ScreenGrabber.getInstance().grabScreen();
            //
            if (bmp == null) {
                Toast.makeText(pokefly, R.string.scanFailed, Toast.LENGTH_SHORT).show();
                return;
            }

            ScanResult res = pokefly.getOcr().scanPokemon(bmp, pokefly.getTrainerLevel());

            //if scan is successful, this message will be overwritten and not shown.
            String toastMessage = "...";
            if (res.isFailed()) {
                Toast.makeText(pokefly, pokefly.getString(R.string.scan_pokemon_failed), Toast.LENGTH_SHORT)
                        .show();
            }

            if (res.getPokemonHP().isPresent() && res.getPokemonCP().isPresent()) {
                PokemonNameCorrector corrector = new PokemonNameCorrector(pokeInfoCalculator);
                Pokemon poke = corrector.getPossiblePokemon(res.getPokemonName(), res.getCandyName(),
                        res.getUpgradeCandyCost(),
                        res.getPokemonType()).pokemon;
                IVScanResult ivrs = pokeInfoCalculator.getIVPossibilities(poke, res.getEstimatedPokemonLevel(),
                        res
                                .getPokemonHP().get(), res
                                .getPokemonCP().get());

                if (ivrs.getCount() > 0) { //successful scan
                    if (settings.shouldReplaceQuickIvPreviewWithClipboard()) {
                        toastMessage = pokefly.getClipboardStringForIvScan(ivrs);
                    } else {
                        toastMessage = "IV: " + ivrs.getLowestIVCombination().percentPerfect + " - "
                                + ivrs.getHighestIVCombination().percentPerfect + "%";
                    }
                } else {

                    toastMessage = "Failed to perform quickscan";

                }

            }

            Toast.makeText(pokefly, toastMessage, Toast.LENGTH_SHORT).show();

        }

    }
}

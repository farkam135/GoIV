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

    private Pokefly pokefly;

    public IVPreviewPrinter(Pokefly pokefly) {
        this.pokefly = pokefly;
    }

    /**
     * Shows a toast message that displays either a short message about the pokemon currently on the screen, or the
     * users clipboard setting about the pokemon currently on the screen.
     */
    public void printIVPreview() {

        final GoIVSettings settings = GoIVSettings.getInstance(pokefly);
        final PokeInfoCalculator pokeInfoCalculator = PokeInfoCalculator.getInstance();

        if (settings.shouldShowQuickIVPreview()) {
            Handler handler = new Handler();
            //A delayed action, because the screengrabber needs to wait and ensure there's a frame to grab - fails if
            //the delay is not long enough.
            handler.postDelayed(new Runnable() {
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
                    String toastMessage = "Failed to perform quickscan";
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

                        if (ivrs.getCount() > 0) {
                            if (settings.shouldReplaceQuickIvPreviewWithClipboard()) {
                                toastMessage = pokefly.getClipboardStringForIvScan(ivrs);
                            } else {
                                toastMessage = "IV: " + ivrs.getLowestIVCombination().percentPerfect + " - "
                                        + ivrs.getHighestIVCombination().percentPerfect + "%";
                            }
                        }

                    }

                    Toast.makeText(pokefly, toastMessage, Toast.LENGTH_SHORT).show();

                }
            }, 50);

        }


    }
}

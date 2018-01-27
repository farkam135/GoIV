package com.kamron.pogoiv.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.PokemonShareHandler;
import com.kamron.pogoiv.scanlogic.ScanContainer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovesFragment extends Fragment {

    @BindView(R.id.shareWithStorimod)
    ImageView shareWithStorimod;

    Pokefly pokefly;

    public MovesFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_moves, container, false);
        ButterKnife.bind(this, thisView);
        return thisView;
    }

    @OnClick({R.id.shareWithOtherApp})
    /**
     * Creates an intent to share the result of the pokemon scan, and closes the overlay.
     */
    public void shareScannedPokemonInformation() {
        PokemonShareHandler communicator = new PokemonShareHandler();
        communicator.spreadResultIntent(pokefly, ScanContainer.scanContainer.currScan, pokefly.pokemonUniqueID);
        pokefly.cancelInfoDialog();
    }
}

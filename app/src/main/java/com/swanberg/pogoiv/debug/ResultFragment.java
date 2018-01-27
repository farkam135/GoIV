package com.swanberg.pogoiv.debug;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;


/**
 * A fragment which holds all the panels in the result, such as
 * IV, Powerup estimate and Moves.
 */
public class ResultFragment extends Fragment {


    private FragmentManager fragmentManager;
    private Button ivButton;
    private Button movesButton;
    private Button estimateButton;


    //The fragments initialized in createFragments
    private Fragment powerupFragmentEstimate;
    private Fragment ivFragment;
    private Fragment movesFragment;


    private Pokefly pokefly;


    /**
     * Creates the resultfragment, which holds the other result-fragments and lets the user navigate between them.
     */
    public ResultFragment() {
        fragmentManager = getChildFragmentManager();
        createFragments();
        addInitialFragment();
        setupButtons();
    }

    public void initConnections(Pokefly pokefly){
        this.pokefly = pokefly;
    }

    /**
     * Puts the default fragment in the fragmentContainer.
     */
    private void addInitialFragment() {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragmentContainer, ivFragment).commit();
    }

    /**
     * Initializes the fragments that the resultfragment can hold.
     */
    private void createFragments() {
        movesFragment = new MovesFragment();
        ivFragment = new IVResultFragment();
        powerupFragmentEstimate = new PowerupEstimateFragment();
    }

    /**
     * Makes the tab-buttons properly navigate between the fragments & highlight the buttons.
     */
    private void setupButtons() {

        estimateButton = (Button) getView().findViewById(R.id.powerupFragmentViewButton);
        ivButton = (Button) getView().findViewById(R.id.iVFragmentViewButton);
        movesButton = (Button) getView().findViewById(R.id.movesFragmentViewButton);

        ivButton.setOnClickListener(new NavigateToListener(ivButton, ivFragment));
        ivButton.setOnClickListener(new NavigateToListener(movesButton, movesFragment));
        ivButton.setOnClickListener(new NavigateToListener(estimateButton, powerupFragmentEstimate));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result, container, false);
    }

    private class NavigateToListener implements View.OnClickListener {
        Button button;
        Fragment fragment;

        public NavigateToListener(Button button, Fragment fragment) {
            this.button = button;
            this.fragment = fragment;
        }

        @Override public void onClick(View view) {
            ivButton.setBackgroundColor(Color.parseColor("#fafafa"));
            movesButton.setBackgroundColor(Color.parseColor("#fafafa"));
            estimateButton.setBackgroundColor(Color.parseColor("#fafafa"));

            button.setBackgroundColor(Color.parseColor("#4444FF"));

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment).commit();

        }
    }
}

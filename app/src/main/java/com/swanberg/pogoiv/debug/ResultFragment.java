package com.swanberg.pogoiv.debug;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.kamron.pogoiv.R;


/**
 * A fragment which holds all the panels in the result, such as
 * IV, Powerup estimate and Moves.
 */
public class ResultFragment extends Fragment {


    Button ivButton;
    Button movesButton;
    Button estimateButton;
    Fragment powerupFragmentEstimate;
    Fragment ivFragment;
    Fragment movesFragment;

    FragmentManager fragmentManager;

    public ResultFragment() {
        // Required empty public constructor
        setupButtons();
    }

    /**
     * Makes the tab-buttons properly navigate between the fragments & highlight the buttons.
     */
    private void setupButtons() {

        estimateButton = (Button) getView().findViewById(R.id.powerupFragmentViewButton);
        ivButton = (Button) getView().findViewById(R.id.iVFragmentViewButton);
        movesButton = (Button) getView().findViewById(R.id.movesFragmentViewButton);

        fragmentManager = getChildFragmentManager();
        movesFragment = fragmentManager.findFragmentById(R.id.movesFragment);
        ivFragment = fragmentManager.findFragmentById(R.id.iVFragment);
        powerupFragmentEstimate = fragmentManager.findFragmentById(R.id.powerupFragmentEstimate);

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

            fragmentManager.beginTransaction(). show(fragment).commit();

        }
    }
}

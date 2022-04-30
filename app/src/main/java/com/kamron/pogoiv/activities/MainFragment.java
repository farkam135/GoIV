package com.kamron.pogoiv.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.NpTrainerLevelPickerListener;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.scanlogic.Data;
import com.kamron.pogoiv.widgets.PlayerTeamAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class MainFragment extends Fragment {

    private static final String ACTION_UPDATE_LAUNCH_BUTTON = "com.kamron.pogoiv.ACTION_UPDATE_LAUNCH_BUTTON";
    private static final String EXTRA_BUTTON_TEXT_RES_ID = "btn_txt_res_id";
    private static final String EXTRA_BUTTON_ENABLED = "btn_enabled";


    @BindView(R.id.startButton)
    Button startButton;

    @BindView(R.id.trainerLevelPicker)
    NumberPicker trainerLevelPicker;

    @BindView(R.id.teamPickerSpinner)
    Spinner teamPickerSpinner;

    @BindView(R.id.versionNumber)
    TextView versionNumber;

    @BindView(R.id.githubButton)
    ImageButton githubButton;

    @BindView(R.id.redditButton)
    ImageButton redditButton;

    @BindView(R.id.helpButton)
    Button helpButton;


    private final BroadcastReceiver launchButtonChange = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(EXTRA_BUTTON_TEXT_RES_ID)) {
                startButton.setText(intent.getIntExtra(EXTRA_BUTTON_TEXT_RES_ID, 0));
            }
            if (intent.hasExtra(EXTRA_BUTTON_ENABLED)) {
                startButton.setEnabled(intent.getBooleanExtra(EXTRA_BUTTON_ENABLED, true));
            }
        }
    };


    public static void updateLaunchButtonText(@NonNull Context context,
                                              @StringRes int stringResId,
                                              @Nullable Boolean enabled) {
        Intent i = new Intent(ACTION_UPDATE_LAUNCH_BUTTON);
        i.putExtra(EXTRA_BUTTON_TEXT_RES_ID, stringResId);
        if (enabled != null) {
            i.putExtra(EXTRA_BUTTON_ENABLED, enabled);
        }
        LocalBroadcastManager.getInstance(context).sendBroadcastSync(i);
    }


    public MainFragment() {
        super();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initiateGui();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            PreferenceManager.setDefaultValues(getContext(), R.xml.settings, false);
        }
    }

    @Override public void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(launchButtonChange,
                new IntentFilter(ACTION_UPDATE_LAUNCH_BUTTON));

        if (Pokefly.isRunning()) {
            updateLaunchButtonText(getContext(), R.string.main_stop, true);
        } else {
            updateLaunchButtonText(getContext(), R.string.main_start, true);
        }
    }

    @Override public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(launchButtonChange);
        super.onPause();
    }

    /**
     * Initiates all the gui components.
     */
    private void initiateGui() {
        versionNumber.setText(String.format("v%s", getVersionName()));
        initiateLevelPicker();
        initiateTeamPickerSpinner();
        initiateHelpButton();
        initiateCommunityButtons();
        initiateStartButton();
    }

    /**
     * Initiates the team picker spinner.
     */
    private void initiateTeamPickerSpinner() {
        PlayerTeamAdapter adapter = new PlayerTeamAdapter(getContext());
        teamPickerSpinner.setAdapter(adapter);

        teamPickerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                GoIVSettings.getInstance(getContext()).setPlayerTeam(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        teamPickerSpinner.setSelection(GoIVSettings.getInstance(getContext()).playerTeam());
    }

    /**
     * Initiates the links to reddit and github.
     */
    private void initiateCommunityButtons() {
        redditButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://www.reddit.com/r/GoIV/");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });

        githubButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Uri uriUrl = Uri.parse("https://github.com/GoIV-Devs/GoIV");
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
            }
        });
    }

    private void initiateHelpButton() {
        final Context context = getContext();
        if (context == null) {
            return;
        }
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle(R.string.instructions_title)
                        .setMessage(R.string.instructions_message)
                        .setPositiveButton(android.R.string.ok, null)
                        .show();
            }
        });
    }

    /**
     * Configures the logic for the start button.
     */
    private void initiateStartButton() {
        ViewCompat.setBackgroundTintList(startButton, null);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Activity activity = getActivity();
                if (activity instanceof MainActivity) {
                    // This call to clearFocus will accept whatever input the user pressed, without
                    // forcing him to press the green check mark on the keyboard.
                    // Otherwise the typed value won't be read if either:
                    // - the user presses Start before closing the keyboard, or
                    // - the user closes the keyboard with the back button (note that does not cancel
                    //   the typed text).
                    trainerLevelPicker.clearFocus();
                    GoIVSettings.getInstance(activity).setLevel(trainerLevelPicker.getValue());
                    ((MainActivity) activity).runStartButtonLogic();
                }
            }
        });
    }

    /**
     * Initiates the scrollable level picker.
     */
    private void initiateLevelPicker() {
        trainerLevelPicker.setMinValue(Data.MINIMUM_TRAINER_LEVEL);
        trainerLevelPicker.setMaxValue(Data.MAXIMUM_TRAINER_LEVEL);
        trainerLevelPicker.setWrapSelectorWheel(false);
        trainerLevelPicker.setValue(GoIVSettings.getInstance(getContext()).getLevel());
        NpTrainerLevelPickerListener listener = new NpTrainerLevelPickerListener(getContext());
        trainerLevelPicker.setOnScrollListener(listener);
        trainerLevelPicker.setOnValueChangedListener(listener);
    }

    private String getVersionName() {
        Context context = getContext();
        if (context == null) {
            return "";
        }

        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Timber.e("Exception thrown while getting version name");
            Timber.e(e);
        }
        return "Error while getting version name";
    }

}

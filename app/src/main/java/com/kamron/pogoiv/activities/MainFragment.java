package com.kamron.pogoiv.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.NpTrainerLevelPickerListener;
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

    private static final String youtubeTutorialCalibrationUrl = "https://www.youtube.com/embed/w7dNEW1FLjQ?rel=0";


    @BindView(R.id.mainScrollView)
    ScrollView mainScrollView;

    @BindView(R.id.startButton)
    Button startButton;

    @BindView(R.id.trainerLevelPicker)
    NumberPicker trainerLevelPicker;

    @BindView(R.id.teamPickerSpinner)
    Spinner teamPickerSpinner;

    @BindView(R.id.versionNumber)
    TextView versionNumber;

    @BindView(R.id.optimizationWarningLayout)
    LinearLayout optimizationWarningLayout;

    @BindView(R.id.shouldRunOptimizationAgainWarning)
    TextView shouldRunOptimizationAgainWarning;

    @BindView(R.id.neverRunOptimizationWarning)
    TextView neverRunOptimizationWarning;

    @BindView(R.id.nonStandardScreenWarning)
    TextView nonStandardScreenWarning;

    @BindView(R.id.githubButton)
    ImageButton githubButton;

    @BindView(R.id.redditButton)
    ImageButton redditButton;

    @BindView(R.id.helpButton)
    Button helpButton;

    @BindView(R.id.recalibrationHelpButton)
    Button recalibrationHelpButton;

    @BindView(R.id.recalibrationHelpButton2)
    Button recalibrationHelpButton2;

    @BindView(R.id.optimizationVideoTutorialLayout)
    LinearLayout optimizationVideoTutorialLayout;

    @BindView(R.id.optimizationVideoTutorial)
    WebView optimizationVideoTutorial;


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
        LocalBroadcastManager.getInstance(context).sendBroadcast(i);
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

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(launchButtonChange,
                new IntentFilter(ACTION_UPDATE_LAUNCH_BUTTON));

        if (savedInstanceState == null) {
            PreferenceManager.setDefaultValues(getContext(), R.xml.settings, false);
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(launchButtonChange);
        super.onDestroy();
    }

    /**
     * Initiates all the gui components.
     */
    private void initiateGui() {
        versionNumber.setText(String.format("v%s", getVersionName()));
        setupTutorialButton();
        hideWebViewIfOfflineFlavour();
        initiateOptimizationWarning();
        initiateLevelPicker();
        initiateTeamPickerSpinner();
        initiateHelpButton();
        initiateCommunityButtons();
        initiateStartButton();
    }

    private void hideWebViewIfOfflineFlavour() {
        if (BuildConfig.FLAVOR.toLowerCase().contains("offline")) {
            optimizationVideoTutorial.setVisibility(View.GONE);
        }
    }

    /**
     * Show the optimization-warning and its components depending on if the user hasn't a manual screen calibration
     * saved, if the calibration isn't updated and if the device has weird screen ratio.
     */
    private void initiateOptimizationWarning() {
        GoIVSettings settings = GoIVSettings.getInstance(getContext());
        if (settings.hasUpToDateManualScanCalibration()) {
            optimizationWarningLayout.setVisibility(View.GONE); // Ensure the layout isn't visible

        } else {
            optimizationWarningLayout.setVisibility(View.VISIBLE);

            if (settings.hasManualScanCalibration()) {
                // Has outdated calibration
                shouldRunOptimizationAgainWarning.setVisibility(View.VISIBLE);

            } else {
                // Has never calibrated
                neverRunOptimizationWarning.setVisibility(View.VISIBLE);

                Activity activity = getActivity();
                if (activity == null) {
                    return;
                }
                // If the screen ratio isn't standard the user must run calibration
                Display display = activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getRealSize(size);
                float ratio = (float) size.x / size.y;
                float standardRatio = 9 / 16f;
                float tolerance = 1 / 400f;
                if (ratio < (standardRatio - tolerance) || ratio > (standardRatio + tolerance)) {
                    nonStandardScreenWarning.setVisibility(View.VISIBLE);
                }
            }
        }
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
                Uri uriUrl = Uri.parse("https://github.com/farkam135/GoIV");
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

    /**
     * Makes the help-buttons load and navigate to the tutorial youtube webview, or open the browser if using offline
     * build.
     */
    private void setupTutorialButton() {
        View.OnClickListener tutorialListener = new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (BuildConfig.FLAVOR.toLowerCase().contains("online")) {

                    if (optimizationVideoTutorialLayout.getVisibility() == View.GONE) {
                        optimizationVideoTutorialLayout.setVisibility(View.VISIBLE);

                        String frameVideo = "<html><iframe width=\"310\" height=\"480\" src=\""
                                + youtubeTutorialCalibrationUrl
                                + "\" frameborder=\"0\" gesture=\"media\" allow=\"encrypted-media\" "
                                + "allowfullscreen></iframe></html>";

                        optimizationVideoTutorial.setWebViewClient(new WebViewClient() {
                            @Override
                            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                return false;
                            }

                        });
                        optimizationVideoTutorial.getSettings().setJavaScriptEnabled(true);
                        optimizationVideoTutorial.loadData(frameVideo, "text/html", "utf-8");

                        mainScrollView.post(new Runnable() {
                            @Override
                            public void run() {
                                mainScrollView.smoothScrollTo(0, optimizationVideoTutorial.getTop());
                            }
                        });
                    } else {
                        optimizationVideoTutorial.stopLoading();
                        optimizationVideoTutorialLayout.setVisibility(View.GONE);
                    }

                } else {
                    // Running offline version, we cant load the webpage inserted into the app, we need to open browser.
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(youtubeTutorialCalibrationUrl));
                    startActivity(i);
                }
            }
        };

        recalibrationHelpButton.setOnClickListener(tutorialListener);
        recalibrationHelpButton2.setOnClickListener(tutorialListener);
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

package com.kamron.pogoiv.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecalibrateFragment extends Fragment {

    private static final String URL_YOUTUBE_TUTORIAL = "https://www.youtube.com/embed/w7dNEW1FLjQ?rel=0";


    @BindView(R.id.mainScrollView)
    NestedScrollView mainScrollView;

    @BindView(R.id.optimizationWarningLayout)
    LinearLayout optimizationWarningLayout;

    @BindView(R.id.shouldRunOptimizationAgainWarning)
    TextView shouldRunOptimizationAgainWarning;

    @BindView(R.id.neverRunOptimizationWarning)
    TextView neverRunOptimizationWarning;

    @BindView(R.id.nonStandardScreenWarning)
    TextView nonStandardScreenWarning;

    @BindView(R.id.recalibrationHelpButton)
    Button recalibrationHelpButton;

    @BindView(R.id.optimizationVideoTutorialLayout)
    LinearLayout optimizationVideoTutorialLayout;

    @BindView(R.id.optimizationVideoTutorial)
    WebView optimizationVideoTutorial;


    public RecalibrateFragment() {
        super();
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recalibrate, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initiateOptimizationWarning();
        setupTutorialButton();

        // Hide
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
                                + URL_YOUTUBE_TUTORIAL
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
                    i.setData(Uri.parse(URL_YOUTUBE_TUTORIAL));
                    startActivity(i);
                }
            }
        };

        recalibrationHelpButton.setOnClickListener(tutorialListener);
    }

}

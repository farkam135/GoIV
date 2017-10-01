package com.kamron.pogoiv.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.kamron.pogoiv.BuildConfig;
import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanArea;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldAutomaticLocator;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldResults;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanPoint;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OcrCalibrationResultActivity extends AppCompatActivity {

    public static Bitmap sCalibrationImage;

    @BindView(R.id.ocr_calibration_title)
    TextView ocr_calibration_title;
    @BindView(R.id.errorListTextView)
    TextView errorListTextView;
    @BindView(R.id.ocr_calibration_description)
    TextView ocr_calibration_description;
    @BindView(R.id.ocr_calibration_check)
    TextView ocr_calibration_check;
    @BindView(R.id.saveCalibrationButton)
    Button saveCalibrationButton;
    @BindView(R.id.ocr_result_image)
    ImageView resultImageView;
    @BindView(R.id.backToGoivButton)
    Button backToGoivButton;
    @BindView(R.id.backButton)
    Button backButton;
    @BindView(R.id.errorField)
    LinearLayout errorLayout;
    @BindView(R.id.emailErrorButton)
    Button emailErrorButton;


    private ScanFieldResults results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_ocr_calibration_result);
        ButterKnife.bind(this);

        fixHomeButton();

        View decor = getWindow().getDecorView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(0);
        }

        if (sCalibrationImage == null) {
            finish(); // We don't have a screenshot: terminate here
        } else {
            final ProgressDialog dialog = ProgressDialog.show(this, getText(R.string.ocr_calibrating), getText(R.string
                    .ocr_loading), true);

            final Handler mainThreadHandler = new Handler();
            final Runnable recalibrateRunner = new Runnable() {
                @Override
                public void run() {
                    DisplayMetrics realDisplayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getRealMetrics(realDisplayMetrics);

                    results = new ScanFieldAutomaticLocator(
                            sCalibrationImage, realDisplayMetrics.widthPixels, realDisplayMetrics.density)
                            .scan(mainThreadHandler, dialog, getBaseContext());

                    mainThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setMessage(getText(R.string.done));
                            dialog.dismiss();
                            if (results.isCompleteCalibration()) {
                                saveCalibrationButton.setEnabled(true);
                                errorListTextView.setVisibility(View.GONE);
                                ocr_calibration_description.setVisibility(View.VISIBLE);
                                ocr_calibration_check.setVisibility(View.VISIBLE);
                            } else {
                                StringBuilder sb = new StringBuilder();
                                if (results.pokemonNameArea == null) {
                                    sb.append(getText(R.string.ocr_error_name));
                                }
                                if (results.pokemonTypeArea == null) {
                                    sb.append(getText(R.string.ocr_error_type));
                                }
                                if (results.candyNameArea == null) {
                                    sb.append(getText(R.string.ocr_error_candy_name));
                                }
                                if (results.pokemonHpArea == null) {
                                    sb.append(getText(R.string.ocr_error_hp));
                                }
                                if (results.pokemonCpArea == null) {
                                    sb.append(getText(R.string.ocr_error_cp));
                                }
                                if (results.pokemonCandyAmountArea == null) {
                                    sb.append(getText(R.string.ocr_error_candy_amount));
                                }
                                if (results.pokemonEvolutionCostArea == null) {
                                    sb.append(getText(R.string.ocr_error_evo_cost));
                                }
                                if (results.arcCenter == null) {
                                    sb.append(getText(R.string.ocr_error_arc_center));
                                }
                                if (results.arcRadius == null) {
                                    sb.append(getText(R.string.ocr_error_arc_radius));
                                }
                                if (results.infoScreenCardWhitePixelPoint == null) {
                                    sb.append(getText(R.string.ocr_error_locate_pixel_white));
                                }
                                if (results.infoScreenCardWhitePixelColor == null) {
                                    sb.append(getText(R.string.ocr_error_pick_pixel_white));
                                }
                                if (results.infoScreenFabGreenPixelPoint == null) {
                                    sb.append(getText(R.string.ocr_error_locate_pixel_green));
                                }
                                if (results.infoScreenFabGreenPixelColor == null) {
                                    sb.append(getText(R.string.ocr_error_pick_pixel_green));
                                }
                                enableUserEmailErrorReporting(sCalibrationImage, sb.toString());
                                sb.append(getText(R.string.ocr_msg_verify));
                                errorListTextView.setText(sb);
                                ocr_calibration_title.setText(R.string.title_activity_ocr_calibration_error);
                                ocr_calibration_description.setVisibility(View.GONE);
                                ocr_calibration_check.setVisibility(View.GONE);
                                saveCalibrationButton.setVisibility(View.GONE);
                                backButton.setVisibility(View.VISIBLE);
                            }

                            // Draw results on a copy of the original screenshot
                            Bitmap resultIndicatorImage = sCalibrationImage.copy(sCalibrationImage.getConfig(), true);
                            drawResultIndicator(resultIndicatorImage);
                            resultImageView.setImageBitmap(resultIndicatorImage);
                        }
                    });
                }
            };
            new Thread(recalibrateRunner).start();
        }

        saveCalibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCalibrationButton.setVisibility(View.GONE);
                backToGoivButton.setVisibility(View.VISIBLE);
                if (results != null && results.isCompleteCalibration()) {
                    GoIVSettings settings = GoIVSettings.getInstance(OcrCalibrationResultActivity.this);
                    settings.saveScreenCalibrationResults(results);
                    settings.setManualScanCalibration(true);
                    Toast.makeText(OcrCalibrationResultActivity.this,
                            R.string.ocr_calibration_saved, Toast.LENGTH_LONG).show();
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = getPackageManager().getLaunchIntentForPackage("com.nianticlabs.pokemongo");
                if (i != null) {
                    startActivity(i);
                }
            }
        });
    }

    /**
     * Shows the email error section of the view, and adds the button logic that creates an email
     * for the image.
     *
     * @param sCalibrationImage The image that will be emailed.
     * @param errorText         The error message the user got.
     */
    private void enableUserEmailErrorReporting(final Bitmap sCalibrationImage, final String errorText) {
        errorLayout.setVisibility(View.VISIBLE);

        emailErrorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DisplayMetrics realDisplayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getRealMetrics(realDisplayMetrics);


                String pathofBmp = MediaStore.Images.Media.insertImage(getContentResolver(),
                        sCalibrationImage, "goivdebugimgremovable", null);
                Uri bmpUri = Uri.parse(pathofBmp);

                final String os;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !Strings.isNullOrEmpty(Build.VERSION.BASE_OS)) {
                    os = Build.VERSION.BASE_OS;
                } else {
                    os = "Android";
                }

                final Intent email = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto", "goivdevelopment@gmail.com", null));
                email.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                email.putExtra(Intent.EXTRA_EMAIL, new String[]{"goivdevelopment@gmail.com"});
                email.putExtra(Intent.EXTRA_SUBJECT, "GoIV auto calibration image error");
                email.putExtra(Intent.EXTRA_TEXT, getString(R.string.app_name) + " version: " + BuildConfig.VERSION_NAME
                        + "\nDevice maker and model: " + Build.MANUFACTURER + " " + Build.MODEL
                        + "\nOS: " + os + " " + Build.VERSION.RELEASE
                        + "\nScreen density: " + realDisplayMetrics.density
                        + "\n\n\nError message: \n" + errorText);
                email.putExtra(Intent.EXTRA_STREAM, bmpUri);

                // Grant read permission to candidate resolvers
                List<ResolveInfo> resolvers = getPackageManager().queryIntentActivities(email, 0);
                for (ResolveInfo r : resolvers) {
                    grantUriPermission(r.activityInfo.packageName, bmpUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivity(Intent.createChooser(email, "Choose an Email App:"));
            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sCalibrationImage = null; // This screenshot is no longer needed: let it be garbage collected
    }

    private void fixHomeButton() {
        backToGoivButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OcrCalibrationResultActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void hideStatusBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void drawResultIndicator(Bitmap bmp) {
        int purpleBright = Color.parseColor("#FA33FA");
        int purple = Color.parseColor("#AA00BB");

        showAreaIndicator(bmp, results.pokemonNameArea, purple);
        showAreaIndicator(bmp, results.pokemonTypeArea, purple);
        showAreaIndicator(bmp, results.candyNameArea, purple);
        showAreaIndicator(bmp, results.pokemonHpArea, purple);
        showAreaIndicator(bmp, results.pokemonCpArea, purple);
        showAreaIndicator(bmp, results.pokemonCandyAmountArea, purple);
        showAreaIndicator(bmp, results.pokemonEvolutionCostArea, purple);

        showPointIndicator(bmp, results.infoScreenCardWhitePixelPoint,
                results.infoScreenCardWhitePixelColor, purpleBright);
        showPointIndicator(bmp, results.infoScreenFabGreenPixelPoint,
                results.infoScreenFabGreenPixelColor, purpleBright);

        showPointIndicator(bmp, results.arcCenter, null, purpleBright);
        showArcIndicator(bmp, results.arcCenter, results.arcRadius, purple);
    }

    private void showPointIndicator(Bitmap bmp, ScanPoint point, Integer color, Integer strokeColor) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        if (point != null) {
            Paint p = new Paint();
            Canvas c = new Canvas(bmp);
            if (color != null) {
                p.setColor(color);
                c.drawCircle(point.xCoord, point.yCoord, 3 * density, p);
            }
            if (strokeColor != null) {
                p.setStyle(Paint.Style.STROKE);
                p.setColor(strokeColor);
                p.setStrokeWidth(density);
                c.drawCircle(point.xCoord, point.yCoord, 3 * density, p);
            }
        }
    }

    private void showAreaIndicator(Bitmap bmp, ScanArea scanArea, int color) {
        if (scanArea != null) {
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setColor(color);
            p.setStrokeWidth(Resources.getSystem().getDisplayMetrics().density);
            Canvas c = new Canvas(bmp);
            c.drawRect(scanArea.xPoint, scanArea.yPoint,
                    scanArea.xPoint + scanArea.width, scanArea.yPoint + scanArea.height, p);
        }
    }

    private void showArcIndicator(Bitmap bmp, ScanPoint point, Integer radius, int color) {
        if (point != null && radius != null) {
            Canvas c = new Canvas(bmp);
            Paint p = new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(Resources.getSystem().getDisplayMetrics().density);
            p.setColor(color);
            RectF oval = new RectF(point.xCoord - radius, point.yCoord - radius,
                    point.xCoord + radius, point.yCoord + radius);
            c.drawArc(oval, 180, 180, false, p);
        }
    }
}

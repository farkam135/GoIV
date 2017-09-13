package com.kamron.pogoiv.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.CalibrationImage;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanArea;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldAutomaticLocator;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldResults;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanPoint;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OcrCalibrationResultActivity extends AppCompatActivity {

    @BindView(R.id.errorListTextView)
    TextView errorListTextView;
    @BindView(R.id.saveCalibrationButton)
    Button saveCalibrationButton;
    @BindView(R.id.ocr_result_image)
    ImageView resultImageView;
    @BindView(R.id.backToGoivButton)
    Button backToGoivButton;


    private ScanFieldResults results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_ocr_calibration_result);
        ButterKnife.bind(this);

        fixHomeButton();
        final Bitmap bmp = CalibrationImage.calibrationImg;

        // Since the variable is static, we need to null the referenced object to be garbage collected.
        CalibrationImage.calibrationImg = null;

        final ProgressDialog dialog = ProgressDialog.show(this, getText(R.string.ocr_calibrating), getText(R.string
                .ocr_loading), true);

        final Handler mainThreadHandler = new Handler();
        final Runnable recalibrateRunner = new Runnable() {
            @Override public void run() {
                DisplayMetrics realDisplayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getRealMetrics(realDisplayMetrics);
                results = new ScanFieldAutomaticLocator(bmp, realDisplayMetrics.widthPixels, realDisplayMetrics.density)
                        .scan(mainThreadHandler, dialog, getBaseContext());
                mainThreadHandler.post(new Runnable() {
                    @Override public void run() {
                        dialog.setMessage(getText(R.string.done));
                        dialog.dismiss();
                        if (results.isCompleteCalibration()) {
                            saveCalibrationButton.setEnabled(true);
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
                            sb.append(getText(R.string.ocr_msg_verify));
                            errorListTextView.setText(sb);
                        }
                        drawResultIndicator(bmp);
                        resultImageView.setImageBitmap(bmp);
                    }
                });
            }
        };
        new Thread(recalibrateRunner).start();

        saveCalibrationButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
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
    }

    private void fixHomeButton() {
        backToGoivButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {

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

    private void showArcIndicator(Bitmap bmp, ScanPoint point, float radius, int color) {
        if (point != null) {
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

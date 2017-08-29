package com.kamron.pogoiv.activities;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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

    private ScanFieldResults results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideStatusBar();
        setContentView(R.layout.activity_ocr_calibration_result);
        ButterKnife.bind(this);

        final Bitmap bmp = CalibrationImage.calibrationImg;

        // Since the variable is static, we need to null the referenced object to be garbage collected.
        CalibrationImage.calibrationImg = null;

        final ProgressDialog dialog = ProgressDialog.show(this, "Calibrating", "Loading. Please wait...", true);

        final Handler mainThreadHandler = new Handler();
        final Runnable recalibrateRunner = new Runnable() {
            @Override public void run() {
                results = new ScanFieldAutomaticLocator(bmp, Resources.getSystem().getDisplayMetrics().density)
                        .scan(mainThreadHandler, dialog);
                mainThreadHandler.post(new Runnable() {
                    @Override public void run() {
                        dialog.setMessage("Done");
                        dialog.dismiss();
                        if (results.isCompleteCalibration()) {
                            saveCalibrationButton.setEnabled(true);
                        } else {
                            StringBuilder sb = new StringBuilder();
                            if (results.pokemonNameArea == null) {
                                sb.append("Unable to locate 'mon name\n");
                            }
                            if (results.pokemonTypeArea == null) {
                                sb.append("Unable to locate 'mon type\n");
                            }
                            if (results.candyNameArea == null) {
                                sb.append("Unable to locate 'mon candy name\n");
                            }
                            if (results.pokemonHpArea == null) {
                                sb.append("Unable to locate 'mon HP value\n");
                            }
                            if (results.pokemonCpArea == null) {
                                sb.append("Unable to locate 'mon CP value\n");
                            }
                            if (results.pokemonCandyAmountArea == null) {
                                sb.append("Unable to locate 'mon candies amount\n");
                            }
                            if (results.pokemonEvolutionCostArea == null) {
                                sb.append("Unable to locate 'mon evolution cost\n");
                            }
                            if (results.arcCenter == null) {
                                sb.append("Unable to locate level arc center\n");
                            }
                            if (results.arcRadius == null) {
                                sb.append("Unable to compute level arc radius\n");
                            }
                            if (results.infoScreenCardWhitePixelPoint == null) {
                                sb.append("Unable to locate white marker pixel\n");
                            }
                            if (results.infoScreenCardWhitePixelColor == null) {
                                sb.append("Unable to pick white marker pixel color\n");
                            }
                            if (results.infoScreenFabGreenPixelPoint == null) {
                                sb.append("Unable to locate green marker pixel\n");
                            }
                            if (results.infoScreenFabGreenPixelColor == null) {
                                sb.append("Unable to pick green marker pixel color\n");
                            }
                            sb.append("\nATTENTION! Please verify that:\n"
                                    + "① The 'mon info screen is scrolled all the way up\n"
                                    + "② The 'mon 3D model doesn't cover any important information\n"
                                    + "③ The 'mon is not a final evolution! I suggest a Pidgey!");
                            errorListTextView.setText(sb.toString());
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
                if (results != null && results.isCompleteCalibration()) {
                    GoIVSettings settings = GoIVSettings.getInstance(OcrCalibrationResultActivity.this);
                    settings.saveScreenCalibrationResults(results);
                    settings.setManualScanCalibration(true);
                    Toast.makeText(OcrCalibrationResultActivity.this,
                            "Calibration saved!\nRestart GoIV to apply the changes!", Toast.LENGTH_LONG).show();
                }
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

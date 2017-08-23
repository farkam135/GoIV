package com.kamron.pogoiv.activities;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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

    @BindView(R.id.saveCalibrationButton)
    Button saveCalibrationButton;
    @BindView(R.id.ocr_result_image)
    ImageView resultImageView;

    private ScanFieldResults results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_calibration_result);
        ButterKnife.bind(this);


        final Bitmap bmp = CalibrationImage.calibrationImg;

        //Since these variables are public static, we need to null the connected objects to be garbage collected.
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
                }
            }
        });
    }

    private void drawResultIndicator(Bitmap bmp) {
        float screenDensity = getResources().getDisplayMetrics().density;
        int purpleBright = Color.parseColor("#FA33FA");
        int purple = Color.parseColor("#AA00BB");

        showAreaIndicator(bmp, results.pokemonNameArea, purple);
        showAreaIndicator(bmp, results.pokemonTypeArea, purple);
        showAreaIndicator(bmp, results.candyNameArea, purple);
        showAreaIndicator(bmp, results.pokemonHpArea, purple);
        showAreaIndicator(bmp, results.pokemonCpArea, purple);
        showAreaIndicator(bmp, results.pokemonCandyAmountArea, purple);
        showAreaIndicator(bmp, results.pokemonEvolutionCostArea, purple);

        showPointIndicator(bmp, results.infoScreenCardWhitePixelPoint, 3 * screenDensity,
                results.infoScreenCardWhitePixelColor, purpleBright);
        showPointIndicator(bmp, results.infoScreenFabGreenPixelPoint, 3 * screenDensity,
                results.infoScreenFabGreenPixelColor, purpleBright);

        showPointIndicator(bmp, results.arcCenter, 3 * screenDensity, null, purpleBright);
        showAreaIndicator(bmp, new ScanArea(results.arcCenter.xCoord, results.arcCenter.yCoord,
                -results.arcRadius, 2), purple);
    }

    private void showPointIndicator(Bitmap bmp, ScanPoint point, float radius, Integer color, Integer strokeColor) {
        if (point != null) {
            Paint p = new Paint();
            Canvas c = new Canvas(bmp);
            if (color != null) {
                p.setColor(color);
                c.drawCircle(point.xCoord, point.yCoord, radius, p);
            }
            if (strokeColor != null) {
                p.setStyle(Paint.Style.STROKE);
                p.setColor(strokeColor);
                p.setStrokeWidth(radius / 3);
                c.drawCircle(point.xCoord, point.yCoord, radius, p);
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

}

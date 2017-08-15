package com.kamron.pogoiv.activities;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.kamron.pogoiv.GoIVSettings;
import com.kamron.pogoiv.Pokefly;
import com.kamron.pogoiv.R;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.CalibrationImage;
import com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanArea;

import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.arcInit;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.arcRadiusPoint;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.candyName_area;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.greenPokemonMenuPixel;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.pokemonCP_area;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.pokemonCandyAmount_area;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.pokemonEvolutionCost_area;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.pokemonHP_area;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.pokemonName_area;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.pokemonType_area;
import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.whitePixelPokemonScreen;

public class OcrCalibrationResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_calibration_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bitmap bmp = CalibrationImage.calibrationImg;
        Pokefly pokefly = CalibrationImage.pokefly;

        //Since these variables are public static, we need to null the connected objects to be garbage collected.
        CalibrationImage.calibrationImg = null;
        CalibrationImage.pokefly = null;

        ProgressDialog dialog = ProgressDialog.show(this, "Calibrating", "Loading. Please wait...", true);


        pokefly.getOcr().recalibrateScanAreas(bmp, dialog);
        dialog.setMessage("Done");
        dialog.dismiss();

        drawResultIndicator(bmp);

        ImageView result = (ImageView) findViewById(R.id.ocr_result_image);
        result.setImageBitmap(bmp);


    }

    private void drawResultIndicator(Bitmap bmp) {
        GoIVSettings settings = GoIVSettings.getInstance(null); //null because settings should always have been init.


        int color = Color.parseColor("#FA00FA");
        showAreaIndicator(bmp, new ScanArea(pokemonName_area, settings), Color.parseColor("#AA11BB"));
        showAreaIndicator(bmp, new ScanArea(pokemonType_area, settings), Color.parseColor("#AA11BB"));
        showAreaIndicator(bmp, new ScanArea(candyName_area, settings), Color.parseColor("#AA11BB"));
        showAreaIndicator(bmp, new ScanArea(pokemonHP_area, settings), Color.parseColor("#AA11BB"));
        showAreaIndicator(bmp, new ScanArea(pokemonCP_area, settings), Color.parseColor("#AA11BB"));
        showAreaIndicator(bmp, new ScanArea(pokemonCandyAmount_area, settings), Color.parseColor("#AA11BB"));
        showAreaIndicator(bmp, new ScanArea(pokemonEvolutionCost_area, settings), Color.parseColor("#AA11BB"));

        try {
            Point arcInitPoint = getPointFromSettings(settings, arcInit);
            showPointIndicator(bmp, arcInitPoint, 15, color);

            String p = settings.getCalibrationValue(arcRadiusPoint);
            int radius = Integer.valueOf(p);
            showAreaIndicator(bmp, new ScanArea(arcInitPoint.x, arcInitPoint.y, radius, 15), color);


        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Failed to find level arc beginning point", Toast.LENGTH_SHORT).show();
        }
        try {
            showPointIndicator(bmp, getPointFromSettings(settings, whitePixelPokemonScreen), 15, color);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Failed to find white indicator pixel", Toast.LENGTH_SHORT).show();
        }
        try {
            showPointIndicator(bmp, getPointFromSettings(settings, greenPokemonMenuPixel), 15, color);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, "Failed to find green indicator pixel", Toast.LENGTH_SHORT).show();
        }


    }

    private Point getPointFromSettings(GoIVSettings settings, String pointString) {
        String p = settings.getCalibrationValue(pointString);
        String[] pxy = p.split(",");

        if (pxy.length < 2) {
            return new Point(0, 0);
        }
        return new Point(Integer.valueOf(pxy[0]), Integer.valueOf(pxy[1]));


    }

    private void showPointIndicator(Bitmap bmp, Point point, int size, int color) {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (((point.x - size / 2) + x) < bmp.getWidth() && ((point.y - size / 2) + y) < bmp.getHeight()) {
                    bmp.setPixel((point.x - size / 2) + x, (point.y - size / 2) + y, color);
                }
            }
        }
    }

    private void showAreaIndicator(Bitmap bmp, ScanArea scanArea, int color) {
        Paint p = new Paint();
        p.setStyle(Paint.Style.STROKE);
        p.setColor(color);
        p.setStrokeWidth(Resources.getSystem().getDisplayMetrics().density);
        Canvas c = new Canvas(bmp);
        c.drawRect(scanArea.xPoint, scanArea.yPoint,
                scanArea.xPoint + scanArea.width, scanArea.yPoint + scanArea.height, p);
    }

}

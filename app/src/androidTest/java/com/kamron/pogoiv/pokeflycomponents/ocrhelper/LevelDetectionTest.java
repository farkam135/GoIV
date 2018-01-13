package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.kamron.pogoiv.scanlogic.Data;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.lang.ref.WeakReference;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LevelDetectionTest {

    private Context mContext;
    private Context mTargetContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        mTargetContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void scan_Trainer_35_Vaporeon_34_On_Sony_XZ1_Compact() throws IOException {
        scanDevice(Device.SONY_G8441, "vaporeon.png", null, 35, 34);
    }

    @Test
    public void scan_Trainer_35_Tyranitar_35_On_Sony_XZ1_Compact() throws IOException {
        scanDevice(Device.SONY_G8441, "tyranitar.png", null, 35, 35);
    }

    @Test
    public void scan_Trainer_35_Snorlax_33_On_Sony_XZ1_Compact() throws IOException {
        scanDevice(Device.SONY_G8441, "snorlax.png", null, 35, 33);
    }

    @Test
    public void scan_Trainer_35_Exeggutor_33_5_On_Sony_XZ1_Compact() throws IOException {
        scanDevice(Device.SONY_G8441, "exeggutor.png", "vaporeon.png", 35, 33.5);
    }

    private void scanDevice(@NonNull Device device,
                            @NonNull String screenshotFileName,
                            @Nullable String calibrationFileName,
                            int trainerLevel,
                            double expectedMonsterLevel) throws IOException {
        String screenshotPath = device.infoScreensDirPath + "/" + screenshotFileName;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        Bitmap screenshotBmp = BitmapFactory.decodeStream(mContext.getAssets().open(screenshotPath), null, options);

        Bitmap calibrationBmp;
        if (calibrationFileName == null) {
            calibrationBmp = screenshotBmp;
        } else {
            String calibrationPath = device.infoScreensDirPath + "/" + calibrationFileName;
            calibrationBmp = BitmapFactory.decodeStream(mContext.getAssets().open(calibrationPath), null, options);
        }

        ScanFieldAutomaticLocator autoLocator =
                new ScanFieldAutomaticLocator(calibrationBmp, calibrationBmp.getWidth(), device.screenDensity);
        //noinspection ConstantConditions
        ScanFieldResults results = autoLocator.scan(null, new WeakReference<ProgressDialog>(null),
                new WeakReference<>(mTargetContext));

        assertNotNull(results.arcCenter);
        assertNotNull(results.arcRadius);

        Data.setupArcPoints(results.arcCenter, results.arcRadius, trainerLevel);

        Mat image = new Mat();
        Utils.bitmapToMat(screenshotBmp, image);

        OcrHelper.computeAdaptiveThresholdBlockSize(
                screenshotBmp.getWidth(), screenshotBmp.getWidth(), device.screenDensity);
        double detectedMonsterLevel = OcrHelper.getPokemonLevelFromImg(image, trainerLevel);

        assertEquals("Level detection error;", expectedMonsterLevel, detectedMonsterLevel, 0.1);
    }

}

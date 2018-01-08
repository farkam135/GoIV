package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.kamron.pogoiv.scanlogic.Pokemon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class GenderDetectionTest {

    private Context mContext;
    private Context mTargetContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        mTargetContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void scan_Genders_Samsung_Galaxy_S3_Android_19() throws IOException {
        scanDevice(Device.SAMSUNG_GTI9305);
    }

    private void scanDevice(@NonNull Device device) throws IOException {
        String gendersDir = device.infoScreensDirPath + "/gender/";
        ScanFieldResults results = null;

        for (Pokemon.Gender gender : Pokemon.Gender.values()) {
            String genderPath = gendersDir + gender.getCharacter();

            String[] pokemonInfoScreenFileNames = mContext.getAssets().list(genderPath);

            assertTrue("No test images found for gender" + gender.toString() + " and device " + device.toString(),
                    pokemonInfoScreenFileNames.length > 0);

            for (String assetFileName : pokemonInfoScreenFileNames) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inMutable = true;
                Bitmap bmp = BitmapFactory.decodeStream(mContext.getAssets()
                        .open(genderPath + "/" + assetFileName), null, options);

                if (results == null) {
                    ScanFieldAutomaticLocator autoLocator =
                            new ScanFieldAutomaticLocator(bmp, bmp.getWidth(), device.screenDensity);
                    //noinspection ConstantConditions
                    results = autoLocator.scan(null, new WeakReference<ProgressDialog>(null),
                            new WeakReference<>(mTargetContext));
                }

                assertNotNull(results.pokemonGenderArea);

                Mat image = new Mat();
                Utils.bitmapToMat(bmp, image);

                OcrHelper.computeAdaptiveThresholdBlockSize(bmp.getWidth(), bmp.getWidth(), device.screenDensity);
                Pokemon.Gender detectedGender = OcrHelper.getPokemonGenderFromImg(image, results.pokemonGenderArea);

                assertEquals("Gender detection error;", detectedGender, gender);
            }
        }
    }

}

package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.kamron.pogoiv.scanlogic.Pokemon;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static com.kamron.pogoiv.pokeflycomponents.ocrhelper.ScanFieldNames.POKEMON_GENDER_AREA;
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
    public void scan_Female_Samsung_Galaxy_S3_Android_19() throws IOException {
        checkGender(Device.SAMSUNG_GTI9305, Pokemon.Gender.F);
    }

    @Test
    public void scan_Male_Samsung_Galaxy_S3_Android_19() throws IOException {
        checkGender(Device.SAMSUNG_GTI9305, Pokemon.Gender.M);
    }

    @Test
    public void scan_None_Samsung_Galaxy_S3_Android_19() throws IOException {
        checkGender(Device.SAMSUNG_GTI9305, Pokemon.Gender.N);
    }

    private void checkGender(Device device, Pokemon.Gender gender) throws IOException {
        String path = device.infoScreensDirPath + "/gender/";
        switch (gender) {
            case F: path += "f"; break;
            case M: path += "m"; break;
            default:
            case N: path += "n"; break;
        }

        String[] pokemonInfoScreenFileNames = mContext.getAssets().list(path);

        assertTrue("No test images found for gender" + gender.toString() + " and device " + device.toString(),
                pokemonInfoScreenFileNames.length > 0);

        for (String assetFileName : pokemonInfoScreenFileNames) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bmp = BitmapFactory.decodeStream(mContext.getAssets()
                    .open(path + "/" + assetFileName), null, options);
            ScanFieldAutomaticLocator autoLocator =
                    new ScanFieldAutomaticLocator(bmp, bmp.getWidth(), device.screenDensity);
            //noinspection ConstantConditions
            ScanFieldResults results = autoLocator.scan(null, new WeakReference<ProgressDialog>(null),
                    new WeakReference<>(mTargetContext));

            assertNotNull(results.pokemonGenderArea);

            Pokemon.Gender detectedGender = OcrHelper.getPokemonGenderFromImg(bmp, results.pokemonGenderArea);

            assertEquals("Detected gender " + detectedGender.toString()
                            + " differs from expected gender " + gender.toString(), detectedGender, gender);
        }
    }

}

package com.kamron.pogoiv.pokeflycomponents.ocrhelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.filters.LargeTest;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.ref.WeakReference;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScanFieldAutomaticLocatorTest {

    private Context mContext;
    private Context mTargetContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getContext();
        mTargetContext = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void scan_VDF500() throws IOException {
        checkDevice(Device.VODAFONE_VDF_500);
    }

    @Test
    public void scan_Nexus5() throws IOException {
        checkDevice(Device.GOOGLE_NEXUS_5);
    }

    @Test
    public void scan_Nexus6P() throws IOException {
        checkDevice(Device.GOOGLE_NEXUS_6P);
    }

    @Test
    public void scan_PixelXL() throws IOException {
        checkDevice(Device.GOOGLE_PIXEL_XL);
    }

    @Test
    public void scan_G930() throws IOException {
        checkDevice(Device.SAMSUNG_G930);
    }

    @Test
    public void scan_G950() throws IOException {
        checkDevice(Device.SAMSUNG_G950);
    }

    @Test
    public void scan_G950_game_mode() throws IOException {
        checkDevice(Device.SAMSUNG_G950_game_mode);
    }

    @Test
    public void scan_G955() throws IOException {
        checkDevice(Device.SAMSUNG_G955);
    }

    @Test
    public void scan_G955_game_mode() throws IOException {
        checkDevice(Device.SAMSUNG_G955_game_mode);
    }

    @Test
    public void scan_A5000() throws IOException {
        checkDevice(Device.ONEPLUS_A5000);
    }

    @Test
    public void scan_H870() throws IOException {
        checkDevice(Device.LG_H870);
    }

    private void checkDevice(Device device) throws IOException {
        String[] pokemonInfoScreenFileNames = mContext.getAssets().list(device.infoScreensDirPath);

        assertTrue("No test images found for " + device.toString(), pokemonInfoScreenFileNames.length > 0);

        for (String assetFileName : pokemonInfoScreenFileNames) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            Bitmap bmp = BitmapFactory.decodeStream(mContext.getAssets()
                    .open(device.infoScreensDirPath + "/" + assetFileName), null, options);
            ScanFieldAutomaticLocator autoLocator =
                    new ScanFieldAutomaticLocator(bmp, bmp.getWidth(), device.screenDensity);
            //noinspection ConstantConditions
            ScanFieldResults results = autoLocator.scan(null, new WeakReference<ProgressDialog>(null),
                    new WeakReference<>(mTargetContext));
            checkScanFieldResults(device, assetFileName, bmp, results);
        }
    }

    private void checkScanFieldResults(Device device, String testAssetName, Bitmap bmp, ScanFieldResults results) {
        // Execute checks on 'mon name area
        checkScanArea(device.toString(), testAssetName, bmp,
                "name", results.pokemonNameArea, device.expectedNameArea);

        // Execute checks on 'mon type area
        checkScanArea(device.toString(), testAssetName, bmp,
                "type", results.pokemonTypeArea, device.expectedTypeArea);

        // TODO check gender area
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon gender area wasn't detected", results.pokemonGenderArea);

        // Execute checks on 'mon candy name area
        checkScanArea(device.toString(), testAssetName, bmp,
                "candy name", results.candyNameArea, device.expectedCandyNameArea);

        // TODO check HP area
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon HP area wasn't detected", results.pokemonHpArea);

        // TODO check CP area
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon CP area wasn't detected", results.pokemonCpArea);

        // Execute checks on 'mon candy amount
        checkScanArea(device.toString(), testAssetName, bmp,
                "candy amount", results.pokemonCandyAmountArea, device.expectedCandyAmountArea);

        // Execute checks on 'mon evolution cost
        checkScanArea(device.toString(), testAssetName, bmp,
                "evolution cost", results.pokemonEvolutionCostArea, device.expectedEvolutionCost);

        // TODO check power up stardust cost area
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon power up stardust cost area wasn't detected", results.pokemonPowerUpStardustCostArea);

        // TODO check power up candy cost area
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon power up candy cost area wasn't detected", results.pokemonPowerUpCandyCostArea);

        // TODO check arc center point
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon arc center wasn't detected", results.arcCenter);

        // TODO check arc radius value
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon arc radius wasn't detected", results.arcRadius);

        // TODO check white pixel point
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon white pixel point wasn't detected", results.infoScreenCardWhitePixelPoint);

        // TODO check white pixel color value
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon white pixel color wasn't detected", results.infoScreenCardWhitePixelColor);

        // TODO check green pixel point
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon green pixel point wasn't detected", results.infoScreenFabGreenPixelPoint);

        // TODO check green pixel color value
        assertNotNull("File " + testAssetName + " on " + device.toString()
                + ": 'mon green pixel color wasn't detected", results.infoScreenFabGreenPixelColor);
    }

    private void checkScanArea(String deviceName, String testAssetName, Bitmap bmp, String areaLabel,
                               ScanArea result, Rect expected) {
        assertNotNull("File " + testAssetName + " on " + deviceName
                + ": 'mon " + areaLabel + " area wasn't detected", result);

        assertTrue("File " + testAssetName + " on " + deviceName
                + ": 'mon " + areaLabel + " area x coordinate can't be lower than 0", result.xPoint >= 0);

        assertTrue("File " + testAssetName + " on " + deviceName
                + ": 'mon " + areaLabel + " area y coordinate can't be lower than 0", result.yPoint >= 0);

        assertTrue("File " + testAssetName + " on " + deviceName
                        + ": 'mon " + areaLabel + " area can't exceed the image width",
                result.xPoint + result.width < bmp.getWidth());

        assertTrue("File " + testAssetName + " on " + deviceName
                        + ": 'mon " + areaLabel + " area can't exceed the image height",
                result.yPoint + result.height < bmp.getHeight());

        assertTrue("File " + testAssetName + " on " + deviceName
                + ": 'mon " + areaLabel + " area doesn't contain the entire " + areaLabel + "."
                + " Expected " + expected + " got " + result.toRectString(), result.contains(expected));

        int targetArea = 10 * expected.width() * expected.height();
        assertTrue("File " + testAssetName + " on " + deviceName + ": 'mon " + areaLabel + " area looks to big to me!"
                        + " Expected " + expected + " with area " + targetArea
                        +  " got " + result.toRectString() + " with area " + result.width * result.height,
                result.width * result.height < targetArea);
    }

}

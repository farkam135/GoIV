package com.kamron.pogoiv.pokeflycomponents.ocrhelper;


import android.graphics.Point;
import android.graphics.Rect;

public enum Device {
    GOOGLE_NEXUS_5("Google Nexus 5", 3f, "testinfoscreens/nexus5",
            new Rect(398, 823, 398 + 286, 823 + 61),    // Name
            new Rect(388, 1119, 388 + 312, 1118 + 21),  // Type
            new Rect(580, 1288, 280 + 345, 1288 + 21),  // Candy name
            new Rect(0, 0, 0, 0),                       // TODO HP area
            new Rect(0, 0, 0, 0),                       // TODO CP area
            new Rect(728, 1253, 728 + 49, 1253 + 33),   // Candy amount
            new Rect(797, 1575, 797 + 41, 1575 + 29),   // Evolution cost
            new Point(0, 0),                            // TODO Arc center
            0,                                          // TODO Arc radius
            new Point(0, 0),                            // TODO White pixel
            new Point(0, 0)),                           // TODO Green pixel

    VODAFONE_VDF_500("Vodafone Smart turbo 7", 1.3312501f, "testinfoscreens/vdf500",
            new Rect(178, 394, 178 + 122, 394 + 31),    // Name
            new Rect(187, 537, 187 + 122, 537 + 11),    // Type
            new Rect(250, 630, 250 + 166, 630 + 11),    // Candy name
            new Rect(0, 0, 0, 0),                       // TODO HP area
            new Rect(0, 0, 0, 0),                       // TODO CP area
            new Rect(327, 603, 327 + 30, 603 + 16),     // Candy amount
            new Rect(363, 757, 363 + 20, 757 + 14),     // Evolution cost
            new Point(0, 0),                            // TODO Arc center
            0,                                          // TODO Arc radius
            new Point(0, 0),                            // TODO White pixel
            new Point(0, 0)),                           // TODO Green pixel

    SAMSUNG_G930("Samsung Galaxy S7", 4f, "testinfoscreens/g930",
            new Rect(538, 1186, 538 + 365, 1186 + 88),  // Name
            new Rect(582, 1614, 583 + 292, 1614 + 31),  // Type
            new Rect(815, 1896, 815 + 367, 1896 + 30),  // Candy name
            new Rect(625, 1360, 625 + 191, 1360 + 32),  // HP area
            new Rect(524, 181, 524 + 296, 181 + 88),    // CP area
            new Rect(961, 1809, 961 + 72, 1809 + 50),   // Candy amount
            new Rect(1090, 2274, 1090 + 61, 2274 + 43), // Evolution cost
            new Point(720, 910),                        // Arc center
            582,                                        // Arc radius
            new Point(0, 1280),                         // TODO White pixel
            new Point(0, 0));                           // TODO Green pixel

    public final String makerModel;
    public final float screenDensity;
    public final String infoScreensDirPath;
    public final Rect expectedNameArea;
    public final Rect expectedTypeArea;
    public final Rect expectedCandyNameArea;
    public final Rect expectedHpArea;
    public final Rect expectedCpArea;
    public final Rect expectedCandyAmountArea;
    public final Rect expectedEvolutionCost;
    public final Point expectedArcCenter;
    public final Integer expectedArcRadius;
    public final Point expectedWhitePixel;
    public final Point expectedGreenPixel;

    Device(String makerModel, float screenDensity, String infoScreensDirPath,
           Rect expectedNameArea,
           Rect expectedTypeArea,
           Rect expectedCandyNameArea,
           Rect expectedHpArea,
           Rect expectedCpArea,
           Rect expectedCandyAmountArea,
           Rect expectedEvolutionCost,
           Point expectedArcCenter,
           Integer expectedArcRadius,
           Point expectedWhitePixel,
           Point expectedGreenPixel) {
        this.makerModel = makerModel;
        this.screenDensity = screenDensity;
        this.infoScreensDirPath = infoScreensDirPath;
        this.expectedNameArea = expectedNameArea;
        this.expectedTypeArea = expectedTypeArea;
        this.expectedCandyNameArea = expectedCandyNameArea;
        this.expectedHpArea = expectedHpArea;
        this.expectedCpArea = expectedCpArea;
        this.expectedCandyAmountArea = expectedCandyAmountArea;
        this.expectedEvolutionCost = expectedEvolutionCost;
        this.expectedArcCenter = expectedArcCenter;
        this.expectedArcRadius = expectedArcRadius;
        this.expectedWhitePixel = expectedWhitePixel;
        this.expectedGreenPixel = expectedGreenPixel;
    }

    @Override public String toString() {
        return makerModel;
    }
}

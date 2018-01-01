package com.kamron.pogoiv.pokeflycomponents.ocrhelper;


import android.graphics.Point;
import android.graphics.Rect;

public enum Device {
    GOOGLE_NEXUS_5("Google Nexus 5", 3f, "testinfoscreens/nexus5",
            new Rect(398, 823, 398 + 286, 823 + 61),    // Name
            new Rect(388, 1119, 388 + 312, 1118 + 21),  // Type
            new Rect(580, 1313, 580 + 346, 1313 + 21),  // Candy name
            new Rect(0, 0, 0, 0),                       // TODO HP area
            new Rect(0, 0, 0, 0),                       // TODO CP area
            new Rect(728, 1253, 728 + 49, 1253 + 33),   // Candy amount
            new Rect(797, 1575, 797 + 41, 1575 + 29),   // Evolution cost
            new Point(0, 0),                            // TODO Arc center
            0,                                          // TODO Arc radius
            new Point(0, 0),                            // TODO White pixel
            new Point(0, 0)),                           // TODO Green pixel

    GOOGLE_NEXUS_6P("Google Nexus 6P", 3.5f, "testinfoscreens/nexus6P",
            makeRect(588, 1109, 256, 67),               // Name
            makeRect(572, 1508, 306, 30),               // Type
            makeRect(837, 1771, 326, 28),               // Candy name
            makeRect(634, 1270, 169, 30),               // HP area
            makeRect(510, 170, 299, 82),                // CP area
            makeRect(960, 1690, 97, 45),                // Candy amount
            makeRect(1065, 2124, 56, 39),               // Evolution cost
            new Point(720, 849),                        // Arc center
            546,                                        // Arc radius
            new Point(0, 0),                            // TODO White pixel
            new Point(0, 0)),                           // TODO Green pixel

    GOOGLE_PIXEL_XL("Google Pixel XL", 3.5f, "testinfoscreens/pixelXL",
            makeRect(589, 1109, 264, 82),               // Name
            makeRect(553, 1508, 344, 30),               // Type
            makeRect(859, 1771, 288, 28),               // Candy name
            makeRect(632, 1270, 173, 31),               // HP area
            makeRect(537, 170, 271, 82),                // CP area
            makeRect(953, 1690, 98, 45),                // Candy amount
            makeRect(1066, 2124, 55, 39),               // Evolution cost
            new Point(720, 849),                        // Arc center
            546,                                        // Arc radius
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

    SAMSUNG_GTI9305("Samsung Galaxy S3", 306/160f, "testinfoscreens/gt-i9305", // Display density is estimated
            new Rect(0, 0, 0, 0),  // TODO Name
            new Rect(0, 0, 0, 0),  // TODO Type
            new Rect(0, 0, 0, 0),  // TODO Candy name
            new Rect(0, 0, 0, 0),  // TODO HP area
            new Rect(0, 0, 0, 0),    // TODO CP area
            new Rect(0, 0, 0, 0),   // TODO Candy amount
            new Rect(0, 0, 0, 0), // TODO Evolution cost
            new Point(360, 0),                        // TODO Arc center
            0,                                        // TODO Arc radius
            new Point(0, 640),                         // TODO White pixel
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
            new Point(0, 0)),                           // TODO Green pixel

    SAMSUNG_G950("Samsung Galaxy S8", 3.5f, "testinfoscreens/g950",
            new Rect(581, 1181, 581 + 286, 1181 + 91),  // Name
            new Rect(546, 1611, 546 + 362, 1611 + 33),  // Type
            new Rect(845, 1895, 845 + 309, 1895 + 31),  // Candy name
            new Rect(626, 1357, 626 + 187, 1357 + 32),  // HP area
            new Rect(526, 180, 526 + 289, 180 + 89),    // CP area
            new Rect(944, 1806, 944 + 109, 1806 + 50),   // Candy amount
            new Rect(1091, 2272, 1091 + 60, 2272 + 43), // Evolution cost
            new Point(720, 906),                        // Arc center
            583,                                        // Arc radius
            new Point(0, 1480),                         // TODO White pixel
            new Point(0, 0)),                           // TODO Green pixel

    SAMSUNG_G950_game_mode("Samsung Galaxy S8 / Game Mode", 2.625f, "testinfoscreens/g950_game_mode",
            makeRect(309, 879, 465, 77),                // Name
            makeRect(410, 1208, 271, 25),               // Type
            makeRect(634, 1421, 231, 23),               // Candy name
            makeRect(468, 1017, 142, 25),               // HP area
            makeRect(395, 136, 218, 65),                // CP area
            makeRect(696, 1355, 106, 36),               // Candy amount
            makeRect(818, 1704, 45, 32),                // Evolution cost
            new Point(540, 679),                        // Arc center
            377,                                        // Arc radius
            new Point(0, 0),                            // TODO White pixel
            new Point(0, 0)),                           // TODO Green pixel

    SAMSUNG_G955("Samsung Galaxy S8+", 3.5f, "testinfoscreens/g955",
            new Rect(582, 1186, 582 + 283, 1186 + 90),  // Name
            new Rect(523, 1613, 523 + 372, 1613 + 32),  // Type
            new Rect(844, 1895, 844 + 311, 1895 + 30),  // Candy name
            new Rect(626, 1359, 626 + 189, 1359 + 32),  // HP area
            new Rect(526, 182, 526 + 294, 182 + 86),    // CP area
            new Rect(946, 1808, 946 + 106, 1808 + 49),  // Candy amount
            new Rect(1092, 2274, 1092 + 58, 2274 + 41), // Evolution cost
            new Point(720, 908),                        // Arc center
            583,                                        // Arc radius
            new Point(0, 1480),                         // TODO White pixel
            new Point(0, 0)),                           // TODO Green pixel

    SAMSUNG_G955_game_mode("Samsung Galaxy S8+ / Game Mode", 2.625f, "testinfoscreens/g955_game_mode",
            makeRect(435, 886, 215, 69),                // Name
            makeRect(409, 1208, 272, 25),               // Type
            makeRect(634, 1421, 231, 24),               // Candy name
            makeRect(470, 1017, 140, 25),               // HP area
            makeRect(395, 135, 219, 67),                // CP area
            makeRect(708, 1355, 80, 37),                // Candy amount
            makeRect(818, 1704, 45, 32),                // Evolution cost
            new Point(540, 679),                        // Arc center
            437,                                        // Arc radius
            new Point(0, 1480),                         // TODO White pixel
            new Point(0, 0)),                           // TODO Green pixel

    ONEPLUS_A5000("OnePlus 5", 2.625f, "testinfoscreens/a5000",
            new Rect(435, 889, 435 + 208, 889 + 56),  // Name
            new Rect(418, 1210, 418 + 249, 1210 + 25),  // Type
            new Rect(615, 1421, 615 + 264, 1421 + 22),  // Candy name
            new Rect(471, 1019, 471 + 138, 1019 + 25),  // HP area
            new Rect(373, 137, 373 + 240, 137 + 65),    // CP area
            new Rect(708, 1356, 708 + 81, 1356 + 36),   // Candy amount
            new Rect(819, 1704, 819 + 44, 1704 + 31), // Evolution cost
            new Point(540, 680),                        // Arc center
            437,                                        // Arc radius
            new Point(0, 0),                         // TODO White pixel
            new Point(0, 0)),                           // TODO Green pixel

    LG_H870("LG G6", 4f, "testinfoscreens/h870",
            new Rect(582, 1186, 582 + 283, 1186 + 90),  // Name
            new Rect(514, 1613, 514 + 430, 1613 + 32),  // Type
            new Rect(787, 1895, 787 + 422, 1895 + 30),  // Candy name
            new Rect(631, 1359, 631 + 179, 1359 + 32),  // HP area
            new Rect(528, 182, 528 + 289, 182 + 86),    // CP area
            new Rect(946, 1808, 946 + 107, 1808 + 48),   // Candy amount
            new Rect(1092, 2274, 1092 + 58, 2274 + 41), // Evolution cost
            new Point(720, 907),                        // Arc center
            583,                                        // Arc radius
            new Point(0, 0),                         // TODO White pixel
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

    private static Rect makeRect(int x, int y, int w, int h) {
        return new Rect(x, y, x + w, y + h);
    }
}

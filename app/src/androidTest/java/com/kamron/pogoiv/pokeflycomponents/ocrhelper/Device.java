package com.kamron.pogoiv.pokeflycomponents.ocrhelper;


import android.graphics.Rect;

public enum Device {
    GOOGLE_NEXUS_5("Google Nexus 5", 3, "testinfoscreens/nexus5",
            new Rect(277, 823, 277 + 525, 823 + 50),
            new Rect(388, 1119, 388 + 312, 1118 + 21),
            new Rect(580, 1288, 280 + 345, 1288 + 21)),

    VODAFONE_VDF_500("Vodafone Smart turbo 7", 1.3312501f, "testinfoscreens/vdf500",
            new Rect(178, 394, 178 + 122, 394 + 31),
            new Rect(187, 537, 187 + 122, 537 + 11),
            new Rect(250, 630, 250 + 166, 630 + 11));

    public final String makerModel;
    public final float screenDensity;
    public final String infoScreensDirPath;
    public final Rect expectedNameArea;
    public final Rect expectedTypeArea;
    public final Rect expectedCandyNameArea;

    Device(String makerModel, float screenDensity, String infoScreensDirPath, Rect expectedNameArea,
           Rect expectedTypeArea, Rect expectedCandyNameArea) {
        this.makerModel = makerModel;
        this.screenDensity = screenDensity;
        this.infoScreensDirPath = infoScreensDirPath;
        this.expectedNameArea = expectedNameArea;
        this.expectedTypeArea = expectedTypeArea;
        this.expectedCandyNameArea = expectedCandyNameArea;
    }

    @Override public String toString() {
        return makerModel;
    }
}

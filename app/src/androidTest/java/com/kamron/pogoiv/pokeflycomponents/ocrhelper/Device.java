package com.kamron.pogoiv.pokeflycomponents.ocrhelper;


import android.graphics.Rect;

public enum Device {
    GOOGLE_NEXUS_5("Google Nexus 5", 3, "testinfoscreens/nexus5",
            new Rect(277, 823, 277 + 525, 823 + 50)),

    VODAFONE_VDF_500("Vodafone Smart turbo 7", 1.3312501f, "testinfoscreens/vdf500",
            new Rect(178, 394, 178 + 122, 394 + 31));

    public final String makerModel;
    public final float screenDensity;
    public final String infoScreensDirPath;
    public final Rect expectedNameArea;

    Device(String makerModel, float screenDensity, String infoScreensDirPath, Rect expectedNameArea) {
        this.makerModel = makerModel;
        this.screenDensity = screenDensity;
        this.infoScreensDirPath = infoScreensDirPath;
        this.expectedNameArea = expectedNameArea;
    }

    @Override public String toString() {
        return makerModel;
    }
}

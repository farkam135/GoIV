package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class SponsoredGeofenceGiftSettings {
    @Expose
    private Integer giftPersistenceTimeMs;
    @Expose
    private Integer mapPresentationTimeMs;
    @Expose
    private Boolean enableSponsoredGeofenceGift = false;
    @Expose
    private Integer fullscreenDisableExitButtonTimeMs;
    @Expose
    private BalloonGiftSettings balloonGiftSettings;

    public Integer getGiftPersistenceTimeMs() { return giftPersistenceTimeMs; }

    public void setGiftPersistenceTimeMs(Integer giftPersistenceTimeMs) {
        this.giftPersistenceTimeMs = giftPersistenceTimeMs;
    }

    public Integer getMapPresentationTimeMs() { return mapPresentationTimeMs; }

    public void setMapPresentationTimeMs(Integer mapPresentationTimeMs) {
        this.mapPresentationTimeMs = mapPresentationTimeMs;
    }

    public Boolean getEnableSponsoredGeofenceGift() { return enableSponsoredGeofenceGift; }

    public void setEnableSponsoredGeofenceGift(Boolean enableSponsoredGeofenceGift) {
        this.enableSponsoredGeofenceGift = enableSponsoredGeofenceGift;
    }

    public Integer getFullscreenDisableExitButtonTimeMs() {
        return fullscreenDisableExitButtonTimeMs;
    }

    public void setFullscreenDisableExitButtonTimeMs(Integer fullscreenDisableExitButtonTimeMs) {
        this.fullscreenDisableExitButtonTimeMs = fullscreenDisableExitButtonTimeMs;
    }

    public BalloonGiftSettings getBalloonGiftSettings() {
        return balloonGiftSettings;
    }

    public void setBalloonGiftSettings(
            BalloonGiftSettings balloonGiftSettings) {
        this.balloonGiftSettings = balloonGiftSettings;
    }
}

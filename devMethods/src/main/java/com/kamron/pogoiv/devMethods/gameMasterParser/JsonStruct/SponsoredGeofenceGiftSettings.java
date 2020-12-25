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
    private Boolean enablePoiGift = false;
    @Expose
    private Boolean enableIncidentGift = false;

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

    public Boolean getEnablePoiGift() { return enablePoiGift; }

    public void setEnablePoiGift(Boolean enablePoiGift) { this.enablePoiGift = enablePoiGift; }

    public Boolean getEnableIncidentGift() { return enableIncidentGift; }

    public void setEnableIncidentGift(Boolean enableIncidentGift) { this.enableIncidentGift = enableIncidentGift; }
}

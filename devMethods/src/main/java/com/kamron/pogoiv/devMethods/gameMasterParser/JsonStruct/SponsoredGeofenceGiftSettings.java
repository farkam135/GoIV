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

    public void setGiftPersistenceTimeMs(Integer giftPersistenceTimeMs) { this.giftPersistenceTimeMs = giftPersistenceTimeMs; }

    public Integer getMapPresentationTimeMs() { return mapPresentationTimeMs; }

    public void setMapPresentationTimeMs(Integer mapPresentationTimeMs) { this.mapPresentationTimeMs = mapPresentationTimeMs; }

    public Boolean getEnableSponsoredGeofenceGift() { return enableSponsoredGeofenceGift = false; }

    public void setEnableSponsoredGeofenceGift(Boolean enableSponsoredGeofenceGift) { this.enableSponsoredGeofenceGift = enableSponsoredGeofenceGift = false; }

    public Boolean getEnablePoiGift() { return enablePoiGift = false; }

    public void setEnablePoiGift(Boolean enablePoiGift) { this.enablePoiGift = enablePoiGift = false; }

    public Boolean getEnableIncidentGift() { return enableIncidentGift = false; }

    public void setEnableIncidentGift(Boolean enableIncidentGift) { this.enableIncidentGift = enableIncidentGift = false; }
}

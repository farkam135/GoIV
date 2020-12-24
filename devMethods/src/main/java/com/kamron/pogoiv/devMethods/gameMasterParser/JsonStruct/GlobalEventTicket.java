package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class GlobalEventTicket {
    @Expose
    private String eventBadge;
    @Expose
    private String grantBadgeBeforeEventStartMs;
    @Expose
    private String eventStartTime;
    @Expose
    private String eventEndTime;
    @Expose
    private String itemBagDescriptionKey;
    @Expose
    private String clientEventStartTimeUtcMs;
    @Expose
    private String clientEventEndTimeUtcMs;

    public String getEventBadge() { return eventBadge; }

    public void setEventBadge(String eventBadge) { this.eventBadge = eventBadge; }

    public String getGrantBadgeBeforeEventStartMs() { return grantBadgeBeforeEventStartMs; }

    public void setGrantBadgeBeforeEventStartMs(String grantBadgeBeforeEventStartMs) { this.grantBadgeBeforeEventStartMs = grantBadgeBeforeEventStartMs; }

    public String getEventStartTime() { return eventStartTime; }

    public void setEventStartTime(String eventStartTime) { this.eventStartTime = eventStartTime; }

    public String getEventEndTime() { return eventEndTime; }

    public void setEventEndTime(String eventEndTime) { this.eventEndTime = eventEndTime; }

    public String getItemBagDescriptionKey() { return itemBagDescriptionKey; }

    public void setItemBagDescriptionKey(String itemBagDescriptionKey) { this.itemBagDescriptionKey = itemBagDescriptionKey; }

    public String getClientEventStartTimeUtcMs() { return clientEventStartTimeUtcMs; }

    public void setClientEventStartTimeUtcMs(String clientEventStartTimeUtcMs) { this.clientEventStartTimeUtcMs = clientEventStartTimeUtcMs; }

    public String getClientEventEndTimeUtcMs() { return clientEventEndTimeUtcMs; }

    public void setClientEventEndTimeUtcMs(String clientEventEndTimeUtcMs) { this.clientEventEndTimeUtcMs = clientEventEndTimeUtcMs; }
}

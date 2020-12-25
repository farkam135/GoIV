package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class StickerMetadata {
    @Expose
    private String stickerId;
    @Expose
    private String stickerUrl;
    @Expose
    private Integer maxCount;

    public String getStickerId() { return stickerId; }

    public void setStickerId(String stickerId) { this.stickerId = stickerId; }

    public String getStickerUrl() { return stickerUrl; }

    public void setStickerUrl(String stickerUrl) { this.stickerUrl = stickerUrl; }

    public Integer getMaxCount() { return maxCount; }

    public void setMaxCount(Integer maxCount) { this.maxCount = maxCount; }
}

package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class IapItemDisplay {
    @Expose
    private String sku;
    @Expose
    private String category;
    @Expose
    private Integer sortOrder;
    @Expose
    private Boolean sale = false;
    @Expose
    private Boolean hidden = false;
    @Expose
    private String title;
    @Expose
    private String spriteId;
    @Expose
    private String description;
    @Expose
    private String skuEnableTime;
    @Expose
    private String skuDisableTime;
    @Expose
    private String skuEnableTimeUtcMs;
    @Expose
    private String skuDisableTimeUtcMs;
    @Expose
    private String imageUrl;

    public String getSku() { return sku; }

    public void setSku(String sku) { this.sku = sku; }

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public Integer getSortOrder() { return sortOrder; }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getSale() { return sale; }

    public void setSale(Boolean sale) { this.sale = sale; }

    public Boolean getHidden() { return hidden; }

    public void setHidden(Boolean hidden) { this.hidden = hidden; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getSpriteId() { return spriteId; }

    public void setSpriteId(String spriteId) { this.spriteId = spriteId; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public String getSkuEnableTime() { return skuEnableTime; }

    public void setSkuEnableTime(String skuEnableTime) { this.skuEnableTime = skuEnableTime; }

    public String getSkuDisableTime() { return skuDisableTime; }

    public void setSkuDisableTime(String skuDisableTime) { this.skuDisableTime = skuDisableTime; }

    public String getSkuEnableTimeUtcMs() { return skuEnableTimeUtcMs; }

    public void setSkuEnableTimeUtcMs(String skuEnableTimeUtcMs) { this.skuEnableTimeUtcMs = skuEnableTimeUtcMs; }

    public String getSkuDisableTimeUtcMs() { return skuDisableTimeUtcMs; }

    public void setSkuDisableTimeUtcMs(String skuDisableTimeUtcMs) { this.skuDisableTimeUtcMs = skuDisableTimeUtcMs; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

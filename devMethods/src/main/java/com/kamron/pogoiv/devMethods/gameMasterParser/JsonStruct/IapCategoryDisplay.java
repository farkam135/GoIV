package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class IapCategoryDisplay {
    @Expose
    private String category;
    @Expose
    private Integer sortOrder;
    @Expose
    private String imageUrl;
    @Expose
    private String description;
    @Expose
    private Boolean bannerEnabled = false;
    @Expose
    private String bannerTitle;
    @Expose
    private String name;
    @Expose
    private Integer displayRows;
    @Expose
    private Boolean hidden = false;

    public String getCategory() { return category; }

    public void setCategory(String category) { this.category = category; }

    public Integer getSortOrder() { return sortOrder; }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getImageUrl() { return imageUrl; }

    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public Boolean getBannerEnabled() { return bannerEnabled; }

    public void setBannerEnabled(Boolean bannerEnabled) { this.bannerEnabled = bannerEnabled; }

    public String getBannerTitle() { return bannerTitle; }

    public void setBannerTitle(String bannerTitle) { this.bannerTitle = bannerTitle; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Integer getDisplayRows() { return displayRows; }

    public void setDisplayRows(Integer displayRows) { this.displayRows = displayRows; }

    public Boolean getHidden() { return hidden; }

    public void setHidden(Boolean hidden) { this.hidden = hidden; }
}

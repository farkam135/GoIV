
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IapCategoryDisplay {

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("sortOrder")
    @Expose
    private Integer sortOrder;
    @SerializedName("bannerEnabled")
    @Expose
    private Boolean bannerEnabled;
    @SerializedName("imageUrl")
    @Expose
    private String imageUrl;
    @SerializedName("description")
    @Expose
    private String description;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getBannerEnabled() {
        return bannerEnabled;
    }

    public void setBannerEnabled(Boolean bannerEnabled) {
        this.bannerEnabled = bannerEnabled;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}

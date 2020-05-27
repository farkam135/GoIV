
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IapItemDisplay {

    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("sortOrder")
    @Expose
    private Integer sortOrder;
    @SerializedName("sale")
    @Expose
    private Boolean sale;
    @SerializedName("hidden")
    @Expose
    private Boolean hidden;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

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

    public Boolean getSale() {
        return sale;
    }

    public void setSale(Boolean sale) {
        this.sale = sale;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

}

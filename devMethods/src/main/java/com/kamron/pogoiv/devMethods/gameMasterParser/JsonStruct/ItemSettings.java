
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemSettings {

    @SerializedName("itemId")
    @Expose
    private String itemId;
    @SerializedName("itemType")
    @Expose
    private String itemType;
    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("dropTrainerLevel")
    @Expose
    private Integer dropTrainerLevel;
    @SerializedName("food")
    @Expose
    private Food food;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getDropTrainerLevel() {
        return dropTrainerLevel;
    }

    public void setDropTrainerLevel(Integer dropTrainerLevel) {
        this.dropTrainerLevel = dropTrainerLevel;
    }

    public Food getFood() {
        return food;
    }

    public void setFood(Food food) {
        this.food = food;
    }

}

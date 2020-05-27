
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PogoJson {

    @SerializedName("itemTemplate")
    @Expose
    private List<ItemTemplate> itemTemplates = null;
    @SerializedName("timestampMs")
    @Expose
    private String timestampMs;

    public List<ItemTemplate> getItemTemplates() {
        return itemTemplates;
    }

    public void setItemTemplates(List<ItemTemplate> itemTemplates) {
        this.itemTemplates = itemTemplates;
    }

    public String getTimestampMs() {
        return timestampMs;
    }

    public void setTimestampMs(String timestampMs) {
        this.timestampMs = timestampMs;
    }

}

package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VsSeekerClientSettings {
    @SerializedName("allowedVsSeekerLeagueTemplateId")
    @Expose
    private List<String> allowedVsSeekerLeagueTemplateIds = null;

    public List<String> getAllowedVsSeekerLeagueTemplateIds() { return allowedVsSeekerLeagueTemplateIds; }

    public void setAllowedVsSeekerLeagueTemplateIds(List<String> allowedVsSeekerLeagueTemplateIds) {
        this.allowedVsSeekerLeagueTemplateIds = allowedVsSeekerLeagueTemplateIds;
    }
}

package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class MonodepthSettings {
    @Expose
    private Boolean enableOcclusions = false;
    @Expose
    private Boolean occlusionsDefaultOn = false;
    @Expose
    private Boolean occlusionsToggleVisible = false;
    @Expose
    private Boolean enableGroundSuppression = false;
    @Expose
    private Double minGroundSuppressionThresh;
    @Expose
    private Integer suppressionChannelId;
}

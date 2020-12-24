package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TempEvoOverride {
    @Expose
    private String tempEvoId;
    @Expose
    private Stats stats;
    @Expose
    private Double averageHeightM;
    @Expose
    private Double averageWeightKg;
    @Expose
    private String typeOverride1;
    @Expose
    private String typeOverride2;
    @SerializedName("camera")
    @Expose
    private CameraData cameraData; // diskRadiusM, shoulderModeScale fields don't get used here.
    @Expose
    private Double modelScaleV2;
    @Expose
    private Double modelHeight;
    @Expose
    private List<Double> buddyOffsetMale = null;
    @Expose
    private List<Double> buddyOffsetFemale = null;
    @Expose
    private List<Double> buddyPortraitOffset = null;

    public String getTempEvoId() { return tempEvoId; }

    public void setTempEvoId(String tempEvoId) { this.tempEvoId = tempEvoId; }

    public Stats getStats() { return stats; }

    public void setStats(Stats stats) { this.stats = stats; }

    public Double getAverageHeightM() { return averageHeightM; }

    public void setAverageHeightM(Double averageHeightM) { this.averageHeightM = averageHeightM; }

    public Double getAverageWeightKg() { return averageWeightKg; }

    public void setAverageWeightKg(Double averageWeightKg) { this.averageWeightKg = averageWeightKg; }

    public String getTypeOverride1() { return typeOverride1; }

    public void setTypeOverride1(String typeOverride1) { this.typeOverride1 = typeOverride1; }

    public String getTypeOverride2() { return typeOverride2; }

    public void setTypeOverride2(String typeOverride2) { this.typeOverride2 = typeOverride2; }

    public CameraData getCameraData() { return cameraData; }

    public void setCameraData(CameraData cameraData) { this.cameraData = cameraData; }

    public Double getModelScaleV2() { return modelScaleV2; }

    public void setModelScaleV2(Double modelScaleV2) { this.modelScaleV2 = modelScaleV2; }

    public Double getModelHeight() { return modelHeight; }

    public void setModelHeight(Double modelHeight) { this.modelHeight = modelHeight; }

    public List<Double> getBuddyOffsetMale() { return buddyOffsetMale; }

    public void setBuddyOffsetMale(List<Double> buddyOffsetMale) { this.buddyOffsetMale = buddyOffsetMale; }

    public List<Double> getBuddyOffsetFemale() { return buddyOffsetFemale; }

    public void setBuddyOffsetFemale(List<Double> buddyOffsetFemale) { this.buddyOffsetFemale = buddyOffsetFemale; }

    public List<Double> getBuddyPortraitOffset() { return buddyPortraitOffset; }

    public void setBuddyPortraitOffset(List<Double> buddyPortraitOffset) { this.buddyPortraitOffset = buddyPortraitOffset; }
}

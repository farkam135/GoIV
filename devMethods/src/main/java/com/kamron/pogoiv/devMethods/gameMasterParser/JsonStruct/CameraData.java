package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CameraData {

    @Expose
    private Double diskRadiusM;
    @SerializedName("cylRadiusM")
    @Expose
    private Double cylinderRadiusM;
    @SerializedName("cylHeightM")
    @Expose
    private Double cylinderHeightM;
    @Expose
    private Double shoulderModeScale;
    @SerializedName("cylGroundM")
    @Expose
    private Double cylinderGroundM;

    public Double getDiskRadiusM() {
        return diskRadiusM;
    }

    public void setDiskRadiusM(Double diskRadiusM) {
        this.diskRadiusM = diskRadiusM;
    }

    public Double getCylinderRadiusM() {
        return cylinderRadiusM;
    }

    public void setCylinderRadiusM(Double cylinderRadiusM) {
        this.cylinderRadiusM = cylinderRadiusM;
    }

    public Double getCylinderHeightM() {
        return cylinderHeightM;
    }

    public void setCylinderHeightM(Double cylinderHeightM) {
        this.cylinderHeightM = cylinderHeightM;
    }

    public Double getShoulderModeScale() {
        return shoulderModeScale;
    }

    public void setShoulderModeScale(Double shoulderModeScale) {
        this.shoulderModeScale = shoulderModeScale;
    }

    public Double getCylinderGroundM() { return cylinderGroundM; }

    public void setCylinderGroundM(Double cylinderGroundM) { this.cylinderGroundM = cylinderGroundM; }
}
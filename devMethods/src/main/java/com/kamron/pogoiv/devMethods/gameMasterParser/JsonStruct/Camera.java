
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Camera {

    @SerializedName("diskRadiusM")
    @Expose
    private Double diskRadiusM;
    @SerializedName("cylinderRadiusM")
    @Expose
    private Double cylinderRadiusM;
    @SerializedName("cylinderHeightM")
    @Expose
    private Double cylinderHeightM;
    @SerializedName("shoulderModeScale")
    @Expose
    private Double shoulderModeScale;

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

}

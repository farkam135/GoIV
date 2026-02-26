package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Camera {
    @Expose
    private List<String> interpolation = null;
    @Expose
    private List<String> targetType = null;
    @Expose
    private List<Double> easeInSpeed = null;
    @Expose
    private List<Double> easeOutSpeed = null;
    @Expose
    private List<Double> durationS = null;
    @Expose
    private List<Double> waitS = null;
    @Expose
    private List<Double> transitionS = null;
    @Expose
    private List<Double> angleDeg = null;
    @Expose
    private List<Double> angleOffsetDeg = null;
    @Expose
    private List<Double> pitchDeg = null;
    @Expose
    private List<Double> pitchOffsetDeg = null;
    @Expose
    private List<Double> rollDeg = null;
    @Expose
    private List<Double> distanceM = null;
    @Expose
    private List<Double> heightPercent = null;
    @Expose
    private List<Double> vertCtrRatio = null;
    @Expose
    private String nextCamera;

    public List<String> getInterpolation() { return interpolation; }

    public void setInterpolation(List<String> interpolation) { this.interpolation = interpolation; }

    public List<String> getTargetType() { return targetType; }

    public void setTargetType(List<String> targetType) { this.targetType = targetType; }

    public List<Double> getEaseInSpeed() { return easeInSpeed; }

    public void setEaseInSpeed(List<Double> easeInSpeed) { this.easeInSpeed = easeInSpeed; }

    public List<Double> getEaseOutSpeed() { return easeOutSpeed; }

    public void setEaseOutSpeed(List<Double> easeOutSpeed) { this.easeOutSpeed = easeOutSpeed; }

    public List<Double> getDurationS() { return durationS; }

    public void setDurationS(List<Double> durationS) { this.durationS = durationS; }

    public List<Double> getWaitS() { return waitS; }

    public void setWaitS(List<Double> waitS) { this.waitS = waitS; }

    public List<Double> getTransitionS() { return transitionS; }

    public void setTransitionS(List<Double> transitionS) { this.transitionS = transitionS; }

    public List<Double> getAngleDeg() { return angleDeg; }

    public void setAngleDeg(List<Double> angleDeg) { this.angleDeg = angleDeg; }

    public List<Double> getAngleOffsetDeg() { return angleOffsetDeg; }

    public void setAngleOffsetDeg(List<Double> angleOffsetDeg) { this.angleOffsetDeg = angleOffsetDeg; }

    public List<Double> getPitchDeg() { return pitchDeg; }

    public void setPitchDeg(List<Double> pitchDeg) { this.pitchDeg = pitchDeg; }

    public List<Double> getPitchOffsetDeg() { return pitchOffsetDeg; }

    public void setPitchOffsetDeg(List<Double> pitchOffsetDeg) { this.pitchOffsetDeg = pitchOffsetDeg; }

    public List<Double> getRollDeg() { return rollDeg; }

    public void setRollDeg(List<Double> rollDeg) { this.rollDeg = rollDeg; }

    public List<Double> getDistanceM() { return distanceM; }

    public void setDistanceM(List<Double> distanceM) { this.distanceM = distanceM; }

    public List<Double> getHeightPercent() { return heightPercent; }

    public void setHeightPercent(List<Double> heightPercent) { this.heightPercent = heightPercent; }

    public List<Double> getVertCtrRatio() { return vertCtrRatio; }

    public void setVertCtrRatio(List<Double> vertCtrRatio) { this.vertCtrRatio = vertCtrRatio; }

    public String getNextCamera() { return nextCamera; }

    public void setNextCamera(String nextCamera) { this.nextCamera = nextCamera; }
}

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
    private List<Double> durationSeconds = null;
    @Expose
    private List<Double> waitSeconds = null;
    @Expose
    private List<Double> transitionSeconds = null;
    @Expose
    private List<Double> angleDegree = null;
    @Expose
    private List<Double> angleOffsetDegree = null;
    @Expose
    private List<Double> pitchDegree = null;
    @Expose
    private List<Double> pitchOffsetDegree = null;
    @Expose
    private List<Double> rollDegree = null;
    @Expose
    private List<Double> distanceMeters = null;
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

    public List<Double> getDurationSeconds() { return durationSeconds; }

    public void setDurationSeconds(List<Double> durationSeconds) { this.durationSeconds = durationSeconds; }

    public List<Double> getWaitSeconds() { return waitSeconds; }

    public void setWaitSeconds(List<Double> waitSeconds) { this.waitSeconds = waitSeconds; }

    public List<Double> getTransitionSeconds() { return transitionSeconds; }

    public void setTransitionSeconds(List<Double> transitionSeconds) { this.transitionSeconds = transitionSeconds; }

    public List<Double> getAngleDegree() { return angleDegree; }

    public void setAngleDegree(List<Double> angleDegree) { this.angleDegree = angleDegree; }

    public List<Double> getAngleOffsetDegree() { return angleOffsetDegree; }

    public void setAngleOffsetDegree(List<Double> angleOffsetDegree) { this.angleOffsetDegree = angleOffsetDegree; }

    public List<Double> getPitchDegree() { return pitchDegree; }

    public void setPitchDegree(List<Double> pitchDegree) { this.pitchDegree = pitchDegree; }

    public List<Double> getPitchOffsetDegree() { return pitchOffsetDegree; }

    public void setPitchOffsetDegree(List<Double> pitchOffsetDegree) { this.pitchOffsetDegree = pitchOffsetDegree; }

    public List<Double> getRollDegree() { return rollDegree; }

    public void setRollDegree(List<Double> rollDegree) { this.rollDegree = rollDegree; }

    public List<Double> getDistanceMeters() { return distanceMeters; }

    public void setDistanceMeters(List<Double> distanceMeters) { this.distanceMeters = distanceMeters; }

    public List<Double> getHeightPercent() { return heightPercent; }

    public void setHeightPercent(List<Double> heightPercent) { this.heightPercent = heightPercent; }

    public List<Double> getVertCtrRatio() { return vertCtrRatio; }

    public void setVertCtrRatio(List<Double> vertCtrRatio) { this.vertCtrRatio = vertCtrRatio; }

    public String getNextCamera() { return nextCamera; }

    public void setNextCamera(String nextCamera) { this.nextCamera = nextCamera; }
}

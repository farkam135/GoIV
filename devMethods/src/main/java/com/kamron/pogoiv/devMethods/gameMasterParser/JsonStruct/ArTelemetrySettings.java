package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class ArTelemetrySettings {
    @Expose
    private Boolean measureBattery = false;
    @Expose
    private Integer batterySamplingIntervalMs;
    @Expose
    private Boolean measureFramerate = false;
    @Expose
    private Integer framerateSamplingIntervalMs;
    @Expose
    private Double percentageSessionsToSample;

    public Boolean getMeasureBattery() { return measureBattery; }

    public void setMeasureBattery(Boolean measureBattery) { this.measureBattery = measureBattery; }

    public Integer getBatterySamplingIntervalMs() { return batterySamplingIntervalMs; }

    public void setBatterySamplingIntervalMs(Integer batterySamplingIntervalMs) {
        this.batterySamplingIntervalMs = batterySamplingIntervalMs;
    }

    public Boolean getMeasureFramerate() { return measureFramerate; }

    public void setMeasureFramerate(Boolean measureFramerate) { this.measureFramerate = measureFramerate; }

    public Integer getFramerateSamplingIntervalMs() { return framerateSamplingIntervalMs; }

    public void setFramerateSamplingIntervalMs(Integer framerateSamplingIntervalMs) {
        this.framerateSamplingIntervalMs = framerateSamplingIntervalMs;
    }

    public Double getPercentageSessionsToSample() { return percentageSessionsToSample; }

    public void setPercentageSessionsToSample(Double percentageSessionsToSample) {
        this.percentageSessionsToSample = percentageSessionsToSample;
    }
}

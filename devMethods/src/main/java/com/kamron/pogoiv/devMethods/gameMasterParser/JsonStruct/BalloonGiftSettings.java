package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BalloonGiftSettings {
    @Expose
    private boolean enableBalloonGift;
    @Expose
    private Integer balloonAutoDismissTimeMs;
    @Expose
    private Integer getWasabiAdRpcIntervalMs;

    public boolean isEnableBalloonGift() {
        return enableBalloonGift;
    }

    public void setEnableBalloonGift(boolean enableBalloonGift) {
        this.enableBalloonGift = enableBalloonGift;
    }

    public Integer getBalloonAutoDismissTimeMs() {
        return balloonAutoDismissTimeMs;
    }

    public void setBalloonAutoDismissTimeMs(Integer balloonAutoDismissTimeMs) {
        this.balloonAutoDismissTimeMs = balloonAutoDismissTimeMs;
    }

    public Integer getGetWasabiAdRpcIntervalMs() {
        return getWasabiAdRpcIntervalMs;
    }

    public void setGetWasabiAdRpcIntervalMs(Integer getWasabiAdRpcIntervalMs) {
        this.getWasabiAdRpcIntervalMs = getWasabiAdRpcIntervalMs;
    }
}

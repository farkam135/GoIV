package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class ReferralSettings {
    @Expose
    private boolean featureEnabled;
    @Expose
    private List<RecentFeature> recentFeatures;
    @Expose
    private String addReferrerGracePeriodMs;
    @Expose
    private Integer minNumDaysWithoutSessionForLapsedPlayer;

    public boolean isFeatureEnabled() {
        return featureEnabled;
    }

    public void setFeatureEnabled(boolean featureEnabled) {
        this.featureEnabled = featureEnabled;
    }

    public List<RecentFeature> getRecentFeatures() {
        return recentFeatures;
    }

    public void setRecentFeatures(List<RecentFeature> recentFeatures) {
        this.recentFeatures = recentFeatures;
    }

    public String getAddReferrerGracePeriodMs() {
        return addReferrerGracePeriodMs;
    }

    public void setAddReferrerGracePeriodMs(String addReferrerGracePeriodMs) {
        this.addReferrerGracePeriodMs = addReferrerGracePeriodMs;
    }

    public Integer getMinNumDaysWithoutSessionForLapsedPlayer() {
        return minNumDaysWithoutSessionForLapsedPlayer;
    }

    public void setMinNumDaysWithoutSessionForLapsedPlayer(Integer minNumDaysWithoutSessionForLapsedPlayer) {
        this.minNumDaysWithoutSessionForLapsedPlayer = minNumDaysWithoutSessionForLapsedPlayer;
    }
}

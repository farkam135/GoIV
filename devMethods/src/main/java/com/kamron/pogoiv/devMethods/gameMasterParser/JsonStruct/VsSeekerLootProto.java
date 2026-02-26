package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class VsSeekerLootProto {
    @Expose
    private Integer rankLevel;
    @Expose
    private List<Reward> rewards = null;
    @Expose
    private String rewardTrack;

    public Integer getRankLevel() { return rankLevel; }

    public void setRankLevel(Integer rankLevel) { this.rankLevel = rankLevel; }

    public List<Reward> getRewards() { return rewards; }

    public void setRewards(List<Reward> rewards) { this.rewards = rewards; }

    public String getRewardTrack() { return rewardTrack; }

    public void setRewardTrack(String rewardTrack) { this.rewardTrack = rewardTrack; }
}

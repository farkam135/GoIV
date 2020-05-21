
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuestSettings {

    @SerializedName("questType")
    @Expose
    private String questType;
    @SerializedName("dailyQuest")
    @Expose
    private DailyQuest dailyQuest;

    public String getQuestType() {
        return questType;
    }

    public void setQuestType(String questType) {
        this.questType = questType;
    }

    public DailyQuest getDailyQuest() {
        return dailyQuest;
    }

    public void setDailyQuest(DailyQuest dailyQuest) {
        this.dailyQuest = dailyQuest;
    }

}

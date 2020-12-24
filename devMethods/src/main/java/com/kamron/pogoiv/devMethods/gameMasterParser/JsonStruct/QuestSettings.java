package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class QuestSettings {
    @Expose
    private String questType;
    @Expose
    private DailyQuest dailyQuest;

    public String getQuestType() { return questType; }

    public void setQuestType(String questType) { this.questType = questType; }

    public DailyQuest getDailyQuest() { return dailyQuest; }

    public void setDailyQuest(DailyQuest dailyQuest) { this.dailyQuest = dailyQuest; }
}

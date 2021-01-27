package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class QuestEvolutionSettings {
    @Expose
    private Boolean enableQuestEvolutions = false;

    public Boolean getEnableQuestEvolutions() { return enableQuestEvolutions; }

    public void setEnableQuestEvolutions(Boolean enableQuestEvolutions) {
        this.enableQuestEvolutions = enableQuestEvolutions;
    }
}

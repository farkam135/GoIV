package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class QuestDisplay {
    @Expose
    private String questRequirementTemplateId;

    public String getQuestRequirementTemplateId() { return questRequirementTemplateId; }

    public void setQuestRequirementTemplateId(String questRequirementTemplateId) {
        this.questRequirementTemplateId = questRequirementTemplateId;
    }
}

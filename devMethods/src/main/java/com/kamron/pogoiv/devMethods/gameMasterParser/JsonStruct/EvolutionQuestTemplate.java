package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class EvolutionQuestTemplate {
    @Expose
    private String questTemplateId;
    @Expose
    private String questType;
    @Expose
    private List<Goal> goals = null;
    @Expose
    private String context;
    @Expose
    private Display display;

    public String getQuestTemplateId() { return questTemplateId; }

    public void setQuestTemplateId(String questTemplateId) { this.questTemplateId = questTemplateId; }

    public String getQuestType() { return questType; }

    public void setQuestType(String questType) { this.questType = questType; }

    public List<Goal> getGoals() { return goals; }

    public void setGoals(List<Goal> goals) { this.goals = goals; }

    public String getContext() { return context; }

    public void setContext(String context) { this.context = context; }

    public Display getDisplay() { return display; }

    public void setDisplay(Display display) { this.display = display; }
}

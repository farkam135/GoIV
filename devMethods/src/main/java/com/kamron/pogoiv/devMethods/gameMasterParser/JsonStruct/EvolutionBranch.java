package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class EvolutionBranch {
    @Expose
    private String evolution;
    @Expose
    private Integer candyCost;
    @Expose
    private String form;
    @Expose
    private String temporaryEvolution;
    @Expose
    private Integer temporaryEvolutionEnergyCost;
    @Expose
    private Integer temporaryEvolutionEnergyCostSubsequent;
    @Expose
    private String evolutionItemRequirement;
    @Expose
    private Boolean noCandyCostViaTrade = false;
    @Expose
    private String lureItemRequirement;
    @Expose
    private List<QuestDisplay> questDisplay = null;
    @Expose
    private Double kmBuddyDistanceRequirement;
    @Expose
    private Boolean mustBeBuddy = false;
    @Expose
    private Boolean onlyDaytime = false;
    @Expose
    private Integer priority;
    @Expose
    private Boolean onlyNighttime = false;
    @Expose
    private String genderRequirement;

    public String getEvolution() { return evolution; }

    public void setEvolution(String evolution) { this.evolution = evolution; }

    public Integer getCandyCost() { return candyCost; }

    public void setCandyCost(Integer candyCost) { this.candyCost = candyCost; }

    public String getForm() { return form; }

    public void setForm(String form) { this.form = form; }

    public String getTemporaryEvolution() { return temporaryEvolution; }

    public void setTemporaryEvolution(String temporaryEvolution) { this.temporaryEvolution = temporaryEvolution; }

    public Integer getTemporaryEvolutionEnergyCost() { return temporaryEvolutionEnergyCost; }

    public void setTemporaryEvolutionEnergyCost(Integer temporaryEvolutionEnergyCost) { this.temporaryEvolutionEnergyCost = temporaryEvolutionEnergyCost; }

    public Integer getTemporaryEvolutionEnergyCostSubsequent() { return temporaryEvolutionEnergyCostSubsequent; }

    public void setTemporaryEvolutionEnergyCostSubsequent(Integer temporaryEvolutionEnergyCostSubsequent) { this.temporaryEvolutionEnergyCostSubsequent = temporaryEvolutionEnergyCostSubsequent; }

    public String getEvolutionItemRequirement() { return evolutionItemRequirement; }

    public void setEvolutionItemRequirement(String evolutionItemRequirement) { this.evolutionItemRequirement = evolutionItemRequirement; }

    public Boolean getNoCandyCostViaTrade() { return noCandyCostViaTrade = false; }

    public void setNoCandyCostViaTrade(Boolean noCandyCostViaTrade) { this.noCandyCostViaTrade = noCandyCostViaTrade = false; }

    public String getLureItemRequirement() { return lureItemRequirement; }

    public void setLureItemRequirement(String lureItemRequirement) { this.lureItemRequirement = lureItemRequirement; }

    public List<QuestDisplay> getQuestDisplay() { return questDisplay; }

    public void setQuestDisplay(
            List<QuestDisplay> questDisplay) { this.questDisplay = questDisplay; }

    public Double getKmBuddyDistanceRequirement() { return kmBuddyDistanceRequirement; }

    public void setKmBuddyDistanceRequirement(Double kmBuddyDistanceRequirement) { this.kmBuddyDistanceRequirement = kmBuddyDistanceRequirement; }

    public Boolean getMustBeBuddy() { return mustBeBuddy; }

    public void setMustBeBuddy(Boolean mustBeBuddy) { this.mustBeBuddy = mustBeBuddy; }

    public Boolean getOnlyDaytime() { return onlyDaytime; }

    public void setOnlyDaytime(Boolean onlyDaytime) { this.onlyDaytime = onlyDaytime; }

    public Integer getPriority() { return priority; }

    public void setPriority(Integer priority) { this.priority = priority; }

    public Boolean getOnlyNighttime() { return onlyNighttime; }

    public void setOnlyNighttime(Boolean onlyNighttime) { this.onlyNighttime = onlyNighttime; }

    public String getGenderRequirement() { return genderRequirement; }

    public void setGenderRequirement(String genderRequirement) { this.genderRequirement = genderRequirement; }
}

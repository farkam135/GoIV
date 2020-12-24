package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class BuddyEncounterCameoSettings {
    @Expose
    private Double buddyWildEncounterCameoChancePercent;
    @Expose
    private Double buddyQuestEncounterCameoChancePercent;
    @Expose
    private Double buddyRaidEncounterCameoChancePercent;
    @Expose
    private Double buddyInvasionEncounterCameoChancePercent;

    public Double getBuddyWildEncounterCameoChancePercent() { return buddyWildEncounterCameoChancePercent; }

    public void setBuddyWildEncounterCameoChancePercent(Double buddyWildEncounterCameoChancePercent) { this.buddyWildEncounterCameoChancePercent = buddyWildEncounterCameoChancePercent; }

    public Double getBuddyQuestEncounterCameoChancePercent() { return buddyQuestEncounterCameoChancePercent; }

    public void setBuddyQuestEncounterCameoChancePercent(Double buddyQuestEncounterCameoChancePercent) { this.buddyQuestEncounterCameoChancePercent = buddyQuestEncounterCameoChancePercent; }

    public Double getBuddyRaidEncounterCameoChancePercent() { return buddyRaidEncounterCameoChancePercent; }

    public void setBuddyRaidEncounterCameoChancePercent(Double buddyRaidEncounterCameoChancePercent) { this.buddyRaidEncounterCameoChancePercent = buddyRaidEncounterCameoChancePercent; }

    public Double getBuddyInvasionEncounterCameoChancePercent() { return buddyInvasionEncounterCameoChancePercent; }

    public void setBuddyInvasionEncounterCameoChancePercent(Double buddyInvasionEncounterCameoChancePercent) { this.buddyInvasionEncounterCameoChancePercent = buddyInvasionEncounterCameoChancePercent; }
}

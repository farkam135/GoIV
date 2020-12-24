package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class CombatLeagueSettings {
    @Expose
    private List<String> combatLeagueTemplateId = null;

    public List<String> getCombatLeagueTemplateId() { return combatLeagueTemplateId; }

    public void setCombatLeagueTemplateId(List<String> combatLeagueTemplateId) { this.combatLeagueTemplateId = combatLeagueTemplateId; }
}

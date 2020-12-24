package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class BattleHubBadgeSettings {
    @Expose
    private List<String> combatHubDisplayedBadges = null;

    public List<String> getCombatHubDisplayedBadges() { return combatHubDisplayedBadges; }

    public void setCombatHubDisplayedBadges(List<String> combatHubDisplayedBadges) { this.combatHubDisplayedBadges = combatHubDisplayedBadges; }
}

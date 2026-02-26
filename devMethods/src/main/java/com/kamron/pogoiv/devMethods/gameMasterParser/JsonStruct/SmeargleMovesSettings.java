package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class SmeargleMovesSettings {
    @Expose
    private List<String> quickMoves = null;
    @Expose
    private List<String> cinematicMoves = null;

    public List<String> getQuickMoves() { return quickMoves; }

    public void setQuickMoves(List<String> quickMoves) { this.quickMoves = quickMoves; }

    public List<String> getCinematicMoves() { return cinematicMoves; }

    public void setCinematicMoves(List<String> cinematicMoves) { this.cinematicMoves = cinematicMoves; }
}

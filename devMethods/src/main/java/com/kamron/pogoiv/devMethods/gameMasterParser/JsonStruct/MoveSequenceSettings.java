package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class MoveSequenceSettings {
    @Expose
    private List<String> sequence = null;

    public List<String> getSequence() { return sequence; }

    public void setSequence(List<String> sequence) { this.sequence = sequence; }
}

package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokemonCaughtTimestamp {
    @Expose
    private String afterTimestamp;

    public String getAfterTimestamp() { return afterTimestamp; }

    public void setAfterTimestamp(String afterTimestamp) { this.afterTimestamp = afterTimestamp; }
}

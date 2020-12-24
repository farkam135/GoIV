package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class WithPokemonCpLimit {
    @Expose
    private Integer minCp;
    @Expose
    private Integer maxCp;

    public Integer getMinCp() { return minCp; }

    public void setMinCp(Integer minCp) { this.minCp = minCp; }

    public Integer getMaxCp() { return maxCp; }

    public void setMaxCp(Integer maxCp) { this.maxCp = maxCp; }
}

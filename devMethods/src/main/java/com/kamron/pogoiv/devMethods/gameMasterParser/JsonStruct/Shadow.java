package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Shadow {
    @Expose
    private Integer purificationStardustNeeded;
    @Expose
    private Integer purificationCandyNeeded;
    @Expose
    private String purifiedChargeMove;
    @Expose
    private String shadowChargeMove;

    public Integer getPurificationStardustNeeded() { return purificationStardustNeeded; }

    public void setPurificationStardustNeeded(Integer purificationStardustNeeded) {
        this.purificationStardustNeeded = purificationStardustNeeded;
    }

    public Integer getPurificationCandyNeeded() { return purificationCandyNeeded; }

    public void setPurificationCandyNeeded(Integer purificationCandyNeeded) {
        this.purificationCandyNeeded = purificationCandyNeeded;
    }

    public String getPurifiedChargeMove() { return purifiedChargeMove; }

    public void setPurifiedChargeMove(String purifiedChargeMove) { this.purifiedChargeMove = purifiedChargeMove; }

    public String getShadowChargeMove() { return shadowChargeMove; }

    public void setShadowChargeMove(String shadowChargeMove) { this.shadowChargeMove = shadowChargeMove; }
}


package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GymLevel {

    @SerializedName("requiredExperience")
    @Expose
    private List<Integer> requiredExperience = null;
    @SerializedName("leaderSlots")
    @Expose
    private List<Integer> leaderSlots = null;
    @SerializedName("trainerSlots")
    @Expose
    private List<Integer> trainerSlots = null;

    public List<Integer> getRequiredExperience() {
        return requiredExperience;
    }

    public void setRequiredExperience(List<Integer> requiredExperience) {
        this.requiredExperience = requiredExperience;
    }

    public List<Integer> getLeaderSlots() {
        return leaderSlots;
    }

    public void setLeaderSlots(List<Integer> leaderSlots) {
        this.leaderSlots = leaderSlots;
    }

    public List<Integer> getTrainerSlots() {
        return trainerSlots;
    }

    public void setTrainerSlots(List<Integer> trainerSlots) {
        this.trainerSlots = trainerSlots;
    }

}

package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class PokestopInvasionAvailabilitySettings {
    @Expose
    private String availabilityStartMinute;
    @Expose
    private String availabilityEndMinute;

    public String getAvailabilityStartMinute() { return availabilityStartMinute; }

    public void setAvailabilityStartMinute(String availabilityStartMinute) { this.availabilityStartMinute = availabilityStartMinute; }

    public String getAvailabilityEndMinute() { return availabilityEndMinute; }

    public void setAvailabilityEndMinute(String availabilityEndMinute) { this.availabilityEndMinute = availabilityEndMinute; }
}

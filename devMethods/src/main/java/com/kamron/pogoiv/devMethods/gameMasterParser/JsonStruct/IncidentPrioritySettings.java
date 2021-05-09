package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class IncidentPrioritySettings {
    @Expose
    private List<IncidentPriority> incidentPriority;

    public List<IncidentPriority> getIncidentPriority() {
        return incidentPriority;
    }

    public void setIncidentPriority(
            List<IncidentPriority> incidentPriority) {
        this.incidentPriority = incidentPriority;
    }
}

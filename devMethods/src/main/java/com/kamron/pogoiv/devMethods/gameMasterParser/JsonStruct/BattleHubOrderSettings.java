package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BattleHubOrderSettings {
    @SerializedName("section")
    @Expose
    private List<Section> sections = null;
    @SerializedName("sectionGroup")
    @Expose
    private List<SectionGroup> sectionGroups = null;

    public List<Section> getSections() { return sections; }

    public void setSections(List<Section> sections) { this.sections = sections; }

    public List<SectionGroup> getSectionGroups() { return sectionGroups; }

    public void setSectionGroups(List<SectionGroup> sectionGroups) { this.sectionGroups = sectionGroups; }
}

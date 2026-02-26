package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Section {
    @Expose
    private String mainSection;
    @SerializedName("subsection")
    @Expose
    private List<String> subsections = null;

    public String getMainSection() { return mainSection; }

    public void setMainSection(String mainSection) { this.mainSection = mainSection; }

    public List<String> getSubsections() { return subsections; }

    public void setSubsections(List<String> subsections) { this.subsections = subsections; }
}

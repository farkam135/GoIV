package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SectionGroup {
    @SerializedName("subsection")
    @Expose
    private List<String> subsections = null;

    public List<String> getSubsections() { return subsections; }

    public void setSubsections(List<String> subsections) { this.subsections = subsections; }
}


package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PogoJson {
    @Expose
    private String result;
    @SerializedName("template")
    @Expose
    private List<Template> templates = null;
    @Expose
    private String batchId;
    @SerializedName("experimentId")
    @Expose
    private List<Integer> experimentIds = null;

    public String getResult() { return result; }

    public void setResult(String result) { this.result = result; }

    public List<Template> getTemplates() { return templates; }

    public void setTemplates(List<Template> templates) { this.templates = templates; }

    public String getBatchId() { return batchId; }

    public void setBatchId(String batchId) { this.batchId = batchId; }

    public List<Integer> getExperimentIds() { return experimentIds; }

    public void setExperimentIds(List<Integer> experimentIds) { this.experimentIds = experimentIds; }
}

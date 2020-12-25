package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Template {
    @Expose
    private String templateId;
    @Expose
    private Data data;

    public String getTemplateId() { return templateId; }

    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public Data getData() { return data; }

    public void setData(Data data) { this.data = data; }
}

package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class Group {
    @Expose
    private String name;
    @Expose
    private Integer order;

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Integer getOrder() { return order; }

    public void setOrder(Integer order) { this.order = order; }
}

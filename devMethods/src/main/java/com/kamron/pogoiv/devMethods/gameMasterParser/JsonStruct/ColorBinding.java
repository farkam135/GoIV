package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class ColorBinding {
    @Expose
    private String color;
    @Expose
    private String hexCode;

    public String getColor() { return color; }

    public void setColor(String color) { this.color = color; }

    public String getHexCode() { return hexCode; }

    public void setHexCode(String hexCode) { this.hexCode = hexCode; }
}

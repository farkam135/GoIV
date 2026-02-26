package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class LevelUpRewards {
    @Expose
    private Integer level;
    @Expose
    private List<String> items = null;
    @Expose
    private List<Integer> itemsCount = null;
    @Expose
    private List<String> itemsUnlocked = null;
    @Expose
    private List<String> avatarTemplateIds = null;

    public Integer getLevel() { return level; }

    public void setLevel(Integer level) { this.level = level; }

    public List<String> getItems() { return items; }

    public void setItems(List<String> items) { this.items = items; }

    public List<Integer> getItemsCount() { return itemsCount; }

    public void setItemsCount(List<Integer> itemsCount) { this.itemsCount = itemsCount; }

    public List<String> getItemsUnlocked() { return itemsUnlocked; }

    public void setItemsUnlocked(List<String> itemsUnlocked) { this.itemsUnlocked = itemsUnlocked; }

    public List<String> getAvatarTemplateIds() { return avatarTemplateIds; }

    public void setAvatarTemplateIds(List<String> avatarTemplateIds) { this.avatarTemplateIds = avatarTemplateIds; }
}

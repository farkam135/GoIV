package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;

public class AvatarCustomization {
    @Expose
    private Boolean enabled = false;
    @Expose
    private String avatarType;
    @Expose
    private List<String> slot = null;
    @Expose
    private String bundleName;
    @Expose
    private String assetName;
    @Expose
    private String groupName;
    @Expose
    private Integer sortOrder;
    @Expose
    private String unlockType;
    @Expose
    private String iconName;
    @Expose
    private String iapSku;
    @Expose
    private String unlockBadgeType;
    @Expose
    private Integer unlockBadgeLevel;
    @Expose
    private Integer unlockPlayerLevel;

    public Boolean getEnabled() { return enabled; }

    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getAvatarType() { return avatarType; }

    public void setAvatarType(String avatarType) { this.avatarType = avatarType; }

    public List<String> getSlot() { return slot; }

    public void setSlot(List<String> slot) { this.slot = slot; }

    public String getBundleName() { return bundleName; }

    public void setBundleName(String bundleName) { this.bundleName = bundleName; }

    public String getAssetName() { return assetName; }

    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getGroupName() { return groupName; }

    public void setGroupName(String groupName) { this.groupName = groupName; }

    public Integer getSortOrder() { return sortOrder; }

    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public String getUnlockType() { return unlockType; }

    public void setUnlockType(String unlockType) { this.unlockType = unlockType; }

    public String getIapSku() { return iapSku; }

    public void setIapSku(String iapSku) { this.iapSku = iapSku; }

    public String getIconName() { return iconName; }

    public void setIconName(String iconName) { this.iconName = iconName; }

    public String getUnlockBadgeType() { return unlockBadgeType; }

    public void setUnlockBadgeType(String unlockBadgeType) { this.unlockBadgeType = unlockBadgeType; }

    public Integer getUnlockBadgeLevel() { return unlockBadgeLevel; }

    public void setUnlockBadgeLevel(Integer unlockBadgeLevel) { this.unlockBadgeLevel = unlockBadgeLevel; }

    public Integer getUnlockPlayerLevel() { return unlockPlayerLevel; }

    public void setUnlockPlayerLevel(Integer unlockPlayerLevel) { this.unlockPlayerLevel = unlockPlayerLevel; }
}

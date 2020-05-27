
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvatarCustomization {

    @SerializedName("enabled")
    @Expose
    private Boolean enabled;
    @SerializedName("avatarType")
    @Expose
    private String avatarType;
    @SerializedName("slot")
    @Expose
    private List<String> slot = null;
    @SerializedName("bundleName")
    @Expose
    private String bundleName;
    @SerializedName("assetName")
    @Expose
    private String assetName;
    @SerializedName("groupName")
    @Expose
    private String groupName;
    @SerializedName("sortOrder")
    @Expose
    private Integer sortOrder;
    @SerializedName("unlockType")
    @Expose
    private String unlockType;
    @SerializedName("iapSku")
    @Expose
    private String iapSku;
    @SerializedName("iconName")
    @Expose
    private String iconName;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAvatarType() {
        return avatarType;
    }

    public void setAvatarType(String avatarType) {
        this.avatarType = avatarType;
    }

    public List<String> getSlot() {
        return slot;
    }

    public void setSlot(List<String> slot) {
        this.slot = slot;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getUnlockType() {
        return unlockType;
    }

    public void setUnlockType(String unlockType) {
        this.unlockType = unlockType;
    }

    public String getIapSku() {
        return iapSku;
    }

    public void setIapSku(String iapSku) {
        this.iapSku = iapSku;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

}

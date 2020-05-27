
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ItemTemplate {

    @SerializedName("templateId")
    @Expose
    private String templateId;
    @SerializedName("avatarCustomization")
    @Expose
    private AvatarCustomization avatarCustomization;
    @SerializedName("badgeSettings")
    @Expose
    private BadgeSettings badgeSettings;
    @SerializedName("formSettings")
    @Expose
    private FormSettings formSettings;
    @SerializedName("friendshipMilestoneSettings")
    @Expose
    private FriendshipMilestoneSettings friendshipMilestoneSettings;
    @SerializedName("gymBadgeSettings")
    @Expose
    private GymBadgeSettings gymBadgeSettings;
    @SerializedName("gymLevel")
    @Expose
    private GymLevel gymLevel;
    @SerializedName("iapCategoryDisplay")
    @Expose
    private IapCategoryDisplay iapCategoryDisplay;
    @SerializedName("iapSettings")
    @Expose
    private IapSettings iapSettings;
    @SerializedName("itemSettings")
    @Expose
    private ItemSettings itemSettings;
    @SerializedName("luckyPokemonSettings")
    @Expose
    private LuckyPokemonSettings luckyPokemonSettings;
    @SerializedName("playerLevel")
    @Expose
    private PlayerLevel playerLevel;
    @SerializedName("pokemonScaleSettings")
    @Expose
    private PokemonScaleSettings pokemonScaleSettings;
    @SerializedName("typeEffective")
    @Expose
    private TypeEffective typeEffective;
    @SerializedName("pokemonUpgrades")
    @Expose
    private PokemonUpgrades pokemonUpgrades;
    @SerializedName("questSettings")
    @Expose
    private QuestSettings questSettings;
    @SerializedName("genderSettings")
    @Expose
    private GenderSettings genderSettings;
    @SerializedName("pokemon")
    @Expose
    private PokemonSettings pokemonSettings;
    @SerializedName("weatherAffinities")
    @Expose
    private WeatherAffinities weatherAffinities;
    @SerializedName("weatherBonusSettings")
    @Expose
    private WeatherBonusSettings weatherBonusSettings;
    @SerializedName("iapItemDisplay")
    @Expose
    private IapItemDisplay iapItemDisplay;
    @SerializedName("camera")
    @Expose
    private Camera_ camera;
    @SerializedName("moveSequence")
    @Expose
    private MoveSequenceSettings moveSequenceSettings;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public AvatarCustomization getAvatarCustomization() {
        return avatarCustomization;
    }

    public void setAvatarCustomization(AvatarCustomization avatarCustomization) {
        this.avatarCustomization = avatarCustomization;
    }

    public BadgeSettings getBadgeSettings() {
        return badgeSettings;
    }

    public void setBadgeSettings(BadgeSettings badgeSettings) {
        this.badgeSettings = badgeSettings;
    }

    public FormSettings getFormSettings() {
        return formSettings;
    }

    public void setFormSettings(FormSettings formSettings) {
        this.formSettings = formSettings;
    }

    public FriendshipMilestoneSettings getFriendshipMilestoneSettings() {
        return friendshipMilestoneSettings;
    }

    public void setFriendshipMilestoneSettings(FriendshipMilestoneSettings friendshipMilestoneSettings) {
        this.friendshipMilestoneSettings = friendshipMilestoneSettings;
    }

    public GymBadgeSettings getGymBadgeSettings() {
        return gymBadgeSettings;
    }

    public void setGymBadgeSettings(GymBadgeSettings gymBadgeSettings) {
        this.gymBadgeSettings = gymBadgeSettings;
    }

    public GymLevel getGymLevel() {
        return gymLevel;
    }

    public void setGymLevel(GymLevel gymLevel) {
        this.gymLevel = gymLevel;
    }

    public IapCategoryDisplay getIapCategoryDisplay() {
        return iapCategoryDisplay;
    }

    public void setIapCategoryDisplay(IapCategoryDisplay iapCategoryDisplay) {
        this.iapCategoryDisplay = iapCategoryDisplay;
    }

    public IapSettings getIapSettings() {
        return iapSettings;
    }

    public void setIapSettings(IapSettings iapSettings) {
        this.iapSettings = iapSettings;
    }

    public ItemSettings getItemSettings() {
        return itemSettings;
    }

    public void setItemSettings(ItemSettings itemSettings) {
        this.itemSettings = itemSettings;
    }

    public LuckyPokemonSettings getLuckyPokemonSettings() {
        return luckyPokemonSettings;
    }

    public void setLuckyPokemonSettings(LuckyPokemonSettings luckyPokemonSettings) {
        this.luckyPokemonSettings = luckyPokemonSettings;
    }

    public PlayerLevel getPlayerLevel() {
        return playerLevel;
    }

    public void setPlayerLevel(PlayerLevel playerLevel) {
        this.playerLevel = playerLevel;
    }

    public PokemonScaleSettings getPokemonScaleSettings() {
        return pokemonScaleSettings;
    }

    public void setPokemonScaleSettings(PokemonScaleSettings pokemonScaleSettings) {
        this.pokemonScaleSettings = pokemonScaleSettings;
    }

    public TypeEffective getTypeEffective() {
        return typeEffective;
    }

    public void setTypeEffective(TypeEffective typeEffective) {
        this.typeEffective = typeEffective;
    }

    public PokemonUpgrades getPokemonUpgrades() {
        return pokemonUpgrades;
    }

    public void setPokemonUpgrades(PokemonUpgrades pokemonUpgrades) {
        this.pokemonUpgrades = pokemonUpgrades;
    }

    public QuestSettings getQuestSettings() {
        return questSettings;
    }

    public void setQuestSettings(QuestSettings questSettings) {
        this.questSettings = questSettings;
    }

    public GenderSettings getGenderSettings() {
        return genderSettings;
    }

    public void setGenderSettings(GenderSettings genderSettings) {
        this.genderSettings = genderSettings;
    }

    public PokemonSettings getPokemonSettings() {
        return pokemonSettings;
    }

    public void setPokemonSettings(PokemonSettings pokemonSettings) {
        this.pokemonSettings = pokemonSettings;
    }

    public WeatherAffinities getWeatherAffinities() {
        return weatherAffinities;
    }

    public void setWeatherAffinities(WeatherAffinities weatherAffinities) {
        this.weatherAffinities = weatherAffinities;
    }

    public WeatherBonusSettings getWeatherBonusSettings() {
        return weatherBonusSettings;
    }

    public void setWeatherBonusSettings(WeatherBonusSettings weatherBonusSettings) {
        this.weatherBonusSettings = weatherBonusSettings;
    }

    public IapItemDisplay getIapItemDisplay() {
        return iapItemDisplay;
    }

    public void setIapItemDisplay(IapItemDisplay iapItemDisplay) {
        this.iapItemDisplay = iapItemDisplay;
    }

    public Camera_ getCamera() {
        return camera;
    }

    public void setCamera(Camera_ camera) {
        this.camera = camera;
    }

    public MoveSequenceSettings getMoveSequenceSettings() {
        return moveSequenceSettings;
    }

    public void setMoveSequenceSettings(MoveSequenceSettings moveSequenceSettings) {
        this.moveSequenceSettings = moveSequenceSettings;
    }

}

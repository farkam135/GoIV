
package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PokemonSettings {

    public String getTemplateID() {
        return templateID;
    }

    public void setTemplateID(String templateID) {
        this.templateID = templateID;
    }

    private String templateID;

    @SerializedName("pokemonId")
    @Expose
    private String pokemonId;
    @SerializedName("modelScale")
    @Expose
    private Double modelScale;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("type2")
    @Expose
    private String type2;
    @SerializedName("camera")
    @Expose
    private Camera camera;
    @SerializedName("encounter")
    @Expose
    private Encounter encounter;
    @SerializedName("stats")
    @Expose
    private Stats stats;
    @SerializedName("quickMoves")
    @Expose
    private List<String> quickMoves = null;
    @SerializedName("cinematicMoves")
    @Expose
    private List<String> cinematicMoves = null;
    @SerializedName("animationTime")
    @Expose
    private List<Double> animationTime = null;
    @SerializedName("evolutionIds")
    @Expose
    private List<String> evolutionIds = null;
    @SerializedName("evolutionPips")
    @Expose
    private Integer evolutionPips;
    @SerializedName("pokedexHeightM")
    @Expose
    private Double pokedexHeightM;
    @SerializedName("pokedexWeightKg")
    @Expose
    private Double pokedexWeightKg;
    @SerializedName("heightStdDev")
    @Expose
    private Double heightStdDev;
    @SerializedName("weightStdDev")
    @Expose
    private Double weightStdDev;
    @SerializedName("familyId")
    @Expose
    private String familyId;
    @SerializedName("candyToEvolve")
    @Expose
    private Integer candyToEvolve;
    @SerializedName("kmBuddyDistance")
    @Expose
    private Double kmBuddyDistance;
    @SerializedName("modelHeight")
    @Expose
    private Double modelHeight;
    @SerializedName("evolutionBranch")
    @Expose
    private List<EvolutionBranch> evolutionBranch = null;
    @SerializedName("modelScaleV2")
    @Expose
    private Double modelScaleV2;
    @SerializedName("buddyOffsetMale")
    @Expose
    private List<Double> buddyOffsetMale = null;
    @SerializedName("buddyOffsetFemale")
    @Expose
    private List<Double> buddyOffsetFemale = null;
    @SerializedName("buddyScale")
    @Expose
    private Double buddyScale;
    @SerializedName("buddyPortraitOffset")
    @Expose
    private List<Double> buddyPortraitOffset = null;
    @SerializedName("isTransferable")
    @Expose
    private Boolean isTransferable;
    @SerializedName("isDeployable")
    @Expose
    private Boolean isDeployable;
    @SerializedName("rarity")
    @Expose
    private String rarity;
    @SerializedName("form")
    @Expose
    private String form;


    @SerializedName("parentPokemonId")
    @Expose
    private String parentPokemonId;

    public String getParentPokemonId() {
        return parentPokemonId;
    }

    public void setParentPokemonId(String parentPokemonId) {
        this.parentPokemonId = parentPokemonId;
    }

    public String getPokemonId() {

        return pokemonId;
    }

    public void setPokemonId(String pokemonId) {
        this.pokemonId = pokemonId;
    }

    public Double getModelScale() {
        return modelScale;
    }

    public void setModelScale(Double modelScale) {
        this.modelScale = modelScale;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Encounter getEncounter() {
        return encounter;
    }

    public void setEncounter(Encounter encounter) {
        this.encounter = encounter;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    public List<String> getQuickMoves() {
        return quickMoves;
    }

    public void setQuickMoves(List<String> quickMoves) {
        this.quickMoves = quickMoves;
    }

    public List<String> getCinematicMoves() {
        return cinematicMoves;
    }

    public void setCinematicMoves(List<String> cinematicMoves) {
        this.cinematicMoves = cinematicMoves;
    }

    public List<Double> getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(List<Double> animationTime) {
        this.animationTime = animationTime;
    }

    public List<String> getEvolutionIds() {
        return evolutionIds;
    }

    public void setEvolutionIds(List<String> evolutionIds) {
        this.evolutionIds = evolutionIds;
    }

    public Integer getEvolutionPips() {
        return evolutionPips;
    }

    public void setEvolutionPips(Integer evolutionPips) {
        this.evolutionPips = evolutionPips;
    }

    public Double getPokedexHeightM() {
        return pokedexHeightM;
    }

    public void setPokedexHeightM(Double pokedexHeightM) {
        this.pokedexHeightM = pokedexHeightM;
    }

    public Double getPokedexWeightKg() {
        return pokedexWeightKg;
    }

    public void setPokedexWeightKg(Double pokedexWeightKg) {
        this.pokedexWeightKg = pokedexWeightKg;
    }

    public Double getHeightStdDev() {
        return heightStdDev;
    }

    public void setHeightStdDev(Double heightStdDev) {
        this.heightStdDev = heightStdDev;
    }

    public Double getWeightStdDev() {
        return weightStdDev;
    }

    public void setWeightStdDev(Double weightStdDev) {
        this.weightStdDev = weightStdDev;
    }

    public String getFamilyId() {
        return familyId;
    }

    public void setFamilyId(String familyId) {
        this.familyId = familyId;
    }

    public Integer getCandyToEvolve() {
        return candyToEvolve;
    }

    public void setCandyToEvolve(Integer candyToEvolve) {
        this.candyToEvolve = candyToEvolve;
    }

    public Double getKmBuddyDistance() {
        return kmBuddyDistance;
    }

    public void setKmBuddyDistance(Double kmBuddyDistance) {
        this.kmBuddyDistance = kmBuddyDistance;
    }

    public Double getModelHeight() {
        return modelHeight;
    }

    public void setModelHeight(Double modelHeight) {
        this.modelHeight = modelHeight;
    }

    public List<EvolutionBranch> getEvolutionBranch() {
        return evolutionBranch;
    }

    public void setEvolutionBranch(List<EvolutionBranch> evolutionBranch) {
        this.evolutionBranch = evolutionBranch;
    }

    public Double getModelScaleV2() {
        return modelScaleV2;
    }

    public void setModelScaleV2(Double modelScaleV2) {
        this.modelScaleV2 = modelScaleV2;
    }

    public List<Double> getBuddyOffsetMale() {
        return buddyOffsetMale;
    }

    public void setBuddyOffsetMale(List<Double> buddyOffsetMale) {
        this.buddyOffsetMale = buddyOffsetMale;
    }

    public List<Double> getBuddyOffsetFemale() {
        return buddyOffsetFemale;
    }

    public void setBuddyOffsetFemale(List<Double> buddyOffsetFemale) {
        this.buddyOffsetFemale = buddyOffsetFemale;
    }

    public Double getBuddyScale() {
        return buddyScale;
    }

    public void setBuddyScale(Double buddyScale) {
        this.buddyScale = buddyScale;
    }

    public List<Double> getBuddyPortraitOffset() {
        return buddyPortraitOffset;
    }

    public void setBuddyPortraitOffset(List<Double> buddyPortraitOffset) {
        this.buddyPortraitOffset = buddyPortraitOffset;
    }

    public Boolean getIsTransferable() {
        return isTransferable;
    }

    public void setIsTransferable(Boolean isTransferable) {
        this.isTransferable = isTransferable;
    }

    public Boolean getIsDeployable() {
        return isDeployable;
    }

    public void setIsDeployable(Boolean isDeployable) {
        this.isDeployable = isDeployable;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getForm() {
        return form;
    }

    public void setForm(String form) {
        this.form = form;
    }

}

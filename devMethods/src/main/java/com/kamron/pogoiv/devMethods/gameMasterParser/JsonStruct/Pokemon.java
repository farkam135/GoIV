package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Pokemon {
    // Additional Field for the ApplicationDatabaseUpdate logic
    private String templateId;

    public String getTemplateId() { return templateId; }

    public void setTemplateId(String templateId) { this.templateId = templateId; }

    @Expose
    private String uniqueId;
    @Expose
    private Double modelScale;
    @Expose
    private String type1;
    @Expose
    private String type2;
    @SerializedName("camera")
    @Expose
    private CameraData cameraData;
    @Expose
    private Encounter encounter;
    @Expose
    private Stats stats;
    @Expose
    private List<String> quickMoves = null;
    @Expose
    private List<String> cinematicMoves = null;
    @SerializedName("animTime")
    @Expose
    private List<Double> animationTime = null;
    @SerializedName("evolution")
    @Expose
    private List<String> evolutionIds = null;
    @Expose
    private Integer evolutionPips;
    @Expose
    private Double pokedexHeightM;
    @Expose
    private Double pokedexWeightKg;
    @Expose
    private Double heightStdDev;
    @Expose
    private Double weightStdDev;
    @Expose
    private String familyId;
    @Expose
    private Integer candyToEvolve;
    @Expose
    private Double kmBuddyDistance;
    @Expose
    private Double modelHeight;
    @SerializedName("evolutionBranch")
    @Expose
    private List<EvolutionBranch> evolutionBranches = null;
    @Expose
    private Double modelScaleV2;
    @Expose
    private List<Double> buddyOffsetMale = null;
    @Expose
    private List<Double> buddyOffsetFemale = null;
    @Expose
    private Double buddyScale;
    @Expose
    private ThirdMove thirdMove;
    @Expose
    private Boolean isTransferable = false;
    @Expose
    private Boolean isDeployable = false;
    @Expose
    private Integer buddyGroupNumber;
    @Expose
    private Integer buddyWalkedMegaEnergyAward;
    @Expose
    private String form;
    @Expose
    private Boolean disableTransferToPokemonHome = false;
    @Expose
    private Shadow shadow;
    @Expose
    private String parentId;
    @Expose
    private String buddySize;
    @Expose
    private List<Double> combatShoulderCameraAngle = null;
    @Expose
    private List<Double> combatDefaultCameraAngle = null;
    @Expose
    private List<Double> combatPlayerFocusCameraAngle = null;
    @SerializedName("eliteCinematicMove")
    @Expose
    private List<String> eliteCinematicMoves = null;
    @Expose
    private List<TempEvoOverride> tempEvoOverrides = null;
    @SerializedName("eliteQuickMove")
    @Expose
    private List<String> eliteQuickMoves = null;
    @Expose
    private List<Double> buddyPortraitOffset = null;
    @Expose
    private Double raidBossDistanceOffset;
    @Expose
    private List<Double> combatPlayerPokemonPositionOffset = null;
    @SerializedName("pokemonClass")
    @Expose
    private String rarity;
    @Expose
    private List<Double> combatOpponentFocusCameraAngle = null;

    public String getUniqueId() { return uniqueId; }

    public void setUniqueId(String uniqueId) { this.uniqueId = uniqueId; }

    public Double getModelScale() { return modelScale; }

    public void setModelScale(Double modelScale) { this.modelScale = modelScale; }

    public String getType1() { return type1; }

    public void setType1(String type1) { this.type1 = type1; }

    public String getType2() { return type2; }

    public void setType2(String type2) { this.type2 = type2; }

    public CameraData getCameraData() { return cameraData; }

    public void setCameraData(CameraData cameraData) { this.cameraData = cameraData; }

    public Encounter getEncounter() { return encounter; }

    public void setEncounter(Encounter encounter) { this.encounter = encounter; }

    public Stats getStats() { return stats; }

    public void setStats(Stats stats) { this.stats = stats; }

    public List<String> getQuickMoves() { return quickMoves; }

    public void setQuickMoves(List<String> quickMoves) { this.quickMoves = quickMoves; }

    public List<String> getCinematicMoves() { return cinematicMoves; }

    public void setCinematicMoves(List<String> cinematicMoves) { this.cinematicMoves = cinematicMoves; }

    public List<Double> getAnimationTime() { return animationTime; }

    public void setAnimationTime(List<Double> animationTime) { this.animationTime = animationTime; }

    public List<String> getEvolutionIds() { return evolutionIds; }

    public void setEvolutionIds(List<String> evolutionIds) { this.evolutionIds = evolutionIds; }

    public Integer getEvolutionPips() { return evolutionPips; }

    public void setEvolutionPips(Integer evolutionPips) { this.evolutionPips = evolutionPips; }

    public Double getPokedexHeightM() { return pokedexHeightM; }

    public void setPokedexHeightM(Double pokedexHeightM) { this.pokedexHeightM = pokedexHeightM; }

    public Double getPokedexWeightKg() { return pokedexWeightKg; }

    public void setPokedexWeightKg(Double pokedexWeightKg) { this.pokedexWeightKg = pokedexWeightKg; }

    public Double getHeightStdDev() { return heightStdDev; }

    public void setHeightStdDev(Double heightStdDev) { this.heightStdDev = heightStdDev; }

    public Double getWeightStdDev() { return weightStdDev; }

    public void setWeightStdDev(Double weightStdDev) { this.weightStdDev = weightStdDev; }

    public String getFamilyId() { return familyId; }

    public void setFamilyId(String familyId) { this.familyId = familyId; }

    public Integer getCandyToEvolve() { return candyToEvolve; }

    public void setCandyToEvolve(Integer candyToEvolve) { this.candyToEvolve = candyToEvolve; }

    public Double getKmBuddyDistance() { return kmBuddyDistance; }

    public void setKmBuddyDistance(Double kmBuddyDistance) { this.kmBuddyDistance = kmBuddyDistance; }

    public Double getModelHeight() { return modelHeight; }

    public void setModelHeight(Double modelHeight) { this.modelHeight = modelHeight; }

    public List<EvolutionBranch> getEvolutionBranches() { return evolutionBranches; }

    public void setEvolutionBranches(
            List<EvolutionBranch> evolutionBranches) { this.evolutionBranches = evolutionBranches; }

    public Double getModelScaleV2() { return modelScaleV2; }

    public void setModelScaleV2(Double modelScaleV2) { this.modelScaleV2 = modelScaleV2; }

    public List<Double> getBuddyOffsetMale() { return buddyOffsetMale; }

    public void setBuddyOffsetMale(List<Double> buddyOffsetMale) { this.buddyOffsetMale = buddyOffsetMale; }

    public List<Double> getBuddyOffsetFemale() { return buddyOffsetFemale; }

    public void setBuddyOffsetFemale(List<Double> buddyOffsetFemale) { this.buddyOffsetFemale = buddyOffsetFemale; }

    public Double getBuddyScale() { return buddyScale; }

    public void setBuddyScale(Double buddyScale) { this.buddyScale = buddyScale; }

    public ThirdMove getThirdMove() { return thirdMove; }

    public void setThirdMove(ThirdMove thirdMove) { this.thirdMove = thirdMove; }

    public Boolean getTransferable() { return isTransferable; }

    public void setTransferable(Boolean transferable) { isTransferable = transferable; }

    public Boolean getDeployable() { return isDeployable; }

    public void setDeployable(Boolean deployable) { isDeployable = deployable; }

    public Integer getBuddyGroupNumber() { return buddyGroupNumber; }

    public void setBuddyGroupNumber(Integer buddyGroupNumber) { this.buddyGroupNumber = buddyGroupNumber; }

    public Integer getBuddyWalkedMegaEnergyAward() { return buddyWalkedMegaEnergyAward; }

    public void setBuddyWalkedMegaEnergyAward(Integer buddyWalkedMegaEnergyAward) { this.buddyWalkedMegaEnergyAward = buddyWalkedMegaEnergyAward; }

    public String getForm() { return form; }

    public void setForm(String form) { this.form = form; }

    public Boolean getDisableTransferToPokemonHome() { return disableTransferToPokemonHome; }

    public void setDisableTransferToPokemonHome(Boolean disableTransferToPokemonHome) { this.disableTransferToPokemonHome = disableTransferToPokemonHome; }

    public Shadow getShadow() { return shadow; }

    public void setShadow(Shadow shadow) { this.shadow = shadow; }

    public String getParentId() { return parentId; }

    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getBuddySize() { return buddySize; }

    public void setBuddySize(String buddySize) { this.buddySize = buddySize; }

    public List<Double> getCombatShoulderCameraAngle() { return combatShoulderCameraAngle; }

    public void setCombatShoulderCameraAngle(List<Double> combatShoulderCameraAngle) { this.combatShoulderCameraAngle = combatShoulderCameraAngle; }

    public List<Double> getCombatDefaultCameraAngle() { return combatDefaultCameraAngle; }

    public void setCombatDefaultCameraAngle(List<Double> combatDefaultCameraAngle) { this.combatDefaultCameraAngle = combatDefaultCameraAngle; }

    public List<Double> getCombatPlayerFocusCameraAngle() { return combatPlayerFocusCameraAngle; }

    public void setCombatPlayerFocusCameraAngle(List<Double> combatPlayerFocusCameraAngle) { this.combatPlayerFocusCameraAngle = combatPlayerFocusCameraAngle; }

    public List<String> getEliteCinematicMoves() { return eliteCinematicMoves; }

    public void setEliteCinematicMoves(List<String> eliteCinematicMoves) { this.eliteCinematicMoves = eliteCinematicMoves; }

    public List<TempEvoOverride> getTempEvoOverrides() { return tempEvoOverrides; }

    public void setTempEvoOverrides(List<TempEvoOverride> tempEvoOverrides) { this.tempEvoOverrides = tempEvoOverrides; }

    public List<String> getEliteQuickMoves() { return eliteQuickMoves; }

    public void setEliteQuickMoves(List<String> eliteQuickMoves) { this.eliteQuickMoves = eliteQuickMoves; }

    public List<Double> getBuddyPortraitOffset() { return buddyPortraitOffset; }

    public void setBuddyPortraitOffset(List<Double> buddyPortraitOffset) { this.buddyPortraitOffset = buddyPortraitOffset; }

    public Double getRaidBossDistanceOffset() { return raidBossDistanceOffset; }

    public void setRaidBossDistanceOffset(Double raidBossDistanceOffset) { this.raidBossDistanceOffset = raidBossDistanceOffset; }

    public List<Double> getCombatPlayerPokemonPositionOffset() { return combatPlayerPokemonPositionOffset; }

    public void setCombatPlayerPokemonPositionOffset(List<Double> combatPlayerPokemonPositionOffset) { this.combatPlayerPokemonPositionOffset = combatPlayerPokemonPositionOffset; }

    public String getRarity() { return rarity; }

    public void setRarity(String rarity) { this.rarity = rarity; }

    public List<Double> getCombatOpponentFocusCameraAngle() { return combatOpponentFocusCameraAngle; }

    public void setCombatOpponentFocusCameraAngle(List<Double> combatOpponentFocusCameraAngle) { this.combatOpponentFocusCameraAngle = combatOpponentFocusCameraAngle; }
}

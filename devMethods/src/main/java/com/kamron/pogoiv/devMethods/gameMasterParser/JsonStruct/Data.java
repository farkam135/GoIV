package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {
    @Expose
    private String templateId;

    // Of these, only one should appear.
    // TODO: is there really no way to clean this up with polymorphism?
    @Expose
    private ArTelemetrySettings arTelemetrySettings;
    @Expose
    private AvatarGroupOrderSettings avatarGroupOrderSettings;
    @Expose
    private AvatarCustomization avatarCustomization;
    @Expose
    private LevelUpRewards levelUpRewardSettings;
    @Expose
    private BackgroundModeSettings backgroundModeSettings;
    @Expose
    private Badge badgeSettings;
    @Expose
    private BattleHubBadgeSettings battleHubBadgeSettings;
    @Expose
    private BattleHubOrderSettings battleHubOrderSettings;
    @Expose
    private BattleSettings battleSettings;
    @Expose
    private BelugaPokemonWhitelist belugaPokemonWhitelist;
    @Expose
    private BuddyActivitySettings buddyActivitySettings;
    @Expose
    private BuddyActivityCategorySettings buddyActivityCategorySettings;
    @Expose
    private BuddyEmotionLevelSettings buddyEmotionLevelSettings;
    @Expose
    private BuddyEncounterCameoSettings buddyEncounterCameoSettings;
    @Expose
    private BuddyHungerSettings buddyHungerSettings;
    @Expose
    private BuddyInteractionSettings buddyInteractionSettings;
    @Expose
    private BuddyLevelSettings buddyLevelSettings;
    @Expose
    private BuddySwapSettings buddySwapSettings;
    @Expose
    private BuddyWalkSettings buddyWalkSettings;
    @Expose
    private InvasionNpcDisplaySettings invasionNpcDisplaySettings;
    @Expose
    private CombatCompetitiveSeasonSettings combatCompetitiveSeasonSettings;
    @Expose
    private CombatLeague combatLeague;
    @Expose
    private CombatLeagueSettings combatLeagueSettings;
    @Expose
    private CombatType combatType;
    @Expose
    private CombatRankingProtoSettings combatRankingProtoSettings;
    @Expose
    private CombatSettings combatSettings;
    @Expose
    private CombatStatStageSettings combatStatStageSettings;
    @Expose
    private CombatMove combatMove;
    @Expose
    private CrossGameSocialSettings crossGameSocialSettings;
    @Expose
    private FriendProfileSettings friendProfileSettings;
    @Expose
    private EncounterSettings encounterSettings;
    @Expose
    private PokemonHomeEnergyCosts pokemonHomeEnergyCosts;
    @Expose
    private ExRaidSettings exRaidSettings;
    @Expose
    private FormSettings formSettings;
    @Expose
    private FriendshipMilestoneSettings friendshipMilestoneSettings;
    @Expose
    private GeotargetedQuestSettings geotargetedQuestSettings;
    @Expose
    private GuiSearchSettings guiSearchSettings;
    @Expose
    private GymBadgeSettings gymBadgeSettings;
    @Expose
    private GymLevel gymLevel;
    @Expose
    private IapCategoryDisplay iapCategoryDisplay;
    @Expose
    private IapSettings iapSettings;
    @Expose
    private PokestopInvasionAvailabilitySettings pokestopInvasionAvailabilitySettings;
    @Expose
    private InventorySettings inventorySettings;
    @Expose
    private ItemSettings itemSettings;
    @Expose
    private LimitedPurchaseSkuSettings limitedPurchaseSkuSettings;
    @Expose
    private LuckyPokemonSettings luckyPokemonSettings;
    @Expose
    private MapDisplaySettings mapDisplaySettings;
    @Expose
    private MegaEvoSettings megaEvoSettings;
    @Expose
    private MonodepthSettings monodepthSettings;
    @Expose
    private OnboardingV2Settings onboardingV2Settings;
    @Expose
    private PartyRecommendationSettings partyRecommendationSettings;
    @Expose
    private PlatypusRolloutSettings platypusRolloutSettings;
    @Expose
    private PlayerLevel playerLevel;
    @Expose
    private PokecoinPurchaseDisplayGmt pokecoinPurchaseDisplayGmt;
    @Expose
    private PokemonHomeSettings pokemonHomeSettings;
    @Expose
    private PokemonScaleSettings pokemonScaleSettings;
    @Expose
    private PokemonTagSettings pokemonTagSettings;
    @Expose
    private TypeEffective typeEffective;
    @Expose
    private PokemonUpgrades pokemonUpgrades;
    @Expose
    private QuestEvolutionSettings questEvolutionSettings;
    @Expose
    private QuestSettings questSettings;
    @Expose
    private RaidSettings raidSettings;
    @SerializedName("recomendedSearchSettings")
    @Expose
    private RecommendedSearchSettings recommendedSearchSettings;
    @Expose
    private EvolutionQuestTemplate evolutionQuestTemplate;
    @Expose
    private SmeargleMovesSettings smeargleMovesSettings;
    @Expose
    private GenderSettings genderSettings;
    @Expose
    private SponsoredGeofenceGiftSettings sponsoredGeofenceGiftSettings;
    @Expose
    private StickerMetadata stickerMetadata;
    @Expose
    private IapItemDisplay iapItemDisplay;
    @Expose
    private TemporaryEvolutionSettings temporaryEvolutionSettings;
    @Expose
    private CombatNpcTrainer combatNpcTrainer;
    @Expose
    private CombatNpcPersonality combatNpcPersonality;
    @Expose
    private PokemonFamily pokemonFamily;
    @Expose
    private PokemonSettings pokemonSettings;
    @Expose
    private MoveSettings moveSettings;
    @Expose
    private PokemonHomeFormReversion pokemonHomeFormReversions;
    @Expose
    private VsSeekerClientSettings vsSeekerClientSettings;
    @Expose
    private VsSeekerLoot vsSeekerLoot;
    @Expose
    private VsSeekerPokemonRewards vsSeekerPokemonRewards;
    @Expose
    private WeatherAffinities weatherAffinities;
    @Expose
    private WeatherBonusSettings weatherBonusSettings;
    @Expose
    private AdventureSyncV2Gmt adventureSyncV2Gmt;
    @Expose
    private Camera camera;
    @Expose
    private DeepLinkingSettings deepLinkingSettings;
    @Expose
    private MoveSequenceSettings moveSequenceSettings;
    @Expose
    private EggTransparencySettings eggTransparencySettings;
    @Expose
    private IncidentPrioritySettings incidentPrioritySettings;
    @Expose
    private ReferralSettings referralSettings;

    public String getTemplateId() { return templateId; }

    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public ArTelemetrySettings getArTelemetrySettings() { return arTelemetrySettings; }

    public void setArTelemetrySettings(ArTelemetrySettings arTelemetrySettings) {
        this.arTelemetrySettings = arTelemetrySettings;
    }

    public AvatarGroupOrderSettings getAvatarGroupOrderSettings() { return avatarGroupOrderSettings; }

    public void setAvatarGroupOrderSettings(AvatarGroupOrderSettings avatarGroupOrderSettings) {
        this.avatarGroupOrderSettings = avatarGroupOrderSettings;
    }

    public AvatarCustomization getAvatarCustomization() { return avatarCustomization; }

    public void setAvatarCustomization(AvatarCustomization avatarCustomization) {
        this.avatarCustomization = avatarCustomization;
    }

    public LevelUpRewards getLevelUpRewardSettings() { return levelUpRewardSettings; }

    public void setLevelUpRewardSettings(LevelUpRewards levelUpRewardSettings) { this.levelUpRewardSettings = levelUpRewardSettings; }

    public BackgroundModeSettings getBackgroundModeSettings() { return backgroundModeSettings; }

    public void setBackgroundModeSettings(BackgroundModeSettings backgroundModeSettings) {
        this.backgroundModeSettings = backgroundModeSettings;
    }

    public Badge getBadgeSettings() { return badgeSettings; }

    public void setBadgeSettings(Badge badgeSettings) { this.badgeSettings = badgeSettings; }

    public BattleHubBadgeSettings getBattleHubBadgeSettings() { return battleHubBadgeSettings; }

    public void setBattleHubBadgeSettings(BattleHubBadgeSettings battleHubBadgeSettings) {
        this.battleHubBadgeSettings = battleHubBadgeSettings;
    }

    public BattleHubOrderSettings getBattleHubOrderSettings() { return battleHubOrderSettings; }

    public void setBattleHubOrderSettings(BattleHubOrderSettings battleHubOrderSettings) {
        this.battleHubOrderSettings = battleHubOrderSettings;
    }

    public BattleSettings getBattleSettings() { return battleSettings; }

    public void setBattleSettings(BattleSettings battleSettings) { this.battleSettings = battleSettings; }

    public BelugaPokemonWhitelist getBelugaPokemonWhitelist() { return belugaPokemonWhitelist; }

    public void setBelugaPokemonWhitelist(BelugaPokemonWhitelist belugaPokemonWhitelist) {
        this.belugaPokemonWhitelist = belugaPokemonWhitelist;
    }

    public BuddyActivitySettings getBuddyActivitySettings() { return buddyActivitySettings; }

    public void setBuddyActivitySettings(BuddyActivitySettings buddyActivitySettings) {
        this.buddyActivitySettings = buddyActivitySettings;
    }

    public BuddyActivityCategorySettings getBuddyActivityCategorySettings() {
        return buddyActivityCategorySettings;
    }

    public void setBuddyActivityCategorySettings(BuddyActivityCategorySettings buddyActivityCategorySettings) {
        this.buddyActivityCategorySettings = buddyActivityCategorySettings;
    }

    public BuddyEmotionLevelSettings getBuddyEmotionLevelSettings() { return buddyEmotionLevelSettings; }

    public void setBuddyEmotionLevelSettings(BuddyEmotionLevelSettings buddyEmotionLevelSettings) {
        this.buddyEmotionLevelSettings = buddyEmotionLevelSettings;
    }

    public BuddyEncounterCameoSettings getBuddyEncounterCameoSettings() { return buddyEncounterCameoSettings; }

    public void setBuddyEncounterCameoSettings(BuddyEncounterCameoSettings buddyEncounterCameoSettings) {
        this.buddyEncounterCameoSettings = buddyEncounterCameoSettings;
    }

    public BuddyHungerSettings getBuddyHungerSettings() { return buddyHungerSettings; }

    public void setBuddyHungerSettings(BuddyHungerSettings buddyHungerSettings) {
        this.buddyHungerSettings = buddyHungerSettings;
    }

    public BuddyInteractionSettings getBuddyInteractionSettings() { return buddyInteractionSettings; }

    public void setBuddyInteractionSettings(BuddyInteractionSettings buddyInteractionSettings) {
        this.buddyInteractionSettings = buddyInteractionSettings;
    }

    public BuddyLevelSettings getBuddyLevelSettings() { return buddyLevelSettings; }

    public void setBuddyLevelSettings(BuddyLevelSettings buddyLevelSettings) {
        this.buddyLevelSettings = buddyLevelSettings;
    }

    public BuddySwapSettings getBuddySwapSettings() { return buddySwapSettings; }

    public void setBuddySwapSettings(BuddySwapSettings buddySwapSettings) {
        this.buddySwapSettings = buddySwapSettings;
    }

    public BuddyWalkSettings getBuddyWalkSettings() { return buddyWalkSettings; }

    public void setBuddyWalkSettings(BuddyWalkSettings buddyWalkSettings) {
        this.buddyWalkSettings = buddyWalkSettings;
    }

    public InvasionNpcDisplaySettings getInvasionNpcDisplaySettings() { return invasionNpcDisplaySettings; }

    public void setInvasionNpcDisplaySettings(InvasionNpcDisplaySettings invasionNpcDisplaySettings) {
        this.invasionNpcDisplaySettings = invasionNpcDisplaySettings;
    }

    public CombatCompetitiveSeasonSettings getCombatCompetitiveSeasonSettings() {
        return combatCompetitiveSeasonSettings;
    }

    public void setCombatCompetitiveSeasonSettings(CombatCompetitiveSeasonSettings combatCompetitiveSeasonSettings) {
        this.combatCompetitiveSeasonSettings = combatCompetitiveSeasonSettings;
    }

    public CombatLeague getCombatLeague() { return combatLeague; }

    public void setCombatLeague(CombatLeague combatLeague) { this.combatLeague = combatLeague; }

    public CombatLeagueSettings getCombatLeagueSettings() { return combatLeagueSettings; }

    public void setCombatLeagueSettings(CombatLeagueSettings combatLeagueSettings) {
        this.combatLeagueSettings = combatLeagueSettings;
    }

    public CombatType getCombatType() { return combatType; }

    public void setCombatType(CombatType combatType) { this.combatType = combatType; }

    public CombatRankingProtoSettings getCombatRankingProtoSettings() { return combatRankingProtoSettings; }

    public void setCombatRankingProtoSettings(CombatRankingProtoSettings combatRankingProtoSettings) {
        this.combatRankingProtoSettings = combatRankingProtoSettings;
    }

    public CombatSettings getCombatSettings() { return combatSettings; }

    public void setCombatSettings(CombatSettings combatSettings) { this.combatSettings = combatSettings; }

    public CombatStatStageSettings getCombatStatStageSettings() { return combatStatStageSettings; }

    public void setCombatStatStageSettings(CombatStatStageSettings combatStatStageSettings) {
        this.combatStatStageSettings = combatStatStageSettings;
    }

    public CombatMove getCombatMove() { return combatMove; }

    public void setCombatMove(CombatMove combatMove) { this.combatMove = combatMove; }

    public CrossGameSocialSettings getCrossGameSocialSettings() { return crossGameSocialSettings; }

    public void setCrossGameSocialSettings(CrossGameSocialSettings crossGameSocialSettings) {
        this.crossGameSocialSettings = crossGameSocialSettings;
    }

    public FriendProfileSettings getFriendProfileSettings() { return friendProfileSettings; }

    public void setFriendProfileSettings(FriendProfileSettings friendProfileSettings) {
        this.friendProfileSettings = friendProfileSettings;
    }

    public EncounterSettings getEncounterSettings() { return encounterSettings; }

    public void setEncounterSettings(EncounterSettings encounterSettings) {
        this.encounterSettings = encounterSettings;
    }

    public PokemonHomeEnergyCosts getPokemonHomeEnergyCosts() { return pokemonHomeEnergyCosts; }

    public void setPokemonHomeEnergyCosts(PokemonHomeEnergyCosts pokemonHomeEnergyCosts) {
        this.pokemonHomeEnergyCosts = pokemonHomeEnergyCosts;
    }

    public ExRaidSettings getExRaidSettings() { return exRaidSettings; }

    public void setExRaidSettings(ExRaidSettings exRaidSettings) { this.exRaidSettings = exRaidSettings; }

    public FormSettings getFormSettings() { return formSettings; }

    public void setFormSettings(FormSettings formSettings) { this.formSettings = formSettings; }

    public FriendshipMilestoneSettings getFriendshipMilestoneSettings() { return friendshipMilestoneSettings; }

    public void setFriendshipMilestoneSettings(FriendshipMilestoneSettings friendshipMilestoneSettings) {
        this.friendshipMilestoneSettings = friendshipMilestoneSettings;
    }

    public GeotargetedQuestSettings getGeotargetedQuestSettings() { return geotargetedQuestSettings; }

    public void setGeotargetedQuestSettings(GeotargetedQuestSettings geotargetedQuestSettings) {
        this.geotargetedQuestSettings = geotargetedQuestSettings;
    }

    public GuiSearchSettings getGuiSearchSettings() { return guiSearchSettings; }

    public void setGuiSearchSettings(GuiSearchSettings guiSearchSettings) {
        this.guiSearchSettings = guiSearchSettings;
    }

    public GymBadgeSettings getGymBadgeSettings() { return gymBadgeSettings; }

    public void setGymBadgeSettings(GymBadgeSettings gymBadgeSettings) { this.gymBadgeSettings = gymBadgeSettings; }

    public GymLevel getGymLevel() { return gymLevel; }

    public void setGymLevel(GymLevel gymLevel) { this.gymLevel = gymLevel; }

    public IapCategoryDisplay getIapCategoryDisplay() { return iapCategoryDisplay; }

    public void setIapCategoryDisplay(IapCategoryDisplay iapCategoryDisplay) {
        this.iapCategoryDisplay = iapCategoryDisplay;
    }

    public IapSettings getIapSettings() { return iapSettings; }

    public void setIapSettings(IapSettings iapSettings) { this.iapSettings = iapSettings; }

    public PokestopInvasionAvailabilitySettings getPokestopInvasionAvailabilitySettings() {
        return pokestopInvasionAvailabilitySettings;
    }

    public void setPokestopInvasionAvailabilitySettings(
            PokestopInvasionAvailabilitySettings pokestopInvasionAvailabilitySettings
    ) {
        this.pokestopInvasionAvailabilitySettings = pokestopInvasionAvailabilitySettings;
    }

    public InventorySettings getInventorySettings() { return inventorySettings; }

    public void setInventorySettings(InventorySettings inventorySettings) {
        this.inventorySettings = inventorySettings;
    }

    public ItemSettings getItemSettings() { return itemSettings; }

    public void setItemSettings(ItemSettings itemSettings) { this.itemSettings = itemSettings; }

    public LimitedPurchaseSkuSettings getLimitedPurchaseSkuSettings() { return limitedPurchaseSkuSettings; }

    public void setLimitedPurchaseSkuSettings(LimitedPurchaseSkuSettings limitedPurchaseSkuSettings) {
        this.limitedPurchaseSkuSettings = limitedPurchaseSkuSettings;
    }

    public LuckyPokemonSettings getLuckyPokemonSettings() { return luckyPokemonSettings; }

    public void setLuckyPokemonSettings(LuckyPokemonSettings luckyPokemonSettings) {
        this.luckyPokemonSettings = luckyPokemonSettings;
    }

    public MapDisplaySettings getMapDisplaySettings() { return mapDisplaySettings; }

    public void setMapDisplaySettings(MapDisplaySettings mapDisplaySettings) {
        this.mapDisplaySettings = mapDisplaySettings;
    }

    public MegaEvoSettings getMegaEvoSettings() { return megaEvoSettings; }

    public void setMegaEvoSettings(MegaEvoSettings megaEvoSettings) { this.megaEvoSettings = megaEvoSettings; }

    public MonodepthSettings getMonodepthSettings() { return monodepthSettings; }

    public void setMonodepthSettings(MonodepthSettings monodepthSettings) {
        this.monodepthSettings = monodepthSettings;
    }

    public OnboardingV2Settings getOnboardingV2Settings() { return onboardingV2Settings; }

    public void setOnboardingV2Settings(OnboardingV2Settings onboardingV2Settings) {
        this.onboardingV2Settings = onboardingV2Settings;
    }

    public PartyRecommendationSettings getPartyRecommendationSettings() { return partyRecommendationSettings; }

    public void setPartyRecommendationSettings(PartyRecommendationSettings partyRecommendationSettings) {
        this.partyRecommendationSettings = partyRecommendationSettings;
    }

    public PlatypusRolloutSettings getPlatypusRolloutSettings() { return platypusRolloutSettings; }

    public void setPlatypusRolloutSettings(PlatypusRolloutSettings platypusRolloutSettings) {
        this.platypusRolloutSettings = platypusRolloutSettings;
    }

    public PlayerLevel getPlayerLevel() { return playerLevel; }

    public void setPlayerLevel(PlayerLevel playerLevel) { this.playerLevel = playerLevel; }

    public PokecoinPurchaseDisplayGmt getPokecoinPurchaseDisplayGmt() { return pokecoinPurchaseDisplayGmt; }

    public void setPokecoinPurchaseDisplayGmt(PokecoinPurchaseDisplayGmt pokecoinPurchaseDisplayGmt) {
        this.pokecoinPurchaseDisplayGmt = pokecoinPurchaseDisplayGmt;
    }

    public PokemonHomeSettings getPokemonHomeSettings() { return pokemonHomeSettings; }

    public void setPokemonHomeSettings(PokemonHomeSettings pokemonHomeSettings) {
        this.pokemonHomeSettings = pokemonHomeSettings;
    }

    public PokemonScaleSettings getPokemonScaleSettings() { return pokemonScaleSettings; }

    public void setPokemonScaleSettings(PokemonScaleSettings pokemonScaleSettings) {
        this.pokemonScaleSettings = pokemonScaleSettings;
    }

    public PokemonTagSettings getPokemonTagSettings() { return pokemonTagSettings; }

    public void setPokemonTagSettings(PokemonTagSettings pokemonTagSettings) {
        this.pokemonTagSettings = pokemonTagSettings;
    }

    public TypeEffective getTypeEffective() { return typeEffective; }

    public void setTypeEffective(TypeEffective typeEffective) { this.typeEffective = typeEffective; }

    public PokemonUpgrades getPokemonUpgrades() { return pokemonUpgrades; }

    public void setPokemonUpgrades(PokemonUpgrades pokemonUpgrades) { this.pokemonUpgrades = pokemonUpgrades; }

    public QuestEvolutionSettings getQuestEvolutionSettings() { return questEvolutionSettings; }

    public void setQuestEvolutionSettings(QuestEvolutionSettings questEvolutionSettings) {
        this.questEvolutionSettings = questEvolutionSettings;
    }

    public QuestSettings getQuestSettings() { return questSettings; }

    public void setQuestSettings(QuestSettings questSettings) { this.questSettings = questSettings; }

    public RaidSettings getRaidSettings() { return raidSettings; }

    public void setRaidSettings(RaidSettings raidSettings) {
        this.raidSettings = raidSettings;
    }

    public RecommendedSearchSettings getRecommendedSearchSettings() { return recommendedSearchSettings; }

    public void setRecommendedSearchSettings(RecommendedSearchSettings recommendedSearchSettings) {
        this.recommendedSearchSettings = recommendedSearchSettings;
    }

    public EvolutionQuestTemplate getEvolutionQuestTemplate() { return evolutionQuestTemplate; }

    public void setEvolutionQuestTemplate(EvolutionQuestTemplate evolutionQuestTemplate) {
        this.evolutionQuestTemplate = evolutionQuestTemplate;
    }

    public SmeargleMovesSettings getSmeargleMovesSettings() { return smeargleMovesSettings; }

    public void setSmeargleMovesSettings(SmeargleMovesSettings smeargleMovesSettings) {
        this.smeargleMovesSettings = smeargleMovesSettings;
    }

    public GenderSettings getGenderSettings() { return genderSettings; }

    public void setGenderSettings(GenderSettings genderSettings) { this.genderSettings = genderSettings; }

    public SponsoredGeofenceGiftSettings getSponsoredGeofenceGiftSettings() { return sponsoredGeofenceGiftSettings; }

    public void setSponsoredGeofenceGiftSettings(SponsoredGeofenceGiftSettings sponsoredGeofenceGiftSettings) {
        this.sponsoredGeofenceGiftSettings = sponsoredGeofenceGiftSettings;
    }

    public StickerMetadata getStickerMetadata() { return stickerMetadata; }

    public void setStickerMetadata(StickerMetadata stickerMetadata) { this.stickerMetadata = stickerMetadata; }

    public IapItemDisplay getIapItemDisplay() { return iapItemDisplay; }

    public void setIapItemDisplay(IapItemDisplay iapItemDisplay) { this.iapItemDisplay = iapItemDisplay; }

    public TemporaryEvolutionSettings getTemporaryEvolutionSettings() { return temporaryEvolutionSettings; }

    public void setTemporaryEvolutionSettings(TemporaryEvolutionSettings temporaryEvolutionSettings) {
        this.temporaryEvolutionSettings = temporaryEvolutionSettings;
    }

    public CombatNpcTrainer getCombatNpcTrainer() { return combatNpcTrainer; }

    public void setCombatNpcTrainer(CombatNpcTrainer combatNpcTrainer) { this.combatNpcTrainer = combatNpcTrainer; }

    public CombatNpcPersonality getCombatNpcPersonality() { return combatNpcPersonality; }

    public void setCombatNpcPersonality(CombatNpcPersonality combatNpcPersonality) {
        this.combatNpcPersonality = combatNpcPersonality;
    }

    public PokemonFamily getPokemonFamily() { return pokemonFamily; }

    public void setPokemonFamily(PokemonFamily pokemonFamily) { this.pokemonFamily = pokemonFamily; }

    public PokemonSettings getPokemonSettings() { return pokemonSettings; }

    public void setPokemonSettings(PokemonSettings pokemonSettings) { this.pokemonSettings = pokemonSettings; }

    public MoveSettings getMoveSettings() { return moveSettings; }

    public void setMoveSettings(MoveSettings moveSettings) { this.moveSettings = moveSettings; }

    public PokemonHomeFormReversion getPokemonHomeFormReversions() { return pokemonHomeFormReversions; }

    public void setPokemonHomeFormReversions(PokemonHomeFormReversion pokemonHomeFormReversions) {
        this.pokemonHomeFormReversions = pokemonHomeFormReversions;
    }

    public VsSeekerClientSettings getVsSeekerClientSettings() { return vsSeekerClientSettings; }

    public void setVsSeekerClientSettings(VsSeekerClientSettings vsSeekerClientSettings) {
        this.vsSeekerClientSettings = vsSeekerClientSettings;
    }

    public VsSeekerLoot getVsSeekerLoot() { return vsSeekerLoot; }

    public void setVsSeekerLoot(VsSeekerLoot vsSeekerLoot) {
        this.vsSeekerLoot = vsSeekerLoot;
    }

    public VsSeekerPokemonRewards getVsSeekerPokemonRewards() { return vsSeekerPokemonRewards; }

    public void setVsSeekerPokemonRewards(VsSeekerPokemonRewards vsSeekerPokemonRewards) {
        this.vsSeekerPokemonRewards = vsSeekerPokemonRewards;
    }

    public WeatherAffinities getWeatherAffinities() { return weatherAffinities; }

    public void setWeatherAffinities(WeatherAffinities weatherAffinities) {
        this.weatherAffinities = weatherAffinities;
    }

    public WeatherBonusSettings getWeatherBonusSettings() { return weatherBonusSettings; }

    public void setWeatherBonusSettings(WeatherBonusSettings weatherBonusSettings) {
        this.weatherBonusSettings = weatherBonusSettings;
    }

    public AdventureSyncV2Gmt getAdventureSyncV2Gmt() { return adventureSyncV2Gmt; }

    public void setAdventureSyncV2Gmt(AdventureSyncV2Gmt adventureSyncV2Gmt) {
        this.adventureSyncV2Gmt = adventureSyncV2Gmt;
    }

    public Camera getCamera() { return camera; }

    public void setCamera(Camera camera) { this.camera = camera; }

    public DeepLinkingSettings getDeepLinkingSettings() { return deepLinkingSettings; }

    public void setDeepLinkingSettings(DeepLinkingSettings deepLinkingSettings) {
        this.deepLinkingSettings = deepLinkingSettings;
    }

    public MoveSequenceSettings getMoveSequenceSettings() { return moveSequenceSettings; }

    public void setMoveSequenceSettings(
            MoveSequenceSettings moveSequenceSettings) { this.moveSequenceSettings = moveSequenceSettings; }

    public EggTransparencySettings getEggTransparencySettings() {
        return eggTransparencySettings;
    }

    public void setEggTransparencySettings(
            EggTransparencySettings eggTransparencySettings) {
        this.eggTransparencySettings = eggTransparencySettings;
    }

    public IncidentPrioritySettings getIncidentPrioritySettings() {
        return incidentPrioritySettings;
    }

    public void setIncidentPrioritySettings(
            IncidentPrioritySettings incidentPrioritySettings) {
        this.incidentPrioritySettings = incidentPrioritySettings;
    }

    public ReferralSettings getReferralSettings() {
        return referralSettings;
    }

    public void setReferralSettings(ReferralSettings referralSettings) {
        this.referralSettings = referralSettings;
    }
}

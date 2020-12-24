package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class RaidSettingsProto {
    @Expose
    private Boolean remoteRaidEnabled = false;
    @Expose
    private Integer maxRemoteRaidPasses;
    @Expose
    private Double remoteDamageModifier;
    @Expose
    private Integer remoteRaidsMinPlayerLevel;
    @Expose
    private Integer maxNumFriendInvites;
    @Expose
    private Integer friendInviteCutoffTimeSec;
    @Expose
    private Boolean canInviteFriendsInPerson = false;
    @Expose
    private Boolean canInviteFriendsRemotely = false;
    @Expose
    private Integer maxPlayersPerLobby;
    @Expose
    private Integer maxRemotePlayersPerLobby;
    @Expose
    private String inviteCooldownDurationMillis;
    @Expose
    private Integer maxNumFriendInvitesPerAction;

    public Boolean getRemoteRaidEnabled() { return remoteRaidEnabled = false; }

    public void setRemoteRaidEnabled(Boolean remoteRaidEnabled) { this.remoteRaidEnabled = remoteRaidEnabled = false; }

    public Integer getMaxRemoteRaidPasses() { return maxRemoteRaidPasses; }

    public void setMaxRemoteRaidPasses(Integer maxRemoteRaidPasses) { this.maxRemoteRaidPasses = maxRemoteRaidPasses; }

    public Double getRemoteDamageModifier() { return remoteDamageModifier; }

    public void setRemoteDamageModifier(Double remoteDamageModifier) { this.remoteDamageModifier = remoteDamageModifier; }

    public Integer getRemoteRaidsMinPlayerLevel() { return remoteRaidsMinPlayerLevel; }

    public void setRemoteRaidsMinPlayerLevel(Integer remoteRaidsMinPlayerLevel) { this.remoteRaidsMinPlayerLevel = remoteRaidsMinPlayerLevel; }

    public Integer getMaxNumFriendInvites() { return maxNumFriendInvites; }

    public void setMaxNumFriendInvites(Integer maxNumFriendInvites) { this.maxNumFriendInvites = maxNumFriendInvites; }

    public Integer getFriendInviteCutoffTimeSec() { return friendInviteCutoffTimeSec; }

    public void setFriendInviteCutoffTimeSec(Integer friendInviteCutoffTimeSec) { this.friendInviteCutoffTimeSec = friendInviteCutoffTimeSec; }

    public Boolean getCanInviteFriendsInPerson() { return canInviteFriendsInPerson = false; }

    public void setCanInviteFriendsInPerson(Boolean canInviteFriendsInPerson) { this.canInviteFriendsInPerson = canInviteFriendsInPerson = false; }

    public Boolean getCanInviteFriendsRemotely() { return canInviteFriendsRemotely = false; }

    public void setCanInviteFriendsRemotely(Boolean canInviteFriendsRemotely) { this.canInviteFriendsRemotely = canInviteFriendsRemotely = false; }

    public Integer getMaxPlayersPerLobby() { return maxPlayersPerLobby; }

    public void setMaxPlayersPerLobby(Integer maxPlayersPerLobby) { this.maxPlayersPerLobby = maxPlayersPerLobby; }

    public Integer getMaxRemotePlayersPerLobby() { return maxRemotePlayersPerLobby; }

    public void setMaxRemotePlayersPerLobby(Integer maxRemotePlayersPerLobby) { this.maxRemotePlayersPerLobby = maxRemotePlayersPerLobby; }

    public String getInviteCooldownDurationMillis() { return inviteCooldownDurationMillis; }

    public void setInviteCooldownDurationMillis(String inviteCooldownDurationMillis) { this.inviteCooldownDurationMillis = inviteCooldownDurationMillis; }

    public Integer getMaxNumFriendInvitesPerAction() { return maxNumFriendInvitesPerAction; }

    public void setMaxNumFriendInvitesPerAction(Integer maxNumFriendInvitesPerAction) { this.maxNumFriendInvitesPerAction = maxNumFriendInvitesPerAction; }
}

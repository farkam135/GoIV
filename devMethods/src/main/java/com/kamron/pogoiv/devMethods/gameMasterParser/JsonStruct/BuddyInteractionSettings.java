package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class BuddyInteractionSettings {
    @Expose
    private List<String> feedItemWhitelist = null;

    public List<String> getFeedItemWhitelist() { return feedItemWhitelist; }

    public void setFeedItemWhitelist(List<String> feedItemWhitelist) { this.feedItemWhitelist = feedItemWhitelist; }
}

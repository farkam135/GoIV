package com.kamron.pogoiv.updater;


public class AppUpdate {
    private String assetUrl;
    private String version;
    private String changelog;

    public String getAssetUrl() {
        return assetUrl;
    }

    public String getVersion() {
        return version;
    }

    public String getChangelog() {
        return changelog;
    }

    public AppUpdate(String assetUrl, String version, String changelog) {
        this.assetUrl = assetUrl;
        this.version = version;
        this.changelog = changelog;
    }
}

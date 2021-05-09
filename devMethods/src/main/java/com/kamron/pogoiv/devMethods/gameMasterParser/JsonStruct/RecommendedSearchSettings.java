package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class RecommendedSearchSettings {
    @Expose
    private String searchLabel;
    @Expose
    private String appendSearchString;
    @Expose
    private String searchKey;

    public String getSearchLabel() { return searchLabel; }

    public void setSearchLabel(String searchLabel) { this.searchLabel = searchLabel; }

    public String getAppendSearchString() { return appendSearchString; }

    public void setAppendSearchString(String appendSearchString) {
        this.appendSearchString = appendSearchString;
    }

    public String getSearchKey() { return searchKey; }

    public void setSearchKey(String searchKey) { this.searchKey = searchKey; }
}

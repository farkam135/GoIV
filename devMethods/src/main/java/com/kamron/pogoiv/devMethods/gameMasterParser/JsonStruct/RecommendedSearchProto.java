package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class RecommendedSearchProto {
    @Expose
    private String searchLabel;
    @Expose
    private String appendedSearchString;
    @Expose
    private String searchKey;

    public String getSearchLabel() { return searchLabel; }

    public void setSearchLabel(String searchLabel) { this.searchLabel = searchLabel; }

    public String getAppendedSearchString() { return appendedSearchString; }

    public void setAppendedSearchString(String appendedSearchString) { this.appendedSearchString = appendedSearchString; }

    public String getSearchKey() { return searchKey; }

    public void setSearchKey(String searchKey) { this.searchKey = searchKey; }
}

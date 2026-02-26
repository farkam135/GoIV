package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

public class InvasionNpcDisplaySettings {
    @Expose
    private String trainerName;
    @Expose
    private Avatar avatar;
    @Expose
    private String trainerTitle;
    @Expose
    private String trainerQuote;
    @Expose
    private String iconUrl;
    @Expose
    private String backdropImageBundle;
    @Expose
    private String modelName;
    @Expose
    private String tutorialOnLossString;
    @Expose
    private Boolean isMale = false;

    public String getTrainerName() { return trainerName; }

    public void setTrainerName(String trainerName) { this.trainerName = trainerName; }

    public Avatar getAvatar() { return avatar; }

    public void setAvatar(Avatar avatar) { this.avatar = avatar; }

    public String getTrainerTitle() { return trainerTitle; }

    public void setTrainerTitle(String trainerTitle) { this.trainerTitle = trainerTitle; }

    public String getTrainerQuote() { return trainerQuote; }

    public void setTrainerQuote(String trainerQuote) { this.trainerQuote = trainerQuote; }

    public String getIconUrl() { return iconUrl; }

    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }

    public String getBackdropImageBundle() { return backdropImageBundle; }

    public void setBackdropImageBundle(String backdropImageBundle) { this.backdropImageBundle = backdropImageBundle; }

    public String getModelName() { return modelName; }

    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getTutorialOnLossString() { return tutorialOnLossString; }

    public void setTutorialOnLossString(String tutorialOnLossString) {
        this.tutorialOnLossString = tutorialOnLossString;
    }

    public Boolean getMale() { return isMale; }

    public void setMale(Boolean male) { isMale = male; }
}

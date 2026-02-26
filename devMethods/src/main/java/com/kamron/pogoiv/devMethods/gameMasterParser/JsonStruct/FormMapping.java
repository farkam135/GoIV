package com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct;

import com.google.gson.annotations.Expose;

import java.util.List;

public class FormMapping {
    @Expose
    private String revertedForm;
    @Expose
    private List<String> unauthorizedForms = null;
    @Expose
    private String revertedFormString;

    public String getRevertedForm() { return revertedForm; }

    public void setRevertedForm(String revertedForm) { this.revertedForm = revertedForm; }

    public List<String> getUnauthorizedForms() { return unauthorizedForms; }

    public void setUnauthorizedForms(List<String> unauthorizedForms) { this.unauthorizedForms = unauthorizedForms; }

    public String getRevertedFormString() { return revertedFormString; }

    public void setRevertedFormString(String revertedFormString) { this.revertedFormString = revertedFormString; }
}

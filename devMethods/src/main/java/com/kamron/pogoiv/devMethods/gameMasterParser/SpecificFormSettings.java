package com.kamron.pogoiv.devMethods.gameMasterParser;

import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.Form;
import com.kamron.pogoiv.devMethods.gameMasterParser.JsonStruct.FormSettings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SpecificFormSettings extends FormSettings {
    static String NORMAL_FORM = "NORMAL";
    static String[] COMMON_FORMS = {"SHADOW", "PURIFIED", "FALL_2019", "COPY_2019", "VS_2019"};
    static String[] REGION_FORMS = {"ALOLA", "GALARIAN"};

    private List<Form> forms = null;

    public SpecificFormSettings(FormSettings formSettings) {
        setName(formSettings.getName());
        setForms(formSettings.getForms());
    }

    @Override public List<Form> getForms() {
        if (forms == null) {
            List<Form> allForms = super.getForms();
            if (allForms != null) {
                forms = new ArrayList<>();
                Pattern pattern = Pattern.compile("^.*_(" + String.join("|", COMMON_FORMS) + ")$");
                for (Form form : allForms) {
                    if (!pattern.matcher(form.getForm()).matches()) {
                        forms.add(form);
                    }
                }
                if (forms.size() == 1 && forms.get(0).getForm().matches("^.*_(" + NORMAL_FORM + ")$")) {
                    forms.remove(0);
                }

            }
        }
        return forms;
    }

}

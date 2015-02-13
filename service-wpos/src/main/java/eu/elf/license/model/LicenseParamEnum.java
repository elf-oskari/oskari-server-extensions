package eu.elf.license.model;

import java.util.ArrayList;
import java.util.List;

/**
 * License parameter for showing predefined values of textual content. Value is required if multi = true.
 */
public class LicenseParamEnum extends LicenseParam {
    private boolean isMulti = false;
    private String defaultValue;
    // options presented to the user
    private List<String> options = new ArrayList<String>();
    // values that the user selected based on presented options (to be used on conclude)
    private List<String> selections = new ArrayList<String>();

    public boolean isMulti() {
        return isMulti;
    }

    public void setMulti(boolean isMulti) {
        this.isMulti = isMulti;
    }

    public void clearOptions() {
        options.clear();
    }

    public void addOption(final String opt) {
        options.add(opt);
    }

    public List<String> getOptions() {
        return options;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void clearSelections() {
        selections.clear();
    }

    public void addSelection(final String opt) {
        selections.add(opt);
    }

    public List<String> getSelections() {
        return selections;
    }

    public boolean isValid() {
        return !getSelections().isEmpty() || isMulti();
    }
}

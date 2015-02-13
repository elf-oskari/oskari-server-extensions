package eu.elf.license.model;

/**
 * License parameter for showing NON-editable textual content f. ex. license text.
 */
public class LicenseParamDisplay extends LicenseParam {
    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public boolean isValid() {
        return true;
    }
}

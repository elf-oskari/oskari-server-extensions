package eu.elf.license.model;

/**
 * License parameter that is boolean. Value is always required.
 */
public class LicenseParamBln extends LicenseParam {

    private static final String BOOLEAN_TRUE = "TRUE";
    private boolean value = false;

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public void setValue(String value) {
        this.value = BOOLEAN_TRUE.equals(value);
    }

    public boolean isValid() {
        return true;
    }
}

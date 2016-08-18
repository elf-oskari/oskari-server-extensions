package eu.elf.license.model;

/**
 * License parameter that is numeric. Value is always required.
 */
public class LicenseParamInt extends LicenseParam {
    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isValid() {
        return true;
    }
}

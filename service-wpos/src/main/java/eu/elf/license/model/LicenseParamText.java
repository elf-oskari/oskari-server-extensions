package eu.elf.license.model;

/**
 * License parameter for showing EDITABLE textual content. Value is always required.
 */
public class LicenseParamText extends LicenseParamDisplay {
    public boolean isValid() {
        return getValue() != null && !getValue().trim().isEmpty();
    }
}

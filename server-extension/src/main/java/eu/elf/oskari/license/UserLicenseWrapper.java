package eu.elf.oskari.license;

import eu.elf.license.model.LicenseModelGroup;
import eu.elf.license.model.UserLicense;

/**
 * Wrapper for user license/LicenseModelGroup for nicer JSON formatting
 */
public class UserLicenseWrapper extends LicenseModelGroup {

    private UserLicense license;

    public UserLicenseWrapper(UserLicense license, LicenseModelGroup group) {
        setUserLicense(true);
        this.license = license;
        setId(group.getId());
        setUrl(group.getUrl());
        setName(group.getName());
        setDescription(group.getDescription());
        setLicenseModels(group.getLicenseModels());
    }

    public String getLicenseId() {
        return license.getLicenseId();
    }
    public String getValidTo() {
        return license.getValidTo();
    }

    public String getSecureServiceURL() {
        return license.getSecureServiceURL();
    }
}

package eu.elf.license.model;

import java.util.ArrayList;
import java.util.List;

public class UserLicenses {
	private List<UserLicense> userLicenses;
	
	public UserLicenses() {
		this.userLicenses = new ArrayList<UserLicense>();
	}

	
	public List<UserLicense> getUserLicenses() {
		return userLicenses;
	}

	public void setUserLicenses(List<UserLicense> userLicenses) {
		this.userLicenses = userLicenses;
	}
	
	public void addUserLicense(UserLicense userLicense) {
		userLicenses.add(userLicense);
    }

    /**
     * Finds group based on URL
     *
     * @param url
     * @return LicenseModelGroup with matching url or null if none match the url.
     */
    public UserLicense getLicenseForURL(final String url) {
        if (url == null || userLicenses == null) {
            return null;
        }
        for (UserLicense userLicense : userLicenses) {
            if(userLicense.getLmgList() == null) {
                continue;
            }
            for (LicenseModelGroup group : userLicense.getLmgList()) {
                // NOTE! -> URL <-
                if (url.equalsIgnoreCase(group.getUrl())) {
                    // NOTE! -> returning user license, not group <-
                    return userLicense;
                }
            }
        }
        return null;
    }
}

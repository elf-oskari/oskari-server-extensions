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
     * Finds license based on URL. Returns the first license to have LicenseModelGroup with matching url.
     *
     * @param url
     * @return UserLicense with LicenseModelGroup having a matching url or null if none match the url.
     */
    public UserLicense getLicenseForServiceURL(final String url) {
        if (url == null || userLicenses == null) {
            return null;
        }
        for (UserLicense userLicense : userLicenses) {
            LicenseModelGroup group = userLicense.getLicenseModelGroupForURL(url);
            if(group == null) {
                continue;
            }
            return userLicense;
        }
        return null;
    }
}

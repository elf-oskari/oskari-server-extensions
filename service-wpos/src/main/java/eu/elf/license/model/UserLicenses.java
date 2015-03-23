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
	
}

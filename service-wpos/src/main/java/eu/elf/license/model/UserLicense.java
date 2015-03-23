package eu.elf.license.model;

import java.util.ArrayList;
import java.util.List;

public class UserLicense {
	private String licenseId;
	private String validTo;
	private String secureServiceURL;
	private List<LicenseModelGroup> lmgList;
	
	
	public String getLicenseId() {
		return licenseId;
	}
	public void setLicenseId(String licenseId) {
		this.licenseId = licenseId;
	}
	public String getValidTo() {
		return validTo;
	}
	public void setValidTo(String validTo) {
		this.validTo = validTo;
	}
	public String getSecureServiceURL() {
		return secureServiceURL;
	}
	public void setSecureServiceURL(String secureServiceURL) {
		this.secureServiceURL = secureServiceURL;
	}
	public List<LicenseModelGroup> getLmgList() {
		return lmgList;
	}
	public void setLmgList(List<LicenseModelGroup> lmgList) {
		this.lmgList = lmgList;
	}

	
	
}

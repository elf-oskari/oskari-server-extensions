package eu.elf.license.model;

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

    /**
     * Finds LicenseModelGroup based on URL
     *
     * @param url
     * @return LicenseModelGroup with matching url or null if none match the url.
     */
    public LicenseModelGroup getLicenseModelGroupForURL(final String url) {
        if (url == null || lmgList == null) {
            return null;
        }
        for (LicenseModelGroup group : lmgList) {
            // NOTE! -> URL <-
            if (url.equalsIgnoreCase(group.getUrl())) {
                return group;
            }
        }
        return null;
    }
	
	
}

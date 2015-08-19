package eu.elf.license.conclude;

public class LicenseConcludeResponseObject {
	private String productId;
	private String licenseId;
	private String validTo;
	
	public LicenseConcludeResponseObject() {
		
	}

	/**
	 * LicenseModel id
	 * @return
	 */
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}

	/**
	 * Id for the newly concluded license
	 * @return
	 */
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
	
}

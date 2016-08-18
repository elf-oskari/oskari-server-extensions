package eu.elf.license.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class UserLicense {
	private String licenseId;
	private String validTo;
	private String secureServiceURL;
	private Boolean isActive = false;	// default value = false
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
	
	public Calendar getValidToAsCalendarObject() {
		Calendar cal = new GregorianCalendar();
		
		int year = Integer.parseInt(validTo.substring(0, 4));
		int month = Integer.parseInt(validTo.substring(5, 7));
		int day = Integer.parseInt(validTo.substring(8, 10));
		int hours = Integer.parseInt(validTo.substring(11, 13));
		int minutes = Integer.parseInt(validTo.substring(14, 16));
		int seconds = Integer.parseInt(validTo.substring(17, 19));
		
		/**
		System.out.println("year: "+year);
		System.out.println("month: "+month);
		System.out.println("day: "+day);
		System.out.println("hours: "+hours);
		System.out.println("minutes: "+minutes);
		System.out.println("seconds: "+seconds);
		**/

   		cal.set(Calendar.YEAR, year);
   		cal.set(Calendar.MONTH, month);
   		cal.set(Calendar.DATE, day);
   		cal.set(Calendar.HOUR_OF_DAY, hours);
   		cal.set(Calendar.MINUTE, minutes);
   		cal.set(Calendar.SECOND, seconds);
   	
   		return cal;
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
	public Boolean getIsActive() {
		return isActive;
	}
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
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

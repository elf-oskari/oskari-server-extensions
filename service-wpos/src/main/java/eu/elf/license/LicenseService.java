package eu.elf.license;

import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;

import java.util.List;

public class LicenseService {
	private LicenseQueryHandler lqh;
	
	public LicenseService(String WPOSUrl, String WPOSUsername, String WPOSPassword, String licenseManagerURL) {
		try {
			this.lqh = new LicenseQueryHandler(WPOSUrl, WPOSUsername, WPOSPassword, licenseManagerURL);
		
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
    public List<LicenseModelGroup> getLicenseGroups() {
    	List<LicenseModelGroup> lmgList = null;

    	try {
    		lmgList = lqh.getListOfLicensesAsLicenseModelGroupList();
    			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
        return lmgList;
    }

    public List<LicenseModelGroup> getLicenseGroupsForUser(final String userid) {
    	List<LicenseModelGroup> lmgList = null;
    	
    	try {
    		//lmgList = lqh.getListOfLicensesAsLicenseModelGroupList(userid);
    			
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
        return lmgList;
    }

    public LicenseModelGroup getLicenseForURL(final String url) {
        return getLicenseGroupsForURL(getLicenseGroups(), url);
    }

    public LicenseModelGroup getUserLicenseForURL(final String userid, final String url) {
        return getLicenseGroupsForURL(getLicenseGroupsForUser(userid), url);
    }

    /**
     * Finds group based on URL
     * @param groups
     * @param url
     * @return LicenseModelGroup with matching url or null if none match the url.
     */
    public LicenseModelGroup getLicenseGroupsForURL(List<LicenseModelGroup> groups, final String url) {
        if(url == null || groups == null) {
            return null;
        }
        for(LicenseModelGroup group : groups) {
            if(url.equalsIgnoreCase(group.getUrl())) {
                return group;
            }
        }
        return null;
    }

    /**
     * Subscribe a license
     * @param model model to conclude (check need for userid as param if we add it to LicenseModel for deactivate?)
     * @return true if successfully deactivated (or do we need some other output?)
     */
    public boolean concludeLicense(final LicenseModel model, final String userId) {
        // TODO: call licensemanager
        return false;
    }

    /**
     * Unsubscribe from a license
     * @param model model to deactivate (it has id, do we need userid as well?)
     * @return true if successfully deactivated
     */
    public boolean deactivateLicense(final LicenseModel model) {
        // TODO: call licensemanager
        return false;
    }
}

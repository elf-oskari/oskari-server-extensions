package eu.elf.license;

import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;

import java.util.List;

/**
 * Dummy service interface implementation
 */
public class LicenseService {

    public List<LicenseModelGroup> getLicenseGroups() {
        // TODO: populate from WPOS
        return null;
    }

    public List<LicenseModelGroup> getLicenseGroupsForUser(final String userid) {
        // TODO: populate from WPOS
        return null;
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

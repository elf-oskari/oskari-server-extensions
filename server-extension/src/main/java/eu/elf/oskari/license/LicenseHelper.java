package eu.elf.oskari.license;

import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SMAKINEN on 24.2.2015.
 */
public class LicenseHelper {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LogFactory.getLogger(LicenseHandler.class);
    /**
     * Formats the JSON presentation for the object
     * @param param
     * @return
     */
    public static String getAsJSON(Object param) {
        try {
            return mapper.writeValueAsString(param);
        } catch (Exception e) {
            log.error(e, "Exception writing license data to JSON");
        }
        return "";
    }

    /**
     * Calls filterModelsByRoles(user, group) for each group in list
     * @param user user with roles
     * @param groups list of groups to filter
     * @return filtered groups list
     */
    public static List<LicenseModelGroup> filterModelsByRoles(User user, List<LicenseModelGroup> groups) {
        final List<LicenseModelGroup> filteredList = new ArrayList<LicenseModelGroup>();
        for(LicenseModelGroup group : groups) {
            filteredList.add(filterModelsByRoles(user, group));
        }
        return filteredList;
    }

    /**
     * Removing any license models from group that are restricted and specific to roles that the user doesn't have
     * @param user user with roles
     * @param group group to filter
     * @return filtered group
     */
    public static LicenseModelGroup filterModelsByRoles(User user, LicenseModelGroup group) {
        final List<LicenseModel> toRemove = new ArrayList<LicenseModel>();
        for(LicenseModel model : group.getLicenseModels()) {
            if(!model.isRestricted()) {
                // all roles are ok
                continue;
            }
            // restricted, check if user has roles specified in model.getRoles()
            final String[] rolesArray = model.getRoles().toArray(new String[0]);
            if(!user.hasAnyRoleIn(rolesArray)) {
                toRemove.add(model);
            }
        }
        // remove models that the user has no permission to
        for(LicenseModel model : toRemove) {
            group.getLicenseModels().remove(model);
        }
        return group;
    }
}

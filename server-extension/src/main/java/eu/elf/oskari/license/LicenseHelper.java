package eu.elf.oskari.license;

import eu.elf.license.model.*;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Calls removeNonUIParams(model) for each model in group
     * @param group group which models should be filtered
     * @return filtered group
     */
    public static LicenseModelGroup removeNonUIParams(LicenseModelGroup group) {
        if(group.getLicenseModels() == null) {
            return group;
        }
        final List<LicenseModel> filteredList = new ArrayList<LicenseModel>();
        for(LicenseModel model : group.getLicenseModels()) {
            filteredList.add(removeNonUIParams(model));
        }
        group.setLicenseModels(filteredList);
        return group;
    }

    /**
     * Removes any parameter thats name doesn't start with "LICENSE_TEXT_" or "LICENSE_PARAMETER_"
     * @param model model to filter
     * @return filtered model
     */
    public static LicenseModel removeNonUIParams(LicenseModel model) {
        final List<LicenseParam> filteredList = new ArrayList<LicenseParam>();
        for(LicenseParam param : model.getParams()) {
            if(param.getName() == null) {
                continue;
            }
            // only include params starting with
            if(param.getName().startsWith("LICENSE_TEXT_") ||
                param.getName().startsWith("LICENSE_PARAMETER_")) {
                filteredList.add(param);
            }
            else {
                log.debug("Filtering out param:", param.getName());
            }
        }
        model.setParams(filteredList);
        return model;
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

    public static void setValues(LicenseModel model, JSONArray list) throws Exception {
        Map<String, Object> values = new HashMap<>();
        for(int i = 0; i < list.length(); ++i) {
            JSONObject param = list.optJSONObject(i);
            values.put(param.optString("name"), param.opt("values"));
        }

        for(LicenseParam param : model.getParams()) {
            Object obj = values.get(param.getName());
            if(obj == null) {
                continue;
            }
            if(param instanceof LicenseParamInt) {
                ((LicenseParamInt) param).setValue((int) obj);
            }
            else if(param instanceof LicenseParamBln) {
                ((LicenseParamBln) param).setValue((boolean) obj);
            }
            else {
                if(!(obj instanceof JSONArray)) {
                    log.warn("Unknown param type for", param.getName(), ":", obj.getClass(), "-", obj);
                    continue;
                }
                final JSONArray listValues = (JSONArray) obj;
                if(param instanceof LicenseParamEnum) {
                    LicenseParamEnum lp = (LicenseParamEnum) param;
                    lp.clearSelections();
                    for(int i=0; i < listValues.length(); ++i) {
                        lp.addSelection(listValues.optString(i));
                    }
                }
                else if(param instanceof LicenseParamText) {
                    LicenseParamText lp = (LicenseParamText) param;
                    lp.clearValues();
                    for(int i=0; i < listValues.length(); ++i) {
                        lp.addValue(listValues.optString(i));
                    }
                }
            }
            /*
            // TODO: check obj type?
            else if(param instanceof LicenseParamEnum) {
                LicenseParamEnum lp = (LicenseParamEnum) param;
                lp.addSelection("");
            }
            // TODO: check obj type?
            else if(param instanceof LicenseParamText) {
                LicenseParamText lp = (LicenseParamText) param;
                lp.addValue((String) obj);
            }
            */
        }
    }
}

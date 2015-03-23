package eu.elf.oskari.license;

import eu.elf.license.LicenseService;
import eu.elf.license.conclude.LicenseConcludeResponseObject;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import eu.elf.license.model.UserLicense;
import eu.elf.license.model.UserLicenses;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.ConversionHelper;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.PropertyUtil;
import fi.nls.oskari.util.ResponseHelper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import static fi.nls.oskari.control.ActionConstants.*;

/**
 * Handles the service licenses
 */
@OskariActionRoute("ELFLicense")
public class LicenseHandler extends RestActionHandler {

    private static final Logger log = LogFactory.getLogger(LicenseHandler.class);
    private static final String PARAM_MODELID = "modelid";
    private static final String PARAM_DATA = "data";
    private static final String KEY_PRICE = "price";
    private static final String KEY_SUCCESS = "success";
    private static final String KEY_GROUPID = "groupid";

    private LicenseService service = null;

    @Override
    public void init() {
        super.init();
        service = new LicenseService(
                PropertyUtil.getNecessary("actionhandler.ELFLicense.wpos.url"),
                PropertyUtil.getNecessary("actionhandler.ELFLicense.wpos.user"),
                PropertyUtil.getNecessary("actionhandler.ELFLicense.wpos.pass"),
                PropertyUtil.getNecessary("actionhandler.ELFLicense.wpos.soapUrl"),
                PropertyUtil.getNecessary("actionhandler.ELFLicense.wpos.loginUrl"));
    }

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        final String url = params.getHttpParam(PARAM_ID);
        final User user = params.getUser();
        if(url != null) {
            final UserLicenseWrapper userLicense = getUserLicense(params, url);
            // TODO: check the handling of case where user already has the license, service should tag group with a boolean or something?
            if(userLicense != null) {
                // User already has license, respond with it!
                log.debug("User has license");
                ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(userLicense));
                return;
            }
            final List<LicenseModelGroup> licenseGroups = service.getLicenseGroups();
            // return the license info about the service for user to fill out
            final LicenseModelGroup group = service.getLicenseGroupsForURL(licenseGroups, url);
            if(group == null) {
                throw new ActionParamsException("Can't find license with url: " + url);
            }
            log.debug("Showing license options to user");
            final LicenseModelGroup groupForUser = LicenseHelper.filterModelsByRoles(user, group);
            final LicenseModelGroup groupForUI = LicenseHelper.removeNonUIParams(groupForUser);
            ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(groupForUI));
        }
        else {
            final List<LicenseModelGroup> licenseGroups = service.getLicenseGroups();
            final List<LicenseModelGroup> groupsForUser = LicenseHelper.filterModelsByRoles(user, licenseGroups);
            // note! non-ui params are not removed here!
            ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(groupsForUser));
        }
    }

    private UserLicenseWrapper getUserLicense(ActionParameters params, final String url) {

        final UserLicenses userLicenses = service.getLicenseGroupsForUser(params.getUser().getScreenname());
        if(userLicenses == null) {
            return null;
        }
        final UserLicense license = userLicenses.getLicenseForServiceURL(url);
        if(license == null) {
            return null;
        }
        List<LicenseModelGroup> groups = license.getLmgList();
        if(groups == null || groups.isEmpty()) {
            log.warn("User had license, but no LicenseModelGroups");
            return null;

        }
        // groups don't need filtering by roles since user already has these
        if(groups.size() > 1) {
            log.warn("User license had more than one group, should have only one/service!");
        }
        LicenseModelGroup group = groups.get(0);
        LicenseHelper.removeNonUIParams(group);
        return new UserLicenseWrapper(license, group);
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        // Get Price
        final LicenseModel model = getModel(params);
        String price = service.getLicenseModelPrice(model, params.getUser().getScreenname());
        double priceDigit =  ConversionHelper.getDouble(price, -1);
        if(priceDigit < 0) {
            // TODO: fix the error handling in LicenseService
            throw new ActionParamsException("Couldn't get price from '" + price + "'");
        }

        final LicenseModel modelForUI = LicenseHelper.removeNonUIParams(model);
        JSONObject resp = JSONHelper.createJSONObject(LicenseHelper.getAsJSON(modelForUI));
        JSONHelper.putValue(resp, KEY_PRICE, priceDigit);
        JSONHelper.putValue(resp, KEY_GROUPID, params.getRequiredParam(PARAM_ID));
        
        ResponseHelper.writeResponse(params, resp);
    }

    @Override
    public void handlePut(ActionParameters params) throws ActionException {
        // conclude the license
        final LicenseModel model = getModel(params);
        final LicenseConcludeResponseObject resp = service.concludeLicense(model, params.getUser().getScreenname());
        if(resp == null) {
            // TODO: error handling in LicenseService?
            throw new ActionParamsException("Couldn't conclude license");
        }
        ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(resp));
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        // deactivate the license
        final boolean success = service.deactivateLicense(params.getRequiredParam(PARAM_ID));
        ResponseHelper.writeResponse(params, JSONHelper.createJSONObject(KEY_SUCCESS, success));
    }

    private LicenseModel getModel(ActionParameters params) throws ActionException {
        // only users
        params.requireLoggedInUser();

        final List<LicenseModelGroup> licenseGroups = service.getLicenseGroups();
        final LicenseModelGroup group = service.getLicenseGroupsForId(licenseGroups, params.getRequiredParam(PARAM_ID));
        if(group == null) {
            throw new ActionParamsException("Couldn't find group by id");
        }
        final LicenseModel model = group.getModelById(params.getRequiredParam(PARAM_MODELID));
        if(model == null) {
            throw new ActionParamsException("Couldn't find model by id");
        }

        try {
            // populate model params from ActionParameters
            JSONArray list = JSONHelper.createJSONArray(params.getHttpParam(PARAM_DATA, "[]"));
            // TODO: maybe check that all required fields have been submitted?
            LicenseHelper.setValues(model, list);
        }
        catch (Exception ex ) {
            throw new ActionException("Error parsing license data", ex);
        }
        return model;
    }

}

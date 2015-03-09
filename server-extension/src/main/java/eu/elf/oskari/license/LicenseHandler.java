package eu.elf.oskari.license;

import eu.elf.license.LicenseService;
import eu.elf.license.conclude.LicenseConcludeResponseObject;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
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
        final List<LicenseModelGroup> licenseGroups = service.getLicenseGroups();
        final String url = params.getHttpParam(PARAM_ID);
        final User user = params.getUser();
        if(url != null) {
            final List<LicenseModelGroup> userLicenseGroups = service.getLicenseGroupsForUser(params.getUser().getScreenname());
            final LicenseModelGroup userLicense = service.getLicenseGroupsForURL(userLicenseGroups, url);
            // TODO: check the handling of case where user already has the license, service should tag group with a boolean or something?
            if(userLicense != null) {
                // User already has license, respond with it!
                // this doesn't need filtering by roles supposedly?
                final LicenseModelGroup userLicenseForUI = LicenseHelper.removeNonUIParams(userLicense);
                ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(userLicenseForUI));
                return;
            }
            // return the license info about the service for user to fill out
            final LicenseModelGroup group = service.getLicenseGroupsForURL(licenseGroups, url);
            if(group == null) {
                throw new ActionParamsException("Can't find license with url: " + url);
            }
            final LicenseModelGroup groupForUser = LicenseHelper.filterModelsByRoles(user, group);
            final LicenseModelGroup groupForUI = LicenseHelper.removeNonUIParams(groupForUser);
            ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(groupForUI));
        }
        else {
            final List<LicenseModelGroup> groupsForUser = LicenseHelper.filterModelsByRoles(user, licenseGroups);
            // note! non-ui params are not removed here!
            ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(groupsForUser));
        }
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        // Get Price
        final LicenseModel model = getModel(params);
        String price = service.getLicenseModelPrice(model, params.getUser().getScreenname());
        JSONObject resp = JSONHelper.createJSONObject(LicenseHelper.getAsJSON(model));
        JSONHelper.putValue(resp, KEY_PRICE, price);
        ResponseHelper.writeResponse(params, resp);
    }

    @Override
    public void handlePut(ActionParameters params) throws ActionException {
        // TODO: conclude the license
        final LicenseModel model = getModel(params);
        final LicenseConcludeResponseObject resp = service.concludeLicense(model, params.getUser().getScreenname());
        ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(resp));
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        final boolean success = service.deactivateLicense(params.getRequiredParam(PARAM_ID));
        ResponseHelper.writeResponse(params, JSONHelper.createJSONObject(KEY_SUCCESS, success));
    }

    private LicenseModel getModel(ActionParameters params) throws ActionException {
        // only users
        params.requireLoggedInUser();

        final List<LicenseModelGroup> licenseGroups = service.getLicenseGroups();
        final LicenseModelGroup group = service.getLicenseGroupsForURL(licenseGroups, params.getRequiredParam(PARAM_ID));
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

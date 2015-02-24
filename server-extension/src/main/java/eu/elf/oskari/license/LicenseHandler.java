package eu.elf.oskari.license;

import eu.elf.license.LicenseService;
import eu.elf.license.model.LicenseModelGroup;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import fi.nls.oskari.util.ResponseHelper;
import java.util.List;

import static fi.nls.oskari.control.ActionConstants.*;

/**
 * Handles the service licenses
 */
@OskariActionRoute("ELFLicense")
public class LicenseHandler extends RestActionHandler {

    private static final Logger log = LogFactory.getLogger(LicenseHandler.class);

    private LicenseService service = null;

    @Override
    public void init() {
        super.init();
        service = new LicenseService(
                PropertyUtil.getNecessary("actionhandler.ELFLicense.wpos.url"),
                PropertyUtil.getNecessary("actionhandler.ELFLicense.wpos.user"),
                PropertyUtil.getNecessary("actionhandler.ELFLicense.wpos.pass"),
                PropertyUtil.getNecessary("actionhandler.ELFLicense.wpos.soapUrl"));
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
                ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(userLicense));
                return;
            }
            // return the license info about the service for user to fill out
            final LicenseModelGroup group = service.getLicenseGroupsForURL(licenseGroups, url);
            if(group == null) {
                throw new ActionParamsException("Can't find license with url: " + url);
            }
            final LicenseModelGroup groupForUser = LicenseHelper.filterModelsByRoles(user, group);
            ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(groupForUser));
        }
        else {
            final List<LicenseModelGroup> groupsForUser = LicenseHelper.filterModelsByRoles(user, licenseGroups);
            ResponseHelper.writeResponse(params, LicenseHelper.getAsJSON(groupsForUser));
        }
    }

    @Override
    public void handlePost(ActionParameters params) throws ActionException {
        handlePut(params);
    }

    @Override
    public void handlePut(ActionParameters params) throws ActionException {
        // TODO: conclude the license
        //service.concludeLicense()
    }

    @Override
    public void handleDelete(ActionParameters params) throws ActionException {
        // TODO: deactivate license
        //service.deactivateLicense();
    }


}

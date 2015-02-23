package eu.elf.oskari.license;

import eu.elf.license.LicenseService;
import eu.elf.license.model.LicenseModelGroup;
import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.control.RestActionHandler;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import fi.nls.oskari.util.ResponseHelper;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;

import static fi.nls.oskari.control.ActionConstants.*;

/**
 * Handles the service licenses
 */
@OskariActionRoute("ELFLicense")
public class LicenseHandler extends RestActionHandler {

    private static final Logger log = LogFactory.getLogger(LicenseHandler.class);

    private LicenseService service = null;
    private static final ObjectMapper mapper = new ObjectMapper();

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
        if(url != null) {
            final List<LicenseModelGroup> userLicenseGroups = service.getLicenseGroupsForUser(params.getUser().getScreenname());
            final LicenseModelGroup userLicense = service.getLicenseGroupsForURL(userLicenseGroups, url);
            // TODO: check the handling of case where user already has the license
            if(userLicense != null) {
                // User already has license, respond with it!
                ResponseHelper.writeResponse(params, getAsJSON(userLicense));
                return;
            }
            // return the license info about the service for user to fill out
            final LicenseModelGroup group = service.getLicenseGroupsForURL(licenseGroups, url);
            if(group == null) {
                throw new ActionParamsException("Can't find license with url: " + url);
            }
            ResponseHelper.writeResponse(params, getAsJSON(group));
        }
        else {
            ResponseHelper.writeResponse(params, getAsJSON(licenseGroups));
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

    private String getAsJSON(Object param) {
        try {
            return mapper.writeValueAsString(param);
        } catch (Exception e) {
            log.error(e, "Exception writing license data to JSON");
        }
        return "";
    }

}

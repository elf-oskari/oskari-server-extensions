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
import fi.nls.oskari.util.ResponseHelper;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.List;

import static fi.nls.oskari.control.ActionConstants.*;

/**
 * Get list of localised names of ELF countries
 *
 * - names are based on ELF geolocator administrator names
 * - names are mapped to countries in the resource file geolocator-countries.json
 * - resource file is in oskari-server (oskari-search-nls) jar
 */
@OskariActionRoute("License")
public class LicenseHandler extends RestActionHandler {

    private static final Logger log = LogFactory.getLogger(LicenseHandler.class);

    private LicenseService service = null;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void init() {
        super.init();
        // TODO: setup url
        service = new LicenseService();
    }

    @Override
    public void handleGet(ActionParameters params) throws ActionException {
        final List<LicenseModelGroup> licenseGroups = service.getLicenseGroups();
        final String id = params.getHttpParam(PARAM_ID);
        if(id != null) {
            LicenseModelGroup group = service.getLicenseGroupsForURL(licenseGroups, id);
            if(group == null) {
                throw new ActionParamsException("Can't find license with url: " + id);
            }
            ResponseHelper.writeResponse(params, getAsJSON(group));
        }
        else {
            ResponseHelper.writeResponse(params, getAsJSON(licenseGroups));
        }
    }

    private String getAsJSON(Object param) {
        try {
            return mapper.writeValueAsString(param); // thread-safe
        } catch (Exception e) {
            log.error(e, "Exception writing license data to JSON");
        }
        return "";
    }

}

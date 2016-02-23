package eu.elf.oskari.sitemap;

import fi.nls.oskari.annotation.OskariViewModifier;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.ConversionHelper;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.view.modifier.ModifierException;
import fi.nls.oskari.view.modifier.ModifierParams;
import fi.nls.oskari.control.view.modifier.param.ParamHandler;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.StringWriter;


/**
 * Created by RLINKALA on 23.2.2016.
 */
@OskariViewModifier("metadata")
public class MetadataParamsHandler extends ParamHandler {


    private static final Logger log = LogFactory.getLogger(MetadataParamsHandler.class);


    public boolean handleParam(final ModifierParams params) throws ModifierException {
        log.debug("MetadataParam");
        if(params.getParamValue() == null) {
            return false;
        }
        log.debug("parmas: " + params.getParamValue());

        final JSONObject mapfullState = getBundleState(params.getConfig(), BUNDLE_MAPFULL);

        JSONHelper.putValue(mapfullState, "meta_uuid", params.getParamValue());
        return false;
    }

}

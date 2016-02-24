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
import org.json.JSONException;
import java.io.StringWriter;


/**
 * Created by RLINKALA on 23.2.2016.
 */
@OskariViewModifier("metadata")
public class MetadataParamsHandler extends ParamHandler {
    private static final Logger LOG = LogFactory.getLogger(MetadataParamsHandler.class);
	private static final String BUNDLE_METADATA_FLYOUT = "metadataflyout";
    private static final String KEY_ALLMETADATA = "allMetadata";
    private static final String KEY_UUID = "uuid";


    public boolean handleParam(final ModifierParams params) throws ModifierException {
        LOG.debug("MetadataParam");
        if(params.getParamValue() == null) {
            return false;
        }
        LOG.debug("params: " + params.getParamValue());

        try {
            final JSONObject metadataState = getBundleState(params.getConfig(), BUNDLE_METADATA_FLYOUT);

            final JSONArray allMetadata = new JSONArray();
            final JSONObject metadata = new JSONObject();
            metadata.put(KEY_UUID, params.getParamValue());
            allMetadata.put(metadata);

            JSONHelper.putValue(metadataState, KEY_ALLMETADATA, allMetadata);
        }
        catch (JSONException jex) {
            LOG.error("Couldn't handle metadata param", jex.getMessage());
        }
        return false;
    }

}

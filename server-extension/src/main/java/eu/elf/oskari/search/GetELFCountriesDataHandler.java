package eu.elf.oskari.search;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.control.ActionParamsException;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.search.util.ELFGeoLocatorCountries;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.PropertyUtil;
import fi.nls.oskari.util.ResponseHelper;
import fi.nls.oskari.search.channel.ELFGeoLocatorSearchChannel;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Get list of localised names of ELF countries
 *
 * - names are based on ELF geolocator administrator names
 * - names are mapped to countries in the resource file geolocator-countries.json
 * - resource file is in oskari-server (oskari-search-nls) jar
 */
@OskariActionRoute("GetELFCountriesData")
public class GetELFCountriesDataHandler extends ActionHandler {

    private static final Logger LOG = LogFactory.getLogger(GetELFCountriesDataHandler.class);

    private static final String LANG_PARAM = "lang";
    private ELFGeoLocatorCountries elfCountries = null;

    @Override
    public void init() {
        elfCountries = ELFGeoLocatorCountries.getInstance();
    }

    @Override
    public void handleAction(ActionParameters params) throws ActionException {

        final String lang = params.getRequiredParam(LANG_PARAM, PropertyUtil.getDefaultLanguage());
        Locale locale = new Locale(lang);
        JSONArray result = new JSONArray();

        try {
            for (String countryCode : elfCountries.getCountries()) {
                Locale countryLocale = new Locale("", countryCode);
                JSONObject item = new JSONObject();
                item.put("id", countryLocale.getCountry());
                item.put("value", countryLocale.getDisplayCountry(locale) );
                item.put("label", countryLocale.getDisplayCountry(locale) + "  (" + countryLocale.getCountry().toLowerCase() +")");
                result.put(item);
            }
        } catch (JSONException e) {
            throw new ActionParamsException("Failed to get ELF country codes: " + e.getMessage());
        }

        ResponseHelper.writeResponse(params, result);
    }
}

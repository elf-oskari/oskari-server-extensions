package eu.elf.oskari.search;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.service.OskariComponentManager;
import fi.nls.oskari.util.PropertyUtil;
import fi.nls.oskari.util.ResponseHelper;
import fi.nls.oskari.search.channel.ELFGeoLocatorSearchChannel;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import org.json.JSONArray;
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
    private Map<String, String> countryMap = null;

    @Override
    public void init() {
        ELFGeoLocatorSearchChannel elfchannel = new ELFGeoLocatorSearchChannel();
        elfchannel.init();
        try {
            countryMap = elfchannel.getElfCountryMap();
        } catch(Exception e) {
            LOG.warn("Error loading countries information");
        }
    }

    @Override
    public void handleAction(ActionParameters params) throws ActionException {
        if(countryMap.isEmpty()) {
            try {
                countryMap = ((ELFGeoLocatorSearchChannel) OskariComponentManager.getComponentsOfType(ELFGeoLocatorSearchChannel.class)
                        .get(ELFGeoLocatorSearchChannel.ID)).getElfCountryMap();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final String lang = params.getRequiredParam(LANG_PARAM, PropertyUtil.getDefaultLanguage());
        Locale locale = new Locale(lang);
        JSONArray result = new JSONArray();

        try {
            String[] locales = Locale.getISOCountries();

            for (String countryCode : locales) {

                Locale obj = new Locale("", countryCode);
                if(countryMap.containsKey(obj.getCountry().toLowerCase()))
                {
                    JSONObject item = new JSONObject();
                    item.put("id", obj.getCountry());
                    item.put("value", obj.getDisplayCountry(locale) );
                    item.put("label", obj.getDisplayCountry(locale) + "  (" + obj.getCountry().toLowerCase() +")");
                    result.put(item);

                }

            }
        } catch (Exception e) {
            throw new ActionException("Failed to get ELF country codes");

        }

        ResponseHelper.writeResponse(params, result);
    }
}

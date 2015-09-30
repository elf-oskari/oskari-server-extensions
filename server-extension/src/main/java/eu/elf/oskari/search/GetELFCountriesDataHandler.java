package eu.elf.oskari.search;

import fi.nls.oskari.annotation.OskariActionRoute;
import fi.nls.oskari.control.ActionException;
import fi.nls.oskari.control.ActionHandler;
import fi.nls.oskari.control.ActionParameters;
import fi.nls.oskari.util.ResponseHelper;
import fi.nls.oskari.search.channel.ELFGeoLocatorSearchChannel;

import java.util.Locale;

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
    private static final String LANG_PARAM = "lang";
    private  JSONObject countryMap = null;

    @Override
    public void init() {
        ELFGeoLocatorSearchChannel elfchannel = new ELFGeoLocatorSearchChannel();
        elfchannel.init();
        countryMap = elfchannel.getElfCountryMap();

    }

    @Override
    public void handleAction(ActionParameters params) throws ActionException {

        // TODO use default lang if not found?
        final String lang = params.getRequiredParam(LANG_PARAM);
        Locale locale = new Locale(lang);
        JSONArray result = new JSONArray();

        try {
            String[] locales = Locale.getISOCountries();

            for (String countryCode : locales) {

                Locale obj = new Locale("", countryCode);
                if(countryMap.has(obj.getCountry()))
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

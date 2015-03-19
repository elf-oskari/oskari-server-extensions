package eu.elf.oskari.search;

import fi.mml.portti.service.search.SearchResultItem;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.search.channel.MetadataCatalogueResultParser;
import fi.nls.oskari.util.PropertyUtil;
import org.apache.axiom.om.OMElement;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Hooking information about service license to metadata search results.
 * If the service URL starts with the configured prefix, url is added to the
 * search result as license url. Property to configure the prefix is:
 * "search.channel.METADATA_CATALOGUE_CHANNEL.licenseUrlPrefix"
 */
public class MetadataCatalogueELFResultParser extends MetadataCatalogueResultParser {

    private static final Logger log = LogFactory.getLogger(MetadataCatalogueELFResultParser.class);

    public static final String KEY_LICENSE = "license";
    public static final String KEY_RATING = "rating";
    private String urlPrefix = PropertyUtil.getOptional("search.channel.METADATA_CATALOGUE_CHANNEL.licenseUrlPrefix");

    @Override
    public SearchResultItem parseResult(OMElement elem, String locale) throws Exception {
        final SearchResultItem item = super.parseResult(elem, locale);
        log.debug("Parsed service url for", item.getResourceId(), ":", item.getGmdURL());
        // Check OnlineResource URL: if matches securitymanagerin url -> setup KEY_LICENSE = service url
        if(urlPrefix != null && item.getGmdURL() != null && item.getGmdURL().startsWith(urlPrefix)) {
            item.addValue(KEY_LICENSE, item.getGmdURL());
        }
        item.addValue(KEY_RATING, getRatingForSearchResult(item));

        return item;
    }

    /**
     * Get ratings from rating service (geoviqua)
     *
     * @param item
     * @return
     */
    private String getRatingForSearchResult(SearchResultItem item){
        item.getResourceId();

        // Testing
        String url = "http://dev.paikkatietoikkuna.fi/gf/api/v1/feedback/items/1/?format=xml";
        try{

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "moz");

            int responseCode = con.getResponseCode();
            log.debug("\nSending 'GET' request to URL : " + url);
            log.debug("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            log.debug("GOT FROM SERVICE:");
            log.debug(response.toString());
        }catch (Exception e){
            log.debug("reading from service failed");
            log.debug(e.toString());
        }


        return "4";
    }
}

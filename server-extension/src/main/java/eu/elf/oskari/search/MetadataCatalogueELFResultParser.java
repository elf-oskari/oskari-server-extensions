package eu.elf.oskari.search;

import fi.mml.portti.service.search.SearchResultItem;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.search.channel.MetadataCatalogueResultParser;
import fi.nls.oskari.util.PropertyUtil;
import org.apache.axiom.om.OMElement;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
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
    public static final String AVERAGE_RATING_NODE = "gvq:averageRating";
    //TODO move this to prperies file
    public static final String RATING_SERVER_URL = "https://geoviqua.stcorp.nl/devel/api/v1/feedback/collections/?format=xml";

    private String urlPrefix = PropertyUtil.getOptional("search.channel.METADATA_CATALOGUE_CHANNEL.licenseUrlPrefix");

    @Override
    public SearchResultItem parseResult(OMElement elem, String locale) throws Exception {
        final SearchResultItem item = super.parseResult(elem, locale);
        log.debug("Parsed service url for", item.getResourceId(), ":", item.getGmdURL());

        // Check OnlineResource URL: if matches securitymanagerin url -> setup KEY_LICENSE = service url
        if(urlPrefix != null && item.getGmdURL() != null && item.getGmdURL().startsWith(urlPrefix)) {
            item.addValue(KEY_LICENSE, item.getGmdURL());
        }

        String rating = getRatingForSearchResult(item, "https://geoviqua.stcorp.nl/devel/api/v1/feedback/collections/?format=xml&target_code=datasetti&target_codespace=mml.fi");

        log.debug("ResourceId: " + item.getResourceId());
        log.debug("ActionURL: " + item.getActionURL());
        item.addValue(KEY_RATING, rating);

        return item;
    }

    /**
     * Get ratings from rating service (geoviqua)
     *
     * @param item
     * @return
     */
    private String getRatingForSearchResult(SearchResultItem item, String url){
        String resourceId = item.getResourceId();
        log.debug("resourceId: " + resourceId);

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


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(con.getInputStream());

            String averageValue = parseAverageValueFromXMLDocument(doc);

        }catch (Exception e){
            log.debug("reading from service failed");
            log.debug(e.toString());
        }
        // for test purposes returning 4
        return "4";
    }


    private String parseAverageValueFromXMLDocument(Document doc){
        String value = null;

        try{
            NodeList nodeList = doc.getElementsByTagName(AVERAGE_RATING_NODE);
            for(int i = 0; i < nodeList.getLength(); i++){
                log.debug("Rating average: " + nodeList.item(i).getFirstChild().getNodeValue());
                value = nodeList.item(i).getFirstChild().getNodeValue();
            }

            XPath xPath =  XPathFactory.newInstance().newXPath();
            value = xPath.compile("/gvq:collection/gvq:GVQ_FeedbackCollection/gvq:summary/gvq:averageRating").evaluate(doc);


        }catch(Exception e){
            log.warn(e.getMessage());
            e.printStackTrace();
        }
        return value;
    }

}

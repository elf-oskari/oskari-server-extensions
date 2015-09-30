package eu.elf.oskari.metadatafeedback;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.PropertyUtil;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by RLINKALA on 7.5.2015.
 */
public class MetadataFeedback {

    private static final Logger log = LogFactory.getLogger(MetadataFeedback.class);

    public static String getAverage(String resourceId){


        String feedbackServer = PropertyUtil.get("oskari.elf.feedback.server");
        if(feedbackServer == null || feedbackServer.equals("")){
            log.error("Feedback server missing!!!! (>oskari.elf.feedback.server< property missing from properties file)");
        }

        String rating = null;
        try{

            StringBuffer sb = new StringBuffer(feedbackServer);
            sb.append("collections/?format=xml&target_code=");
            sb.append(resourceId);
            sb.append("&target_codespace=ELF_METADATA");

            log.debug("Searching from feedback server: " + sb.toString());
            //String rating = getRatingForSearchResult(item, sb.toString());
            rating = getRatingForSearchResult(sb.toString());
            log.debug("rating: " + rating);
        }catch(Exception e){
            e.printStackTrace();
            log.debug("error when getting rating");
        }

        return rating;
    }


    private static String getRatingForSearchResult(String url) {
        String averageValue = null;

        try {

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            log.debug("\nSending 'GET' request to URL : " + url);
            log.debug("Response Code : " + responseCode);


            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(con.getInputStream());

            averageValue = parseAverageValueFromXMLDocument(doc);

            log.debug("AverageValue: " + averageValue);
        } catch (Exception e) {
            log.debug("reading from service failed");
            log.debug(e.toString());
        }
        return averageValue;
    }


    private static String parseAverageValueFromXMLDocument(Document doc){
        String value = null;

        try{
            NodeList nodeList = doc.getElementsByTagName("gvq:averageRating");
            for(int i = 0; i < nodeList.getLength(); i++){
                log.debug("Rating average: " + nodeList.item(i).getFirstChild().getNodeValue());
                value = nodeList.item(i).getFirstChild().getNodeValue();
            }
            //XPath xPath =  XPathFactory.newInstance().newXPath();
            //value = xPath.compile("/gvq:collection/gvq:GVQ_FeedbackCollection/gvq:summary/gvq:averageRating").evaluate(doc);

        }catch(Exception e){
            log.warn(e.getMessage());
            e.printStackTrace();
        }
        return value;
    }

}

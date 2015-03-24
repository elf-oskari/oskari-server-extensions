/**
 * LicenseQueryHandler Sends queries to the WPOS API
 *
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */

package eu.elf.license;

import elf.license.licensedetails.GetLicenseDetailsQueryPojo;
import eu.elf.license.conclude.LicenseConcludeResponseObject;
import eu.elf.license.model.*;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.IOHelper;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.w3c.dom.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.List;

public class LicenseQueryHandler {

    private static final Logger log = LogFactory.getLogger(LicenseQueryHandler.class);
    private String WPOSServiceAddress = "";
    private String SOAPAddress = "";
    URL wposURL = null;
    URL soapURL = null;
    private String username = null;
    private String password = null;


    /**
     * Constructor
     *
     * @param WPOSServiceAddress - Address of the WPOS Service
     * @param WPOSUsername       - WPOS Service user's username
     * @param WPOSPassword       - WPOS Service user's password
     * @param SOAPAddress
     * @throws MalformedURLException
     */
    public LicenseQueryHandler(String WPOSServiceAddress, String WPOSUsername, String WPOSPassword, String SOAPAddress) throws MalformedURLException {
        try {
            this.WPOSServiceAddress = WPOSServiceAddress;
            this.SOAPAddress = SOAPAddress;
            this.wposURL = new URL(this.WPOSServiceAddress);
            this.soapURL = new URL(this.SOAPAddress);
            this.username = WPOSUsername;
            this.password = WPOSPassword;

        } catch (MalformedURLException mue) {
            throw mue;
        }
    }


    public String getWPOSServiceAddress() {
        return WPOSServiceAddress;
    }

    public void setWPOSServiceAddress(String wPOSServiceAddress) {
        WPOSServiceAddress = wPOSServiceAddress;
    }


    /**
     * Makes GetCapabilities query to the WPOS Service
     *
     * @return WPOS Service response as XML string
     * @throws IOException
     */
    public String getCapabilities() throws IOException {
        StringBuffer buf = null;

        try {
            buf = doHTTPQuery(this.wposURL, "get", "", false);

        } catch (IOException ioe) {
            throw ioe;
        }

        return buf.toString();
    }


    /**
     * Gets the list of available licenses in the WPOS Service
     * Performs GetCatalog query to the wpos API
     *
     * @return WPOS Service response as XML
     * @throws IOException
     */
    public String getListOfLicensesAsXMLString() throws IOException {
        StringBuffer buf = null;

        String wposQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" version=\"1.1.0\">" +
                "<wpos:GetCatalog id=\"ct-license-manager\" brief=\"true\" onlyDirectChilds=\"false\" noChilds=\"false\" />" +
                "</wpos:WPOSRequest>";

        try {
            buf = doHTTPQuery(this.wposURL, "post", wposQuery, false);

        } catch (IOException ioe) {
            throw ioe;
        }

        return buf.toString();
    }

    /**
     * Gets the list of available licenses As List of LicenseModelGroup objects
     * Performs GetCatalog query to the wpos API
     *
     * @return WPOS Service response as List of LicenseModelGroup objects
     * @throws IOException
     */
    public List<LicenseModelGroup> getListOfLicensesAsLicenseModelGroupList() throws Exception {
        String wposQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" version=\"1.1.0\">" +
                "<wpos:GetCatalog id=\"ct-license-manager\" brief=\"false\" onlyDirectChilds=\"false\" noChilds=\"false\" />" +
                "</wpos:WPOSRequest>";

        try {
            StringBuffer buf = doHTTPQuery(this.wposURL, "post", wposQuery, false);

            //System.out.println("buf "+buf.toString());

            return LicenseParser.parseListOfLicensesAsLicenseModelGroupList(buf.toString());

        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Gets the list of licenses that the user has concluded from the WPOS Service
     *
     * @param user - user name
     * @return WPOS service response
     * @throws IOException
     */
    public String getUserLicensesAsXMLString(String user) throws Exception {

        String getUserLicensesQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" xmlns:xcpf=\"http://www.conterra.de/xcpf/1.1\" version=\"1.1.0\">" +
                "<wpos:GetOrderList brief=\"false\">" +
                "<wpos:Filter>" +
                "<wpos:CustomerId>" + StringEscapeUtils.escapeXml10(user) + "</wpos:CustomerId>" +
                "</wpos:Filter>" +
                "</wpos:GetOrderList>" +
                "</wpos:WPOSRequest>";

        try {
            return doHTTPQuery(this.wposURL, "post", getUserLicensesQuery, false).toString();

        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Gets the list of licenses that the user has concluded from the WPOS Service
     *
     * @param user - user name
     * @return WPOS service response
     * @throws IOException
     */
    //public List<LicenseModelGroup> getUserLicensesAsLicenseModelGroupList(String user) throws Exception {
    public UserLicenses getUserLicensesAsLicenseUserLicensesObject(String user) throws Exception {

        String getUserLicensesQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" xmlns:xcpf=\"http://www.conterra.de/xcpf/1.1\" version=\"1.1.0\">" +
                "<wpos:GetOrderList brief=\"false\">" +
                "<wpos:Filter>" +
                "<wpos:CustomerId>" + StringEscapeUtils.escapeXml10(user) + "</wpos:CustomerId>" +
                "</wpos:Filter>" +
                "</wpos:GetOrderList>" +
                "</wpos:WPOSRequest>";

        final String response = doHTTPQuery(this.wposURL, "post", getUserLicensesQuery, false).toString();
        UserLicenses userLicenses = LicenseParser.parseUserLicensesAsLicenseModelGroupList(response);
        if(userLicenses.getUserLicenses() != null && userLicenses.getUserLicenses().size() > 0) {
            log.debug("User licenses found: ", userLicenses.getUserLicenses().size());
        }
        else {
            log.debug("User has no licenses");
        }

        return userLicenses;
    }


    /**
     * Gets the details of the specific license
     *
     * @param licenseId - License identifier
     * @return WPOS Service response
     * @throws IOException
     */
    public GetLicenseDetailsQueryPojo getLicenseDetails(String licenseId) throws IOException, Exception {
        StringBuffer buf = null;
        GetLicenseDetailsQueryPojo pojo = null;

        String wposQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" version=\"1.1.0\">" +
                "<wpos:GetPriceModel collapse=\"false\">" +
                "<wpos:Product>" +
                "<wpos:id>" + StringEscapeUtils.escapeXml10(licenseId) + "</wpos:id>" +
                "</wpos:Product>" +
                "</wpos:GetPriceModel>" +
                "</wpos:WPOSRequest>";

        try {
            buf = doHTTPQuery(this.wposURL, "post", wposQuery, false);

            pojo = new GetLicenseDetailsQueryPojo(buf.toString());
            //System.out.println("out: "+pojo.getXMLRepresentation());

        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            throw e;
        }

        return pojo;
    }


    /**
     * Gets the price of the License Model
     *
     * @param lm     - LicenseModel object
     * @param userId - UserId
     * @throws Exception
     * @return         - ProductPriceSum as String
     */
    public String getLicenseModelPrice(LicenseModel lm, String userId) throws Exception {
        StringBuffer buf = null;
        String productPriceSum = "";
        List<LicenseParam> lpList = lm.getParams();

        String productPriceQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" version=\"1.1.0\">" +
                "<wpos:GetPrice collapse=\"true\">" +
                "<wpos:Product id=\"" + StringEscapeUtils.escapeXml10(lm.getId()) + "\">" +
                "<wpos:ConfigParams>";

        for (int i = 0; i < lpList.size(); i++) {
            if (lpList.get(i).getParameterClass().equals("configurationParameter")) {
                LicenseParam lp = (LicenseParam) lpList.get(i);

                productPriceQuery += "<wpos:Parameter name=\"" + StringEscapeUtils.escapeXml10(lp.getName()) + "\">";
                //System.out.println(lp.getName()+"  "+lp.getParameterClass());


                if (lp instanceof LicenseParamInt) {
                    LicenseParamInt lpi = (LicenseParamInt) lp;
                    //System.out.println("int "+lpi.getName());

                    productPriceQuery += "<wpos:Value selected=\"true\">" + lpi.getValue() + "</wpos:Value>" +
                            "</wpos:Parameter>";
                } else if (lp instanceof LicenseParamBln) {
                    LicenseParamBln lpBln = (LicenseParamBln) lp;
                    //System.out.println("Bln "+lpBln.getName());

                    productPriceQuery += "<wpos:Value selected=\"true\">" + lpBln.getValue() + "</wpos:Value>" +
                            "</wpos:Parameter>";
                } else if (lp instanceof LicenseParamDisplay) {
                    LicenseParamDisplay lpd = (LicenseParamDisplay) lp;
                    List<String> values = lpd.getValues();

                    //System.out.println("Display "+lpd.getName());

                    if (lp.getName().equals("LICENSE_USER_GROUP")) {
                        productPriceQuery += "<wpos:Value>Users</wpos:Value>";

                    } else if (lp.getName().equals("LICENSE_USER_ID")) {
                        productPriceQuery += "<wpos:Value>" + userId + "</wpos:Value>";

                    } else {
                        for (int l = 0; l < values.size(); l++) {
                            productPriceQuery += "<wpos:Value selected=\"true\">" + StringEscapeUtils.escapeXml10(values.get(l)) + "</wpos:Value>";
                        }
                    }

                    productPriceQuery += "</wpos:Parameter>";
                } else if (lp instanceof LicenseParamEnum) {
                    LicenseParamEnum lpEnum = (LicenseParamEnum) lp;
                    //System.out.println("Enum "+lpEnum.getName());

                    List<String> tempOptions = lpEnum.getOptions();
                    List<String> tempSelections = lpEnum.getSelections();

                    if (tempSelections.size() == 0) {
                        productPriceQuery += "<wpos:Value selected=\"true\">" + StringEscapeUtils.escapeXml10(tempOptions.get(0)) + "</wpos:Value>" +
                                "</wpos:Parameter>";
                    } else {
                        for (int j = 0; j < tempSelections.size(); j++) {
                            productPriceQuery += "<wpos:Value selected=\"true\">" + StringEscapeUtils.escapeXml10(tempSelections.get(j)) + "</wpos:Value>" +
                                    "</wpos:Parameter>";
                        }
                    }
                } else if (lp instanceof LicenseParamText) {
                    LicenseParamText lpText = (LicenseParamText) lp;
                    //System.out.println("Text "+lpText.getName());

                    List<String> values = lpText.getValues();

                    for (int k = 0; k < values.size(); k++) {
                        productPriceQuery += "<wpos:Value selected=\"true\">" + lpText.getValues() + "</wpos:Value>";
                    }
                    productPriceQuery += "</wpos:Parameter>";
                }


            }

        }

        productPriceQuery += "</wpos:ConfigParams>" +
                "</wpos:Product>" +
                "</wpos:GetPrice>" +
                "</wpos:WPOSRequest>";

        //System.out.println("query: "+productPriceQuery.toString());


        try {
            buf = doHTTPQuery(this.wposURL, "post", productPriceQuery, false);
            //System.out.println("response: "+buf.toString());


            // Get productPriceInfo from the response
            Document xmlDoc = LicenseParser.createXMLDocumentFromString(buf.toString());
            Element catalogElement = (Element) xmlDoc.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "catalog").item(0);
            NodeList productGroupElementList = catalogElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "productGroup");

            for (int m = 0; m < productGroupElementList.getLength(); m++) {
                Element productGroupElement = (Element) productGroupElementList.item(m);
                Element calculationElement = (Element) productGroupElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "calculation").item(0);
                Element declarationListElement = (Element) calculationElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "declarationList").item(0);
                Element referencedParametersElement = (Element) declarationListElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "referencedParameters").item(0);
                NodeList referencedParametersParameterList = referencedParametersElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "parameter");

                for (int n = 0; n < referencedParametersParameterList.getLength(); n++) {
                    Element referencedParametersParameterElement = (Element) referencedParametersParameterList.item(n);

                    NamedNodeMap referencedParametersParameterAttributeMap = referencedParametersParameterElement.getAttributes();

                    for (int o = 0; o < referencedParametersParameterAttributeMap.getLength(); o++) {
                        Attr attrs = (Attr) referencedParametersParameterAttributeMap.item(o);

                        if (attrs.getNodeName().equals("name")) {
                            if (attrs.getNodeValue().equals("productPriceSum")) {
                                Element valueElement = (Element) referencedParametersParameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "value").item(0);

                                productPriceSum = valueElement.getTextContent();
                            }
                        }
                    }

                }


            }

        } catch (Exception e) {
            throw e;
        }

        return productPriceSum;
    }


    /**
     * Send ConcludeLicense query to the WPOS Service, Return response as LicenseConcludeResponseObject
     *
     * @param orderProductQuery - pre-formatted conclude query
     * @throws Exception
     * @return LicenseConcludeResponseObject
     */
    public LicenseConcludeResponseObject concludeLicenseObjectResponse(String orderProductQuery) throws Exception {
        //log.debug("Concluding license with payload:\n", orderProductQuery);
        StringBuffer buf = doHTTPQuery(this.wposURL, "post", orderProductQuery, false);
        String response = buf.toString();
        //log.debug("Conclude response was:\n", response);
        //System.out.println("response "+buf.toString());
        if (response.contains("ExceptionReport")) {
            return null;
        } else {
            return LicenseParser.parseConcludeLicenseResponse(response);
        }

    }

    /**
     * Send ConcludeLicense query to the WPOS Service, Return response as LicenseConcludeResponseObject
     *
     * @param lm     - License Model to be concluded with selected values for configrurationParameters
     * @param userId - userid
     * @return LicenseConcludeResponseObject
     * @throws Exception
     */
    public LicenseConcludeResponseObject concludeLicenseObjectResponse(LicenseModel lm, String userId) throws Exception {
        // create query string
        final String wposQuery = createOrderProductQueryFromLicenseModel(lm, userId);

        //System.out.println("wposquery "+wposQuery);

        return concludeLicenseObjectResponse(wposQuery);
    }

    /**
     * ConcludeLicense query to the WPOS Service
     *
     * @param lm     - License Model to be concluded with selected values for configrurationParameters
     * @param userId - userid
     * @return Wpos response as String
     * @throws Exception
     */
    public String concludeLicenseStringResponse(LicenseModel lm, String userId) throws Exception {
        String wposQuery = createOrderProductQueryFromLicenseModel(lm, userId);
        return doHTTPQuery(this.wposURL, "post", wposQuery, false).toString();
    }


    /**
     * Creates OrderProductQuery from LicenseModel that is used for Concluding the License
     *
     * @param lm     - License Model to be concluded with selected values for configrurationParameters
     * @param userId - userid
     * @return OrderProduct query string for the WPOS Service
     * @throws Exception
     */
    private String createOrderProductQueryFromLicenseModel(LicenseModel lm, String userId) throws Exception {
        List<LicenseParam> lpList = lm.getParams();

        // create query string
        String wposQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" xmlns:xcpf=\"http://www.conterra.de/xcpf/1.1\" version=\"1.1.0\">" +
                "<wpos:OrderProduct brief=\"true\">" +
                "<wpos:Customer>" +
                "<xcpf:UserIdentifier>" + userId + "</xcpf:UserIdentifier>" +
                "</wpos:Customer>" +
                "<wpos:Product id=\"" + lm.getId() + "\">" +
                "<wpos:ConfigParams>";

        for (int i = 0; i < lpList.size(); i++) {
            if (lpList.get(i).getParameterClass().equals("configurationParameter")) {

                if (lpList.get(i).getName().equals("LICENSE_USER_GROUP")) {
                    wposQuery += "<wpos:Parameter name=\"" + StringEscapeUtils.escapeXml10(lpList.get(i).getName()) + "\">" +
                            "<wpos:Value>Users</wpos:Value>" +
                            "</wpos:Parameter>";
                } else if (lpList.get(i).getName().equals("LICENSE_USER_ID")) {
                    wposQuery += "<wpos:Parameter name=\"" + StringEscapeUtils.escapeXml10(lpList.get(i).getName()) + "\">" +
                            "<wpos:Value selected=\"true\">" + userId + "</wpos:Value>" +
                            "</wpos:Parameter>";
                } else if (lpList.get(i).getName().equals("LICENSE_USER_TYPE")) {
                    wposQuery += "<wpos:parameter name=\"" + StringEscapeUtils.escapeXml10(lpList.get(i).getName()) + "\" type=\"string\" multi=\"false\">" +
                            "<wpos:value title=\"Individual\" selected=\"true\">SubjectId</wpos:value>" +
                            "</wpos:parameter>";
                } else if (lpList.get(i).getName().equals("LICENSE_DURATION")) {
                    wposQuery += "<wpos:Parameter name=\"" + StringEscapeUtils.escapeXml10(lpList.get(i).getName()) + "\">";

                    LicenseParamEnum duration = (LicenseParamEnum) lpList.get(i);

                    wposQuery += "<wpos:Value selected=\"true\">" + StringEscapeUtils.escapeXml10(duration.getSelections().get(0)) + "</wpos:Value>" +
                            "</wpos:Parameter>";
                } else {

                    if (lpList.get(i) instanceof LicenseParamText) {
                        LicenseParamText lpt = (LicenseParamText) lpList.get(i);

                        wposQuery += "<wpos:parameter name=\"" + StringEscapeUtils.escapeXml10(lpt.getName()) + "\" type=\"string\">" +
                                "<wpos:value />" +
                                "</wpos:parameter>";
                    }
                    if (lpList.get(i) instanceof LicenseParamInt) {
                        LicenseParamInt lpi = (LicenseParamInt) lpList.get(i);

                        wposQuery += "<wpos:parameter name=\"" + StringEscapeUtils.escapeXml10(lpi.getName()) + "\" type=\"real\">" +
                                "<wpos:value>" + lpi.getValue() + "</wpos:value>" +
                                "</wpos:parameter>";
                    }
                    if (lpList.get(i) instanceof LicenseParamBln) {
                        LicenseParamBln lpbln = (LicenseParamBln) lpList.get(i);

                        wposQuery += "<wpos:parameter name=\"" + StringEscapeUtils.escapeXml10(lpbln.getName()) + "\" type=\"boolean\">" +
                                "<wpos:value>" + lpbln.getValue() + "</wpos:value>" +
                                "</wpos:parameter>";
                    }
                    if (lpList.get(i) instanceof LicenseParamEnum) {

                        LicenseParamEnum lpenum = (LicenseParamEnum) lpList.get(i);

                        if (lpenum.isMulti()) {
                            wposQuery += "<wpos:parameter name=\"" + StringEscapeUtils.escapeXml10(lpenum.getName()) + "\" type=\"string\" multi=\"true\" optional=\"true\">";

                            for (int j = 0; j < lpenum.getOptions().size(); j++) {
                                if (CheckIfListOfStringContainsValue(lpenum.getSelections(), lpenum.getOptions().get(j)) == true) {
                                    wposQuery += "<wpos:value selected=\"true\">" + StringEscapeUtils.escapeXml10(lpenum.getOptions().get(j)) + "</wpos:value>";
                                } else {
                                    wposQuery += "<wpos:value selected=\"false\">" + StringEscapeUtils.escapeXml10(lpenum.getOptions().get(j)) + "</wpos:value>";
                                }

                            }

                            wposQuery += "</wpos:parameter>";
                        } else {
                            wposQuery += "<wpos:parameter name=\"" + StringEscapeUtils.escapeXml10(lpenum.getName()) + "\" type=\"string\" multi=\"false\">";

                            for (int k = 0; k < lpenum.getOptions().size(); k++) {
                                if (CheckIfListOfStringContainsValue(lpenum.getSelections(), lpenum.getOptions().get(k)) == true) {
                                    wposQuery += "<wpos:value selected=\"true\">" + StringEscapeUtils.escapeXml10(lpenum.getOptions().get(k)) + "</wpos:value>";
                                } else {
                                    wposQuery += "<wpos:value>" + StringEscapeUtils.escapeXml10(lpenum.getOptions().get(k)) + "</wpos:value>";
                                }
                            }

                            wposQuery += "</wpos:parameter>";
                        }


                    }

                }

            }
        }

        wposQuery += "</wpos:ConfigParams>" +
                "</wpos:Product>" +
                "</wpos:OrderProduct>" +
                "</wpos:WPOSRequest>";

        return wposQuery;
    }


    /**
     * Check if list of strings contains a specific string
     *
     * @param list  - list of strings
     * @param value - string to compare
     * @return true or false
     */
    private Boolean CheckIfListOfStringContainsValue(List<String> list, String value) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).equals(value)) {
                return true;
            }
        }

        return false;
    }


    /**
     * Deactivate license
     *
     * @param bcs
     * @param licenseId
     * @throws ClientProtocolException
     * @throws IOException
     */
    public Boolean deactivateLicense(BasicCookieStore bcs, String licenseId) throws ClientProtocolException, IOException {
        String SOAPAction = "http://security.conterra.de/LicenseManager/DeactivateLicense";

        String deactivateLicenseQuery = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<Envelope xmlns=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<Body>" +
                "<licmanp:DeactivateLicense ID=\"B1202458930484\" Version=\"2.0\" IssueInstant=\"2001-12-17T09:30:47.0Z\" " +
                " xmlns:licmanp=\"http://www.52north.org/licensemanagerprotocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
                "<saml:AssertionIDRef>" + StringEscapeUtils.escapeXml10(licenseId) + "</saml:AssertionIDRef>" +
                "</licmanp:DeactivateLicense>" +
                "</Body>" +
                "</Envelope>";

        Boolean success = sendSOAP(bcs, deactivateLicenseQuery, SOAPAction);

        return success;
    }

    /**
     * Activate license
     *
     * @param bcs
     * @param licenseId
     * @throws ClientProtocolException
     * @throws IOException
     */
    public Boolean activateLicense(BasicCookieStore bcs, String licenseId) throws ClientProtocolException, IOException {
        String SOAPAction = "http://security.conterra.de/LicenseManager/ActivateLicense";

        String activateLicenseQuery = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<Envelope xmlns=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<Body>" +
                "<licmanp:ActivateLicense ID=\"B1202458930484\" Version=\"2.0\" IssueInstant=\"2001-12-17T09:30:47.0Z\" " +
                " xmlns:licmanp=\"http://www.52north.org/licensemanagerprotocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
                "<saml:AssertionIDRef>" + StringEscapeUtils.escapeXml10(licenseId) + "</saml:AssertionIDRef>" +
                "</licmanp:ActivateLicense>" +
                "</Body>" +
                "</Envelope>";

        Boolean success = sendSOAP(bcs, activateLicenseQuery, SOAPAction);

        return success;
    }

    /**
     * Deactivate license
     *
     * @param bcs
     * @param licenseId
     * @throws ClientProtocolException
     * @throws IOException
     */
    public Boolean deleteLicense(BasicCookieStore bcs, String licenseId) throws ClientProtocolException, IOException {
        String SOAPAction = "http://security.conterra.de/LicenseManager/DeleteLicense";

        String deleteLicenseQuery = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                "<Envelope xmlns=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<Body>" +
                "<licmanp:DeleteLicense ID=\"B1202458930484\" Version=\"2.0\" IssueInstant=\"2001-12-17T09:30:47.0Z\" " +
                " xmlns:licmanp=\"http://www.52north.org/licensemanagerprotocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
                "<saml:AssertionIDRef>" + StringEscapeUtils.escapeXml10(licenseId) + "</saml:AssertionIDRef>" +
                "</licmanp:DeleteLicense>" +
                "</Body>" +
                "</Envelope>";

        Boolean success = sendSOAP(bcs, deleteLicenseQuery, SOAPAction);

        return success;
    }


    /**
     * Send SOAP Message for activating/deactivating/deleting a license
     *
     * @param bcs
     * @param query
     * @param SOAPAction
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private Boolean sendSOAP(BasicCookieStore bcs, String query, String SOAPAction) throws IOException {

        log.debug("Querying SOAP - URL:", SOAPAddress, "- Payload:\n", query);
        InputStream response = null;
        try {
            List<Cookie> cookieList = bcs.getCookies();
            StringWriter cookieHeader = new StringWriter();
            for (Cookie c : cookieList) {
                cookieHeader.append(c.getName());
                cookieHeader.append("=");
                cookieHeader.append(c.getValue());
                cookieHeader.append("; ");
            }
            HttpURLConnection conn = IOHelper.getConnection(SOAPAddress);
            IOHelper.setContentType(conn, "text/xml; charset=utf-8");
            IOHelper.writeHeader(conn, "SOAPAction", SOAPAction);
            IOHelper.writeHeader(conn, "Cookie", cookieHeader.toString());

            IOHelper.writeToConnection(conn, query);
            response = conn.getInputStream();
            IOHelper.debugResponse(response);
            int code = conn.getResponseCode();
            if (code == 200) {
                return true;
            } else {
                return false;
            }
        } finally {
            IOHelper.close(response);
        }


    }


    /**
     * Performs HTTP GET or POST query to the backend server
     *
     * @param queryURL       The request string that is to be sent to the server
     * @param queryType      - Type of the query (get || post)
     * @param payloadForPost - POST payload, empty string for GET queries
     * @return The response of the server
     * @throws IOException
     */
    public StringBuffer doHTTPQuery(URL queryURL, String queryType, String payloadForPost, Boolean useBasicAuthentication) throws IOException {
        StringBuffer sbuf = new StringBuffer();
        URLConnection uc = null;
        HttpURLConnection httpuc;
        HttpsURLConnection httpsuc;
        String WFSResponseLine = "";
        String userPassword = "";
        String encoding = "";

        //System.out.println("queryURL: "+queryURL.toString());
        //System.out.println("PayloadForPost: "+payloadForPost);

        try {

            httpuc = (HttpURLConnection) queryURL.openConnection();
            httpuc.setDoInput(true);
            httpuc.setDoOutput(true);

            if (useBasicAuthentication == true) {
                userPassword = this.username + ":" + this.password;
                encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
                httpuc.setRequestProperty("Authorization", "Basic " + encoding);
            }

            if (queryType.equals("get")) {
                //System.out.println("queryURL: "+queryURL);

                BufferedReader buf = new BufferedReader(new InputStreamReader(httpuc.getInputStream(), "utf-8"));

                while ((WFSResponseLine = buf.readLine()) != null) {
                    sbuf.append(WFSResponseLine);
                }

                buf.close();
            } else if (queryType.equals("post")) {
                //System.out.println("PayloadForPost"+payloadForPost);

                httpuc.setRequestMethod("POST");
                httpuc.setRequestProperty("Content-Type", "text/xml;charset=UTF8");
                httpuc.setRequestProperty("Content-Type", "text/plain");

                OutputStreamWriter osw = new OutputStreamWriter(httpuc.getOutputStream(), Charset.forName("UTF-8"));
                osw.write(URLDecoder.decode(payloadForPost, "UTF-8"));

                osw.flush();

                BufferedReader in = new BufferedReader(new InputStreamReader(httpuc.getInputStream(), Charset.forName("UTF8")));

                while ((WFSResponseLine = in.readLine()) != null) {
                    //System.out.println("WFSResponseLine: "+WFSResponseLine);

                    sbuf.append(WFSResponseLine);
                }

                in.close();
                osw.close();
            }


        } catch (IOException ioe) {
            throw ioe;
        }

        return sbuf;
    }


}

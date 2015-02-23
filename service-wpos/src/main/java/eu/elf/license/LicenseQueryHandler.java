/**
 * LicenseQueryHandler Sends queries to the WPOS API
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */

package eu.elf.license;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2Impl;

import elf.license.licensedetails.GetLicenseDetailsQueryPojo;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import eu.elf.license.model.LicenseParam;
import eu.elf.license.model.LicenseParamBln;
import eu.elf.license.model.LicenseParamDisplay;
import eu.elf.license.model.LicenseParamEnum;
import eu.elf.license.model.LicenseParamInt;
import eu.elf.license.model.LicenseParamText;


public class LicenseQueryHandler {
	private String WPOSServiceAddress = "";
	private String SOAPAddress = "";
	URL wposURL = null;
	URL soapURL = null;
	private boolean proxyEnabled = false;
	private String username = null;
	private String password = null;
	
	
	/**
	 * Constructor
	 * 
	 * @param WPOSServiceAddress - Address of the WPOS Service
	 * @param WPOSUsername - WPOS Service user's username
	 * @param WPOSPassword - WPOS Service user's password
	 * @param SOAPAddress
	 * @throws MalformedURLException
	 */
	LicenseQueryHandler(String WPOSServiceAddress, String WPOSUsername, String WPOSPassword, String SOAPAddress) throws MalformedURLException {
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
	 * @throws IOException 
	 * @return WPOS Service response as XML string
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
			
		String wposQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
						   "<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" version=\"1.1.0\">"+
						   "<wpos:GetCatalog id=\"ct-license-manager\" brief=\"true\" onlyDirectChilds=\"false\" noChilds=\"false\" />"+
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
		String wposQuery =  "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
							"<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" version=\"1.1.0\">"+
							"<wpos:GetCatalog id=\"ct-license-manager\" brief=\"false\" onlyDirectChilds=\"false\" noChilds=\"false\" />"+
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
	 * @throws IOException
 	 * @return WPOS service response 
	 */
	public String getUserLicenses(String user) throws IOException {
		StringBuffer buf = null; 
		
		String getUserLicensesQuery = 	"<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
						 				"<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" xmlns:xcpf=\"http://www.conterra.de/xcpf/1.1\" version=\"1.1.0\">"+
						 				"<wpos:GetOrderList brief=\"false\">"+
						 				"<wpos:Filter>"+
						 				"<wpos:CustomerId>"+user+"</wpos:CustomerId>"+
						 				"</wpos:Filter>"+
						 				"</wpos:GetOrderList>"+
						 				"</wpos:WPOSRequest>";
		
		try {	
			buf = doHTTPQuery(this.wposURL, "post", getUserLicensesQuery, false);
						
		} catch (IOException ioe) {
			throw ioe; 
		}
		
		return buf.toString();
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
		
		String wposQuery = 	"<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
							"<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" version=\"1.1.0\">"+
							"<wpos:GetPriceModel collapse=\"false\">"+
							"<wpos:Product>"+
							"<wpos:id>"+licenseId+"</wpos:id>"+
							"</wpos:Product>"+
							"</wpos:GetPriceModel>"+
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
	 * ConcludeLicense query to the WPOS Service
	 * 
	 * @param orderProductQuery - Pre-formed orderProductQuery that will be sent to the WPOS Service
	 * @return WPOS Service response
	 * @throws IOException
	 */
	public String concludeLicense(String orderProductQuery)  throws IOException {
		StringBuffer buf = null; 
		
		//String wposQuery = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"+
		//		   "<wpos:WPOSRequest xmlns:wpos=\"http://www.conterra.de/wpos/1.1\" xmlns:xcpf=\"http://www.conterra.de/xcpf/1.1\" version=\"1.1.0\">"+
		//		   "<wpos:OrderProduct brief=\"true\">"+
		//		   "<wpos:Customer>"+
		//		   "<xcpf:UserIdentifier>"+parameterMap.get("user")+"</xcpf:UserIdentifier>"+
		//		   "</wpos:Customer>"+
		//		   "<wpos:Product id=\"urn:conterra:names:sdi-suite:licensing:license:WFS:FGI_LICENCE_TEST_3:LICENSE_WFS_TEST_LICENCE_3_LICENSEMODEL\">"+
		//		   "<wpos:ConfigParams>"+
		//		   "<wpos:Parameter name=\"LICENSE_USER_ID\">"+
		//		   "<wpos:Value selected=\"true\">"+parameterMap.get("user")+"</wpos:Value>"+
		//		   "</wpos:Parameter>"+
		//		   "<wpos:Parameter name=\"LICENSE_DURATION\">"+
		//		   "<wpos:Value title=\"3 Months\">"+parameterMap.get("licenseDuration")+"</wpos:Value>"+
		//		   "</wpos:Parameter>"+
		//		   "<wpos:Parameter name=\"LICENSE_USER_GROUP\">"+
		//		   "<wpos:Value>Users</wpos:Value>"+
		//		   "</wpos:Parameter>"+
		//		   "</wpos:ConfigParams>"+
		//		   "</wpos:Product>"+
		//		   "</wpos:OrderProduct>"+
		//		   "</wpos:WPOSRequest>";
		
		try {	
			buf = doHTTPQuery(this.wposURL, "post", orderProductQuery, false);
			
		} catch (IOException ioe) {
			throw ioe; 
		}
		
		return buf.toString();
	}
	
	
	
	/**
	 * Makes DeactivateLicense query to the WPOS Service
	 * 
	 * @param XMLPOSTString
	 * @return
	 */
	public String deactivateLicense(String licenseId) throws IOException {
		StringBuffer buf = null; 
		
		String deactivateLicenseQuery = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
										"<Envelope xmlns=\"http://www.w3.org/2003/05/soap-envelope\">"+
										"<Body>"+
										"<licmanp:DeactivateLicense ID=\"B1202458930484\" Version=\"2.0\" IssueInstant=\"2001-12-17T09:30:47.0Z\" "+
										" xmlns:licmanp=\"http://www.52north.org/licensemanagerprotocol\" xmlns:saml=\"urn:oasis:names:tc:SAML:2.0:assertion\">"+
										"<saml:AssertionIDRef>"+licenseId+"</saml:AssertionIDRef>"+
										"</licmanp:DeactivateLicense>"+
										"</Body>"+
										"</Envelope>"; 
		
		try {	
			buf = doHTTPQuery(this.soapURL, "post", deactivateLicenseQuery, true);
			
		} catch (IOException ioe) {
			throw ioe; 
		}
		
		return buf.toString();
	}
	

	
	/**
	 * Performs HTTP GET or POST query to the backend server
	 * 
	 * @param queryURL The request string that is to be sent to the server
	 * @param queryType - Type of the query (get || post)
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

		   if(proxyEnabled) {
			   Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("46.30.132.7", 8080));
			   httpuc = (HttpURLConnection) queryURL.openConnection(proxy);
			   httpuc.setDoInput(true);
			   httpuc.setDoOutput(true);
		   }
			 
		   else {
			   httpuc = (HttpURLConnection) queryURL.openConnection();
			   httpuc.setDoInput(true);
			   httpuc.setDoOutput(true); 
		   }
			
			if (useBasicAuthentication == true) {
				userPassword = this.username + ":" + this.password;
				encoding = new sun.misc.BASE64Encoder().encode(userPassword.getBytes());
				httpuc.setRequestProperty("Authorization", "Basic "	 + encoding);
			}
			
			if (queryType.equals("get")) {
				//System.out.println("queryURL: "+queryURL);
				
				BufferedReader buf = new BufferedReader(new InputStreamReader(httpuc.getInputStream(), "utf-8"));	
				
				while((WFSResponseLine = buf.readLine()) != null) {
					sbuf.append(WFSResponseLine);
				}    
				
				buf.close();
			}
			
			else if (queryType.equals("post")) {
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

package eu.elf.license;

import eu.elf.license.conclude.LicenseConcludeResponseObject;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import eu.elf.license.model.UserLicense;
import eu.elf.license.model.UserLicenses;
import eu.elf.license.LicenseQueryHandler;

import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import fi.nls.oskari.util.ConversionHelper;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.List;

public class LicenseService {

    private final Logger log = LogFactory.getLogger(LicenseService.class);
    private LicenseQueryHandler lqh;
    private String loginUrl;
    private String user;
    private String pass;

    public LicenseService(String WPOSUrl, String WPOSUsername, String WPOSPassword, String licenseManagerURL, String loginUrl) {
        user = WPOSUsername;
        pass = WPOSPassword;
        this.loginUrl = loginUrl;
        try {
            this.lqh = new LicenseQueryHandler(WPOSUrl, WPOSUsername, WPOSPassword, licenseManagerURL);

        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public List<LicenseModelGroup> getLicenseGroups() {
        List<LicenseModelGroup> lmgList = null;

        try {
            lmgList = lqh.getListOfLicensesAsLicenseModelGroupList();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lmgList;
    }


    public UserLicenses getLicenseGroupsForUser(final String userid) {
    	UserLicenses userLicenses = null;
    	
        try {
        	userLicenses = lqh.getUserLicensesAsLicenseUserLicensesObject(userid);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return userLicenses;
    }

    public LicenseModelGroup getLicenseForURL(final String url) {
        return getLicenseGroupsForURL(getLicenseGroups(), url);
    }

    public LicenseModelGroup getUserLicenseForURL(final String userid, final String url) {
        UserLicenses ul = getLicenseGroupsForUser(userid);
        List<UserLicense> userLicenseList = ul.getUserLicenses();
        
        for (int i = 0; i < userLicenseList.size(); i++) {
        	List<LicenseModelGroup> lmgList = userLicenseList.get(i).getLmgList();
        	
        	LicenseModelGroup lmg = getLicenseGroupsForURL(lmgList, url);
        	
        	if (lmg != null) {
        		return lmg;
        	}
        }
    	
        return null;
    }
    

    /**
     * Finds group based on URL
     *
     * @param groups
     * @param url
     * @return LicenseModelGroup with matching url or null if none match the url.
     */
    public LicenseModelGroup getLicenseGroupsForURL(List<LicenseModelGroup> groups, final String url) {
        if (url == null || groups == null) {
            return null;
        }
        for (LicenseModelGroup group : groups) {
            // NOTE! -> URL <-
            if (url.equalsIgnoreCase(group.getUrl())) {
                return group;
            }
        }
        return null;
    }

    /**
     * Finds group based on id
     *
     * @param groups
     * @param id
     * @return LicenseModelGroup with matching id or null if none match the id.
     */
    public LicenseModelGroup getLicenseGroupsForId(List<LicenseModelGroup> groups, final String id) {
        if (id == null || groups == null) {
            return null;
        }
        for (LicenseModelGroup group : groups) {
            // NOTE! -> ID <-
            if (id.equalsIgnoreCase(group.getId())) {
                return group;
            }
        }
        return null;
    }


    /**
     * Gets the price of the license model
     *
     * @param lm - license model object
     * @return String    - price of the license model
     */
    public String getLicenseModelPrice(LicenseModel lm, String userId) {
        String productPriceSum = "";

        try {
            productPriceSum = lqh.getLicenseModelPrice(lm, userId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return productPriceSum;
    }


    /**
     * Subscribe a license
     *
     * @param model model to conclude (check need for userid as param if we add it to LicenseModel for deactivate?)
     * @return true if successfully deactivated (or do we need some other output?)
     */
    public LicenseConcludeResponseObject concludeLicense(final LicenseModel model, final String userId) {
    	LicenseConcludeResponseObject response = null;

        try {
            response = lqh.concludeLicenseObjectResponse(model, userId);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }


    /**
     * Unsubscribe from a license
     *
     * @param licenseId LicenseId
     * @return boolean - indicates only if the HTTP query went through, not that license was actually deactivated!!!
     */
    public boolean deactivateLicense(String licenseId) {
        Boolean success = false;

        try {
            BasicCookieStore bcs = getAdminConsoleCookies();
            //System.out.println("cookies fetched");

            success = lqh.deactivateLicense(bcs, licenseId);


        } catch (ClientProtocolException cpe) {
            cpe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return success;
    }

    /**
     * Activate license
     *
     * @param licenseId LicenseId
     * @return boolean - indicates only if the HTTP query went through, not that license was actually deactivated!!!
     */
    public boolean activateLicense(String licenseId) {
        Boolean success = false;

        try {
            BasicCookieStore bcs = getAdminConsoleCookies();
            //System.out.println("cookies fetched");

            success = lqh.activateLicense(bcs, licenseId);


        } catch (ClientProtocolException cpe) {
            cpe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return success;
    }

    /**
     * delete a license
     *
     * @param licenseId LicenseId
     * @return boolean - indicates only if the HTTP query went through, not that license was actually deactivated!!!
     */
    public boolean deleteLicense(String licenseId) {
        Boolean success = false;

        try {
            BasicCookieStore bcs = getAdminConsoleCookies();
            //System.out.println("cookies fetched");

            success = lqh.deleteLicense(bcs, licenseId);


        } catch (ClientProtocolException cpe) {
            cpe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return success;
    }


    @SuppressWarnings("deprecation")
    public BasicCookieStore getAdminConsoleCookies() {
        //System.out.println("Fetching cookies...");
        //CloseableHttpClient httpclient = HttpClients.createDefault();
        DefaultHttpClient httpclient = new DefaultHttpClient();
        // TODO: check for loginUrl.protocol() + ".proxyHost"
        if(System.getProperty("http.proxyHost") != null) {
            log.info("http.proxyHost configured - using it for http client:",
                    System.getProperty("http.proxyHost"),
                    "port:", System.getProperty("http.proxyPort"));
            int proxyPort = ConversionHelper.getInt(System.getProperty("http.proxyPort"), -1);
            if(proxyPort == -1) {
                log.warn("http.proxyHost configured, but http.proxyPort isn't");
            }
            else {
                HttpHost proxy = new HttpHost(System.getProperty("http.proxyHost"), proxyPort);
                httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,proxy);
            }
        }
        else {
            log.info("No proxy configured");
        }

        HttpPost post = new HttpPost(loginUrl + "&username=" + user + "&password=" + pass);
        BasicCookieStore bcs = new BasicCookieStore();

        try {

            HttpResponse response = httpclient.execute(post);
            bcs = (BasicCookieStore) httpclient.getCookieStore();

            //List<Cookie> cookieList = bcs.getCookies();
            //System.out.println("cookieList.size: "+cookieList.size());

            //for (int c = 0; c < cookieList.size(); c++) {
            //	System.out.println("cookie "+c);
            //	System.out.println(cookieList.get(c).getName());
            //	System.out.println(cookieList.get(c).getValue());
            //}


        } catch (ClientProtocolException cpe) {
            cpe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return bcs;
    }


}

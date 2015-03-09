package eu.elf.license;

import eu.elf.license.conclude.LicenseConcludeResponseObject;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.util.List;

public class LicenseService {
    private LicenseQueryHandler lqh;
    private String user;
    private String pass;

    public LicenseService(String WPOSUrl, String WPOSUsername, String WPOSPassword, String licenseManagerURL) {
        user = WPOSUsername;
        pass = WPOSPassword;
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

    public List<LicenseModelGroup> getLicenseGroupsForUser(final String userid) {
        List<LicenseModelGroup> lmgList = null;

        try {
            lmgList = lqh.getUserLicensesAsLicenseModelGroupList(userid);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lmgList;
    }

    public LicenseModelGroup getLicenseForURL(final String url) {
        return getLicenseGroupsForURL(getLicenseGroups(), url);
    }

    public LicenseModelGroup getUserLicenseForURL(final String userid, final String url) {
        return getLicenseGroupsForURL(getLicenseGroupsForUser(userid), url);
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
            if (url.equalsIgnoreCase(group.getUrl())) {
                return group;
            }
        }
        return null;
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
     * Unsubscribe from a license
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
        HttpPost post = new HttpPost("http://54.247.162.180:8080/administration/account/login?returnURL=http%3A%2F%2F54.247.162.180%3A8080%2Fadministration%2Fstart.do&app=&tokenFormat=&username=" + user + "&password=" + pass);
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

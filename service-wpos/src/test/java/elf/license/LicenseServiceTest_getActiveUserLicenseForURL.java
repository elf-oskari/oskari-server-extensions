package elf.license;

import eu.elf.license.model.*;
import org.junit.Ignore;
import org.junit.Test;

import eu.elf.license.LicenseService;

import static org.junit.Assert.*;

public class LicenseServiceTest_getActiveUserLicenseForURL {

    @Test
    @Ignore("So we don't need to setup urls/credentials when compiling")
    public void test() throws Exception {
    	//	final String xml = TestHelper.readString(getClass().getResourceAsStream("GetCatalogQueryResponse.xml"));

        LicenseService ls = TestHelper.createService();
    	UserLicense license = ls.getActiveUserLicenseForURL("FGI", "http://54.247.162.180:8080/wss/service/FGI_WFS_TEST_3/WSS");
        LicenseModelGroup lmg = license.getLmgList().get(0);
		
		assertEquals("Id should match expected", "urn:conterra:names:sdi-suite:licensing:licenseset:WFS:FGI_LICENCE_TEST_3", lmg.getId());
    }
    	
}
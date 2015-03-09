package elf.license;

import org.junit.Test;

import eu.elf.license.LicenseParser;
import eu.elf.license.LicenseService;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import eu.elf.license.model.LicenseParam;
import eu.elf.license.model.LicenseParamBln;
import eu.elf.license.model.LicenseParamDisplay;
import eu.elf.license.model.LicenseParamEnum;
import eu.elf.license.model.LicenseParamInt;
import eu.elf.license.model.LicenseParamText;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LicenseServiceTest_getUserLicenseForURL {

    @Test
    public void test() throws Exception {
    	//	final String xml = TestHelper.readString(getClass().getResourceAsStream("GetCatalogQueryResponse.xml"));


        LicenseService ls = TestHelper.createService();
    	LicenseModelGroup lmg = ls.getUserLicenseForURL("FGI", "http://54.247.162.180:8080/wss/service/FGI_WFS_TEST_3/WSS");
		
		assertEquals("Id should match expected", "urn:conterra:names:sdi-suite:licensing:licenseset:WFS:FGI_LICENCE_TEST_3", lmg.getId());
	

    	}
    	
}
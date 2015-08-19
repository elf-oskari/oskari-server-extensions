package elf.license;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.elf.license.LicenseService;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;


public class LicenseServiceTest_getLicenseModelPrice {

	  @Test
	  public void test() throws Exception { 
		String productPriceSum = "";
		LicenseService ls = TestHelper.createService();
	
		List<LicenseModelGroup> lmgList = new ArrayList<LicenseModelGroup>();
		    	
		List<LicenseModel>  lmList = new ArrayList<LicenseModel>();
		
		lmgList = ls.getLicenseGroups();

		for (int i = 0; i < lmgList.size(); i++) {
			//System.out.println("lmgList.id "+lmgList.get(i).getId());
		
				// Conclude the licensemodel with specific id
				if (lmgList.get(i).getId().equals("urn:conterra:names:sdi-suite:licensing:licenseset:WFS:FGI_License_Test_20141023")) {
					lmList = lmgList.get(i).getLicenseModels();
		    	
		           	for (int j = 0; j < lmList.size(); j++) {
		        		//System.out.println("\tlmList.id "+lmList.get(j).getId());
		        		
		        		if (lmList.get(j).getId().equals("urn:conterra:names:sdi-suite:licensing:license:WFS:FGI_License_Test_20141023:LICENSE_CONTERRA-Test")) {			
		        			LicenseModel templm = lmList.get(j);
		        		        	
		        			productPriceSum = ls.getLicenseModelPrice(templm, "FGI");
		        			
		        			System.out.println("ProductPriceSum: "+productPriceSum);
		        			assertEquals("Price should match the expected", "241.57", productPriceSum.toString());
		        			
		        		}
		        		
		           	}
				}
			}
		
	  }

}

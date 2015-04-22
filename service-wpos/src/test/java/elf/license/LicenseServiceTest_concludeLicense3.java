package elf.license;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.elf.license.LicenseParser;
import eu.elf.license.LicenseQueryHandler;
import eu.elf.license.LicenseService;
import eu.elf.license.conclude.LicenseConcludeResponseObject;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import eu.elf.license.model.LicenseParam;
import eu.elf.license.model.LicenseParamBln;
import eu.elf.license.model.LicenseParamDisplay;
import eu.elf.license.model.LicenseParamEnum;
import eu.elf.license.model.LicenseParamInt;
import eu.elf.license.model.LicenseParamText;

public class LicenseServiceTest_concludeLicense3 {

	  @Test
	  public void test() throws Exception { 
		LicenseConcludeResponseObject lcro = null;
		
        LicenseService ls = TestHelper.createService();
		List<LicenseModelGroup> lmgList = new ArrayList<LicenseModelGroup>();
		    	
		List<LicenseModel>  lmList = new ArrayList<LicenseModel>();
		List<LicenseParam> lpList = new ArrayList<LicenseParam>();
		
		lmgList = ls.getLicenseGroups();
		for (int i = 0; i < lmgList.size(); i++) {
			//System.out.println("lmgList.id "+lmgList.get(i).getId());
			
				// Conclude the licensemodel with specific id
				if (lmgList.get(i).getId().equals("urn:conterra:names:sdi-suite:licensing:licenseset:WFS:FGI_License_Test_20141023")) {		
					lmList = lmgList.get(i).getLicenseModels();
		    	
		           	for (int j = 0; j < lmList.size(); j++) {
		        		//System.out.println("\tlmList.id "+lmList.get(j).getId());	        		
		        		if (lmList.get(j).getId().equals("urn:conterra:names:sdi-suite:licensing:license:WFS:FGI_License_Test_20141023:LICENSE_GPF_dev_test")) {			
		        			LicenseModel templm = lmList.get(j);
		        		
		        			List<LicenseParam> templpList = templm.getParams();	        			
		        			// Set selected items for the enumeration parameters
		        			for (int k = 0; k < templpList.size(); k++) {
		        				if (templpList.get(k).getParameterClass().equals("configurationParameter")) {			
		        					//System.out.println("param.name "+templpList.get(k).getName());	
		        					
		        					
		        					if (templpList.get(k).getName().equals("LICENSE_PARAMETER_Integer type parameter")) {
		        						LicenseParamInt tempParamReplace = (LicenseParamInt)templpList.get(k); 		        						
		        						tempParamReplace.setValue(50);
		        						templpList.set(k,tempParamReplace);
		        					}
		        					if (templpList.get(k).getName().equals("LICENSE_PARAMETER_Text type parameter")) {
		        						LicenseParamText tempParamReplace = (LicenseParamText)templpList.get(k); 		
		        						List<String> selectedValueList = new ArrayList<String>();
		        						selectedValueList.add("green");
		        						tempParamReplace.setValues(selectedValueList);
		        						templpList.set(k,tempParamReplace);
		        					}
		        					if (templpList.get(k).getName().equals("LICENSE_PARAMETER_Boolean type parameter")) {
		        						LicenseParamBln tempParamReplace = (LicenseParamBln)templpList.get(k); 		        						
		        						tempParamReplace.setValue(false);
		        						templpList.set(k,tempParamReplace);
		        					}
		        					if (templpList.get(k).getName().equals("LICENSE_PARAMETER_Enumeration type parameter")) {
		        						LicenseParamEnum tempParamReplace = (LicenseParamEnum)templpList.get(k); 		        						
		        						tempParamReplace.setMulti(false);
		        						tempParamReplace.addSelection("3");
		        						templpList.set(k,tempParamReplace);
		        					}
		        					if (templpList.get(k).getName().equals("LICENSE_PARAMETER_Enumeration with multiple choice")) {
		        						LicenseParamEnum tempParamReplace = (LicenseParamEnum)templpList.get(k); 	
		        						tempParamReplace.setMulti(true);
		        						tempParamReplace.addSelection("Indoor use");
		        						tempParamReplace.addSelection("Outdoor use");		        						
		        						templpList.set(k,tempParamReplace);
		        					}
		        				}
		        			}
		        		
		        			
		        			lcro = ls.concludeLicense(lmList.get(j), "FGI");
		        			
		        			System.out.println("productId: "+lcro.getProductId());
		        			System.out.println("licenseId: "+lcro.getLicenseId());
		        			System.out.println("validTo: "+lcro.getValidTo());		// This hasn't been defined in the license model, supposed to be null
		        		}
		        		
		           	}
				}
			}
		
	  }

}

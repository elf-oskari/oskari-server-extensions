package elf.license;

import eu.elf.license.LicenseParser;
import org.junit.Test;

import eu.elf.license.LicenseService;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import eu.elf.license.model.LicenseParam;
import eu.elf.license.model.LicenseParamDisplay;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class LicenseServiceTest_getLicenseGroupsForUser {

    @Test
    public void test() throws Exception {
        final String xml = TestHelper.readString(getClass().getResourceAsStream("GetCatalogQueryResponse.xml"));
        List<LicenseModelGroup> lmgList = LicenseParser.parseListOfLicensesAsLicenseModelGroupList(xml);
    	List<LicenseModel>  lmList = new ArrayList<LicenseModel>();
    	List<LicenseParam> lpList = new ArrayList<LicenseParam>();
    	//List<String> valueList = new ArrayList<String>();
    	
    	System.out.println("lmgList.size "+lmgList.size());
    
    	
    	
    	for (int i = 0; i < lmgList.size(); i++) {
    		System.out.println("lmgList.id "+lmgList.get(i).getId());
    		System.out.println("lmgList.name "+lmgList.get(i).getName());
    		System.out.println("lmgList.description "+lmgList.get(i).getDescription());
    		System.out.println("lmgList.url "+lmgList.get(i).getUrl());
    		System.out.println("LicenseModels");
    		
    		lmList = lmgList.get(i).getLicenseModels();
    		
        	System.out.println("lmList.size "+lmList.size());
        	
        	for (int j = 0; j < lmList.size(); j++) {
        		System.out.println("\tlmList.id "+lmList.get(j).getId());
        		System.out.println("\tlmList.name "+lmList.get(j).getName());
        		System.out.println("\tlmList.description "+lmList.get(j).getDescription());
        		System.out.println("\n");
        		
        		lpList = lmList.get(j).getParams();
        	
        		System.out.println("\t\tparamList.size: "+lpList.size());
        		
        		for (int k = 0; k < lpList.size(); k++) {
        			System.out.println("\t\tlpList.name "+lpList.get(k).getName());
        			System.out.println("\t\tlpList.title "+lpList.get(k).getTitle());
        			//System.out.println("\tvsize "+lpList.get(k).getValues());
        			
        			if (lpList.get(k).getParameterClass().equals("predefinedParameter")) {
        				LicenseParamDisplay lpd = (LicenseParamDisplay) lpList.get(k);
        				List<String> valueList = lpd.getValues();
        				
        				for (int l = 0; l < valueList.size(); l++) {
        					System.out.println("\t\t\tvalue: "+valueList.get(l));
        				}
        			}
        			
        			
        		}
        	}
    		System.out.println("\n");
    	}
    	
        //assertEquals("Abstract should match expected", "The product catalog of the license manager", pojo.getXMLObjectPojo().get_catalog().get_title());
        
    }

}
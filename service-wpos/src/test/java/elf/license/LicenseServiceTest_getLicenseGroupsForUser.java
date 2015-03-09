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

public class LicenseServiceTest_getLicenseGroupsForUser {

    @Test
    public void test() throws Exception {
        //LicenseService ls = TestHelper.createService();
    	//List<LicenseModelGroup> lmgList = new ArrayList<LicenseModelGroup>();
    	//lmgList = ls.getLicenseGroupsForUser("FGI");
    	
       	final String xml = TestHelper.readString(getClass().getResourceAsStream("GetOrderListQueryResponse.xml"));    	

    	List<LicenseModelGroup> lmgList = LicenseParser.parseUserLicensesAsLicenseModelGroupList(xml);
     	
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
        	
        		System.out.println("\t\tlpList.size: "+lpList.size());
        		
        		for (int k = 0; k < lpList.size(); k++) {
        		
        			if (lpList.get(k).getParameterClass().equals("predefinedParameter")) {
        				LicenseParamDisplay lpd = (LicenseParamDisplay) lpList.get(k);
        				
        				System.out.println("\n\t\tlpList.parameterClass "+lpList.get(k).getParameterClass());
            			System.out.println("\t\tlpList.name "+lpList.get(k).getName());
            			System.out.println("\t\tlpList.title "+lpList.get(k).getTitle());
        				
        				List<String> valueList = lpd.getValues();
        				
        				
        				for (int l = 0; l < valueList.size(); l++) {
        					System.out.println("\t\t\tvalue: "+valueList.get(l));
        				}
        			}
        			
        			if (lpList.get(k).getParameterClass().equals("configurationParameter")) {
        				
        				LicenseParam lp = (LicenseParam) lpList.get(k);
        				
        				
        				if(lp instanceof LicenseParamInt) {
        					LicenseParamInt lpi = (LicenseParamInt) lpList.get(k);
        					System.out.println("lpi integer------------------------------------------------------------------");
        					System.out.println("\n\t\tlpList.parameterClass "+lpi.getParameterClass());
                			System.out.println("\t\tlpList.name "+lpi.getName());
                			System.out.println("\t\tlpList.title "+lpi.getTitle());
        				}
        				else if(lp instanceof LicenseParamBln) {
        					LicenseParamBln lpBln = (LicenseParamBln) lpList.get(k);
        					System.out.println("lp bln------------------------------------------------------------------");
        					System.out.println("\n\t\tlpList.parameterClass "+lpBln.getParameterClass());
                			System.out.println("\t\tlpList.name "+lpBln.getName());
                			System.out.println("\t\tlpList.title "+lpBln.getTitle());
                			System.out.println("\t\tlpList.value "+lpBln.getValue());
        				}
        				else if(lp instanceof LicenseParamDisplay) {
        					LicenseParamDisplay lpd = (LicenseParamDisplay) lpList.get(k);
        					System.out.println("lp display------------------------------------------------------------------");
        					System.out.println("\n\t\tlpList.parameterClass "+lpd.getParameterClass());
                			System.out.println("\t\tlpList.name "+lpd.getName());
                			System.out.println("\t\tlpList.title "+lpd.getTitle());
        				}
        				else if(lp instanceof LicenseParamEnum) {
        					LicenseParamEnum lpEnum = (LicenseParamEnum) lpList.get(k);
        					System.out.println("lp enum------------------------------------------------------------------");
        					System.out.println("\n\t\tlpList.parameterClass "+lpEnum.getParameterClass());
                			System.out.println("\t\tlpList.name "+lpEnum.getName());
                			System.out.println("\t\tlpList.title "+lpEnum.getTitle());
                			
                			List<String> tempOptions = lpEnum.getOptions();
                			
                			for (int m = 0; m < tempOptions.size(); m++) {
                				System.out.println("\t\t\toption("+m+") "+tempOptions.get(m));                			}
        				}
        				else if(lp instanceof LicenseParamText) {
        					LicenseParamText lpText = (LicenseParamText) lpList.get(k);
        					System.out.println("lp text------------------------------------------------------------------");
        					System.out.println("\n\t\tlpList.parameterClass "+lpText.getParameterClass());
                			System.out.println("\t\tlpList.name "+lpText.getName());
                			System.out.println("\t\tlpList.title "+lpText.getTitle());
        				}
        				//else  {
        				//	System.out.println("lp else------------------------------------------------------------------");
        				//	System.out.println("\n\t\tlpList.parameterClass "+lpList.get(k).getParameterClass());
                		//	System.out.println("\t\tlpList.name "+lpList.get(k).getName());
                		//	System.out.println("\t\tlpList.title "+lpList.get(k).getTitle());
        				//}
        	
        			}
        			
        			if (lpList.get(k).getParameterClass().equals("precalculatedParameter")) {
        				LicenseParamDisplay lpd = (LicenseParamDisplay) lpList.get(k);
        				
        				System.out.println("\n\t\tprecalculatedParameter.parameterClass "+lpList.get(k).getParameterClass());
            			System.out.println("\t\tprecalculatedParameter.name "+lpList.get(k).getName());
            			System.out.println("\t\tprecalculatedParameter.title "+lpList.get(k).getTitle());
        				
        				List<String> valueList = lpd.getValues();
        				
        				
        				for (int l = 0; l < valueList.size(); l++) {
        					System.out.println("\t\t\tprecalculatedParameter.value: "+valueList.get(l));
        				}
        			}
        			
        			if (lpList.get(k).getParameterClass().equals("resultParameter")) {
        				LicenseParamDisplay lpd = (LicenseParamDisplay) lpList.get(k);
        				
        				System.out.println("\n\t\tresultParameter.parameterClass "+lpList.get(k).getParameterClass());
            			System.out.println("\t\tresultParameter.name "+lpList.get(k).getName());
            			System.out.println("\t\tresultParameter.title "+lpList.get(k).getTitle());
        				
        				List<String> valueList = lpd.getValues();
        				
        				
        				for (int l = 0; l < valueList.size(); l++) {
        					System.out.println("\t\t\tresultParameter.value: "+valueList.get(l));
        				}
        			}
        			
        			
        		}
        	}
        	
        	
    		System.out.println("\n");
    	}
    
    
		
		
        //assertEquals("Abstract should match expected", "The product catalog of the license manager", pojo.getXMLObjectPojo().get_catalog().get_title());
        
    }

}
package elf.license;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.elf.license.LicenseService;
import eu.elf.license.model.LicenseModel;
import eu.elf.license.model.LicenseModelGroup;
import eu.elf.license.model.LicenseParam;

public class LicenseServiceTest_deactivateLicense {
	 @Test
	 public void test() throws Exception {
         LicenseService ls = TestHelper.createService();
	
		 List<LicenseModelGroup> lmgList = new ArrayList<LicenseModelGroup>();
		 List<LicenseModel>  lmList = new ArrayList<LicenseModel>();
		 List<LicenseParam> lpList = new ArrayList<LicenseParam>();
		
		 
		 ls.deactivateLicense("UUID_20150306-105001-057-53627-2424");
		 
		
	 }
}

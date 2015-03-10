package eu.elf.oskari.license;

import eu.elf.license.model.*;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.service.DummyUserService;
import fi.nls.oskari.util.PropertyUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LicenseHelperTest {

    @Before
    public void setup() throws Exception {
        PropertyUtil.addProperty("oskari.user.service", DummyUserService.class.getName());
    }

    @After
    public void teardown() {
        PropertyUtil.clearProperties();
    }

    @Test
    public void testFilterModelsByRoles() throws Exception {

        LicenseModelGroup group = new LicenseModelGroup();
        LicenseModel publicModel = new LicenseModel();
        publicModel.setRestricted(false);
        publicModel.setName("publicModel");
        group.addLicenseModel(publicModel);

        User user = new User();
        user.addRole(1, "myRole");

        LicenseModel userModel = new LicenseModel();
        userModel.setRestricted(true);
        userModel.addRole("myRole");
        userModel.setName("userModel");
        group.addLicenseModel(userModel);

        LicenseModel adminModel = new LicenseModel();
        adminModel.setRestricted(true);
        adminModel.addRole("adminRole");
        adminModel.setName("adminModel");
        group.addLicenseModel(adminModel);

        assertEquals("Original group should have 3 models", 3 , group.getLicenseModels().size());

        LicenseModelGroup filteredGroup = LicenseHelper.filterModelsByRoles(user, group);
        assertEquals("Filtered group should have 3 models", 2 , filteredGroup.getLicenseModels().size());

        for(LicenseModel model : filteredGroup.getLicenseModels()) {
            assertTrue("Remaining models should be publicModel or userModel", model.getName().equals("publicModel") || model.getName().equals("userModel"));
        }
    }

    private LicenseModel getModel() {
        LicenseModel model = new LicenseModel();
        model.addParam("non UI bln", true);
        model.addParam("LICENSE_PARAMETER_testbln", false);

        model.addParam("non UI int", 1);
        model.addParam("LICENSE_PARAMETER_testint", 2);

        model.addParam("non UI text", "to be removed");
        model.addParam("LICENSE_TEXT_testtxt", "should not be removed");

        return model;
    }

    @Test
    public void testRemoveNonUIParams() {
        LicenseModel model = getModel();
        assertEquals("Should have 6 params", 6, model.getParams().size());
        LicenseModel uiModel = LicenseHelper.removeNonUIParams(model);
        assertEquals("Should have 3 params", 3, uiModel.getParams().size());
        for(LicenseParam param : uiModel.getParams()) {
            assertTrue("Param names should start with known prefixes", param.getName().startsWith("LICENSE_TEXT_") ||
                    param.getName().startsWith("LICENSE_PARAMETER_"));
        }

    }
}
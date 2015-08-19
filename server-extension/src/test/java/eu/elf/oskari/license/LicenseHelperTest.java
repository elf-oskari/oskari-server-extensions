package eu.elf.oskari.license;

import eu.elf.license.model.*;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.service.DummyUserService;
import fi.nls.oskari.util.IOHelper;
import fi.nls.oskari.util.JSONHelper;
import fi.nls.oskari.util.PropertyUtil;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

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

    @Test
    public void testSetValues() throws Exception {
        LicenseModel model = getModel();
        LicenseParamBln beforeBln = (LicenseParamBln)model.getParam("LICENSE_PARAMETER_testbln");
        assertNotNull("Model should have bln param", beforeBln);
        assertFalse("Model should have 'false' bln param value", beforeBln.getValue());

        LicenseParamInt beforeInt = (LicenseParamInt)model.getParam("LICENSE_PARAMETER_testint");
        assertNotNull("Model should have int param", beforeInt);
        assertEquals("Model should have '2' int param value", 2, beforeInt.getValue());

        LicenseParamText beforeText = (LicenseParamText)model.getParam("LICENSE_TEXT_testtxt");
        assertNotNull("Model should have text param", beforeText);
        assertEquals("Model should have 'should not be removed' text param value",
                "should not be removed", beforeText.getValues().get(0));

        // read and set values
        final String json = IOHelper.readString(getClass().getResourceAsStream("input.json"));
        LicenseHelper.setValues(model, JSONHelper.createJSONArray(json));

        // check that values changed
        LicenseParamBln afterBln = (LicenseParamBln)model.getParam("LICENSE_PARAMETER_testbln");
        assertNotNull("Model should have bln param", afterBln);
        assertTrue("Model should have 'true' bln param value", afterBln.getValue());

        LicenseParamInt afterInt = (LicenseParamInt)model.getParam("LICENSE_PARAMETER_testint");
        assertNotNull("Model should have int param", afterInt);
        assertEquals("Model should have '37' int param value", 37, afterInt.getValue());

        LicenseParamText afterText = (LicenseParamText)model.getParam("LICENSE_TEXT_testtxt");
        assertNotNull("Model should have text param", afterText);

        assertEquals("Model should have 'As a service' text param value",
                "As a service", afterText.getValues().get(0));
        assertEquals("Model should have 'As an embedded webmap' text param value",
                "As an embedded webmap", afterText.getValues().get(1));

    }
}
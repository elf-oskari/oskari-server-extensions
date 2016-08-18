package eu.elf.license;

import eu.elf.license.model.UserLicenses;
import fi.nls.oskari.util.IOHelper;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by SMAKINEN on 27.6.2016.
 */
public class LicenseParserTest {

    @Test
    public void testParseUserLicensesAsLicenseModelGroupList()
            throws Exception {
        final String xml = IOHelper.readString(getClass().getResourceAsStream("UserLicensesAsLicenseModelGroupList-input.xml"), "UTF-8");
        LicenseParser parser = new LicenseParser();
        UserLicenses result = parser.parseUserLicensesAsLicenseModelGroupList(xml, "CONT");

        assertNotNull(result);

    }
}
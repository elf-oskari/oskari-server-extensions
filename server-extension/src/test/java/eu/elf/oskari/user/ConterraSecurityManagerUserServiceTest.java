package eu.elf.oskari.user;

import fi.nls.oskari.domain.Role;
import fi.nls.oskari.domain.User;
import fi.nls.oskari.util.IOHelper;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConterraSecurityManagerUserServiceTest {

    final ConterraSecurityManagerUserService service = new ConterraSecurityManagerUserService();

    @Test
    public void testParseResponseNotExpectedInput() throws Exception {

        User nullUser  = service.parseResponse(null);
        assertNull("UserNotFound response should result in <null> user.", nullUser);

        final String response = IOHelper.readString(getClass().getResourceAsStream("UserNotFoundResponse.xml"));
        User notFoundUser  = service.parseResponse(response);
        assertNull("UserNotFound response should result in <null> user.", notFoundUser);

        final String wrongStatusResponse = IOHelper.readString(getClass().getResourceAsStream("LoginWithWrongStatus.xml"));
        User wrongStatusUser  = service.parseResponse(wrongStatusResponse);
        assertNull("UserNotFound response should result in <null> user.", wrongStatusUser);
    }

    @Test
    public void testParseResponseSuccesful() throws Exception {
        final String response = IOHelper.readString(getClass().getResourceAsStream("SuccessfulLogin.xml"));
        User user  = service.parseResponse(response);
        assertNotNull("SuccessfulLogin response should result in actual user.", user);
        assertEquals("Attribute should match: screenname", "CONT", user.getScreenname());
        assertEquals("Attribute should match: email", "r.gartmann@conterra.de", user.getEmail());
        assertEquals("Attribute should match: firstname", "con terra GmbH", user.getFirstname());
        assertEquals("Attribute should match: familyname", "Germany", user.getLastname());

        assertEquals("Should have 2 roles", 2, user.getRoles().size());
        for(Role role : user.getRoles()) {
            assertTrue("Roles should match: [sM_Administrator || User]", "sM_Administrator".equals(role.getName()) || "User".equals(role.getName()));
        }
    }
}
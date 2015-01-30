package eu.elf.oskari.user;

import fi.nls.oskari.domain.User;
import fi.nls.oskari.service.ServiceException;
import fi.nls.oskari.util.IOHelper;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConterraSecurityManagerUserServiceTest {

    final ConterraSecurityManagerUserService service = new ConterraSecurityManagerUserService();

    @Test(expected = ServiceException.class)
    public void testLogin() throws Exception {
        service.init();
        //assertEquals("");
    }

    @Test
    public void testParseResponseNotExpectedInput() throws Exception {

        User nullUser  = service.parseResponse(null);
        assertNull("UserNotFound response should result in <null> user.", nullUser);

        final String response = IOHelper.readString(getClass().getResourceAsStream("UserNotFoundResponse.xml"));
        User notFoundResponse  = service.parseResponse(response);
        assertNull("UserNotFound response should result in <null> user.", notFoundResponse);
    }

    @Test
    public void testParseResponseSuccesful() throws Exception {

        final String response = IOHelper.readString(getClass().getResourceAsStream("SuccessfulLogin.xml"));
        User user  = service.parseResponse(response);
        //assertNull("UserNotFound response should result in <null> user.", user);
    }
}
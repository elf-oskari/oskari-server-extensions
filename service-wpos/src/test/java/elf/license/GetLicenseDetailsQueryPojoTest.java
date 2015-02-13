package elf.license;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class GetLicenseDetailsQueryPojoTest {

    @Test
    public void test() throws Exception {
        InputStream in = getClass().getResourceAsStream("AllLicenseDetails.xml");

        final String xmlStringAllLicenseDetails = TestHelper.readString(in);
        GetLicenseDetailsQueryPojo pojo = new GetLicenseDetailsQueryPojo(xmlStringAllLicenseDetails);

        // For example: get <w:WPOSElement> -> <x:catalog> -> x:title value
        assertEquals("Abstract should match expected", "The product catalog of the license manager", pojo.getXMLObjectPojo().get_catalog().get_title());
        //System.out.println("abstract: "+pojo.getXMLObjectPojo().get_catalog().get_title());

        // Serialize into XML string
        // Namespaces don't serialize properly with stax2. Woodstock STAX API should be able to fix it but I haven't tested it
        System.out.println("out: "+pojo.getXMLRepresentation());
    }

}
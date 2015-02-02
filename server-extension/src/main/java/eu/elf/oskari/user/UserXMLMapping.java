package eu.elf.oskari.user;

import fi.nls.oskari.domain.User;
import fi.nls.oskari.log.LogFactory;
import fi.nls.oskari.log.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;
import java.util.*;

public class UserXMLMapping {

    private static Logger log = LogFactory.getLogger(UserXMLMapping.class);

    private static final String SUCCESS_STATUS = "samlp:Success";

    private XPath xpath = XPathFactory.newInstance().newXPath();
    private XPathExpression XPATH_STATUS = null;
    private XPathExpression XPATH_NameIdentifier = null;
    private XPathExpression XPATH_Attribute = null;
    private Set<String> ADDITIONAL_ATTR_NAMES = new HashSet<String>();

    public UserXMLMapping() {
        ADDITIONAL_ATTR_NAMES.add("urn:conterra:names:sdi-suite:policy:attribute:user-id");
        ADDITIONAL_ATTR_NAMES.add("urn:conterra:names:sdi-suite:policy:attribute:group-id");
        ADDITIONAL_ATTR_NAMES.add("urn:conterra:names:sdi-suite:policy:attribute:group-name");
        ADDITIONAL_ATTR_NAMES.add("country");
        /*
        /Response/Status/StatusCode[Value=samlp:Success]
        /Response/Assertion/AuthenticationStatement/Subject/NameIdentifier -> text content == username
        /Response/Assertion/AttributeStatement/Subject/NameIdentifier -> text content == username
        /Response/Assertion/AttributeStatement/Attribute[AttributeName=urn:conterra:names:sdi-suite:policy:attribute:user-id]/AttributeValue -> user-id (number)
         */
        try {
            XPATH_STATUS = xpath.compile("./Response/Status/StatusCode/@Value");
            XPATH_NameIdentifier = xpath.compile("./Response/Assertion/AuthenticationStatement/Subject/NameIdentifier/text()");
            //(0..*)
            XPATH_Attribute = xpath.compile("./Response/Assertion/AttributeStatement/Attribute");

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public User parse(String xml) {
        try {
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(false);
            final DocumentBuilder db = dbf.newDocumentBuilder();
            final Document doc = db.parse(new InputSource(new StringReader(xml)));

            if(!isStatusOk(doc)) {
                return null;
            }

            final Node nameid = (Node) XPATH_NameIdentifier.evaluate(doc, XPathConstants.NODE);
            log.debug("Name id:", nameid.getNodeValue());

            final Map<String, Set<String>> attributes = getAttributes(doc);
            final User user = new User();
            user.setScreenname(nameid.getNodeValue());
            final Set<String> email = getAttribute(attributes, "mail");
            if(!email.isEmpty()) {
                user.setEmail(email.iterator().next());
            }
            final Set<String> givenname = getAttribute(attributes, "givenname");
            if(!givenname.isEmpty()) {
                user.setFirstname(givenname.iterator().next());
            }
            final Set<String> lastname = getAttribute(attributes, "familyname");
            if(!lastname.isEmpty()) {
                user.setLastname(lastname.iterator().next());
            }
            final Set<String> roles = getAttribute(attributes, "urn:conterra:names:sdi-suite:policy:attribute:role");
            Iterator<String> it = roles.iterator();
            while(it.hasNext()) {
                user.addRole(-1, it.next());
            }
            // other attributes to a json field
            for(String key : ADDITIONAL_ATTR_NAMES) {
                final Set<String> attr = getAttribute(attributes, key);
                if (!attr.isEmpty()) {
                    user.setAttribute(key, attr.iterator().next());
                }
            }

            log.debug("User:", user);
            return user;
        }
        catch (Exception e) {
            log.error(e, "Exception while parsing user data");
            e.printStackTrace();
        }
        return null;
    }

    private boolean isStatusOk(final Document doc) throws Exception {

        final Node statusNode = (Node) XPATH_STATUS.evaluate(doc, XPathConstants.NODE);
        if(statusNode == null) {
            log.warn("Status node missing on login - Handling as error!");
            return false;
        }
        final String status = statusNode.getNodeValue();
        log.debug("Status:", status);
        if(!SUCCESS_STATUS.equals(status)) {
            log.warn("Unknown status on login:", status, "- Handling as error!");
            return false;
        }
        return true;
    }
    private Set<String> getAttribute(final Map<String, Set<String>> attributes, final String key) {
        Set<String> value = attributes.get(key);
        if(value != null) {
            return value;
        }
        return Collections.emptySet();
    }

    private Map<String, Set<String>> getAttributes(final Document doc) throws Exception {

        //(0..*)
        final NodeList nodeList = (NodeList) XPATH_Attribute.evaluate(doc, XPathConstants.NODESET);
        final Map<String, Set<String>> attributes = new HashMap<String, Set<String>>(5);
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node attr = nodeList.item(i);
            final String name = attr.getAttributes().getNamedItem("AttributeName").getNodeValue();
            NodeList values = attr.getChildNodes();
            for (int j = 0; j < values.getLength(); j++) {
                if(values.item(j).getNodeType() != Node.ELEMENT_NODE) {
                    // empty space is handled as text node so skipping those
                    continue;
                }
                String value = values.item(j).getTextContent();
                Set<String> attrValues = attributes.get(name);
                if(attrValues == null) {
                    attrValues = new HashSet<String>();
                    attributes.put(name, attrValues);
                }
                attrValues.add(value);
                log.debug(name,"=", value);
            }
        }
        return attributes;
    }

}

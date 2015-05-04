package eu.elf.license;

import eu.elf.license.conclude.LicenseConcludeResponseObject;
import eu.elf.license.model.*;

import org.apache.commons.lang3.StringEscapeUtils;
import org.w3c.dom.*;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.Locator2Impl;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Refactored from QueryHandler
 */
public class LicenseParser { 

    public static List<LicenseModelGroup> parseListOfLicensesAsLicenseModelGroupList(String xml) throws Exception {

        try {
            Document xmlDoc = createXMLDocumentFromString(xml);
            NodeList productGroupElementList = xmlDoc.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "productGroup");
            
            return createLicenseModelGroupList(productGroupElementList);

        } catch (Exception e) {
            throw e;
        }
    }
       
    
    
    public static UserLicenses parseUserLicensesAsLicenseModelGroupList(String xml, String userid) throws Exception {      
    	UserLicenses userLicenses = new UserLicenses();
    	
    	try {
            Document xmlDoc = createXMLDocumentFromString(xml);
            
            Element ordersElement = (Element)xmlDoc.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "orders").item(0);
            NodeList orderList = ordersElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "order");
            
            for (int i = 0; i < orderList.getLength(); i++) {
            	UserLicense userLicense = new UserLicense();
            	
            	Element orderElement = (Element)orderList.item(i);
            	Element productionElement = (Element)orderElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "production").item(0);
            	Element productionItemElement = (Element)productionElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "productionItem").item(0);
            	Element LicenseReferenceElement = (Element)productionItemElement.getElementsByTagNameNS("http://www.52north.org/license/0.3.2", "LicenseReference").item(0);
            	Element attributeStatementElement = (Element)LicenseReferenceElement.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "AttributeStatement").item(0);
            	
            	NodeList attributeElementList = attributeStatementElement.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Attribute");
    	    	
            	
    	    	for (int j = 0; j < attributeElementList.getLength(); j++) {
    	    		Element attributeElement = (Element)attributeElementList.item(j);
    	    		Element AttributeValueElement = (Element)attributeElement.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "AttributeValue").item(0);
    	    		    			
    	    		NamedNodeMap attributeMap = attributeElement.getAttributes();
    	
    	    
    	    		
    	            for (int k = 0; k < attributeMap.getLength(); k++) {
    	            	Attr attrs = (Attr) attributeMap.item(k);
    	            	if (attrs.getNodeName().equals("Name") ) {
    	      
    	    	        	if(attrs.getNodeValue().equals("urn:opengeospatial:ows4:geodrm:NotOnOrAfter")) {
    	    	        		userLicense.setValidTo(AttributeValueElement.getTextContent());
    	                    }
    	                    if(attrs.getNodeValue().equals("urn:opengeospatial:ows4:geodrm:LicenseID")) {
    	                    	userLicense.setLicenseId(AttributeValueElement.getTextContent());
    	                    }
    	    	        }
    	
    	            }
    	            
    	            
    	            userLicense.setSecureServiceURL("/httpauth/licid-"+userLicense.getLicenseId());
    	            
    	    	}
    	    		
    	    	Element orderContentElement = (Element)orderElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "orderContent").item(0);
    	    	Element catalogElement = (Element)orderContentElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "catalog").item(0);
    	    	NodeList productGroupElementList = catalogElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "productGroup");

                // setup user license flags in models
                List<LicenseModelGroup> list = createLicenseModelGroupList(productGroupElementList);
                for(LicenseModelGroup group : list) {
                   group.setUserLicense(true);
                }
    	    	userLicense.setLmgList(list);
    	    	
    	    	String WSS_URL = findWssUrlFromUserLicenseParamList(userLicense.getLmgList());
    	    	
    	    	//Remove WSS from the WSS-url string
    	    	WSS_URL = WSS_URL.substring(0, WSS_URL.lastIndexOf("/"));
    	    	
    	    	userLicense.setSecureServiceURL(WSS_URL+userLicense.getSecureServiceURL());
    	    	
	            userLicenses.addUserLicense(userLicense);
            	
            }
                              
            return userLicenses;

        } catch (Exception e) {
            throw e;
        }
    }
  
      
    
    /**
     * Get WSS_URL Parameter value ** Only for user's licenses
     *  
     * @param lmgList
     * @return
     */
    private static String findWssUrlFromUserLicenseParamList(List<LicenseModelGroup> lmgList) {
    	LicenseParamDisplay wssUrlParam = (LicenseParamDisplay)lmgList.get(0).getLicenseModels().get(0).getParam("WSS_URL");
    	
    	return wssUrlParam.getValues().get(0);
    }
    
    
    /**
     * Creates List of LicenseModelGroup objects
     *
     * @param productGroupElementList - List of <ns:productGroup> elements
     * @return list of LicenseModelGroup objects
     */
    private static List<LicenseModelGroup> createLicenseModelGroupList(NodeList productGroupElementList) {
        List<LicenseModelGroup> lmgList = new ArrayList<LicenseModelGroup>();

        for (int i = 0; i < productGroupElementList.getLength(); i++) {
            LicenseModelGroup tempLMG = new LicenseModelGroup();
            Boolean isAccountGroup = false;

            Element productGroupElement = (Element)productGroupElementList.item(i);

            NamedNodeMap productGroupElementAttributeMap = productGroupElement.getAttributes();

            for (int j = 0; j < productGroupElementAttributeMap.getLength(); j++) {
                Attr attrs = (Attr) productGroupElementAttributeMap.item(j);

                if (attrs.getNodeName().equals("id") ) {
                	
                    if (attrs.getNodeValue().equals("ACCOUNTING_SUMMARY_GROUP")) { // Skip element with id="ACCOUNTING_SUMMARY_GROUP" -  do not add to the list
                        isAccountGroup = true;
                    }

                    if (attrs.getNodeName() != null) {
                        tempLMG.setId(attrs.getNodeValue());
                    }
                }
                if (attrs.getNodeName().equals("name") ) {
                    if (attrs.getNodeName() != null) {
                        tempLMG.setUrl(attrs.getNodeValue());
                    }
                }
            }

            if (isAccountGroup == true) {
                continue; // Skip element with id="ACCOUNTING_SUMMARY_GROUP" -  do not add to the list
            }

            Element abstractElement = (Element)productGroupElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "abstract").item(0);
            if (abstractElement != null) {
                tempLMG.setDescription(abstractElement.getTextContent());
            }

            Element titleElement = (Element)productGroupElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "title").item(0);
            if (titleElement != null) {
                tempLMG.setName(titleElement.getTextContent());
            }



            NodeList productElementList = productGroupElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "product");
            tempLMG.setLicenseModels(createLicenseModelList(productElementList));

            lmgList.add(tempLMG);
        }

        return lmgList;
    }


    /**
     * Creates list of LicenseModel elements
     *
     * @param productElementList - NodeList of <ns:product> elements:
     * @return List of LicenseModel objects
     */
    private static List<LicenseModel> createLicenseModelList(NodeList productElementList) {
        List<LicenseModel> lmList = new ArrayList<LicenseModel>();

        for (int i = 0; i < productElementList.getLength(); i++) {
            LicenseModel tempLM = new LicenseModel();
            Boolean isRestricted = true;

            Element productElement = (Element)productElementList.item(i);

            NamedNodeMap productElementAttributeMap = productElement.getAttributes();

            for (int j = 0; j < productElementAttributeMap.getLength(); j++) {
                Attr attrs = (Attr) productElementAttributeMap.item(j);

                if (attrs.getNodeName().equals("id") ) {
                    if (attrs.getNodeName() != null) {
                        tempLM.setId(attrs.getNodeValue());
                    }
                }

            }

            Element titleElement = (Element)productElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "title").item(0);
            if (titleElement != null) {
                tempLM.setName(titleElement.getTextContent());
            }

            Element abstractElement = (Element)productElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "abstract").item(0);
            if (abstractElement != null) {
                tempLM.setDescription(abstractElement.getTextContent());
            }

            Element calculationElement = (Element)productElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "calculation").item(0);
            Element declarationListElement = (Element)calculationElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "declarationList").item(0);
            Element predefinedParametersElement = (Element)declarationListElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "predefinedParameters").item(0);

            NodeList predefinedParametersParameterElementList = predefinedParametersElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "parameter");

            for (int k = 0; k < predefinedParametersParameterElementList.getLength(); k++) {
                Element parameterElement = (Element)predefinedParametersParameterElementList.item(k);

                NamedNodeMap parameterElementAttributeMap = parameterElement.getAttributes();

                for (int l = 0; l < parameterElementAttributeMap.getLength(); l++) {
                    Attr attrs = (Attr) parameterElementAttributeMap.item(l);

                    if (attrs.getNodeName().equals("name") ) {
                        if (attrs.getNodeName() != null) {                       	
                            if (attrs.getNodeValue().equals("ALL_ROLES")) {
                                Element valueElement = (Element)parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "value").item(0);

                                if (valueElement.getTextContent().equals("false")) {
                                    isRestricted = true;
                                }
                            }
                        }
                    }

                    if (attrs.getNodeName().equals("name") ) {
                        if (attrs.getNodeName() != null) {
                            if (attrs.getNodeValue().equals("ALLOWED_ROLES")) {
                                NodeList valueElementList = parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "value");

                                for (int m = 0; m < valueElementList.getLength(); m++) {
                                    tempLM.addRole(valueElementList.item(m).getTextContent());
                                }

                            }
                        }
                    }

                }


            }

            tempLM.setRestricted(isRestricted);

            tempLM.setParams(createLicenseModelParamList(declarationListElement));

            lmList.add(tempLM);
        }

        return lmList;
    }
    /**
     * Creates list of license model parameters
     *
     * @param declarationListElement - XML element <x:declarationList>
     * @return List of LicenseParam objects
     */
    private static List<LicenseParam> createLicenseModelParamList(Element declarationListElement) {   	
        List<LicenseParam> paramList = new ArrayList<LicenseParam>();

        // Predefined Parameters - create as LicenseParamDisplay objects
        Element predefinedParametersElement = (Element)declarationListElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "predefinedParameters").item(0);
        
        if (predefinedParametersElement != null) {
	        NodeList predefinedParametersParameterElementList = predefinedParametersElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "parameter");
	
	        for (int n = 0; n < predefinedParametersParameterElementList.getLength(); n++) {
	            Element parameterElement = (Element)predefinedParametersParameterElementList.item(n);
	
	            LicenseParamDisplay displayParam = createLicenseParamDisplay(parameterElement, "predefinedParameter");
	            paramList.add(displayParam);
	
	        }
        }

        // Configuration Parameters - might be LicenseParamDisplay || LicenseParamBln || LicenseParamEnum || LicenseParamInt || LicenseParamText
        Element configurationParametersElement = (Element)declarationListElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "configurationParameters").item(0);
        
        if (configurationParametersElement != null) {       	
	        NodeList configurationParametersParameterElementList = configurationParametersElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "parameter");
	
	        for (int n = 0; n < configurationParametersParameterElementList.getLength(); n++) {
	            Element parameterElement = (Element)configurationParametersParameterElementList.item(n);
	
	            LicenseParam lp = null;
	            Boolean isStringType = false;
	            Boolean multiAttributeExists = false; // true for enumration type parameters
	            //Boolean multiAttributeValue = false; // value of the multi attribute 
	            //Boolean optionalAttribute = false;  // What does optional attribute signify???
	
	            NamedNodeMap parameterElementAttributeMap = parameterElement.getAttributes();
	
	            // Create appropriate subclass object based on the "type" and "multi" attributes
	            for (int o = 0; o < parameterElementAttributeMap.getLength(); o++) {
	                Attr attrs = (Attr) parameterElementAttributeMap.item(o);
	
	                if (attrs.getNodeName().equals("type") ) {
	                    if (attrs.getNodeValue().equals("real")) {
	                        lp = createLicenseParamInt(parameterElement, "configurationParameter");
	                        paramList.add(lp);
	                        //System.out.println("lp - name "+lpInt.getName());
	                    }
	                    else if (attrs.getNodeValue().equals("string")) {
	                        isStringType = true;
	                    }
	                    else if (attrs.getNodeValue().equals("boolean")) {
	                        lp = createLicenseParamBln(parameterElement, "configurationParameter");
	                        paramList.add(lp);
	                    }
	
	                }
	                if (attrs.getNodeName().equals("multi")) {
	                	multiAttributeExists = true;
	                	//if (attrs.getNodeValue().equals("true")) {
	                	
	                		//multiAttributeValue = true;
	                	//}
	                	//else {
	                		//multiAttributeExists = false;
	                		//multiAttributeValue = false;
	                	//}
	                }
	               // if (attrs.getNodeName().equals("optional")) {
	               // 	if (attrs.getNodeName().equals("true")) {
	               // 		optionalAttribute = true;
	               // 	}
	               // 	else {
	               // 		optionalAttribute = false;
	               // 	}
	               // }
	
	            }
	
	            if (isStringType == true) {
	                if (multiAttributeExists == true) {
	                		lp = createLicenseParamEnum(parameterElement, "configurationParameter");
	                		paramList.add(lp);
	 
	                	//if (optionalAttribute == true) {
	                    //   
	                    //	 lp = createLicenseParamEnum(parameterElement, "configurationParameter");
		                //     paramList.add(lp);
	                    //} 
	                }
	                else {
	                    lp = createLicenseParamText(parameterElement, "configurationParameter");
	                    paramList.add(lp);
	                }
	            }
	
	        }
        }
        
        // Precalculated Parameters - create as LicenseParamDisplay objects
        Element precalculatedParametersElement = (Element)declarationListElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "precalculatedParameters").item(0);
        
        if (precalculatedParametersElement != null) {
	        NodeList precalculatedParametersParameterElementList = precalculatedParametersElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "parameter");
	       
	        for (int n = 0; n < precalculatedParametersParameterElementList.getLength(); n++) {
	            Element parameterElement = (Element)precalculatedParametersParameterElementList.item(n);
	
	            LicenseParamDisplay displayParam = createLicenseParamDisplay(parameterElement, "precalculatedParameter");
	            paramList.add(displayParam);
	
	        }
        }
        
        // Result Parameters - create as LicenseParamDisplay objects
        Element resultParametersElement = (Element)declarationListElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "resultParameters").item(0);
	       
        if (resultParametersElement != null) {
	        NodeList resultParametersParameterElementList = resultParametersElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "parameter");
	
	        for (int n = 0; n < resultParametersParameterElementList.getLength(); n++) {
	            Element parameterElement = (Element)resultParametersParameterElementList.item(n);
	
	            LicenseParamDisplay displayParam = createLicenseParamDisplay(parameterElement, "resultParameter");
	            paramList.add(displayParam);
	
	        }
        }
        
        
        return paramList;
    }

    /**
     * Creates LicenseParamBln object
     *
     * @param parameterElement
     * @param parameterClass - parameter class (predefinedParameter || precalculatedParameter || referencedParameter || resultParameter || configurationParameter)
     * @return
     */
    private static LicenseParamBln createLicenseParamBln(Element parameterElement, String parameterClass) {
        LicenseParamBln lpbln = new LicenseParamBln();
        lpbln.setParameterClass(parameterClass);

        NamedNodeMap parameterElementAttributeMap = parameterElement.getAttributes();

        for (int i = 0; i < parameterElementAttributeMap.getLength(); i++) {
            Attr attrs = (Attr) parameterElementAttributeMap.item(i);

            if (attrs.getNodeName().equals("name") ) {
                lpbln.setName(attrs.getNodeValue());
            }

        }

        Element parameterTitleElement = (Element)parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "title").item(0);


        if (parameterTitleElement != null) {
            lpbln.setTitle(parameterTitleElement.getTextContent());
        }

        Element parameterValueElement = (Element)parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "value").item(0);

        if (parameterValueElement != null) {
            if (parameterValueElement.getTextContent().equals("true")) {
                lpbln.setValue(true);
            }
            else {
                lpbln.setValue(false);
            }
        }


        return lpbln;
    }


    /**
     * Creates LicenseParamDisplay object
     *
     * @param parameterElement
     * @param parameterClass - parameter class (predefinedParameter || precalculatedParameter || referencedParameter || resultParameter || configurationParameter)
     * @return
     */
    private static LicenseParamDisplay createLicenseParamDisplay(Element parameterElement, String parameterClass) {
        LicenseParamDisplay lpd = new LicenseParamDisplay();
        lpd.setParameterClass(parameterClass);

        NamedNodeMap parameterElementAttributeMap = parameterElement.getAttributes();

        for (int i = 0; i < parameterElementAttributeMap.getLength(); i++) {
            Attr attrs = (Attr) parameterElementAttributeMap.item(i);

            if (attrs.getNodeName().equals("name") ) {
                lpd.setName(attrs.getNodeValue());
            }

        }

        Element parameterTitleElement = (Element)parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "title").item(0);

        if (parameterTitleElement != null) {
            lpd.setTitle(parameterTitleElement.getTextContent());
        }

        NodeList valueElementList = parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "value");

        for (int j = 0; j < valueElementList.getLength(); j++) {
            Element valueElement = (Element)valueElementList.item(j);

            lpd.addValue(valueElement.getTextContent());

        }

        return lpd;
    }


    /**
     * Creates LicenseParamEnum object
     *
     * @param parameterElement
     * @param parameterClass - parameter class (predefinedParameter || precalculatedParameter || referencedParameter || resultParameter || configurationParameter)
     * @return
     */
    private static LicenseParamEnum createLicenseParamEnum(Element parameterElement, String parameterClass) {
    	
        Boolean multiAttributeValue = false;
        LicenseParamEnum lpEnum = new LicenseParamEnum();
        lpEnum.setParameterClass(parameterClass);

        NamedNodeMap parameterElementAttributeMap = parameterElement.getAttributes();

        for (int i = 0; i < parameterElementAttributeMap.getLength(); i++) {
            Attr attrs = (Attr) parameterElementAttributeMap.item(i);

            if (attrs.getNodeName().equals("name") ) {        
                lpEnum.setName(attrs.getNodeValue());
            }

            if (attrs.getNodeName().equals("multi")) {

                if (attrs.getNodeValue().equals("true")) {
					multiAttributeValue = true;
                }
                else {
                	multiAttributeValue = false;
                }
            }

        }

        lpEnum.setMulti(multiAttributeValue);

        Element parameterTitleElement = (Element)parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "title").item(0);

        if (parameterTitleElement != null) {
            lpEnum.setTitle(parameterTitleElement.getTextContent());
        }

        NodeList valueElementList = parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "value");

        for (int j = 0; j < valueElementList.getLength(); j++) {
            Element valueElement = (Element)valueElementList.item(j);

            NamedNodeMap valueElementAttributeMap = valueElement.getAttributes();

            for (int k = 0; k < valueElementAttributeMap.getLength(); k++) {
                Attr attrs = (Attr) valueElementAttributeMap.item(k);

                if (attrs.getNodeName().equals("selected") ) {
                    if (attrs.getNodeValue().equals("true")) {
                        lpEnum.setDefaultValue(valueElement.getTextContent());
                        lpEnum.addSelection(valueElement.getTextContent());
                    }
                }

            }

            lpEnum.addOption(valueElement.getTextContent());
        }

        return lpEnum;
    }

    

    /**
     * Creates LicenseParamInt object
     *
     * @param parameterElement
     * @param parameterClass - parameter class (predefinedParameter || precalculatedParameter || referencedParameter || resultParameter || configurationParameter)
     * @return
     */
    private static LicenseParamInt createLicenseParamInt(Element parameterElement, String parameterClass) {
        LicenseParamInt lpInt = new LicenseParamInt();
        lpInt.setParameterClass(parameterClass);

        NamedNodeMap parameterElementAttributeMap = parameterElement.getAttributes();

        for (int i = 0; i < parameterElementAttributeMap.getLength(); i++) {
            Attr attrs = (Attr) parameterElementAttributeMap.item(i);

            if (attrs.getNodeName().equals("name") ) {
                lpInt.setName(attrs.getNodeValue());
            }

        }

        Element parameterTitleElement = (Element)parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "title").item(0);

        if (parameterTitleElement != null) {
            lpInt.setTitle(parameterTitleElement.getTextContent());
        }

        Element valueElement = (Element)parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "value").item(0);

        if (valueElement != null) {
            lpInt.setValue(Integer.parseInt(valueElement.getTextContent()));
        }


        return lpInt;
    }


    /**
     * Creates LicenseParamText object
     *
     * @param parameterElement
     * @param parameterClass - parameter class (predefinedParameter || precalculatedParameter || referencedParameter || resultParameter || configurationParameter)
     * @return
     */
    private static LicenseParamText createLicenseParamText(Element parameterElement, String parameterClass) {
        LicenseParamText lpt = new LicenseParamText();
        lpt.setParameterClass(parameterClass);

        NamedNodeMap parameterElementAttributeMap = parameterElement.getAttributes();

        for (int i = 0; i < parameterElementAttributeMap.getLength(); i++) {
            Attr attrs = (Attr) parameterElementAttributeMap.item(i);

            if (attrs.getNodeName().equals("name") ) {
                lpt.setName(attrs.getNodeValue());
            }

        }

        Element parameterTitleElement = (Element)parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "title").item(0);

        if (parameterTitleElement != null) {
            lpt.setTitle(parameterTitleElement.getTextContent());
        }

        NodeList valueElementList = parameterElement.getElementsByTagNameNS("http://www.conterra.de/xcpf/1.1", "value");


        for (int j = 0; j < valueElementList.getLength(); j++) {
            Element valueElement = (Element)valueElementList.item(j);

            lpt.addValue(valueElement.getTextContent());

        }

        return lpt;
    }

    /**
     * Creates LicenseConcludeResponseObject from the concludeLicense operation's response string
     * 
     * @param response
     * @return
     * @throws Exception
     */
    public static LicenseConcludeResponseObject parseConcludeLicenseResponse(String response) throws Exception {
    	LicenseConcludeResponseObject lcro = new LicenseConcludeResponseObject();
    	
    	try {
    		Document xmlDoc = LicenseParser.createXMLDocumentFromString(response);
    		NodeList LicenseReferenceNL = xmlDoc.getElementsByTagNameNS("http://www.52north.org/license/0.3.2", "LicenseReference");
    		
    		for (int i = 0; i < LicenseReferenceNL.getLength(); i++ ) {
	    		Element attributeStatementElement = (Element)LicenseReferenceNL.item(i);
	    		
	    		NodeList attributeElementList = attributeStatementElement.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Attribute");
	    		
	    		for (int j = 0; j < attributeElementList.getLength(); j++) {
	    			Element attributeElement = (Element)attributeElementList.item(j);
	    			Element AttributeValueElement = (Element)attributeElement.getElementsByTagName("AttributeValue").item(0);
	    			    			
	    			NamedNodeMap licenseReferenceElementAttributeMap = attributeElement.getAttributes();
	
	                for (int k = 0; k < licenseReferenceElementAttributeMap.getLength(); k++) {
	                    Attr attrs = (Attr) licenseReferenceElementAttributeMap.item(k);
	
	                    if (attrs.getNodeName().equals("Name") ) {
	                       	if(attrs.getNodeValue().equals("urn:opengeospatial:ows4:geodrm:NotOnOrAfter")) {
	                       		lcro.setValidTo(AttributeValueElement.getTextContent());
	                    	}
	                    	if(attrs.getNodeValue().equals("urn:opengeospatial:ows4:geodrm:ProductID")) {
	                    		lcro.setProductId(AttributeValueElement.getTextContent());
					       	}
	                    	if(attrs.getNodeValue().equals("urn:opengeospatial:ows4:geodrm:LicenseID")) {
	                    		lcro.setLicenseId(AttributeValueElement.getTextContent());
	                    	}
	                    }
	
	                }
	                
	    		}
	    		
    		}
    		
    	} catch (Exception e) {
    		throw e;
    	}
    
         
    	return lcro; 
    }
    
    
    /**
     * Creates DOM Document object from XML string
     *
     * @param xmlString
     * @return xml document
     * @throws Exception
     */
    public static Document createXMLDocumentFromString(String xmlString) throws Exception {
        Document document = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);

            DocumentBuilder builder = dbf.newDocumentBuilder();
            InputStream is  = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
            document = builder.parse(is);

        } catch(SAXParseException spe) {
            Locator2Impl locator = new Locator2Impl();
            spe.printStackTrace();
            throw new SAXParseException("", locator);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return document;
    }
    
    /**
     * Parses a list of active licenses from GetLicenseReferences response string
     * 
     * @param xml	- xml response
     * @return		- ArrayList of license identifiers that are active
     * @throws Exception
     */
    public static ArrayList<String> parseActiveLicensesFromGetLicenseReferencesResponse(String xml) throws Exception {
    	ArrayList<String> activeLicenses = new ArrayList<String>();
    	
    	try {
            Document xmlDoc = createXMLDocumentFromString(xml);
            
            NodeList licenseReferenceList = xmlDoc.getElementsByTagNameNS("http://www.52north.org/license/0.3.2", "LicenseReference");
    
            
            for (int i = 0; i < licenseReferenceList.getLength(); i++) {
            	Element licenseReferenceElement = (Element)licenseReferenceList.item(i);
            	
            	NodeList attributeElementList = licenseReferenceElement.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "Attribute");

            	for (int j = 0; j < attributeElementList.getLength(); j++) {
            		Element attributeElement = (Element)attributeElementList.item(j);
            		Element attributeValueElement = (Element)attributeElement.getElementsByTagNameNS("urn:oasis:names:tc:SAML:2.0:assertion", "AttributeValue").item(0);
            	  
            		NamedNodeMap attributeMap = attributeElement.getAttributes();
            		
            		for (int k = 0; k < attributeMap.getLength(); k++) {
            			Attr attrs = (Attr) attributeMap.item(k);
      	            	if (attrs.getNodeName().equals("Name") ) {

      	                    if(attrs.getNodeValue().equals("urn:opengeospatial:ows4:geodrm:LicenseID")) {
      	                    	activeLicenses.add(attributeValueElement.getTextContent());
      	                    }
      	    	        }
      	            	
      	
      	            }
	
            	}
	    		
    
            }
            
	    } catch (Exception e) {
	    	throw e;
	    }
    	
    	return activeLicenses;
    }
    
}

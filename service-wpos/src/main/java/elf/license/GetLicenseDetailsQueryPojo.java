package elf.license;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

/**
 * 	POJO representation of the GetLicenseDetails query response
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
public class GetLicenseDetailsQueryPojo {
	private ObjectMapper xmlMapper = null;
	private GetLicenseDetailsQueryResponse XMLObjectPojo = null;
	
	
	/**
	 * Constructor
	 * 
	 * @param xmlString XML response document
	 * @throws Exception
	 */
	public GetLicenseDetailsQueryPojo(String xmlString) throws Exception {
		try {
			this.xmlMapper = new XmlMapper();
			this.XMLObjectPojo = readXMLStringIntoPojo(xmlString);
		
		} catch  (Exception e) {
			throw e;
		}
	}
	
	/**
	 * Returns the XML query as a POJO
	 * 
	 * @return
	 */
	public GetLicenseDetailsQueryResponse getXMLObjectPojo() {
		return XMLObjectPojo;
	}

	
	/**
	 * Deserializes the xml string into POJO
	 * 
	 * @param xmlString
	 * @return
	 * @throws Exception
	 */
	private GetLicenseDetailsQueryResponse readXMLStringIntoPojo(String xmlString) throws Exception {
		GetLicenseDetailsQueryResponse response = null;
		
		try {
			JacksonXmlModule module = new JacksonXmlModule();
		
			response = this.xmlMapper.readValue(xmlString, GetLicenseDetailsQueryResponse.class);
			
			return response;
			
		} catch (Exception e) {
			//e.printStackTrace();
			
			throw e;
		}
	}
		
	
	/**
	 * Serializes the POJO into XML string
	 * 
	 * @return XML representation of the POJO
	 * @throws Exception
	 */
	public String getXMLRepresentation() throws Exception {
		String XMLString = "";
		
		try {
			XMLString = this.xmlMapper.writeValueAsString(this.XMLObjectPojo);
			
		} catch (Exception e) {
			throw e;
		}
		
		return XMLString;
	}
	

}

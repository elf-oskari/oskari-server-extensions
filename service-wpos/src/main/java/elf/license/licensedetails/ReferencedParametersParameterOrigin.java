package elf.license.licensedetails;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * <x:origin> under <x:referencedParameters> element
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class ReferencedParametersParameterOrigin {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "parameterName", isAttribute = true)	
	private String _parameterNameAttribute;

	
	
	
	
	
	
	public String get_parameterNameAttribute() {
		return _parameterNameAttribute;
	}

	public void set_parameterNameAttribute(String _parameterNameAttribute) {
		this._parameterNameAttribute = _parameterNameAttribute;
	}
		

}

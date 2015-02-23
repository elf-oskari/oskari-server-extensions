package elf.license.licensedetails;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**

 * 	<x:parameter> element under <x:referencedParameters
 *  
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class ReferencedParametersParameter {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "name", isAttribute = true)	
	private String _nameAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "type", isAttribute = true)	
	private String _typeAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "origin")	
	private ReferencedParametersParameterOrigin _origin;

	
	public String get_nameAttribute() {
		return _nameAttribute;
	}

	public void set_nameAttribute(String _nameAttribute) {
		this._nameAttribute = _nameAttribute;
	}

	public String get_typeAttribute() {
		return _typeAttribute;
	}


	public void set_typeAttribute(String _typeAttribute) {
		this._typeAttribute = _typeAttribute;
	}

	public ReferencedParametersParameterOrigin get_origin() {
		return _origin;
	}

	public void set_origin(ReferencedParametersParameterOrigin _origin) {
		this._origin = _origin;
	}

	
	

	
}

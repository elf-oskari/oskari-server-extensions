package elf.license;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**

 * 	<x:parameter> element under <x:resultParameters
 *  
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class ResultParametersParameter {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "fractionDigits", isAttribute = true)	
	private String _fractionDigitsAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "name", isAttribute = true)	
	private String _nameAttribute;
	
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "type", isAttribute = true)	
	private String _typeAttribute;
	

	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "title")
	@JsonIgnoreProperties(ignoreUnknown=true)
	private ResultParametersParameterTitle _title;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "unit")
	@JsonIgnoreProperties(ignoreUnknown=true)
	private ResultParametersParameterUnit _unit;

	
	
	
	public String get_fractionDigitsAttribute() {
		return _fractionDigitsAttribute;
	}

	public void set_fractionDigitsAttribute(String _fractionDigitsAttribute) {
		this._fractionDigitsAttribute = _fractionDigitsAttribute;
	}
	
	
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

	public ResultParametersParameterTitle get_title() {
		return _title;
	}

	public void set_title(ResultParametersParameterTitle _title) {
		this._title = _title;
	}

	public ResultParametersParameterUnit get_unit() {
		return _unit;
	}

	public void set_unit(ResultParametersParameterUnit _unit) {
		this._unit = _unit;
	}
	
	


	

	
}

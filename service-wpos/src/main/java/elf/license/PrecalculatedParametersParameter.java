package elf.license;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**

 * 	<x:parameter> element under <x:precalculatedParameters>
 *  
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class PrecalculatedParametersParameter {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "fn", isAttribute = true)	
	private String _fnAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "fnargs", isAttribute = true)	
	private String _fnargsAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "name", isAttribute = true)	
	private String _nameAttribute;
	
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "type", isAttribute = true)	
	private String _typeAttribute;
	

	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "title")
	@JsonIgnoreProperties(ignoreUnknown=true)
	private PrecalculatedParametersParameterTitle _title;
	
	//@JsonIgnoreProperties(ignoreUnknown=true)
	//@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "title")	
	//private String _title;

	

	public String get_fnAttribute() {
		return _fnAttribute;
	}



	public void set_fnAttribute(String _fnAttribute) {
		this._fnAttribute = _fnAttribute;
	}



	public String get_fnargsAttribute() {
		return _fnargsAttribute;
	}



	public void set_fnargsAttribute(String _fnargsAttribute) {
		this._fnargsAttribute = _fnargsAttribute;
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



	public PrecalculatedParametersParameterTitle get_title() {
		return _title;
	}



	public void set_title(PrecalculatedParametersParameterTitle _title) {
		this._title = _title;
	}



	//public String get_title() {
	//	return _title;
	//}

	//public void set_title(String _title) {
	//	this._title = _title;
	//}


	
}

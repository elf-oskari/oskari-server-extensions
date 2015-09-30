package elf.license;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**

 * 	<x:parameter> element under <x:configurationsParameters>
 *  
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class ConfigurationParametersParameter {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "name", isAttribute = true)	
	private String _nameAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "type", isAttribute = true)	
	private String _typeAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "multi", isAttribute = true)	
	private String _multiAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "select", isAttribute = true)	
	private String _selectAttribute;
	
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "title")
	@JsonIgnoreProperties(ignoreUnknown=true)
	private ConfigurationParametersParameterTitle _title;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "abstract")
	@JsonIgnoreProperties(ignoreUnknown=true)
	private ConfigurationParametersParameterAbstract _abstract;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "value")
	@JsonIgnoreProperties(ignoreUnknown=true)
	private List<ConfigurationParametersParameterValue> _value;

	
	
	
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

	public String get_multiAttribute() {
		return _multiAttribute;
	}

	public void set_multiAttribute(String _multiAttribute) {
		this._multiAttribute = _multiAttribute;
	}

	public String get_selectAttribute() {
		return _selectAttribute;
	}

	public void set_selectAttribute(String _selectAttribute) {
		this._selectAttribute = _selectAttribute;
	}

	public ConfigurationParametersParameterTitle get_title() {
		return _title;
	}

	public void set_title(ConfigurationParametersParameterTitle _title) {
		this._title = _title;
	}

	public ConfigurationParametersParameterAbstract get_abstract() {
		return _abstract;
	}

	public void set_abstract(ConfigurationParametersParameterAbstract _abstract) {
		this._abstract = _abstract;
	}

	public List<ConfigurationParametersParameterValue> get_value() {
		return _value;
	}

	public void set_value(List<ConfigurationParametersParameterValue> _value) {
		this._value = _value;
	}
	

	

}

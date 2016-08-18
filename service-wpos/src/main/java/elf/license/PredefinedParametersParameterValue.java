package elf.license;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * <x:value> under <x:predefinedParameters> element
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class PredefinedParametersParameterValue {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "selected", isAttribute = true)	
	private String _selectedAttribute;
    
    @JacksonXmlText
	private String _value;

	public String get_selectedAttribute() {
		return _selectedAttribute;
	}

	public void set_selectedAttribute(String _selectedAttribute) {
		this._selectedAttribute = _selectedAttribute;
	}

	public String get_value() {
		return _value;
	}

	public void set_value(String _value) {
		this._value = _value;
	}

 
	

}

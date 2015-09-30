package elf.license;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * <x:abstract> under <x:configurationParameters> element
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class ConfigurationParametersParameterAbstract {
	@JacksonXmlProperty(localName = "lang", isAttribute = true)	
	private String _langAttribute;
	
    @JacksonXmlText
	private String _abstract;

	public String get_langAttribute() {
		return _langAttribute;
	}

	public void set_langAttribute(String _langAttribute) {
		this._langAttribute = _langAttribute;
	}

	public String get_abstract() {
		return _abstract;
	}

	public void set_abstract(String _abstract) {
		this._abstract = _abstract;
	}

	

 
	

}

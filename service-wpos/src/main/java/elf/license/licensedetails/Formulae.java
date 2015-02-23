package elf.license.licensedetails;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * <x:formulae> element
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class Formulae {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "function")	
	@JacksonXmlElementWrapper(useWrapping=false)
	private List<Function> _functionList;

	
	
	public List<Function> get_functionList() {
		return _functionList;
	}

	public void set_functionList(List<Function> _functionList) {
		this._functionList = _functionList;
	}
	
	
}

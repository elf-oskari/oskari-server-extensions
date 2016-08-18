package elf.license.licensedetails;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


/**
 * <x:referencedParameters> element
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class ReferencedParameters {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "parameter")	
	@JacksonXmlElementWrapper(useWrapping=false)
	private List<ReferencedParametersParameter> _parameterList;

	
	
	
	
	
	public List<ReferencedParametersParameter> get_parameterList() {
		return _parameterList;
	}

	public void set_parameterList(List<ReferencedParametersParameter> _parameterList) {
		this._parameterList = _parameterList;
	}

	
	
	
}

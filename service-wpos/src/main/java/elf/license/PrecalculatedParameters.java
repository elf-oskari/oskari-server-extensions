package elf.license;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;



/**
 * <x:precalculatedParameters> element
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class PrecalculatedParameters {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "parameter")	
	@JacksonXmlElementWrapper(useWrapping=false)
	private List<PrecalculatedParametersParameter> _parameterList;

	
	
	
	
	
	public List<PrecalculatedParametersParameter> get_parameterList() {
		return _parameterList;
	}

	public void set_parameterList(List<PrecalculatedParametersParameter> _parameterList) {
		this._parameterList = _parameterList;
	}

	
	
	
}

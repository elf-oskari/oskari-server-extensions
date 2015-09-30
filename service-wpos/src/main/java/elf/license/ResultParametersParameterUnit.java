package elf.license;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

/**
 * <x:unit> under <x:resultParameters> element
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class ResultParametersParameterUnit {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "textstyle", isAttribute = true)	
	private String _parameterTextstyle;

	
	
	
	
	
	public String get_parameterTextstyle() {
		return _parameterTextstyle;
	}

	public void set_parameterTextstyle(String _parameterTextstyle) {
		this._parameterTextstyle = _parameterTextstyle;
	}
	

}

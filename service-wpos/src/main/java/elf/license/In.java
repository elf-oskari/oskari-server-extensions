package elf.license;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class In {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "paramRef")
	@JacksonXmlElementWrapper(useWrapping=false)
	private List<String> _paramRef;

	
	
	
	public List<String> get_paramRef() {
		return _paramRef;
	}

	public void set_paramRef(List<String> _paramRef) {
		this._paramRef = _paramRef;
	}

	
	
	



}

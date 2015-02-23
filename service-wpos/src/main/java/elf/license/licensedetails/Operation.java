package elf.license.licensedetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


/**
 * <x:moperation> element NOT IMPLEMENTED
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class Operation {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "math")	
	private Math _math;

	
	
	
	public Math get_math() {
		return _math;
	}

	public void set_math(Math _math) {
		this._math = _math;
	}

	

}

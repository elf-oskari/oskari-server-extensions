package elf.license.licensedetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * <x:function> element
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class Function {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "name", isAttribute = true)	
	private String _nameAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "out")	
	private Out _out;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "in")	
	private In _in;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "operation")	
	private Operation _operation;

	
	
	
	public String get_nameAttribute() {
		return _nameAttribute;
	}

	public void set_nameAttribute(String _nameAttribute) {
		this._nameAttribute = _nameAttribute;
	}
	
	public Out get_out() {
		return _out;
	}

	public void set_out(Out _out) {
		this._out = _out;
	}
	
	public In get_in() {
		return _in;
	}

	public void set_in(In _in) {
		this._in = _in;
	}

	

	public Operation get_operation() {
		return _operation;
	}

	public void set_operation(Operation _operation) {
		this._operation = _operation;
	}


	
}

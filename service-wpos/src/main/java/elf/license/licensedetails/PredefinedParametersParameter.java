package elf.license.licensedetails;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;


@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class PredefinedParametersParameter {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "name", isAttribute = true)	
	private String _nameAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "type", isAttribute = true)	
	private String _typeAttribute;
	

	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "title")
	@JsonIgnoreProperties(ignoreUnknown=true)
	private PredefinedParametersParameterTitle _title;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "abstract")
	@JsonIgnoreProperties(ignoreUnknown=true)
	private PredefinedParametersParameterAbstract _abstract;
	
	@JacksonXmlElementWrapper(useWrapping=false)
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "value")
	@JsonIgnoreProperties(ignoreUnknown=true)
	private List<PredefinedParametersParameterValue> _value;
	

	


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
	

	public PredefinedParametersParameterTitle get_title() {
		return _title;
	}

	

	public void set_title(PredefinedParametersParameterTitle _title) {
		this._title = _title;
	}

	public List<PredefinedParametersParameterValue> get_value() {
		return _value;
	}

	public void set_value(List<PredefinedParametersParameterValue> _value) {
		this._value = _value;
	}

	public PredefinedParametersParameterAbstract get_abstract() {
		return _abstract;
	}

	public void set_abstract(PredefinedParametersParameterAbstract _abstract) {
		this._abstract = _abstract;
	}
	
	
	
}

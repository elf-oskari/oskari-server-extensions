package elf.license.licensedetails;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * <x:declarationList> element
 * 
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class DeclarationList {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "referencedParameters")	
	private ReferencedParameters _referencedParameters;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "predefinedParameters")	
	private PredefinedParameters _predefinedParameters;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "configurationParameters")	
	private ConfigurationParameters _configurationParameters;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "precalculatedParameters")	
	private PrecalculatedParameters _precalculatedParameters;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "resultParameters")	
	private ResultParameters _resultParameters;

	
	
	public ReferencedParameters get_referencedParameters() {
		return _referencedParameters;
	}

	public void set_referencedParameters(ReferencedParameters _referencedParameters) {
		this._referencedParameters = _referencedParameters;
	}

	public PredefinedParameters get_predefinedParameters() {
		return _predefinedParameters;
	}

	public void set_predefinedParameters(PredefinedParameters _predefinedParameters) {
		this._predefinedParameters = _predefinedParameters;
	}

	public ConfigurationParameters get_configurationParameters() {
		return _configurationParameters;
	}

	public void set_configurationParameters(
		ConfigurationParameters _configurationParameters) {
		this._configurationParameters = _configurationParameters;
	}

	public PrecalculatedParameters get_precalculatedParameters() {
		return _precalculatedParameters;
	}

	public void set_precalculatedParameters(
			PrecalculatedParameters _precalculatedParameters) {
		this._precalculatedParameters = _precalculatedParameters;
	}

	public ResultParameters get_resultParameters() {
		return _resultParameters;
	}

	public void set_resultParameters(ResultParameters _resultParameters) {
		this._resultParameters = _resultParameters;
	}

	
	
	
	
}

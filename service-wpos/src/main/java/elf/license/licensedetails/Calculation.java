package elf.license.licensedetails;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * <x:calculation> element
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class Calculation {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "declarationList")	
	private DeclarationList _declarationList;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "formulae")	
	private Formulae _formulae;

	
	
	
	public DeclarationList get_declarationList() {
		return _declarationList;
	}

	public void set_declarationList(DeclarationList _declarationList) {
		this._declarationList = _declarationList;
	}

	public Formulae get_formulae() {
		return _formulae;
	}

	public void set_formulae(Formulae _formulae) {
		this._formulae = _formulae;
	}
	
	
}

package elf.license;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


//xmlns:w="http://www.conterra.de/wpos/1.1" 
//xmlns:x="http://www.conterra.de/xcpf/1.1"


/**
 * <w:WPOSResponse> element
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JacksonXmlRootElement(namespace = "http://www.conterra.de/wpos/1.1", localName = "w:WPOSResponse")
@JsonIgnoreProperties(ignoreUnknown=true)
public class GetLicenseDetailsQueryResponse {
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "status", isAttribute = true)	
	private String _statusAttribute;
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1",	localName = "catalog")
	private Catalog _catalog;
	
	
	
	
	
	
	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1")
	public Catalog get_catalog() {
		return _catalog;
	}

	@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1")
	public void set_catalog(Catalog _catalog) {
		this._catalog = _catalog;
	}

	public String get_statusAttribute() {
		return _statusAttribute;
	}

	public void set_statusAttribute(String _statusAttribute) {
		this._statusAttribute = _statusAttribute;
	}
	
	
	
}
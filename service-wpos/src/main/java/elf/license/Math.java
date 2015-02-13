package elf.license;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

/**
 * <x:math> element NOT IMPLEMENTED
 * @author Pekka Latvala / National Land Survey of Finland
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@javax.xml.bind.annotation.XmlType(namespace = "http://www.conterra.de/xcpf/1.1", name="x")
public class Math {
	//@JacksonXmlProperty(namespace = "http://www.conterra.de/xcpf/1.1", localName = "apply")	
	//private Apply _apply;

	
	
	
	//public Apply get_apply() {
	//	return _apply;
	//}

	//public void set_apply(Apply _apply) {
	//	this._apply = _apply;
	//}

	
}

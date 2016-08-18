package eu.elf.license.model;

import java.util.ArrayList;
import java.util.List;

/**
 * License parameter for showing NON-editable textual content f. ex. license text.
 */
public class LicenseParamDisplay extends LicenseParam {
    //private String value;
    private List<String> values = new ArrayList<String>(); // There can be multiple value elements

	//public List<String> getValues() {
	//	return values;
	//}

    public void clearValues() {
    	values.clear();
	}

	public void addValue(final String value) {
		values.add(value);
	}

	public void setValues(List<String> valueList) {
		this.values = valueList;
	}
	
	public List<String> getValues() {
		return this.values;
	}
	
    //public String getValue() {
    //    return value;
    //}

    //public void setValue(String value) {
    //    this.value = value;
    //}
   
	public boolean isValid() {
        return true;
    }
    
}

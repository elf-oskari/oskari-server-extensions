package eu.elf.license.model;

/**
 * Baseclass for typed params
 */
public abstract class LicenseParam {
    private String name; // technical name
    private String title; // ui name
    private String parameterClass; // parameter class (predefinedParameter || precalculatedParameter || referencedParameter || resultParameter || configurationParameter)

    public String getType() {
    	return getClass().getSimpleName();
    }
    
	public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getParameterClass() {
		return parameterClass;
	}

	public void setParameterClass(String parameterClass) {
		this.parameterClass = parameterClass;
	}
    
    public abstract boolean isValid();
}

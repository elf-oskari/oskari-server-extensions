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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LicenseParam)) return false;

        LicenseParam that = (LicenseParam) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (parameterClass != null ? !parameterClass.equals(that.parameterClass) : that.parameterClass != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (parameterClass != null ? parameterClass.hashCode() : 0);
        return result;
    }
}

package eu.elf.license.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Single License model that the user can subscribe (or has subscribed?)
 */
public class LicenseModel {

    private String id;
    private String name;
    private String description;
    private boolean isRestricted; // !allowAllRoles
    private List<String> roles = new ArrayList<String>(); // only used if(isRestricted)
    private List<LicenseParam> params = new ArrayList<LicenseParam>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void clearParams() {
        params.clear();
    }

    public void addParam(final String key, boolean value) {
        LicenseParamBln param = new LicenseParamBln();
        param.setName(key);
        param.setValue(value);
        params.add(param);
    }

    public void addParam(final String key, int value) {
        LicenseParamInt param = new LicenseParamInt();
        param.setName(key);
        param.setValue(value);
        params.add(param);
    }

    public void addParam(final String key, String... values) {
        LicenseParamText param = new LicenseParamText();
        param.setName(key);
        for(String val : values) {
            param.addValue(val);
        }
        params.add(param);
    }

    public LicenseParam getParam(final String name) {

        if (name == null || params == null) {
            return null;
        }
        for (LicenseParam parm : params) {
            if (name.equalsIgnoreCase(parm.getName())) {
                return parm;
            }
        }
        return null;
    }

    public void addParam(final LicenseParam param) {
        params.add(param);
    }

    public List<LicenseParam> getParams() {
        return params;
    }
    
    public void setParams(List<LicenseParam> lpList) {
        this.params = lpList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRestricted() {
        return isRestricted;
    }

    public void setRestricted(boolean isRestricted) {
        this.isRestricted = isRestricted;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void clearRoles() {
        roles.clear();
    }

    public void addRole(final String rolename) {
        roles.add(rolename);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LicenseModel)) return false;

        LicenseModel that = (LicenseModel) o;

        if (isRestricted != that.isRestricted) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (params != null ? !params.equals(that.params) : that.params != null) return false;
        if (roles != null ? !roles.equals(that.roles) : that.roles != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (isRestricted ? 1 : 0);
        result = 31 * result + (roles != null ? roles.hashCode() : 0);
        result = 31 * result + (params != null ? params.hashCode() : 0);
        return result;
    }
}

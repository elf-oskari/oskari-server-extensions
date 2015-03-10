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
}

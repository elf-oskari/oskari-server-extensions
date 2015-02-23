package eu.elf.license.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a "Product group" or "Container for License models"
 */
public class LicenseModelGroup {

    private String id;
    private String name;
    private String description;
    // base url for service or including user token if user has license?
    private String url;
    private List<LicenseModel> licenseModels = new ArrayList<LicenseModel>();

       
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void clearLicenseModels() {
        licenseModels.clear();
    }

    public void addLicenseModel(final LicenseModel model) {
        licenseModels.add(model);
    }

    public void setLicenseModels(List<LicenseModel> licenseModelList) {
    	this.licenseModels = licenseModelList;
    }
    
    public List<LicenseModel> getLicenseModels() {
        return licenseModels;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}

package eu.elf.license.model;

/**
 * Baseclass for typed params
 */
public abstract class LicenseParam {
    private String name; // technical name
    private String title; // ui name

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

    public abstract boolean isValid();
}

package com.edifecs.epp.security.apps.model;

import java.io.Serializable;

/**
 * Created by sandeep.kath on 6/24/2014.
 */
public class AppComponent implements Serializable {
    private static final long serialVersionUID = -1322322139926390329L;

    private long id;
    private String name;

    private String description;
    private String version;
    private String releaseDate;
    private String componentIconLink;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getComponentIconLink() {
        return componentIconLink;
    }

    public void setComponentIconLink(String componentIconLink) {
        this.componentIconLink = componentIconLink;
    }

}

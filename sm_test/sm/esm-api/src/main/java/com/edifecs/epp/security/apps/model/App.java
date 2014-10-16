package com.edifecs.epp.security.apps.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sandeep.kath on 6/24/2014.
 */
public class App implements Serializable {
    private static final long serialVersionUID = -1322322139926390329L;
    private String id;
    private String name;
    private String description;

    private String displayVersion;
    private String version;
    private String releaseDate;
    private String installedDate;
    private String rating;
    private String appIconLink;
    private List<AppComponent> appComponentList = new ArrayList<AppComponent>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getInstalledDate() {
        return installedDate;
    }

    public void setInstalledDate(String installedDate) {
        this.installedDate = installedDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAppIconLink() {
        return appIconLink;
    }

    public void setAppIconLink(String appIconLink) {
        this.appIconLink = appIconLink;
    }

    public List<AppComponent> getAppComponentList() {
        return appComponentList;
    }

    public void setAppComponentList(List<AppComponent> appComponentList) {
        this.appComponentList = appComponentList;
    }

    public void addComponent(AppComponent appComponent) {
        this.appComponentList.add(appComponent);
    }

    public void removeComponent(AppComponent appComponent) {
        this.appComponentList.remove(appComponent);
    }


    public String getDisplayVersion() {
        return displayVersion;
    }

    public void setDisplayVersion(String displayVersion) {
        this.displayVersion = displayVersion;
    }

}

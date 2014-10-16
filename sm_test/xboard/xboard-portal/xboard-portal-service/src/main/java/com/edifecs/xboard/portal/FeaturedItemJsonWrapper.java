package com.edifecs.xboard.portal;

import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by abhising on 25-08-2014.
 */
public class FeaturedItemJsonWrapper implements Serializable {

    private String app;

    private String version;
    private Set<Section> sections = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !obj.getClass().equals(getClass())) {
            return false;
        }
        FeaturedItemJsonWrapper otherObj = (FeaturedItemJsonWrapper) obj;
        if (otherObj.getApp().equalsIgnoreCase(this.getApp()) && otherObj.getVersion().equals(this.getVersion())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int prime = 29;
        int result = 7;
        result += prime * result + getApp().hashCode();
        result += prime * result + getVersion().hashCode();
        return result;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String appName) {
        this.app = appName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Set<Section> getSections() {
        return sections;
    }

    public void setSections(Set<Section> sections) {
        this.sections = sections;
    }

    public static class Section {
        private String name;
        @Expose(serialize = false)
        private String permission;
        private Set<FeaturedItem> featuredItems = new HashSet<>();

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !obj.getClass().equals(this.getClass())) {
                return false;
            }
            Section otherObj = (Section) obj;
            if (otherObj.getName().equalsIgnoreCase(this.getName())) {
                return true;
            }

            return false;
        }

        @Override
        public int hashCode() {
            int prime = 41;
            int result = 7;
            result += prime * result + getName().hashCode();
            return result;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPermission() {
            return permission;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        public Set<FeaturedItem> getFeaturedItems() {
            return featuredItems;
        }

        public void setFeaturedItems(Set<FeaturedItem> featuredItems) {
            this.featuredItems = featuredItems;
        }
    }

    public static class FeaturedItem implements Comparable<FeaturedItem> {
        private String icon;
        private String title;
        private String description;
        private int weight;
        @Expose(serialize = false)
        private String permission;
        private Set<Link> links = new HashSet<>();

        @Override
        public boolean equals(Object obj) {
            if (obj == null || !obj.getClass().equals(FeaturedItem.class)) {
                return false;
            }
            FeaturedItem otherObj = (FeaturedItem) obj;
            if (otherObj.getTitle().equalsIgnoreCase(this.getTitle())) {
                return true;
            }

            return false;
        }

        @Override
        public int hashCode() {
            int prime = 31;
            int result = 7;
            result += prime * result + getTitle().hashCode();
            return result;
        }

        @Override
        public int compareTo(FeaturedItem o) {
            return o.getWeight() - this.getWeight();
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getPermission() {
            return permission;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        public Set<Link> getLinks() {
            return links;
        }

        public void setLinks(Set<Link> links) {
            this.links = links;
        }

        public int getWeight() {
            return weight;
        }

        public void setWeight(int weight) {
            this.weight = weight;
        }
    }

    public static class Link {
        private String caption;
        private String url;
        private String hrefTarget;
        private String permission;

        public String getPermission() {
            return permission;
        }

        public void setPermission(String permission) {
            this.permission = permission;
        }

        public String getCaption() {
            return caption;
        }

        public void setCaption(String caption) {
            this.caption = caption;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getHrefTarget() {
            return hrefTarget;
        }

        public void setHrefTarget(String hrefTarget) {
            this.hrefTarget = hrefTarget;
        }
    }
}



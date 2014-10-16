package com.edifecs.xboard.portal;

import java.io.Serializable;

public class AppBarMenuEntry implements Serializable {
    private static final long serialVersionUID = 91;
    
    private final String id;
    private String text;
    private String linkUrl;
    private String description;
    private int weight;
    
    public AppBarMenuEntry(String id,String text, String linkUrl) {
        this(id, text, linkUrl, null);
    }

    public AppBarMenuEntry(String id,String text, String linkUrl, String description) {
        this(id, text, linkUrl, description, 0);
    }

    public AppBarMenuEntry(String id,String text, String linkUrl, String description, int weight) {
    	this.id = id;
        this.text = text;
        this.linkUrl = linkUrl;
        this.description = description;
        this.weight=weight;
    }

    public int getWeight(){return weight;}

    public void setWeight(int weight ) {this.weight=weight;}

    public String getId() {return id;}

	public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

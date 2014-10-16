package com.edifecs.xboard.portal;

import java.io.Serializable;

public class AppBarButton implements Serializable {
    private static final long serialVersionUID = 91;
    
    private final String id;
    private String iconUrl;
    private String linkUrl;
    private int weight;
    private String type;
    private String text;
    private boolean hidden;
    private AppBarMenuEntry[] subMenu;

    public AppBarButton(String id, String type, String text, int weight, String iconUrl, String linkUrl,boolean hidden, AppBarMenuEntry... subMenu)
	{
        this.id = id;
        this.type = type;
        this.text = text;
        this.weight=weight;
        this.iconUrl = iconUrl;
        this.linkUrl = linkUrl;
        this.hidden = hidden;
        this.subMenu = subMenu;  
	}
    
    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public String getId() {
        return id;
    }

    public AppBarMenuEntry[] getSubMenu() {
        return subMenu;
    }

    public void setSubMenu(AppBarMenuEntry... subMenu) {
        this.subMenu = subMenu;
    }

    public boolean hasSubMenu() {
        return subMenu != null && subMenu.length > 0;
    }
}

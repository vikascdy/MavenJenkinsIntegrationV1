package com.edifecs.xboard.portal;

import java.io.Serializable;

public class DoormatMenuEntry implements Serializable,
		Comparable<DoormatMenuEntry> {
	private static final long serialVersionUID = 91;

	private final String namespace;
	private final String id;
	private String text;
	private String linkUrl;
    private int weight;
	private String hrefTarget;
	private String javascript;
	private String permission;
	private DoormatMenuEntry[] subMenu;

	public DoormatMenuEntry(String namespace, String id, String text, int weight,
			String linkUrl, DoormatMenuEntry... subMenu) {
		if (namespace == null)
			throw new NullPointerException("namespace cannot be null.");
		if (id == null)
			throw new NullPointerException("id cannot be null.");
		this.namespace = namespace;
		this.id = id;
        this.weight=weight;
		this.text = text;
		this.linkUrl = linkUrl;
		this.subMenu = subMenu;
	}

	public DoormatMenuEntry(String namespace, String id, String text, int weight,
			String linkUrl, String target, String javascript,
			String permission, DoormatMenuEntry... subMenu) {
		if (namespace == null)
			throw new NullPointerException("namespace cannot be null.");
		if (id == null)
			throw new NullPointerException("id cannot be null.");
		this.namespace = namespace;
		this.id = id;
        this.weight=weight;
		this.text = text;
		this.linkUrl = linkUrl;
		this.subMenu = subMenu;
		this.hrefTarget = target;
		this.javascript = javascript;
		this.permission = permission;
	}

	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

    public int getWeight() {return weight;}

    public void setWeight(int weight) {
        this.weight = weight;
    }

	public String getTarget() {
		return hrefTarget;
	}

	public void setTarget(String target) {
		this.hrefTarget = target;
	}

	public String getJavascript() {
		return javascript;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getId() {
		return id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String url) {
		this.linkUrl = url;
	}

	public void setSubMenu(DoormatMenuEntry... subMenu) {
		this.subMenu = subMenu;
	}

	public boolean hasSubMenu() {
		return subMenu != null && subMenu.length > 0;
	}

	public DoormatMenuEntry[] getSubMenu() {
		return subMenu;
	}

	@Override
	public int compareTo(DoormatMenuEntry other) {
		final int cmp1 = this.namespace.compareTo(other.namespace);
		if (cmp1 != 0)
			return cmp1;
		return this.id.compareTo(other.id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
		result = prime * result + namespace.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoormatMenuEntry other = (DoormatMenuEntry) obj;
		if (!id.equals(other.id))
			return false;
		if (!namespace.equals(other.namespace))
			return false;
		return true;
	}
}

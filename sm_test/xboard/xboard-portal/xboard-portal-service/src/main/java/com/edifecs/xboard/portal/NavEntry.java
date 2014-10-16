package com.edifecs.xboard.portal;

import java.io.Serializable;
import java.util.Arrays;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Entry")
public class NavEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private String namespace;
	private String id;
	private String text;
	private String linkUrl;
    private int weight;
	private String hrefTarget;
	private String javascript;
	private String permission;
	private NavEntry[] subMenu;

	@XmlAttribute(name = "permission")
	public String getPermission() {
		return permission;
	}

	public void setPermission(String permission) {
		this.permission = permission;
	}

    @XmlAttribute(name = "weight")
    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

	@XmlAttribute(name = "target")
	public String getTarget() {
		return hrefTarget;
	}

	public void setTarget(String target) {
		this.hrefTarget = target;
	}

	@XmlAttribute(name = "javascript")
	public String getJavascript() {
		return javascript;
	}

	public void setJavascript(String javascript) {
		this.javascript = javascript;
	}

	@XmlAttribute(name = "namespace")
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	@XmlAttribute(name = "id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlElement(name = "Text", required = true)
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@XmlElement(name = "LinkURL")
	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	@XmlElementWrapper(name = "SubMenu")
	@XmlElement(name = "Entry")
	public NavEntry[] getSubMenu() {
		return subMenu;
	}

	public void setSubMenu(NavEntry... subMenu) {
		this.subMenu = subMenu;
	}

	@Override
	public String toString() {
		return "NavEntry [namespace=" + namespace + ", id=" + id + ", text="
				+ text + ", linkUrl=" + linkUrl + ", hrefTarget=" + hrefTarget
				+ ", javascript=" + javascript + ", permission=" + permission
				+ ", subMenu=" + Arrays.toString(subMenu) + "]";
	}

}

package com.edifecs.xboard.portal;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Menu")
public class NavMenu implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;
	private boolean active = true;
	private String name;
	private String type;
	private String taskHeading;
	private String iconUrl;
	private String defaultLinkUrl;
    private int weight;

	private List<NavEntry> columnOne, columnTwo, tasks;

	@XmlAttribute(name = "active")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

    @XmlElement(name = "Weight")
    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    @XmlElement(name = "Name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "Type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@XmlElement(name = "TaskHeading")
	public String getTaskHeading() {
		return taskHeading;
	}

	public void setTaskHeading(String taskHeading) {
		this.taskHeading = taskHeading;
	}

	@XmlElement(name = "IconURL")
	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	@XmlAttribute(name = "id", required = true)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDefaultLinkUrl() {
		return defaultLinkUrl;
	}

	@XmlAttribute(name = "DefaultLinkURL")
	public void setDefaultLinkUrl(String defaultLinkUrl) {
		this.defaultLinkUrl = defaultLinkUrl;
	}

	@XmlElementWrapper(name = "ColumnOne")
	@XmlElement(name = "Entry")
	public List<NavEntry> getColumnOne() {
		return columnOne;
	}

	public void setColumnOne(List<NavEntry> entries) {
		columnOne = entries;
	}

	@XmlElementWrapper(name = "ColumnTwo")
	@XmlElement(name = "Entry")
	public List<NavEntry> getColumnTwo() {
		return columnTwo;
	}

	public void setColumnTwo(List<NavEntry> entries) {
		columnTwo = entries;
	}

	@XmlElementWrapper(name = "Tasks")
	@XmlElement(name = "Entry")
	public List<NavEntry> getTasks() {
		return tasks;
	}

	public void setTasks(List<NavEntry> entries) {
		tasks = entries;
	}

	@Override
	public String toString() {
		return "NavMenu [id=" + id + ", active=" + active + ", name=" + name
				+ ", type=" + type + ", taskHeading=" + taskHeading + ", weight="+weight
				+ ", iconUrl=" + iconUrl + ", permission="
				+ ", defaultLinkUrl=" + defaultLinkUrl + ", columnOne="
				+ columnOne + ", columnTwo=" + columnTwo + ", tasks=" + tasks
				+ "]";
	}

}

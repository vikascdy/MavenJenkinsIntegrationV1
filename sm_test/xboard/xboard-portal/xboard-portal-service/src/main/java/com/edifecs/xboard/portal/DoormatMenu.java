package com.edifecs.xboard.portal;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class DoormatMenu implements Serializable, Comparable<DoormatMenu>,
		Cloneable {
	private static final long serialVersionUID = 91;

	private final String id;
	private boolean active;
	private String name;
    private int weight;
	private String type;
	private String taskHeading;
	private String defaultLinkUrl;
	private String iconUrl;

	private final EnumMap<Section, SortedSet<DoormatMenuEntry>> entries = new EnumMap<Section, SortedSet<DoormatMenuEntry>>(
			Section.class);

	public String getDefaultLinkUrl() {
		return defaultLinkUrl;
	}

	public void setDefaultLinkUrl(String defaultLinkUrl) {
		this.defaultLinkUrl = defaultLinkUrl;
	}

	public DoormatMenu(String id, String name, String iconUrl, boolean active,
                       String taskHeading, String defaultLinkUrl, String type, int weight) {
		if (id == null)
			throw new NullPointerException("id cannot be null.");
		this.id = id;
		this.name = name;
		this.iconUrl = iconUrl;
		this.active = active;
		this.defaultLinkUrl = defaultLinkUrl;
		this.type = type;
        this.weight=weight;
		if (taskHeading == null)
			this.taskHeading = "tasks";
		else
			this.taskHeading = taskHeading;
		for (Section s : Section.values())
			entries.put(s, new TreeSet<DoormatMenuEntry>());
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

    public int getWeight() {return weight;}

    public void setWeight(int weight) {this.weight=weight;}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTaskHeading() {
		return taskHeading;
	}

	public void setTaskHeading(String taskHeading) {
		this.taskHeading = taskHeading;
	}

	public String getIconUrl() {
		return iconUrl;
	}

	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}

	public String getId() {
		return id;
	}

	public boolean addEntry(Section section, DoormatMenuEntry entry) {
		return entries.get(section).add(entry);
	}

	public Collection<DoormatMenuEntry> getEntries(Section section) {
		return entries.get(section);
	}

	public Map<Section, ? extends Collection<DoormatMenuEntry>> getEntries() {
		return entries;
	}

	@Override
	public int compareTo(DoormatMenu other) {
		return id.compareTo(other.id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id.hashCode();
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
		DoormatMenu other = (DoormatMenu) obj;
		if (!id.equals(other.id))
			return false;
		return true;
	}

	public static enum Section {
		COLUMN1, COLUMN2, TASKS;
	}

	@Override
	public DoormatMenu clone() throws CloneNotSupportedException {
		DoormatMenu m = new DoormatMenu(id, name, iconUrl, active, taskHeading,
				defaultLinkUrl, type, weight);
		for (Section s : Section.values())
			for (DoormatMenuEntry e : getEntries(s))
				m.addEntry(s, e);
		return m;
	}

	@Override
	public String toString() {
		return "DoormatMenu [id=" + id + ", active=" + active + ", name="
				+ name + ", type=" + type + ", taskHeading=" + taskHeading
				+ ", defaultLinkUrl=" + defaultLinkUrl + ", iconUrl=" + iconUrl
				+ ", entries=" + entries + "]";
	}
}

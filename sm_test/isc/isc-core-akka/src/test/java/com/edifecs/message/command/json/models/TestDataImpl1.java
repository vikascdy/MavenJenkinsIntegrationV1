package com.edifecs.message.command.json.models;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.edifecs.message.command.json.models.helpers.TestHDMConstants;

public class TestDataImpl1 implements TestData {
	private static final long serialVersionUID = 6669314704289165718L;

	private final String externalId;
	private final String type;
	private final Map<String, Serializable> properties = new HashMap<String, Serializable>();
	private String classification;
	private String recordId;
	private Date creationDate;
	private Date lastModifiedDate;

	public TestDataImpl1(String externalId, String type) {
		this(externalId, type, TestHDMConstants.CLASSIFICATION_DATA);
	}

	public TestDataImpl1(String externalId, String type, String classification) {
		this(externalId, null, type, classification, new Date(), null, null);
	}

	public TestDataImpl1(TestData previousData) {
		if (previousData == null) {
			this.recordId = null;
			this.externalId = null;
			this.type = null;
			this.classification = TestHDMConstants.CLASSIFICATION_DATA;
			this.creationDate = new Date();
		} else {
			this.recordId = previousData.getRecordId();
			this.externalId = previousData.getExternalId();
			this.type = previousData.getType();
			this.classification = previousData.getClassification();
			this.creationDate = previousData.getCreationDate();
			this.lastModifiedDate = previousData.getLastModifiedDate();
			this.properties.putAll(previousData.getProperties());
		}
	}

	/**
	 * THIS CONSTRUCTOR IS FOR USE BY HDM ONLY
	 */
	public TestDataImpl1(TestData previousData, String recordId) {
		this(previousData.getExternalId(), recordId, previousData.getType(),
				previousData.getClassification(), previousData
						.getCreationDate(), previousData.getLastModifiedDate(),
				previousData.getProperties());
	}

	/**
	 * THIS CONSTRUCTOR IS FOR USE BY HDM ONLY
	 */
	public TestDataImpl1(String externalId, String recordId, String type,
			Date creationDate, Date lastModifiedDate,
			Map<String, Serializable> properties) {
		this(externalId, recordId, type, TestHDMConstants.CLASSIFICATION_DATA,
				creationDate, lastModifiedDate, properties);
	}

	/**
	 * THIS CONSTRUCTOR IS FOR USE BY HDM ONLY
	 */
	public TestDataImpl1(String externalId, String recordId, String type,
			String classification, Date creationDate, Date lastModifiedDate,
			Map<String, Serializable> properties) {
		this.externalId = externalId;
		this.recordId = recordId;
		this.type = type;
		this.classification = classification;
		this.creationDate = creationDate;
		this.lastModifiedDate = lastModifiedDate;

		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

	@Override
	public String getExternalId() {
		return externalId;
	}

	@Override
	public String getRecordId() {
		return recordId;
	}

	public void setRecordId(String recordId) {
		this.recordId = recordId;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public String getClassification() {
		return classification;
	}

	@Override
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public void setCreationDate(Date date) {
		this.creationDate = date;
	}

	@Override
	public Date getLastModifiedDate() {
		return (lastModifiedDate == null) ? creationDate : lastModifiedDate;
	}

	@Override
	public void setLastModifiedDate(Date date) {
		this.lastModifiedDate = date;
	}

	@Override
	public void setProperty(String name, Serializable value) {
		if (name != null && !name.isEmpty() && value != null) {
			properties.put(name, value);
		}
	}

	@Override
	public Serializable getProperty(String name) {
		return properties.get(name);
	}

	@Override
	public Map<String, Serializable> getProperties() {
		return properties;
	}

	@Override
	public Date getTimestamp() {
		Serializable timestamp = getProperty(TestHDMConstants.PROP_TIMESTAMP);
		return (timestamp == null) ? null : (Date) timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((externalId == null) ? 0 : externalId.hashCode());
		result = prime * result
				+ ((recordId == null) ? 0 : recordId.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (getClass() != obj.getClass()) {
			return false;
		}

		TestDataImpl other = (TestDataImpl) obj;

		if (creationDate == null) {
			if (other.getCreationDate() != null) {
				return false;
			}
		} else if (!creationDate.equals(other.getCreationDate())) {
			return false;
		}

		if (externalId == null) {
			if (other.getExternalId() != null) {
				return false;
			}
		} else if (!externalId.equals(other.getExternalId())) {
			return false;
		}

		if (lastModifiedDate == null) {
			if (other.getLastModifiedDate() != null) {
				return false;
			}
		} else if (!lastModifiedDate.equals(other.getLastModifiedDate())) {
			return false;
		}

		if (recordId == null) {
			if (other.getRecordId() != null) {
				return false;
			}
		} else if (!recordId.equals(other.getRecordId())) {
			return false;
		}

		if (type == null) {
			if (other.getType() != null) {
				return false;
			}
		} else if (!type.equals(other.getType())) {
			return false;
		}

		return true;
	}

}

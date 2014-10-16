package com.edifecs.epp.security.jpa.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

@Entity(name = "UserSession")
@NamedQueries({
	@NamedQuery(name = UserSessionEntity.GET_BY_SESSION_ID,
			query = "SELECT se from UserSession as se WHERE se.sessionId=:sessionId") })
public class UserSessionEntity {

	public static final String GET_BY_SESSION_ID = "UserSession.findBySessionId";
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Session_Entity_Id", unique = true, nullable = false)
	private long id;

	@Column(name = "Session_Id", nullable = false, unique = true)
	private String sessionId;

	@Column(name = "Session_CreatedOn", nullable = false)
	private Date dateCreated;

	@OneToOne(targetEntity = UserEntity.class)
	private UserEntity belongsToUser;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	public UserEntity getBelongsToUser() {
		return belongsToUser;
	}

	public void setBelongsToUser(UserEntity belongsToUser) {
		this.belongsToUser = belongsToUser;
	}

}

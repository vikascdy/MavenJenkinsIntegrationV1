package com.edifecs.epp.security.data;

import java.util.Date;

public class LoginAttempt {

	private User user;

	private Date lastAttempt;

	private Date dateSuspended;

	private int totalFailedAttempts;

	public LoginAttempt(User user, Date dateSuspended) {
		super();
		this.user = user;
		this.dateSuspended = dateSuspended;
	}

	public LoginAttempt(Date lastAttempt, int totalFailedAttempts) {
		super();
		this.lastAttempt = lastAttempt;
		this.totalFailedAttempts = totalFailedAttempts;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Date getLastAttempt() {
		return lastAttempt;
	}

	public void setLastAttempt(Date lastAttempt) {
		this.lastAttempt = lastAttempt;
	}

	public int getTotalFailedAttempts() {
		return totalFailedAttempts;
	}

	public void setTotalFailedAttempts(int totalFailedAttempts) {
		this.totalFailedAttempts = totalFailedAttempts;
	}

	public Date getDateSuspended() {
		return dateSuspended;
	}

	public void setDateSuspended(Date dateSuspended) {
		this.dateSuspended = dateSuspended;
	}

}

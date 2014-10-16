package com.edifecs.epp.security.data;

import java.io.Serializable;
import java.util.Properties;

public class PasswordPolicy implements Serializable {

	private static final long serialVersionUID = -7742206096215037988L;
	private Long id;
	private int passwdHistory;
	private int passwdAge;
	private int passwdMaxFailure;
	private int passwdResetFailureLockout;
	private boolean changePasswdAtFirstLogin;
	// private boolean enabled;
	private int passwdLockoutDuration;
	private String passwdRegex;
	private String passwdRegexName;
	private String passwdRegexDesc;

	/** constants **/
	public final static String PASSWD_MAX_ATTEMPTS = "password.max.attempts";
	public final static String PASSWD_RESET_LOCKOUT_INTERVL = "password.reset.lockout.interval";
	public final static String PASSWD_HISTORY = "password.history";
	public final static String PASSWD_AGE = "password.age";
	public final static String PASSWD_REGEX = "password.regex";
	public final static String PASSWD_REGEX_DESC = "password.regex.description";
	public final static String PASSWD_REGEX_NAME = "password.regex.name";
	public final static String PASSWD_LOCKOUT_DURATION = "password.lockout.duration";
	public final static String PASSWD_RESET_LOGIN = "password.reset.login";

	public int getPasswdHistory() {
		return passwdHistory;
	}

	public void setPasswdHistory(int passwdHistory) {
		this.passwdHistory = passwdHistory;
	}

	public int getPasswdAge() {
		return passwdAge;
	}

	public void setPasswdAge(int passwdAge) {
		this.passwdAge = passwdAge;
	}

	public int getPasswdMaxFailure() {
		return passwdMaxFailure;
	}

	public void setPasswdMaxFailure(int passwdMaxFailure) {
		this.passwdMaxFailure = passwdMaxFailure;
	}

	public int getPasswdResetFailureLockout() {
		return passwdResetFailureLockout;
	}

	public void setPasswdResetFailureLockout(int passwdResetFailureLockout) {
		this.passwdResetFailureLockout = passwdResetFailureLockout;
	}

	public boolean isChangePasswdAtFirstLogin() {
		return changePasswdAtFirstLogin;
	}

	public void setChangePasswdAtFirstLogin(boolean changePasswdAtFirstLogin) {
		this.changePasswdAtFirstLogin = changePasswdAtFirstLogin;
	}

    public String getPasswdRegexName() {
        return passwdRegexName;
    }

    public void setPasswdRegexName(String passwdRegexName) {
        this.passwdRegexName = passwdRegexName;
    }

    public int getPasswdLockoutDuration() {
		return passwdLockoutDuration;
	}

	public void setPasswdLockoutDuration(int passwdLockoutDuration) {
		this.passwdLockoutDuration = passwdLockoutDuration;
	}

	public String getPasswdRegex() {
		return passwdRegex;
	}

	public void setPasswdRegex(String passwdRegex) {
		this.passwdRegex = passwdRegex;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPasswdRegexDesc() {
		return passwdRegexDesc;
	}

	public void setPasswdRegexDesc(String passwdRegexDesc) {
		this.passwdRegexDesc = passwdRegexDesc;
	}

	public static PasswordPolicy fromProperties(final Properties properties) {
		PasswordPolicy pp = new PasswordPolicy();
		pp.setPasswdMaxFailure(Integer.valueOf((String) properties.get(PASSWD_MAX_ATTEMPTS)));
		pp.setPasswdAge(Integer.valueOf((String) properties.get(PASSWD_AGE)));
		pp.setPasswdHistory(Integer.valueOf((String) properties.get(PASSWD_HISTORY)));
		pp.setPasswdLockoutDuration(Integer.valueOf((String) properties.get(PASSWD_LOCKOUT_DURATION)));
		pp.setPasswdRegex((String) properties.get(PASSWD_REGEX));
		pp.setPasswdRegexDesc((String) properties.get(PASSWD_REGEX_DESC));
		pp.setPasswdRegexName((String) properties.get(PASSWD_REGEX_NAME));
		pp.setPasswdResetFailureLockout(Integer.valueOf((String) properties.get(PASSWD_RESET_LOCKOUT_INTERVL)));
		pp.setChangePasswdAtFirstLogin(Boolean.parseBoolean((String) properties.get(PASSWD_RESET_LOGIN)));
		return pp;
	}
}

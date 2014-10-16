package com.edifecs.epp.security.jpa.entity;

import javax.persistence.*;

@Entity(name = "PasswordPolicyEntity")
public class PasswordPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Password_Policy_Id", unique = true, nullable = false)
    private Long id;

    @Column(name = "Passwd_History")
    private int passwdHistory = 3;

    @Column(name = "Passwd_Age")
    private int passwdAge = 120;

    @Column(name = "Passwd_Max_Failure")
    private int passwdMaxFailure = 3;

    @Column(name = "Passwd_Reset_Failure_Lockout")
    private int passwdResetFailureLockout = 3;

    @OneToOne(mappedBy = "passwordPolicy")
    private TenantEntity tenant;

    @Column(name = "Change_Passwd_At_First_Login")
    private boolean changePasswdAtFirstLogin;

    @Column(name = "Passwd_Lockout_Duration")
    private int passwdLockoutDuration = 3;

    @Column(name = "Passwd_Regex")
    private String passwdRegex;

    @Column(name = "Passwd_Policy_Enabled")
    private boolean enabled;

    @Column(name = "Passwd_Regex_Desc")
    private String passwdRegexDesc;

    @Column(name = "Passwd_Regex_Name")
    private String passwdRegexName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public boolean isChangePasswdAtFirstLogin() {
        return changePasswdAtFirstLogin;
    }

    public void setChangePasswdAtFirstLogin(boolean changePasswdAtFirstLogin) {
        this.changePasswdAtFirstLogin = changePasswdAtFirstLogin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getPasswdRegexDesc() {
        return passwdRegexDesc;
    }

    public void setPasswdRegexDesc(String passwdRegexDesc) {
        this.passwdRegexDesc = passwdRegexDesc;
    }

    public TenantEntity getTenant() {
        return tenant;
    }

    public void setTenant(TenantEntity tenant) {
        this.tenant = tenant;
    }

    public String getPasswdRegexName() {
        return passwdRegexName;
    }

    public void setPasswdRegexName(String passwdRegexName) {
        this.passwdRegexName = passwdRegexName;
    }
}

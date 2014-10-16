package com.edifecs.epp.security.jpa.entity;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * Entity implementation class for Entity: Contact
 */
@Entity(name = "Contact")
@NamedQueries({ @NamedQuery(name = ContactEntity.FIND_ALL_CONTACTS, query = "SELECT c from Contact as c") })
public class ContactEntity {

	public static final String FIND_ALL_CONTACTS = "Contact.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Contact_Id", unique = true, nullable = false)
	private Long id;

	@Column(name = "Salutation")
	private String salutation;

	@Column(name = "First_Name")
	private String firstName;

	@Column(name = "Middle_Name")
	private String middleName;

	@Column(name = "Last_Name")
	private String lastName;

	@Column(name = "Job_Title")
	private String jobTitle;

	@Column(name = "Email_Address", unique = true)
	private String emailAddress;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Language_Code")
	private LanguageEntity preferredLanguage;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "TimeZone_Code")
	private TimeZoneEntity preferredTimezone;

	@OneToMany(mappedBy = "contact", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<AddressEntity> addresses = new ArrayList<>();

	@OneToOne(mappedBy = "contact", fetch = FetchType.LAZY)
	private UserEntity user;

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSalutation() {
		return salutation;
	}

	public void setSalutation(String salutation) {
		this.salutation = salutation;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public LanguageEntity getPreferredLanguage() {
		return preferredLanguage;
	}

	public void setPreferredLanguage(LanguageEntity preferredLanguage) {
		this.preferredLanguage = preferredLanguage;
	}

	public TimeZoneEntity getPreferredTimezone() {
		return preferredTimezone;
	}

	public void setPreferredTimezone(TimeZoneEntity preferredTimezone) {
		this.preferredTimezone = preferredTimezone;
	}

	public List<AddressEntity> getAddresses() {
		return addresses;
	}

	public void setAddresses(List<AddressEntity> addresses) {
		this.addresses = addresses;
	}

}

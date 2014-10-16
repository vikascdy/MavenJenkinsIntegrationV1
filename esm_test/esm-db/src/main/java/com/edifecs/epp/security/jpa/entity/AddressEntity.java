package com.edifecs.epp.security.jpa.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;

/**
 * Entity implementation class for Entity: Role
 */
@Entity(name = "Address")
@NamedQueries({ @NamedQuery(name = AddressEntity.FIND_ALL_ADDRESSES,
		query = "SELECT a from Address as a") })
public class AddressEntity {

	public static final String FIND_ALL_ADDRESSES = "Address.findAll";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "Address_Id", unique = true, nullable = false)
	private Long id;

	@Column(name = "Street_Address_1")
	private String streetAddress1;

	@Column(name = "Street_Address_2")
	private String streetAddress2;

	@Column(name = "Street_Address_3")
	private String streetAddress3;

	@Column(name = "City")
	private String city;

	@Column(name = "Postal_Code")
	private String portalCode;

	@Column(name = "Phone")
	private String phone;

	@Column(name = "Extension")
	private String extension;

	@Column(name = "Alternative_Phone")
	private String alternativePhone;

	@Column(name = "Alternative_Extension")
	private String alternativeExtension;

	@Column(name = "Fax")
	private String fax;

	@ManyToOne
	@JoinColumn(name = "Contact_Id")
	private ContactEntity contact;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Country_Code")
	private CountryEntity country;

	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Region_Code")
	private RegionEntity region;

	public CountryEntity getCountry() {
		return country;
	}

	public void setCountry(CountryEntity country) {
		this.country = country;
	}

	public RegionEntity getRegion() {
		return region;
	}

	public void setRegion(RegionEntity region) {
		this.region = region;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getStreetAddress1() {
		return streetAddress1;
	}

	public void setStreetAddress1(String streetAddress1) {
		this.streetAddress1 = streetAddress1;
	}

	public String getStreetAddress2() {
		return streetAddress2;
	}

	public void setStreetAddress2(String streetAddress2) {
		this.streetAddress2 = streetAddress2;
	}

	public String getStreetAddress3() {
		return streetAddress3;
	}

	public void setStreetAddress3(String streetAddress3) {
		this.streetAddress3 = streetAddress3;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPortalCode() {
		return portalCode;
	}

	public void setPortalCode(String portalCode) {
		this.portalCode = portalCode;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getAlternativePhone() {
		return alternativePhone;
	}

	public void setAlternativePhone(String alternativePhone) {
		this.alternativePhone = alternativePhone;
	}

	public String getAlternativeExtension() {
		return alternativeExtension;
	}

	public void setAlternativeExtension(String alternativeExtension) {
		this.alternativeExtension = alternativeExtension;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}
}

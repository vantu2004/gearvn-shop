package com.gearvn.common.entity;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "customers")
//session đang cố lưu customer dạng byte[], phải serilize luôn Country
public class Customer implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 45)
	@NotBlank
	@Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
	private String email;

	@Column(nullable = false, length = 64)
	@Size(min = 6, max = 64)
	@NotBlank
	private String password;

	@Column(name = "first_name", nullable = false, length = 45)
	@Size(min = 1, max = 45)
	@NotBlank
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 45)
	@Size(min = 1, max = 45)
	@NotBlank
	private String lastName;

	@Column(name = "phone_number", nullable = false, length = 15)
	@Size(min = 6, max = 15)
	@NotBlank
	private String phoneNumber;

	@Column(nullable = false, length = 64)
	@Size(min = 6, max = 64)
	@NotBlank
	private String addressLine1;

	@Column(name = "address_line_2", length = 64)
	private String addressLine2;

	@Column(nullable = false, length = 45)
	@Size(min = 2, max = 45)
	@NotBlank
	private String city;

	@Column(nullable = false, length = 45)
	@Size(min = 2, max = 45)
	@NotBlank
	private String state;

	@Column(name = "postal_code", nullable = false, length = 10)
	@Size(min = 6, max = 10)
	@NotBlank
	private String postalCode;

	@Column(name = "verification_code", length = 64)
	private String verificationCode;

	private boolean enabled;

	@Column(name = "created_time")
	private Date createdTime;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@Enumerated(EnumType.STRING)
	@Column(name = "authentication_type", length = 10)
	private AuthenticationType authenticationType;

	@Column(name = "reset_password_token", length = 30)
	private String resetPasswordToken;

	// dùng cho xuất csv
	@jakarta.persistence.Transient
	private String countryNameCsv;
	@jakarta.persistence.Transient
	private String createdTimeCsv;

	@Transient
	public String getFullName() {
		// TODO Auto-generated method stub
		return this.firstName + " " + this.lastName;
	}

	@Transient
	public String getCountryNameCsv() {
		return country != null ? country.getName() : "";
	}

	@Transient
	public String getCreatedTimeCsv() {
		if (createdTime == null)
			return "";
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createdTime);
	}

}

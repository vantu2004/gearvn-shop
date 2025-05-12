package com.gearvn.common.entity;

import java.beans.Transient;

import org.springframework.util.StringUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "first_name", nullable = false, length = 45)
	@NotBlank
	private String firstName;

	@Column(name = "last_name", nullable = false, length = 45)
	@NotBlank
	private String lastName;

	@Column(name = "phone_number", nullable = false, length = 15)
	@NotBlank
	private String phoneNumber;

	@Column(name = "address_line_1", nullable = false, length = 64)
	@NotBlank
	private String addressLine1;

	@Column(name = "address_line_2", length = 64)
	private String addressLine2;

	@Column(nullable = false, length = 45)
	@NotBlank
	private String city;

	@Column(nullable = false, length = 45)
	@NotBlank
	private String state;

	@Column(name = "postal_code", nullable = false, length = 10)
	@NotBlank
	private String postalCode;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@Column(name = "default_address")
	private boolean defaultForShipping;

	@Transient
	public String getFullAddress() {
		String address = firstName;

		if (StringUtils.hasText(lastName)) {
			address += " " + lastName;
		}
		if (StringUtils.hasText(addressLine1)) {
			address += ", " + addressLine1;
		}
		if (StringUtils.hasText(addressLine2)) {
			address += ", " + addressLine2;
		}
		if (StringUtils.hasText(city)) {
			address += ", " + city;
		}
		if (StringUtils.hasText(state)) {
			address += ", " + state;
		}
		if (country != null && StringUtils.hasText(country.getName())) {
			address += ", " + country.getName();
		}
		if (StringUtils.hasText(postalCode)) {
			address += ". Postal Code: " + postalCode;
		}
		if (StringUtils.hasText(phoneNumber)) {
			address += ". Phone Number: " + phoneNumber;
		}

		return address;
	}

}

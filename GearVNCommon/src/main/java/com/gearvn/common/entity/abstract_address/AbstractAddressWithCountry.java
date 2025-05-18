package com.gearvn.common.entity.abstract_address;

import java.beans.Transient;
import java.io.Serializable;

import org.springframework.util.StringUtils;

import com.gearvn.common.entity.Country;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
// việc implement Serializable đảm bảo sau khi login thì có thể lưu các field vào session
public abstract class AbstractAddressWithCountry extends AbstractAddress implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name = "country_id")
	protected Country country;

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

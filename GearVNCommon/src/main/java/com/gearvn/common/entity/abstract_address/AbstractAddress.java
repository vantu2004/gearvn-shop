package com.gearvn.common.entity.abstract_address;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.NotBlank;
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
//việc implement Serializable đảm bảo sau khi login thì có thể lưu các field vào session
public abstract class AbstractAddress implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "first_name", nullable = false, length = 45)
	@NotBlank
	protected String firstName;

	@Column(name = "last_name", nullable = false, length = 45)
	@NotBlank
	protected String lastName;

	@Column(name = "phone_number", nullable = false, length = 15)
	@NotBlank
	protected String phoneNumber;

	@Column(name = "address_line_1", nullable = false, length = 64)
	@NotBlank
	protected String addressLine1;

	@Column(name = "address_line_2", length = 64)
	protected String addressLine2;

	@Column(nullable = false, length = 45)
	@NotBlank
	protected String city;

	@Column(nullable = false, length = 45)
	@NotBlank
	protected String state;

	@Column(name = "postal_code", nullable = false, length = 10)
	@NotBlank
	protected String postalCode;
}

package com.gearvn.common.entity;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "shipping_rates")
public class ShippingRate {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	/*
	 * giá vận chuyển/1 đơn vị khối lượng (vd: vật 10kg và rate = 5 --> shippingCost
	 * là 10 * 5 = 50)
	 */
	@Min(value = 1)
	private float rate;

	@Min(value = 1)
	private int days;

	@Column(name = "cod_supported")
	private boolean codSupported;

	@ManyToOne
	@JoinColumn(name = "country_id")
	private Country country;

	@Column(nullable = false, length = 45)
	@NotBlank
	private String state;

	@Override
	public int hashCode() {
		return Objects.hash(codSupported, country, days, id, rate, state);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;

		ShippingRate other = (ShippingRate) obj;

		return id != null && id.equals(other.id);
	}

}

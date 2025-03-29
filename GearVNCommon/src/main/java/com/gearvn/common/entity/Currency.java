package com.gearvn.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Table(name = "currencies")
@AllArgsConstructor
@Data
@NoArgsConstructor
public class Currency {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, length = 64)
	private String name;

	@Column(nullable = false, length = 3)
	private String symbol;

	@Column(nullable = false, length = 4)
	private String code;

	public Currency(String name, String symbol, String code) {
		super();
		this.name = name;
		this.symbol = symbol;
		this.code = code;
	}

	@Override
	public String toString() {
		return name + " - " + code + " - "  + symbol;
	}
	
}

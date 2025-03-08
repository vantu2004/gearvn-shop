package com.gearvn.common.entity;

import java.util.Date;

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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true, length = 255, nullable = false)
	@NotBlank
	private String name;

	@Column(unique = true, length = 255, nullable = false)
	@NotBlank
	private String alias;

	@Column(length = 512, nullable = false, name = "short_description")
	@NotBlank
	private String shortDescription;

	@Column(length = 4096, nullable = false, name = "full_description")
	@NotBlank
	private String fullDescription;

	@Column(name = "created_time", nullable = false, updatable = false)
	private Date createdTime;

	@Column(name = "updated_time")
	private Date updatedTime;

	private boolean enabled;

	@Column(name = "in_stock")
	private boolean inStock;

	// giá nhập
	@NotNull
	@Positive
	private float cost;

	// giá bán
	@NotNull
	@Positive
	private float price;

	@Column(name = "discount_percent")
	@Min(0)
	private float discountPercent;

	@Positive
	private float length;
	@Positive
	private float width;
	@Positive
	private float height;
	@Positive
	private float weight;

	@ManyToOne
	@JoinColumn(name = "category_id")
	@NotNull
	private Category category;

	@ManyToOne
	@JoinColumn(name = "brand_id")
	@NotNull
	private Brand brand;

}

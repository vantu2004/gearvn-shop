package com.gearvn.common.entity;

import java.beans.Transient;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
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

	@Column(name = "main_image", nullable = false)
	@NotBlank
	private String mainImage;

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
	
	// CascadeType.ALL nghĩa là mọi thao tác trên Product thì bên ProductImage cũng cập nhật theo
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	@Builder.Default
	// tránh khi in ra set này thì bị lặp vô hạn do quan hệ 2 chiều với product bên productImage kia
	@ToString.Exclude
	private Set<ProductImage> images = new HashSet<ProductImage>();

	public void addExtraImages(String imageName) {
		this.images.add(ProductImage.builder().name(imageName).product(this).build());
	}
	
	@Transient
	public String getMainImagePath() {
		if (id == null || mainImage == null) {
			return "/images/LogoGearvn.png";
		}
		return "/product-images/" + this.mainImage;
	}
}

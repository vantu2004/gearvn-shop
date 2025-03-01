package com.gearvn.common.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Brand {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(nullable = false, unique = true, length = 45)
	@NotBlank
	private String name;

	@Column(nullable = false, length = 128)
	@NotBlank
	private String logo;

	/*
	 * Chỉ quan tâm truy xuất dữ liệu của category từ bên brand nên chỉ khai báo 1
	 * bên (đây còn gọi là unidirectional manytomany). ManyToMany ko có quan hệ sở
	 * hữu nên có thể quyết định bên nào ánh xạ hay đc ánh xạ đều đc.
	 */
	@ManyToMany
	@JoinTable(name = "brands_categories", joinColumns = @JoinColumn(name = "brand_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	@Builder.Default
	@NotEmpty
	private Set<Category> categories = new HashSet<Category>();
	
	@Transient
	public String getLogoPath() {
		if (id == null || logo == null || logo.isEmpty()) {
			return "/images/LogoGearvn.png";
		}

		return "/brand-logos/" + this.logo;
	}
}

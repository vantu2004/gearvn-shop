package com.gearvn.common.entity;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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
@Table(name = "users")
@ToString
public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 128, nullable = false, unique = true)
	@Size(min = 8, max = 128)
	@Email(message = "Email is not valid", regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$")
	@NotEmpty(message = "Email cannot be empty")
	private String email;

	@Column(length = 64, nullable = false)
	@Size(min = 6, max = 64)
	@NotEmpty(message = "Password cannot be empty")
	private String password;

	@Column(length = 32, nullable = false)
	@Size(min = 1, max = 32)
	@NotEmpty(message = "First name cannot be empty")
	private String firstName;

	@Column(length = 32, nullable = false)
	@Size(min = 1, max = 32)
	@NotEmpty(message = "Last name cannot be empty")
	private String lastName;

	@Column(length = 128)
	private String photos;

	private boolean enabled;

	// chỉ cần từ 1 phía user là đủ
	/*
	 * mặc định là lazy loading (chỉ tải User nhưng chưa tải ngay Role) -> security
	 * cần load các role ngay lập tức nhưng bị lazy loading -> lỗi
	 */
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	// vì dùng builder nên mặc định các biến khởi tạo "="
	@Builder.Default
	@NotEmpty(message = "Role cannot be empty")
	private Set<Role> roles = new HashSet<Role>();

	public void addRole(Role role) {
		this.roles.add(role);
	}

	@Transient
	public String getPhotosImagePath() {
		if (id == null || photos == null || photos.isEmpty()) {
			return "/images/LogoGearvn.png";
		}

		return "/user-photos/" + this.photos;
	}
}

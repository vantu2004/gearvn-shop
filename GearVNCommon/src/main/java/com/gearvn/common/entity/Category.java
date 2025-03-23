package com.gearvn.common.entity;

import java.util.Set;

import java.util.HashSet;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// vì đang gọi tập set trong cùng 1 entity -> toString() đệ quy vô hạn -> StackOverflowError
@ToString(exclude = "children")
@Table(name = "categories")
// thực hiện hashCode và equals theo field đc đánh dấu (id)
// hashCode() giúp Java tìm nhanh đối tượng, còn equals() đảm bảo hai đối tượng thực sự bằng nhau.
// bắt buộc phải có, nếu ko sẽ gây lỗi khi get listCategories
// bắt buộc triển khai cả 2 hashCode và equals nếu ko @Data sẽ mất hiệu lực
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Category {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	// chỉ định id sẽ đc equals() so sánh và hashCode() tìm kiếm
	@EqualsAndHashCode.Include
	private Integer id;

	@Column(length = 128, nullable = false, unique = true)
	@NotEmpty
	@Size(min = 2, max = 128)
	private String name;

	@Column(length = 128, nullable = false, unique = true)
	@NotEmpty
	@Size(min = 2, max = 64)
	private String alias;

	@Column(length = 128, nullable = false)
	@NotEmpty
	@Size(min = 2, max = 128)
	private String image;

	private boolean enabled;

	// phục vụ việc tìm kiếm product theo category trong combobox
	// cho phép null vì rootCategory null
	@Column(name = "all_parent_ids", length = 256, nullable = true)
	private String allParentIds;
	
	// nhiều subCategory tham chiếu tới 1 category
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Category parent;

	// 1 category được tham chiếu bởi nhiều subCategories
	@OneToMany(mappedBy = "parent")
	@Builder.Default
	private Set<Category> children = new HashSet<Category>();

	@Transient
	public String getImagePath() {
		if (id == null || image == null || image.isEmpty()) {
			return "/images/LogoGearvn.png";
		}

		return "/category-images/" + this.image;
	}

	@Transient
	public boolean isHasChildren() {
		return children != null && !children.isEmpty();
	}
}

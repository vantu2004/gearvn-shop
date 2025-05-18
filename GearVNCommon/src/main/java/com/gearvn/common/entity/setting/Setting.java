package com.gearvn.common.entity.setting;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "settings")
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Setting {
	// key trùng từ khóa trong mysql nên phải chỉ định cụ thể
	@Id
	@Column(name = "`key`", nullable = false, length = 128)
	private String key;

	@Column(nullable = false, length = 1024)
	private String value;

	// @Enumerated(EnumType.STRING) giúp lưu Enum dưới dạng chuỗi.
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 45)
	private SettingCategory settingCategory;

	/*
	 * Kiểm tra hai đối tượng giữa truyền vào và this có bằng nhau không dựa trên
	 * key, settingCategory, value.
	 */
	// custom lại hàm để chỉ so sánh key (mặc định là so sánh toàn bộ thuộc tính)
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Setting other = (Setting) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}

	// Tạo mã băm để hỗ trợ tìm kiếm nhanh hơn trong tập hợp (Set, Map).
	@Override
	public int hashCode() {
		return Objects.hash(key, settingCategory, value);
	}

}

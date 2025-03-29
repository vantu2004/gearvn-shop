package com.gearvn.admin.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.Setting;
import com.gearvn.common.entity.SettingCategory;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
	/*
	 * trong class Setting có field settingCategory kiểu SettingCategory, field này
	 * dùng @Enumerated(EnumType.STRING) để chuyển từ enum về string
	 */
	List<Setting> findBySettingCategory(SettingCategory settingCategory);
}

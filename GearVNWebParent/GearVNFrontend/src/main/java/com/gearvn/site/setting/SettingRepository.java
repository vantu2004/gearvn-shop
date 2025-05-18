package com.gearvn.site.setting;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gearvn.common.entity.setting.Setting;
import com.gearvn.common.entity.setting.SettingCategory;

@Repository
public interface SettingRepository extends JpaRepository<Setting, String> {
	/*
	 * trong class Setting có field settingCategory kiểu SettingCategory, field này
	 * dùng @Enumerated(EnumType.STRING) để chuyển từ enum về string
	 */
	List<Setting> findBySettingCategory(SettingCategory settingCategory);

	@Query("SELECT s FROM Setting s WHERE s.settingCategory = ?1 OR s.settingCategory = ?2")
	List<Setting> getGeneralAndCurrencySetting(SettingCategory general, SettingCategory currency);
}

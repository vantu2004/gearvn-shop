package com.gearvn.admin.setting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.setting.Setting;
import com.gearvn.common.entity.setting.SettingCategory;

@DataJpaTest(showSql = false)
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class SettingRepositoryTests {
	@Autowired
	private SettingRepository settingRepository;

	@Test
	public void testCreateGeneralsSetting() {
		Setting setting = Setting.builder().key("SITE_NAME").value("gearvn").settingCategory(SettingCategory.GENERAL)
				.build();
		Setting setting1 = Setting.builder().key("SITE_LOGO").value("LogoGearvn.png")
				.settingCategory(SettingCategory.GENERAL).build();
		Setting setting2 = Setting.builder().key("COPYRIGHT").value("Copyright (C) 2022 Gearvn - vantu")
				.settingCategory(SettingCategory.GENERAL).build();

		this.settingRepository.saveAll(List.of(setting1, setting2));

		List<Setting> settings = this.settingRepository.findAll();

		assertThat(settings).size().isGreaterThan(0);
	}

	@Test
	public void testCreateCurrencySettings() {
		Setting currencyId = new Setting("CURRENCY_ID", "1", SettingCategory.CURRENCY);
		Setting symbol = new Setting("CURRENCY_SYMBOL", "$", SettingCategory.CURRENCY);
		Setting symbolPosition = new Setting("CURRENCY_SYMBOL_POSITION", "before", SettingCategory.CURRENCY);
		Setting decimalPointType = new Setting("DECIMAL_POINT_TYPE", "POINT", SettingCategory.CURRENCY);
		Setting decimalDigits = new Setting("DECIMAL_DIGITS", "2", SettingCategory.CURRENCY);
		Setting thousandsPointType = new Setting("THOUSANDS_POINT_TYPE", "COMMA", SettingCategory.CURRENCY);

		this.settingRepository.saveAll(
				List.of(currencyId, symbol, symbolPosition, decimalPointType, decimalDigits, thousandsPointType));

	}

	@Test
	public void testGetListSettingsBySettingCategory() {
		List<Setting> settings = this.settingRepository.findBySettingCategory(SettingCategory.CURRENCY);
		for (Setting setting : settings) {
			System.out.println(setting);
		}
		assertThat(settings).size().isGreaterThan(0);
	}
}

package com.gearvn.site.setting;

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
	public void getGeneralAndCurrencySetting() {
		List<Setting> settings = this.settingRepository.getGeneralAndCurrencySetting(SettingCategory.GENERAL,
				SettingCategory.CURRENCY);
		for (Setting setting : settings) {
			System.out.println(setting.getKey() + " - " + setting.getValue());
		}
	}

}

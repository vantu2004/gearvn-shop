package com.gearvn.site.setting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Setting;
import com.gearvn.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired
	private SettingRepository settingRepository;

	public List<Setting> getGeneralAndCurrencySetting() {
		return this.settingRepository.getGeneralAndCurrencySetting(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}

}

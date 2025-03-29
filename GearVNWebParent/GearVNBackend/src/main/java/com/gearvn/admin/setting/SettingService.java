package com.gearvn.admin.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Setting;
import com.gearvn.common.entity.SettingCategory;

@Service
public class SettingService {
	@Autowired
	private SettingRepository settingRepository;

	public List<Setting> getAllSettings() {
		return this.settingRepository.findAll();
	}

	public GeneralSettingBag getGeneralSettingBag() {
		List<Setting> settings = new ArrayList<Setting>();

		// trong form general setting chỉ có 2 type là GENERAL và CURRENCY
		List<Setting> generalSettings = this.settingRepository.findBySettingCategory(SettingCategory.GENERAL);
		List<Setting> currencySettings = this.settingRepository.findBySettingCategory(SettingCategory.CURRENCY);

		settings.addAll(generalSettings);
		settings.addAll(currencySettings);

		return new GeneralSettingBag(settings);
	}

	public void saveAllSettings(List<Setting> settings) {
		this.settingRepository.saveAll(settings);
	}
}

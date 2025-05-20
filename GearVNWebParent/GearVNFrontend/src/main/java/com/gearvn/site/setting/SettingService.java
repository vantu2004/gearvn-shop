package com.gearvn.site.setting;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.setting.Setting;
import com.gearvn.common.entity.setting.SettingCategory;

@Service
public class SettingService {
	@Autowired

	private SettingRepository settingRepository;

	public List<Setting> getGeneralAndCurrencySetting() {
		return this.settingRepository.getGeneralAndCurrencySetting(SettingCategory.GENERAL, SettingCategory.CURRENCY);
	}

	public EmailSettingBag getEmailSettingBag() {
		List<Setting> settings = new ArrayList<Setting>();

		/*
		 * trong form mail server và mail template setting chỉ có 2 type là MAIL_SERVER
		 * và MAIL_TEMPLATE
		 */
		List<Setting> mailServerSettings = this.settingRepository.findBySettingCategory(SettingCategory.MAIL_SERVER);
		List<Setting> mailTemplateSettings = this.settingRepository
				.findBySettingCategory(SettingCategory.MAIL_TEMPLATE);

		settings.addAll(mailServerSettings);
		settings.addAll(mailTemplateSettings);

		return new EmailSettingBag(settings);
	}

	public CurrencySettingBag getCurrencySettingBag() {
		List<Setting> settings = this.settingRepository.findBySettingCategory(SettingCategory.CURRENCY);

		return new CurrencySettingBag(settings);
	}
}

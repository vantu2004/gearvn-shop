package com.gearvn.site.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Currency;
import com.gearvn.common.entity.setting.Setting;
import com.gearvn.common.entity.setting.SettingCategory;
import com.gearvn.site.currency.CurrencyRepository;

@Service
public class SettingService {

	@Autowired
	private SettingRepository settingRepository;

	@Autowired
	private CurrencyRepository currencyRepository;

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

	public PaymentSettingBag getPaymentSettingBag() {
		List<Setting> settings = this.settingRepository.findBySettingCategory(SettingCategory.PAYMENT);

		return new PaymentSettingBag(settings);
	}

	// lấy currencyCode dựa vào CURRENCY_ID hiện tại lưu trong db
	public String getCurrencyCodeByCurrencyId() {
		Setting setting = this.settingRepository.findByKey("CURRENCY_ID");

		Currency currency = this.currencyRepository.findById(Integer.parseInt(setting.getValue()))
				.orElseThrow(() -> new NoSuchElementException("Currency Not Found!"));

		return currency.getCode();
	}
}

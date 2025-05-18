package com.gearvn.admin.setting;

import java.util.List;

import com.gearvn.common.entity.setting.Setting;
import com.gearvn.common.entity.setting.SettingBag;

public class GeneralSettingBag extends SettingBag {

	/*
	 * super có tác dụng giống this trong lớp cha dùng để gọi constructor, method,
	 * field
	 */

	/*
	 * hiện tại trong form general setting chỉ có 7 field và chưa có
	 * CURRENCY_SYMBOL, SITE_LOGO --> update value dựa vào CURRENCY_ID
	 */

	public GeneralSettingBag(List<Setting> settings) {
		super(settings);
	}

	public void updateCurrencySymbol(String value) {
		super.updateSetting("CURRENCY_SYMBOL", value);
	}

	public void updateSiteLogo(String value) {
		super.updateSetting("SITE_LOGO", value);
	}

	public List<Setting> getSettings() {
		return super.getSettings();
	}
}

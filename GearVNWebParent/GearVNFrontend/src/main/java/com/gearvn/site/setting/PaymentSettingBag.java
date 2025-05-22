package com.gearvn.site.setting;

import java.util.List;

import com.gearvn.common.entity.setting.Setting;
import com.gearvn.common.entity.setting.SettingBag;

public class PaymentSettingBag extends SettingBag {

	public PaymentSettingBag(List<Setting> settings) {
		super(settings);
	}

	public String getPayPalApiBaseUrl() {
		return super.getValue("PAYPAL_API_BASE_URL");
	}

	public String getPayPalApiClientId() {
		return super.getValue("PAYPAL_API_CLIENT_ID");
	}

	public String getPayPalClientSecret() {
		return super.getValue("PAYPAL_API_CLIENT_SECRET");
	}
}

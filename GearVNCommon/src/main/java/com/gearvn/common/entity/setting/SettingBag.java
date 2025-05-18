package com.gearvn.common.entity.setting;

import java.util.List;

public class SettingBag {
	private List<Setting> settings;

	public SettingBag(List<Setting> settings) {
		this.settings = settings;
	}

	public Setting getSetting(String key) {
		/*
		 * tạo 1 setting mới dựa vào key, dùng hàm equals bên Setting để so sánh key và lấy
		 * chỉ mục trong list đã đc truyền vào trong constructor
		 */
		int index = settings.indexOf(Setting.builder().key(key).build());
		if (index >= 0) {
			return settings.get(index);
		}
		return null;
	}

	/*
	 * lấy giá trị dựa vào key (nếu setting được tìm dựa vào key có tồn tại trong
	 * list trong hàm getSetting)
	 */
	public String getValue(String key) {
		Setting setting = getSetting(key);
		return (setting != null) ? setting.getValue() : null;
	}

	// nếu setting có tồn tại trong list và value != null thì update value
	public void updateSetting(String key, String value) {
		Setting setting = getSetting(key);
		if (setting != null && value != null) {
			setting.setValue(value);
		}
	}

	public List<Setting> getSettings() {
		return this.settings;
	}
}

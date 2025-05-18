package com.gearvn.site.setting;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gearvn.common.entity.setting.Setting;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class SettingFilter implements Filter {

	@Autowired
	private SettingService settingService;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub

		HttpServletRequest servletRequest = (HttpServletRequest) request;
		String url = servletRequest.getRequestURL().toString();

		if (url.endsWith(".js") || url.endsWith(".png") || url.endsWith(".jpg") || url.endsWith(".css")) {
			chain.doFilter(servletRequest, response);
			return;
		}

		/*
		 * khi tải 1 trang, trình duyệt sẽ gửi nhiều request ĐỘC LẬP để yêu cầu tài
		 * nguyên, vì ĐỘC LẬP nên return khi gặp các tài nguyên tĩnh ko ảnh hướng đến
		 * việc load data cho request đên html
		 */
		List<Setting> settings = this.settingService.getGeneralAndCurrencySetting();
		for (Setting setting : settings) {
			request.setAttribute(setting.getKey(), setting.getValue());
		}

		chain.doFilter(request, response);
	}

}

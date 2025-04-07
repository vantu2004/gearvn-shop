package com.gearvn.site;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.gearvn.site.setting.EmailSettingBag;

import jakarta.servlet.http.HttpServletRequest;

public class Utility {

	public static String getSiteUrl(HttpServletRequest request) {
		// lấy url trong request hiện tại (full)
		String siteUrl = request.getRequestURL().toString();

		/*
		 * loại bỏ phần url phía sau và chỉ lấy context path (vd:
		 * "http://localhost:8080/myapp/product/view" ➜ "http://localhost:8080/myapp")
		 */
		return siteUrl.replace(request.getServletPath(), "");
	}

	public static JavaMailSenderImpl prepareMailSender(EmailSettingBag emailSettingBag) {
		JavaMailSenderImpl javaMailSenderImpl = new JavaMailSenderImpl();

		javaMailSenderImpl.setHost(emailSettingBag.getHost());
		javaMailSenderImpl.setPort(emailSettingBag.getPort());
		javaMailSenderImpl.setUsername(emailSettingBag.getUserName());
		javaMailSenderImpl.setPassword(emailSettingBag.getPassword());

		Properties properties = new Properties();
		properties.setProperty("mail.smtp.auth", emailSettingBag.getSmtpAuth());
		properties.setProperty("mail.smtp.starttls.enable", emailSettingBag.getSmtpSecured());
		
		javaMailSenderImpl.setJavaMailProperties(properties);

		return javaMailSenderImpl;
	}
}

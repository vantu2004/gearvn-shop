package com.gearvn.site;

import java.util.Properties;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import com.gearvn.site.security.oauth.CustomerOAuth2User;
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

	// có thể dùng @AuthenticationPrincipal như bên class AccountControler
	/*
	 * dùng bên CustomerController để update Customer và bên CartRestController để
	 * lấy email của custoemr đã login
	 */
	public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
		String email = "";

		Object principal = request.getUserPrincipal();

		/*
		 * khi update Customer thì chắc chắn đã login rồi, chỉ dùng cho check login để
		 * addProductToCart
		 */
		if (principal == null) {
			return null;
		}

		/*
		 * chưa login: AnonymousAuthenticationToken
		 */

		/*
		 * đã login: UsernamePasswordAuthenticationToken, OAuth2AuthenticationToken,
		 * RememberMeAuthenticationToken (tick chọn remember-me, thử nhiều lần nhưng vẫn
		 * ko thấy, nhưng để đảm bào thì vẫn thêm vào)
		 */

		if (principal instanceof UsernamePasswordAuthenticationToken
				|| principal instanceof RememberMeAuthenticationToken) {
			email = request.getUserPrincipal().getName();
		} else if (principal instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) principal;
			CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) oAuth2AuthenticationToken.getPrincipal();
			email = customerOAuth2User.getEmail();
		}

		return email;
	}
}

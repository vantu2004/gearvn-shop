package com.gearvn.site.security.oauth;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.gearvn.common.entity.AuthenticationType;
import com.gearvn.common.entity.Customer;
import com.gearvn.site.customer.CustomerService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private int THIRTY_DAYS_SECONDS = 2592000;

	@Autowired
	private CustomerService customerService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		CustomerOAuth2User customerOAuth2User = (CustomerOAuth2User) authentication.getPrincipal();

		String name = customerOAuth2User.getFullName();
		String email = customerOAuth2User.getEmail();
		String countryCode = request.getLocale().getCountry();
		String clientName = customerOAuth2User.getClientName();

		AuthenticationType authenticationType = this.getAuthenticationType(clientName);

		Customer customer = this.customerService.getCustomerByEmail(email);
		if (customer == null) {
			this.customerService.addNewCustomerByGoggleInfo(name, email, countryCode, authenticationType);
		} else {
			this.customerService.updateAuthenticationType(customer, authenticationType);
		}

		// settime session
		request.getSession().setMaxInactiveInterval(THIRTY_DAYS_SECONDS);

		response.sendRedirect(request.getContextPath() + "/");

	}

	private AuthenticationType getAuthenticationType(String clientName) {
		if (clientName.equals("Gooogle")) {
			return AuthenticationType.GOOGLE;
		} else if (clientName.equals("Facebook")) {
			return AuthenticationType.FACEBOOK;
		} else {
			return AuthenticationType.DATABASE;
		}
	}
}

package com.gearvn.site.security;

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

public class DatabaseLoginSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private CustomerService customerService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		GearvnCustomerDetails gearvnCustomerDetails = (GearvnCustomerDetails) authentication.getPrincipal();
		Customer customer = gearvnCustomerDetails.getCustomer();

		this.customerService.updateAuthenticationType(customer, AuthenticationType.DATABASE);

		response.sendRedirect(request.getContextPath() + "/");

	}

}

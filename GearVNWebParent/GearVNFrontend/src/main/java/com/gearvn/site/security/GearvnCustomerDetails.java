package com.gearvn.site.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.gearvn.common.entity.Customer;

public class GearvnCustomerDetails implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Customer customer;

	public GearvnCustomerDetails(Customer customer) {
		this.customer = customer;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPassword() {
		// TODO Auto-generated method stub
		return this.customer.getPassword();
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.customer.getEmail();
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return this.customer.isEnabled();
	}

	// dùng khi login thường, thêm 1 hàm bên CCustomerOAuth2User để khi login gg/fb
	public String getFullName() {
		return this.customer.getFullName();
	}

	// dùng update AuthenticationType khi login bằng Database
	public Customer getCustomer() {
		return this.customer;
	}
}

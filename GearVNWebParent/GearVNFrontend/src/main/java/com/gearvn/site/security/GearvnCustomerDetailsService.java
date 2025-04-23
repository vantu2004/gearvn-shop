package com.gearvn.site.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.gearvn.common.entity.Customer;
import com.gearvn.site.customer.CustomerRepository;

public class GearvnCustomerDetailsService implements UserDetailsService {

	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		Customer customer = this.customerRepository.findByEmail(email);
		if (customer != null) {
			return new GearvnCustomerDetails(customer);
		}

		throw new UsernameNotFoundException("Could not find customer with email " + email);
	}

}

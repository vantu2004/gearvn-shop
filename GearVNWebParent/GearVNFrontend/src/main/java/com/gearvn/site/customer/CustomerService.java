package com.gearvn.site.customer;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.Customer;
import com.gearvn.site.country.CountryRepository;

import jakarta.validation.Valid;

@Service
public class CustomerService {
	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Country> getAllCountries() {
		return this.countryRepository.findAllByOrderByNameAsc();
	}

	public void handleSaveCustomer(@Valid Customer customer) {
		// TODO Auto-generated method stub
		encodePassword(customer);

		customer.setEnabled(false);
		customer.setCreatedTime(new Date());
		customer.setVerificationCode(generateVerificationCode());

		this.customerRepository.save(customer);
	}

	private String generateVerificationCode() {
		Random random = new Random();
		int otpValue = 100000 + random.nextInt(900000);
		return String.valueOf(otpValue);
	}

	private void encodePassword(Customer customer) {
		String encodedPassword = this.passwordEncoder.encode(customer.getPassword());
		customer.setPassword(encodedPassword);
	}

	public boolean isEmailUnique(String email) {
		Customer customer = this.customerRepository.findByEmail(email);
		return customer == null;
	}

	public boolean isVerified(String verificationCode) {
		Customer customer = this.customerRepository.findByVerificationCode(verificationCode);

		// nếu ko tìm thấy customer hay customer đã kích hoạt thì return false
		if (customer == null || customer.isEnabled()) {
			return false;
		}
		// trường hợp còn lại thì update enabled và set verificationCode thành null
		else {
			this.customerRepository.enabledCustomer(customer.getId());
			return true;
		}
	}
}

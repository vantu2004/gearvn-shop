package com.gearvn.site.customer;

import java.util.Date;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gearvn.common.entity.AuthenticationType;
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
		customer.setAuthenticationType(AuthenticationType.DATABASE);

		this.customerRepository.save(customer);
	}

	public void handleSaveUpdateCustomer(@Valid Customer customer) {
		// TODO Auto-generated method stub
		Customer oldCustomer = this.customerRepository.findByEmail(customer.getEmail());
		if (!customer.getPassword().equals(oldCustomer.getPassword())
				&& oldCustomer.getAuthenticationType().equals(AuthenticationType.DATABASE)) {
			encodePassword(customer);
		}

		customer.setEnabled(oldCustomer.isEnabled());
		customer.setCreatedTime(oldCustomer.getCreatedTime());

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

	public void updateAuthenticationType(Customer customer, AuthenticationType authenticationType) {
		if (!customer.getAuthenticationType().equals(authenticationType)) {
			this.customerRepository.updateAuthenticationType(customer.getId(), authenticationType);
		}
	}

	public Customer getCustomerByEmail(String email) {
		// TODO Auto-generated method stub
		return this.customerRepository.findByEmail(email);
	}

	public void addNewCustomerByGoggleInfo(String name, String email, String countryCode,
			AuthenticationType authenticationType) {
		Customer customer = new Customer();
		customer.setEmail(email);
		customer.setPassword("******");

		setName(name, customer);

		customer.setPhoneNumber("******");
		customer.setAddressLine1("******");
		customer.setAddressLine2("******");
		customer.setCity("******");
		customer.setState("******");
		customer.setPostalCode("******");
		customer.setEnabled(true);
		customer.setCreatedTime(new Date());
		customer.setAuthenticationType(authenticationType);
		customer.setCountry(this.countryRepository.findByCode(countryCode));

		this.customerRepository.save(customer);
	}

	private void setName(String name, Customer customer) {
		// TODO Auto-generated method stub
		String[] names = name.split(" ");
		if (names.length < 2) {
			customer.setFirstName(name);
			customer.setLastName("******");
		} else {
			String firstName = names[0];
			String lastName = name.replaceFirst(firstName + " ", "");

			customer.setFirstName(firstName);
			customer.setLastName(lastName);
		}
	}

}

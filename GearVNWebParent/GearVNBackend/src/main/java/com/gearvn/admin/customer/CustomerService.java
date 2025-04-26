package com.gearvn.admin.customer;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gearvn.admin.paging.PagingAndSortingHelper;
import com.gearvn.admin.setting.country.CountryRepository;
import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.Customer;

@Service
public class CustomerService {
	public static final int CUSTOMERS_PER_PAGE = 10;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private CountryRepository countryRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public List<Customer> getAllCustomers() {
		return this.customerRepository.findAll();
	}

	public void getAllCustomersPageable(int currentPage, PagingAndSortingHelper helper) {
		helper.listEntitites(currentPage, CUSTOMERS_PER_PAGE, customerRepository);
	}

	public Customer getCustomerById(Integer id) {
		return this.customerRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Could not find any customers with ID " + id));
	}

	public List<Country> getAllCountries() {
		return this.countryRepository.findAllByOrderByNameAsc();
	}

	public boolean isEmailUnique(Integer id, String email) {
		Customer existCustomer = this.customerRepository.findByEmail(email);
		// nếu email có tồn tại nhưng thuộc về customer khác thì return false
		if (existCustomer != null && existCustomer.getId() != id) {
			return false;
		}

		return true;
	}

	/*
	 * thay vì gán lại từng giá trị từ customerInForm (13 field) vào lại
	 * customerInDB (15 field) thì gán ngc createdTime, verificationCode từ
	 * customerInDB vào customerInForm
	 */
	public void handleSaveCustomer(Customer customerInForm) {
		Customer customerInDB = this.getCustomerById(customerInForm.getId());

		if (!customerInForm.getPassword().equals(customerInDB.getPassword())) {
			String newPassword = this.passwordEncoder.encode(customerInForm.getPassword());
			customerInForm.setPassword(newPassword);
		}

		customerInForm.setCreatedTime(customerInDB.getCreatedTime());
		customerInForm.setVerificationCode(customerInDB.getVerificationCode());
		customerInForm.setAuthenticationType(customerInDB.getAuthenticationType());
		customerInForm.setResetPasswordToken(customerInDB.getResetPasswordToken());

		this.customerRepository.save(customerInForm);
	}

	public void deleteCustomerById(Integer id) {
		Long count = this.customerRepository.countById(id);
		if (count == null || count == 0) {
			throw new NoSuchElementException("Could not find any customers with ID " + id);
		}

		this.customerRepository.deleteById(id);
	}
}

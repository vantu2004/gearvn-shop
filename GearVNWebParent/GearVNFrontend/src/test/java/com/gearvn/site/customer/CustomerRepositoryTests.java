package com.gearvn.site.customer;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.gearvn.common.entity.AuthenticationType;
import com.gearvn.common.entity.Country;
import com.gearvn.common.entity.Customer;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class CustomerRepositoryTests {

	@Autowired
	private CustomerRepository customerRepository;
	
	private static final List<Integer> customerIds = new ArrayList<>();

	@Test
	public void testCreateCustomers() {
		for (int i = 1; i <= 5; i++) {
			Customer customer = Customer.builder().email("testuser" + i + "@example.com").password("password" + i)
					.firstName("First" + i).lastName("Last" + i).phoneNumber("012345678" + i)
					.addressLine1("123 Street " + i).addressLine2("Ward " + i).city("City" + i).state("State" + i)
					.postalCode("1000" + i).verificationCode("code" + i).enabled(true).createdTime(new Date())
					.country(Country.builder().id(1).build()) // Giả định country ID = 1 tồn tại
					.build();

			Customer saved = customerRepository.save(customer);
			customerIds.add(saved.getId());

			assertThat(saved.getId()).isGreaterThan(0);
		}

		assertThat(customerIds).hasSize(5);
	}

	@Test
	public void testReadCustomers() {
		List<Customer> result = customerRepository.findAll();

		assertThat(result).isNotEmpty();
	}

	@Test
	public void testUpdateCustomers() {
		Customer customer = customerRepository.findById(1).get();
		customer.setFirstName("Updated");
		customer.setEnabled(false);
		Customer updated = customerRepository.save(customer);

		assertThat(updated.getFirstName()).isEqualTo("Updated");
		assertThat(updated.isEnabled()).isFalse();
	}

	@Test
	public void testDeleteCustomers() {
		customerRepository.deleteById(1);

		Optional<Customer> result = customerRepository.findById(1);
		assertThat(result).isNotPresent();
	}

	@Test
	public void testGetCustomerByEmail() {
		Customer customer = customerRepository.findByEmail("testuser2@example.com");
		System.out.println(customer);

		assertThat(customer).isNotNull();
	}

	@Test
	public void testGetCustomerByVerificationCode() {
		Customer customer = customerRepository.findByVerificationCode("code3");
		System.out.println(customer);

		assertThat(customer).isNotNull();
	}

	@Test
	public void testUpdateAuthenticationType() {
		this.customerRepository.updateAuthenticationType(2, AuthenticationType.DATABASE);

		Customer customer = this.customerRepository.findById(2).orElse(null);

		assertThat(customer).isNotNull();
		assertThat(customer.getAuthenticationType()).isEqualTo(AuthenticationType.DATABASE);
	}
	
}
